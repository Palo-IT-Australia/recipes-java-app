package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

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

	public ElectronicReferralForm save(ElectronicReferralForm electronicReferralForm) throws FileNotFoundException,
			IOException, MessagingException, IllegalArgumentException, IllegalAccessException {
		electronicReferralForm = electronicReferralJPARepository.save(electronicReferralForm);
		sendEmailToCrm(electronicReferralForm);
		return electronicReferralForm;
	}

	private void sendEmailToCrm(ElectronicReferralForm electronicReferralForm) throws MessagingException,
			FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException {
		String subject = "I-MED Electronic referral submitted";
		
		String emailBody = "A new E-referral has been received for:" + "<br><br>" + "<b>Patient name:</b>"
				+ (electronicReferralForm.getPatientName() != null ? electronicReferralForm.getPatientName() : "")
				+ "<br><br>" + "<b>DOB:</b>"
				+ (electronicReferralForm.getPatientDob() != null ? electronicReferralForm.getPatientDob() : "")
				+ "<br><br>" + "<b>Address:</b>" + electronicReferralForm.getPatientStreet() + ", "
				+ electronicReferralForm.getPatientSuburb() + ", " + electronicReferralForm.getPatientState()
				+ electronicReferralForm.getPatientPostcode() + "<br><br>" + "<b>Patient Email:</b>"
				+ (electronicReferralForm.getPatientEmail() != null ? electronicReferralForm.getPatientEmail() : "")
				+ "<br><br>" + "<b>Telephone:</b>"
				+ (electronicReferralForm.getPatientPhone() != null ? electronicReferralForm.getPatientPhone() : "")
				+ "<br><br>" + "<b>Exam Details:</b>"
				+ (electronicReferralForm.getExamDetails() != null ? electronicReferralForm.getExamDetails() : "")
				+ "<br><br>" + "<b>Clinical Details:</b>"
				+ (electronicReferralForm.getClinicalDetails() != null ? electronicReferralForm.getClinicalDetails()
						: "")
				+ "<br><br>" + "<b>Referring Dr:<b>"
				+ (electronicReferralForm.getDoctorName() != null ? electronicReferralForm.getDoctorName() : "")
				+ "<br><br>" + "<b>Provider No:</b>"
				+ (electronicReferralForm.getDoctorProviderNumber() != null
						? electronicReferralForm.getDoctorProviderNumber()
						: "")
				+ "<br><br>" + "<b>CC Dr:</b>"
				+ (electronicReferralForm.getCcDoctorName() != null ? electronicReferralForm.getCcDoctorName() : "")
				+ "<br><br>" + "<b>CC Dr Provider No:</b>"
				+ (electronicReferralForm.getCcDoctorProviderNumber() != null
						? electronicReferralForm.getCcDoctorProviderNumber()
						: "")
				+ "<br><br><br>" +

				"Please refer the attached for more detail." + "<br><br><br><br>" +

				"This is an automatically generated email, please do not reply to this email";

		InputStreamSource smsPromotionReportInInputStream = new ByteArrayResource(
				pdfReferralGenerator.generatePdfReferral(electronicReferralForm));

		List<String> toList = decideToEmailIds(electronicReferralForm.getPatientPostcode());
		emailService.sendWithStreamAsAttachment(toList, subject, emailBody, smsPromotionReportInInputStream,
				"Electronicreferral.pdf");
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

}
