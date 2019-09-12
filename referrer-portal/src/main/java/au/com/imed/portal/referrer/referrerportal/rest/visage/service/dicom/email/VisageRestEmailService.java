package au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.email;

import java.util.Date;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.util.StringConversionUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.OrderDetails;

@Service
public class VisageRestEmailService {
	private static final String FROM_ADDRESS = "do_not_reply@i-med.com.au";

	private static final String SHARE_REPORT_SUBJECT = "I-MED Radiology Network : Share Report";
	private static final String SHARE_REPORT_HTML_BODY_FMT = "Please click <a href=\"%s\">here</a> to open shared report.";

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSender mailSender;

	public void sendShareReportHtmlEmail(final String to, final String url) throws Exception {
		sendHtmlMail(new String[] { to }, FROM_ADDRESS, SHARE_REPORT_SUBJECT,
				String.format(SHARE_REPORT_HTML_BODY_FMT, url));
	}

	private void sendHtmlMail(String[] tos, String from, String subject, String htmlBody) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

		// TEXT
		MimeMultipart multipart = new MimeMultipart("related");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlBody, "text/html; charset=UTF-8");
		multipart.addBodyPart(messageBodyPart);

		mimeMessage.setContent(multipart);
		helper.setTo(tos);
		helper.setSubject(subject);
		helper.setFrom(from);
		// helper.setBcc(new String [] {from, "values@i-med.com.au"});
		mailSender.send(mimeMessage);
	}

	// AUS Open special treatment
	private static final String[] VH_EMAILS = new String[] { "Cristina.Lopez@i-med.com.au", "Ian.Peacock@i-med.com.au",
			"Martin.Cox@i-med.com.au" };
	private static final String VH_SUBJECT = "Break Glass Notification for Victoria House";
	private static final String VH_FACILITY = "Victoria House Medical Imaging";
	private static final String NL = "<br/>";

	public void checkAndSendVictoriaHouseBreakGlassEmails(final String userName, final Map<String, String> paramMap,
			final OrderDetails order) {
		try {
			final String bg = paramMap.get("breakGlass");
			if ("true".equalsIgnoreCase(bg) && order != null && order.getFacility().contains(VH_FACILITY)) {
				StringBuffer sb = new StringBuffer();
				sb.append("A break glass event has occurred for Victoria House - details as follows:");
				sb.append(NL);
				sb.append(NL);
				sb.append("Date/Time : ");
				sb.append(StringConversionUtil.toAusDateTime(new Date()));
				sb.append(NL);
				sb.append("Referrer(login account id) : ");
				sb.append(userName);
				sb.append(NL);
				sb.append("Patient Name : ");
				sb.append(order.getPatient().getFullName());
				sb.append(NL);
				sb.append("Patient DOB : ");
				sb.append(StringConversionUtil.toAusDateQuick(order.getPatient().getDob()));
				sb.append(NL);
				sb.append("Order uri : " + paramMap.get("orderUri"));
				sb.append(NL);

				this.sendHtmlMail(VH_EMAILS, FROM_ADDRESS, VH_SUBJECT, sb.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
