package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.dcm4che3.net.InputStreamDataWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.PdfGenerator;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository.ElectronicReferralJPARepository;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;

@Service
public class ElectronicReferralService {

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

	@Value("#{'${imed.electronic.referral.ril.email.id}'.split(',')}")
	List<String> rilToEmailIds;

	public ElectronicReferralForm save(ElectronicReferralForm electronicReferralForm, boolean isReferrerLogged) throws Exception {
		electronicReferralForm = electronicReferralJPARepository.save(electronicReferralForm);
		
		sendEmailToCrm(electronicReferralForm, isReferrerLogged);
		
		
		if(electronicReferralForm.isCopyToMe() && StringUtils.isNotEmpty(electronicReferralForm.getDoctorEmail())) {
			sendEmailToReferrer(electronicReferralForm);
		}
		
		if(StringUtils.isNotEmpty(electronicReferralForm.getPatientEmail())) {
			sendEmailToPatient(electronicReferralForm);
		} else if(StringUtils.isNotEmpty(electronicReferralForm.getPatientPhone()) && validMobileNumber(electronicReferralForm.getPatientPhone())){
			sendSmsToPatient(electronicReferralForm.getPatientPhone());	
		}
		
		return electronicReferralForm;
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
		if(electronicReferralForm.isUrgentResult()) {
			subject = subject + " - Urgent";
		}

		SimpleDateFormat submittedDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a z");
		
		String emailBody = "Dear Dr " + electronicReferralForm.getDoctorName().toUpperCase()
				+ "<br><br>Thank you for "+ (electronicReferralForm.isUrgentResult()?"<font color=\"red\">Urgent </font>":"") + "referral to I-MED Radiology."
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
		buName = crmPostcodeJpaRepository.findByPostcode(postalCode).get(0).getBu();

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
		default:
			return rilToEmailIds;
		}

	}
	
	
	private boolean isDrJonesBu(String postalCode) {
		String buName = "";
		buName = crmPostcodeJpaRepository.findByPostcode(postalCode).get(0).getBu();
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

}
