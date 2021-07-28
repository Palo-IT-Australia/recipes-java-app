package au.com.imed.portal.referrer.referrerportal.jpa.clinic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.clinic.model.Appointment;

@Service
public class AppointmentService {
	private Logger logger = LoggerFactory.getLogger(AppointmentService.class);

	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;


	@Autowired
	ReferrerMailService referrerEmailService;

	@Value("${imed.email.test.receiver}")
	private String[] emailTestReceivers;

	@Autowired
	private au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository.ClinicContentRepository clinicRepository;

	private final static String EMAIL_SUBJECT = "Your I-MED Radiology Appointment Request";

	public void sendEmail(final Appointment appointment, final MultipartFile referral) throws Exception {
		String[] toEmails;
		if ("prod".equals(ACTIVE_PROFILE)) {
			ClinicContentEntity clinic = clinicRepository.findById(appointment.getClinicId()).orElseThrow();
			toEmails = clinic.getAppointmentEmails().split(",");
			logger.info("Appointment clinic emails = " + toEmails);
		} else {
			toEmails = emailTestReceivers;
		}

		referrerEmailService.sendHtmlMail(toEmails, EMAIL_SUBJECT, this.buildEmailContent(appointment), referral);
	}

	private String buildEmailContent(final Appointment appointment) {
		final String NL = "<br/>";
		StringBuffer sb = new StringBuffer(256);

		sb.append(getIntroText());

		sb.append(NL);
		sb.append("Clinic: ");
		sb.append(getNonNull(appointment.getClinic()));
		sb.append(NL);
		sb.append("Procedure: ");
		sb.append(getNonNull(appointment.getModality()));
		sb.append(NL);
		sb.append("Date: ");
		sb.append(getNonNull(appointment.getTime()));
		sb.append(NL);
		sb.append(NL);
		sb.append("Given Name: ");
		sb.append(getNonNull(appointment.getFirstName()));
		sb.append(NL);
		sb.append("Surname: ");
		sb.append(getNonNull(appointment.getLastName()));
		sb.append(NL);
		sb.append("Postcode: ");
		sb.append(getNonNull(appointment.getPostcode()));
		sb.append(NL);
		sb.append("Date of Birth: ");
		sb.append(getNonNull(appointment.getDob()));
		sb.append(NL);
		sb.append("Email: ");
		sb.append(getNonNull(appointment.getEmail()));
		sb.append(NL);
		sb.append("Medicare Number: ");
		sb.append(getNonNull(appointment.getMedicareNumber()));
		sb.append(NL);
		sb.append("Contact Number: ");
		sb.append(getNonNull(appointment.getPhone()));
		sb.append(NL);
		sb.append("Notes: ");
		sb.append(getNonNull(appointment.getNotes()));
		sb.append(NL);
		sb.append(NL);

		sb.append("For more information about your procedure please visit https://i-med.com.au/procedures");

		return sb.toString();
	}

	private String getIntroText() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hello and thank you for choosing I-MED Radioloty Network.<br/>")
				.append("Your appointment request has been received.<br/>")
				.append("A member of our team will be contacting your shortly to finalise an appointment time and provide any necessary instructions and preparations.<br/><br/>")
				.append("Your request details are:<br/><br/>");
		return sb.toString();
	}

	private String getNonNull(final String st) {
		return st == null ? "---Not Specified---" : st;
	}

	public boolean validAppointment(final Appointment appointment) {
		return isStringSet(appointment.getFirstName()) && isStringSet(appointment.getLastName())
				&& isStringSet(appointment.getPhone()) && isStringSet(appointment.getEmail())
				&& isStringSet(appointment.getDob()) && isStringSet(appointment.getClinic())
				&& appointment.getClinicId() > 0;
	}

	private boolean isStringSet(final String str) {
		return str != null && str.length() > 0;
	}
}
