package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;

import java.io.FileNotFoundException;
import java.io.IOException;
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

	@Value("#{'${imed.electronic.referral.ril.email.id}'.split(',')}")
	List<String> rilToEmailIds;

	public ElectronicReferralForm save(ElectronicReferralForm electronicReferralForm) throws Exception {
		electronicReferralForm = electronicReferralJPARepository.save(electronicReferralForm);
		sendEmailToCrm(electronicReferralForm);
		return electronicReferralForm;
	}

	private void sendEmailToCrm(ElectronicReferralForm electronicReferralForm) throws Exception {
		String subject = "I-MED Electronic referral submitted";

		String emailBody = "A new E-referral has been received for:" + "<br><br>" + "<b>Patient name:&nbsp;</b>"
				+ (electronicReferralForm.getPatientName() != null ? electronicReferralForm.getPatientName() : "")
				+ "<br><br>" + "<b>DOB:&nbsp;</b>"
				+ (electronicReferralForm.getPatientDob() != null ? electronicReferralForm.getPatientDob() : "")
				+ "<br><br>" + "<b>Address:&nbsp;</b>" + electronicReferralForm.getPatientStreet() + ", "
				+ electronicReferralForm.getPatientSuburb() + ", " + electronicReferralForm.getPatientState()+ ", "
				+ electronicReferralForm.getPatientPostcode() + "<br><br>" + "<b>Patient Email:&nbsp;</b>"
				+ (electronicReferralForm.getPatientEmail() != null ? electronicReferralForm.getPatientEmail() : "")
				+ "<br><br>" + "<b>Telephone:&nbsp;</b>"
				+ (electronicReferralForm.getPatientPhone() != null ? electronicReferralForm.getPatientPhone() : "")
				+ "<br><br>" + "<b>Exam Details:&nbsp;</b>"
				+ (electronicReferralForm.getExamDetails() != null ? electronicReferralForm.getExamDetails() : "")
				+ "<br><br>" + "<b>Clinical Details:&nbsp;</b>"
				+ (electronicReferralForm.getClinicalDetails() != null ? electronicReferralForm.getClinicalDetails()
						: "")
				+ "<br><br>" + "<b>Referring Practitioner:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorName() != null ? electronicReferralForm.getDoctorName() : "")
				+ "<br><br>" + "<b>Provider No:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorProviderNumber() != null
						? electronicReferralForm.getDoctorProviderNumber()
						: "")
				+ "<br><br>" + "<b>Requester No:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorRequesterNumber() != null
						? electronicReferralForm.getDoctorRequesterNumber()
						: "")
				+ "<br><br>" + "<b>Practice name:&nbsp;</b>"
				+ (electronicReferralForm.getDoctorPracticeName() != null
						? electronicReferralForm.getDoctorPracticeName()
						: "")
				+ "<br><br>" + "<b>CC Dr:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorName() != null ? electronicReferralForm.getCcDoctorName() : "")
				+ "<br><br>" + "<b>CC Dr Provider No:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorProviderNumber() != null
						? electronicReferralForm.getCcDoctorProviderNumber()
						: "")
				+ "<br><br>" + "<b>CC Dr Requester No:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorRequesterNumber() != null
						? electronicReferralForm.getCcDoctorRequesterNumber()
						: "")
				+ "<br><br>" + "<b>CC Dr Practice name:&nbsp;</b>"
				+ (electronicReferralForm.getCcDoctorPracticeName() != null
						? electronicReferralForm.getCcDoctorPracticeName()
						: "")
				+ "<br><br><br>" +

				"Please refer the attached for more detail." + "<br><br><br><br>" +

				"This is an automatically generated email, please do not reply to this email";

		InputStreamSource electronicReferralStream = new ByteArrayResource(
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm, false));

		List<String> toList = decideToEmailIds(electronicReferralForm.getPatientPostcode());
		emailService.sendWithStreamAsAttachment(toList, subject, emailBody, electronicReferralStream,
				"Electronicreferral.pdf");

		electronicReferralStream = null;
		
		if(StringUtils.isNotEmpty(electronicReferralForm.getPatientEmail())) {
			sendEmailToPatient(electronicReferralForm);
		} else if(StringUtils.isNotEmpty(electronicReferralForm.getPatientPhone()) && validMobileNumber(electronicReferralForm.getPatientPhone())){
			sendSmsToPatient(electronicReferralForm.getPatientPhone());	
		}
	}

	private void sendEmailToPatient(ElectronicReferralForm electronicReferralForm) throws MessagingException, FileNotFoundException, IllegalArgumentException, IllegalAccessException, IOException {
		String subject = "Imaging Services at I-MED Radiology";

		String emailBody = "Dear " + electronicReferralForm.getPatientName().toUpperCase()
				+ "<br><br>Following your recent Telehealth consultation, we have received a request for imaging from your medical Practitioner - "
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
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm, true));
		
		emailService.sendWithStreamAsAttachmentWithHeaderFooter(toEmailId, subject, emailBody, pdfStream,
				"Electronicreferral.pdf");
		
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
		default:
			return rilToEmailIds;
		}

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
