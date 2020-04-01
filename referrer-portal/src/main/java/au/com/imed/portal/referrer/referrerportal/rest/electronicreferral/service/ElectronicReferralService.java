package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.PdfGenerator;
import au.com.imed.portal.referrer.referrerportal.common.util.StringConversionUtil;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository.ElectronicReferralJPARepository;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;

@Service
public class ElectronicReferralService {
	
	private Logger logger = LoggerFactory.getLogger(ElectronicReferralService.class);

	@Autowired
	ElectronicReferralJPARepository electronicReferralJPARepository;

	@Autowired
	ReferrerMailService emailService;

	@Autowired
	CrmPostcodeJpaRepository crmPostcodeJpaRepository;

	@Autowired
	PdfGenerator pdfReferralGenerator;

	@Autowired
	GoFaxSmsService smsService;
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;

	@Value("#{'${imed.electronic.referral.nsw.email.id}'.split(',')}")
	List<String> nswToEmailIds;

	@Value("#{'${imed.electronic.referral.qld.email.id}'.split(',')}")
	List<String> qldToEmailIds;

	@Value("#{'${imed.electronic.referral.vic.email.id}'.split(',')}")
	List<String> vicToEmailIds;

	@Value("#{'${imed.electronic.referral.wa.email.id}'.split(',')}")
	List<String> waToEmailIds;
		
	@Value("#{'${imed.electronic.referral.drjones.email.id}'.split(',')}")
	List<String> drJonesToEmailIds;
	
	@Value("#{'${imed.electronic.referral.oz.email.id}'.split(',')}")
	List<String> ozToEmailIds;

	@Value("#{'${imed.electronic.referral.ril.email.id}'.split(',')}")
	List<String> rilToEmailIds;

	public ElectronicReferralForm save(ElectronicReferralForm electronicReferralForm, boolean isReferrerLogged) throws Exception {
		electronicReferralForm = electronicReferralJPARepository.save(electronicReferralForm);
		
		boolean isDrJonesBu = isDrJonesBu(electronicReferralForm.getPatientPostcode());
				
		sendEmailToCrm(electronicReferralForm, isReferrerLogged);
				
		if(electronicReferralForm.isCopyToMe() && StringUtils.isNotEmpty(electronicReferralForm.getDoctorEmail()) && !isDrJonesBu) {
			sendEmailToReferrer(electronicReferralForm);
		}
		
		if(StringUtils.isNotEmpty(electronicReferralForm.getPatientEmail()) && !isDrJonesBu) {
			sendEmailToPatient(electronicReferralForm);
		} else if(StringUtils.isNotEmpty(electronicReferralForm.getPatientPhone()) && validMobileNumber(electronicReferralForm.getPatientPhone())  && !isDrJonesBu){
			sendSmsToPatient(electronicReferralForm.getPatientPhone());	
		}
		
		return electronicReferralForm;
	}
	
	
	public void sendDailyEReferralToCrm() throws Exception {
		Date endTime = new Date();
		Date startTime = new Date(endTime.getTime() - (24 * 60 * 60 * 1000));
		SimpleDateFormat submittedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy");
		Map<String, File> fileMap = new HashMap<>(1);
		List<ElectronicReferralForm> ereferralListToSend = electronicReferralJPARepository.findBySubmittedTimeBetweenOrderByIdAsc(startTime, endTime);
		if(ereferralListToSend.size() > 0) {
			try {
				File eReferralsFile = createEReferralCsv(ereferralListToSend);		
				fileMap.put("electronic_referrals.csv", eReferralsFile);
				
				List<String> crmEmailList = new ArrayList<String>();
				if("prod".equals(ACTIVE_PROFILE)) {
					crmEmailList.add("All-CustomerRelationshipManagers@i-med.com.au");
					crmEmailList.add("Susie.Morgan@i-med.com.au");
					crmEmailList.add("Rebecca.Button@i-med.com.au");
					crmEmailList.add("Mark.Burgess@i-med.com.au");
					crmEmailList.add("Melanie.Buttsworth@i-med.com.au");
					crmEmailList.add("Dominique.Gauci@i-med.com.au");
				    crmEmailList.add("Sally.Douglas@i-med.com.au");
				} else {
					crmEmailList.add("Sakthiraj.Kanakarathinam@i-med.com.au");
					crmEmailList.add("Hidehiro.Uehara@i-med.com.au");
				}
				
				String formattedStartDate = submittedDateTimeFormat.format(startTime);
				String formattedEndDate = submittedDateTimeFormat.format(endTime);
				String subject = String.format("E-Referral audit report for %s to %s",formattedStartDate, formattedEndDate);
				
				String emailBody = String.format("Hello Team," + 
						"<br><br>" + 
						"Please find attached the E-Referral audit report for the period between %s to %s" + 
						"<br><br>" + 
						"Thanks.<br>",formattedStartDate, formattedEndDate);
				
				emailService.sendWithFileMap(((String[])crmEmailList.toArray()), 
						subject, emailBody, fileMap);

			} catch(Exception ex) {
				ex.printStackTrace();
			} finally {
				deleteTempFiles(fileMap);
			}
		} else {
			logger.info("There is no new electronic referrals to send to CRM.");
		}
	}
	
	
	private File createEReferralCsv(List<ElectronicReferralForm> ereferralListToSend) throws Exception {
	File tempFile = File.createTempFile("ereferral-", "-csv");
	SimpleDateFormat submittedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a z");
	
    PrintWriter printWriter = new PrintWriter(tempFile);    
    printWriter.println("id,patient_name,patient_gender,patient_dob,patient_phoneno,patient_email,patient_street,patient_suburb,patient_state,patient_postcode,patient_worker_compensation,exam_detail,patient_pregnant,iv_contrast_allergy,iv_renal_disease,iv_diabetes_metformin_treatment,referrer_name,referrer_email,referrer_ahpra,referrer_provider_no,referrer_requester_no,referrer_practice_name,referrer_contact_no,referrer_street,referrer_suburb,referrer_state,referrer_postcode,cc_referrer_name,cc_referrer_email,cc_referrer_provider_no,cc_referrer_requester_no,cc_referrer_practice_name,cc_referrer_street,cc_referrer_suburb,cc_referrer_state,cc_referrer_postcode,urgent,copy_to_me,submitted_time");    
    for(ElectronicReferralForm entity : ereferralListToSend) {
      printWriter.print(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
    		StringConversionUtil.nonQuote(String.valueOf(entity.getId())),
    		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientName())),
    		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientGender())),    		
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientDob())),      		
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientPhone())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientEmail())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientStreet())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientSuburb())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientState())),
    		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientPostcode())),      		      		
      		StringConversionUtil.nonQuote(printYesNoForBoolean(entity.isPatientCompensation())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getExamDetails())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getPatientPregnant())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getIvContrastAllergy())),      		
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getIvRenal())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getIvDiabetes())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorName())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorEmail())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorAhpra())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorProviderNumber())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorRequesterNumber())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorPracticeName())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorPhone())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorStreet())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorSuburb())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorState())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getDoctorPostcode())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorName())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorEmail())),      		
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorProviderNumber())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorRequesterNumber())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorPracticeName())),      		
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorStreet())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorSuburb())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorState())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(entity.getCcDoctorPostcode())),
      		StringConversionUtil.nonQuote(printYesNoForBoolean(entity.isUrgentResult())),
      		StringConversionUtil.nonQuote(printYesNoForBoolean(entity.isCopyToMe())),
      		StringConversionUtil.nonQuote(printEmptyStringIfEmptyOrNull(submittedDateTimeFormat.format(entity.getSubmittedTime()))
   		)));
    }
    printWriter.close();
    return tempFile;
	}
	
	private String printEmptyStringIfEmptyOrNull(String fieldValue) {
		return StringUtils.isEmpty(fieldValue)?"":fieldValue.toString();
	}
	
	private String printYesNoForBoolean(boolean fieldValue) {
		return fieldValue?"Yes":"No";
	}	

	private void sendEmailToCrm(ElectronicReferralForm electronicReferralForm, boolean isReferrerLogged) throws Exception {
		String subject = "I-MED Electronic referral submitted";
		if(electronicReferralForm.isUrgentResult()) {
			subject = subject + " - Urgent";
		}
		String emailBody = 
				"<b>Credential status:&nbsp;</b>" + (isReferrerLogged?"Pre-validated Referrer":"Referrer Validation <u>required</u>")
				+ "<br><br>A new "+ (electronicReferralForm.isUrgentResult()?"<font color=\"red\">Urgent </font>":"") +"E-referral has been received for:" + "<br><br>" + "<b>Patient name:&nbsp;</b>"
				+ (electronicReferralForm.getPatientName() != null ? electronicReferralForm.getPatientName().toUpperCase() : "")
				+ "<br><br>" + "<b>DOB:&nbsp;</b>"
				+ (electronicReferralForm.getPatientDob() != null ? electronicReferralForm.getPatientDob().toUpperCase() : "")
				+ "<br><br>" + "<b>Address:&nbsp;</b>" + electronicReferralForm.getPatientStreet().toUpperCase() + ", "
				+ electronicReferralForm.getPatientSuburb().toUpperCase() + ", " + electronicReferralForm.getPatientState().toUpperCase()+ ", "
				+ electronicReferralForm.getPatientPostcode().toUpperCase() + "<br><br>" + "<b>Patient Email:&nbsp;</b>"
				+ (electronicReferralForm.getPatientEmail() != null ? electronicReferralForm.getPatientEmail().toUpperCase() : "")
				+ "<br><br>" + "<b>Telephone:&nbsp;</b>"
				+ (electronicReferralForm.getPatientPhone() != null ? electronicReferralForm.getPatientPhone().toUpperCase() : "")
				+ "<br><br>" + "<b>Exam Details:&nbsp;</b>"
				+ (electronicReferralForm.getExamDetails() != null ? electronicReferralForm.getExamDetails().toUpperCase() : "")
				+ "<br><br>" + "<b>Clinical Details:&nbsp;</b>"
				+ (electronicReferralForm.getClinicalDetails() != null ? electronicReferralForm.getClinicalDetails().toUpperCase()
						: "")
				+ "<br><br>" + "<b>Referring Practitioner:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorName() != null ? electronicReferralForm.getDoctorName().toUpperCase() : "")
				+ "<br><br>" + "<b>Provider No:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorProviderNumber() != null
						? electronicReferralForm.getDoctorProviderNumber().toUpperCase()
						: "")
				+ "<br><br>" + "<b>Requester No:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorRequesterNumber() != null
						? electronicReferralForm.getDoctorRequesterNumber().toUpperCase()
						: "")
				+ "<br><br>" + "<b>Practice name:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorPracticeName() != null
						? electronicReferralForm.getDoctorPracticeName().toUpperCase()
						: "")
				+ "<br><br>" + "<b>CC Dr:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorName() != null ? electronicReferralForm.getCcDoctorName().toUpperCase() : "")
				+ "<br><br>" + "<b>CC Dr Provider No:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorProviderNumber() != null
						? electronicReferralForm.getCcDoctorProviderNumber().toUpperCase()
						: "")
				+ "<br><br>" + "<b>CC Dr Requester No:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorRequesterNumber() != null
						? electronicReferralForm.getCcDoctorRequesterNumber().toUpperCase()
						: "")
				+ "<br><br>" + "<b>CC Dr Practice name:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorPracticeName() != null
						? electronicReferralForm.getCcDoctorPracticeName().toUpperCase()
						: "")
				+ "<br><br><br>" +

				"Please refer the attached for more detail." + "<br><br><br><br>" +

				"This is an automatically generated email, please do not reply to this email";

		InputStreamSource electronicReferralStream = new ByteArrayResource(
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm, false, false, isReferrerLogged));

		List<String> toList = decideToEmailIds(electronicReferralForm.getPatientPostcode());
		emailService.sendWithStreamAsAttachment(toList, subject, emailBody, electronicReferralStream,
				"Electronicreferral.pdf");

		electronicReferralStream = null;

	}

	private void sendEmailToPatient(ElectronicReferralForm electronicReferralForm) throws MessagingException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
		String subject = "Imaging Services at I-MED Radiology";

		String emailBody = "Dear " + electronicReferralForm.getPatientName().toUpperCase()
				+ "<br><br>Following your recent telehealth consultation, we have received a request for imaging from your medical practitioner - "
				+ electronicReferralForm.getDoctorName().toUpperCase() +". A copy of the referral is attached.<br><br>"
				+ "We will call you in the next few days (during business hours) to arrange a suitable time and location for your radiology appointment.<br><br>"
				+ "We look forward to speaking with you.<br><br>"
				+ "Kind regards.<br><br><br><br>"
				+ "The team at I-MED Radiology Network.";

		List<String> toEmailId = new ArrayList<String>();
		if(ACTIVE_PROFILE.equals("test")) {
			toEmailId.add("Sakthiraj.Kanakarathinam@i-med.com.au");
			toEmailId.add("Martin.Cox@i-med.com.au");
			toEmailId.add("Hidehiro.Uehara@i-med.com.au");
		} else {
			toEmailId.add(electronicReferralForm.getPatientEmail());
		}
		
		InputStreamSource pdfStream = new ByteArrayResource(
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm, true, false, false));
		
		emailService.sendWithStreamAsAttachmentWithHeaderFooter(toEmailId, subject, emailBody, pdfStream,
				"Electronicreferral.pdf", "static/images/public/Request_for_Imaging.png", "static/images/public/ER_Footer.png");
		
		pdfStream=null;

	}
	
	
	private void sendEmailToReferrer(ElectronicReferralForm electronicReferralForm) throws MessagingException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
		String subject = "Request for imaging services at I-MED Radiology";

		SimpleDateFormat submittedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a z");
		
		String emailBody = "Dear Dr " + electronicReferralForm.getDoctorName().toUpperCase()
				+ "<br><br>Thank you for referral to I-MED Radiology."
				+ "<br><br>Please find attached a copy of the electronic referral you recently issued to " + electronicReferralForm.getPatientName().toUpperCase() + " on " + submittedDateTimeFormat.format(electronicReferralForm.getSubmittedTime()).toUpperCase() +"."
				+ "<br><br>We will call your patient in the next few days (during business hours) to arrange a suitable time and location for their radiology appointment."
				+ "<br><br>Kind regards.<br><br><br><br>"
				+ "The team at I-MED Radiology Network.";

		List<String> toEmailId = new ArrayList<String>();
		if(ACTIVE_PROFILE.equals("test")) {
			toEmailId.add("Sakthiraj.Kanakarathinam@i-med.com.au");
			toEmailId.add("Martin.Cox@i-med.com.au");
			toEmailId.add("Hidehiro.Uehara@i-med.com.au");			
		} else {
			toEmailId.add(electronicReferralForm.getDoctorEmail());
		}
		
		InputStreamSource pdfStream = new ByteArrayResource(
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm, false, true, false));
		
		emailService.sendWithStreamAsAttachmentWithHeaderFooter(toEmailId, subject, emailBody, pdfStream,
				"Electronicreferral.pdf", "static/images/public/Request_for_Imaging.png", "static/images/public/ER_Footer.png");
		
		pdfStream=null;

	}

	private List<String> decideToEmailIds(String postalCode) {
		String buName = "";
		
		List<CrmPostcodeEntity> postCodeResult = crmPostcodeJpaRepository.findByPostcode(postalCode); 
		if (postCodeResult.size()>0) {
			buName = postCodeResult.get(0).getBu();
		}

		switch (buName) {
		case "I-MED NSW/ACT":
			return nswToEmailIds;
		case "I-MED WA":
			return waToEmailIds;
		case "MIA Radiology VIC":
			return vicToEmailIds;
		case "I-MED QLD":
			return qldToEmailIds;
		case "Uniting Care QLD JV":
			return qldToEmailIds;
		case "Dr Jones SA/NT JV":
			return drJonesToEmailIds;
		case "I-MED OZ":
			return ozToEmailIds;
		default:
			return rilToEmailIds;
		}

	}
	
	
	private boolean isDrJonesBu(String postalCode) {
		String buName = "";
		List<CrmPostcodeEntity> postCodeResult = crmPostcodeJpaRepository.findByPostcode(postalCode);
		if (postCodeResult.size()>0) {
			buName = postCodeResult.get(0).getBu();
		}
		return buName.equals("Dr Jones SA/NT JV");
		
	}
	
	private boolean validMobileNumber(String telephone) {
		String mobileRegex = "^04[0-9]{8}$";	
		Pattern pattern = Pattern.compile(mobileRegex);	
		return pattern.matcher(telephone).matches();	
	}
	
	private void sendSmsToPatient(String mobileNumber) throws Exception {
		String[] receipeints = null;
		if(ACTIVE_PROFILE.equals("test")) {
			receipeints = new String [] {"0437118213", "0431155939", "0412225274"};
		} else {
			receipeints = new String [] {mobileNumber};
		}
		smsService.send(receipeints, "I-MED Radiology has received a request for imaging from your medical practitioner. We will be in contact shortly to arrange an appointment.");
	}
	
	private void deleteTempFiles(Map<String, File> fileMap) {
		if(fileMap != null) {
			for(String fname : fileMap.keySet()) {
				File file = fileMap.get(fname);
				if(file != null) {
					file.delete();
				}
			}
		}
	}

}
