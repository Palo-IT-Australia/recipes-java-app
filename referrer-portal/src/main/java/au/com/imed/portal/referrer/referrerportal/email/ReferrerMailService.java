package au.com.imed.portal.referrer.referrerportal.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.model.AddPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;

@Service
public class ReferrerMailService {
	private static final String FROM_ADDRESS = "do_not_reply@i-med.com.au";
	public static final String SUPPORT_ADDRESS = "referrer@i-med.com.au";
	private static final ClassPathResource IMED_LOGO = new ClassPathResource("static/images/public/imed.jpg");
	private static final String LOGO_KEY = "imed.jpg";
	private static final String INLINE_LOGO = "<br/><img src=\"cid:imed.jpg\"></img><br/>";

	@Autowired
	@Qualifier("mailSender")
	private JavaMailSender mailSender;

	public void sendMail(String to, String subject, String body) {
		sendMail(to, subject, body, FROM_ADDRESS);
	}

	public void sendMail(String to, String subject, String body, String from) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}

//  public void sendText(final String [] toEmails, final String url) {
//    SimpleMailMessage email = new SimpleMailMessage();
//    email.setTo(toEmails);  
//    email.setSubject("I-MED Radiology Network : To confirm your account, please visit the following url and input passcode sent to your mobile.");
//    email.setFrom(FROM_ADDRESS);
//    email.setText(url);
//    mailSender.send(email);
//  }

	private static final String RESET_SUBJECT = "I-MED Radiology Network : Reset your password";
	private static final String RESET_CONTENT_FMT = "Hello,<br/><br/>As requested, your My I-MED account password is being reset. To complete the process, please click <a href=\"%s\">here</a> to open the password reset confirmation page and complete the process with your sms code.<br/>This link will expire in 24 hours.<br/><br/>Thank you<br/>I-MED Radiology";

	public void sendPasswordResetHtml(final String[] toEmails, final String url) throws Exception {
		sendHtmlMail(toEmails, RESET_SUBJECT, String.format(RESET_CONTENT_FMT, url));
	}

	private static final String SHARE_REPORT_SUBJECT = "I-MED Radiology Network : Results Share";
	private static final String SHARE_REPORT_HTML_BODY_FMT = "Hello,<br/><br/>I have chosen to share my I-MED Radiology results with you.<br/><br/>To view these, please click <a href=\"%s\">here</a>. <br/><br/>Regards<br/></br><i>I-MED Radiology strongly recommends that all results should be discussed with the referring practitioner.</i>";

	public void sendShareReportHtmlEmail(final String to, final String url) throws Exception {
		sendHtmlMail(new String[] { to }, SHARE_REPORT_SUBJECT, String.format(SHARE_REPORT_HTML_BODY_FMT, url));
	}

	private static final String APPLY_SUBJECT = "I-MED Radiology Network:  Final steps to completing your My I-MED account";
	private static final String APPLY_CONTENT_FMT = "Welcome and thank you for applying for your 'My I-MED' account.  Click <a href=\"%s\">here</a>  to access your account application. Please note you will need the passcode we sent you by SMS to complete your application.<br>This link will expire in three days.<br><br>Kind regards from the team at I-MED Radiology.";

	public void sendApplyHtml(final String[] toEmails, final String url) throws Exception {
		sendHtmlMail(toEmails, APPLY_SUBJECT, String.format(APPLY_CONTENT_FMT, url));
	}

	public void sendHtmlMail(final String[] toEmails, final String subject, final String content) throws Exception {
		this.sendHtmlMail(toEmails, subject, content, null);
	}

	// Send html with logo
	public void sendHtmlMail(final String[] toEmails, final String subject, final String content,
			final MultipartFile file) throws Exception {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				"utf-8");
		helper.addAttachment(LOGO_KEY, IMED_LOGO);
		if (file != null) {
			helper.addAttachment(file.getOriginalFilename(), new InputStreamSource() {
				@Override
				public InputStream getInputStream() throws IOException {
					return file.getInputStream();
				}
			});
		}
		helper.setText(content + INLINE_LOGO, true);
		helper.setTo(toEmails);
		helper.setSubject(subject);
		helper.setFrom(FROM_ADDRESS);
		mailSender.send(mimeMessage);
	}

	public void sendWithFiles(final String[] tos, final String subject, final String content, String[] files) {
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setFrom("do_not_reply@i-med.com.au");

			msg.setSubject(subject, "UTF-8");
			msg.setText(content, "UTF-8");

			msg.setSentDate(new Date());
			for (String to : tos) {
				msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
			}
			msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("Hidehiro.Uehara@i-med.com.au", false));

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(content);

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			for (String fname : files) {
				BodyPart fileBodyPart = new MimeBodyPart();
				fileBodyPart.setDataHandler(new DataHandler(new FileDataSource("/tmp/" + fname)));
				fileBodyPart.setFileName(fname);
				multipart.addBodyPart(fileBodyPart);
			}

			// Send the complete message parts
			msg.setContent(multipart);

			Transport.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Send email with attachment from the given array of streams
	 *
	 * @param tos
	 * @param subject
	 * @param content
	 * @param dataSources
	 * @param attachmentFileName
	 * @throws MessagingException 
	 */
	public void sendWithStreamsAsAttachment(final List<String> tos, final String subject, final String content,
			List<InputStreamSource> dataSources, List<String> attachmentFileName) throws MessagingException {
		
			MimeMessage msg = mailSender.createMimeMessage();

			MimeMessageHelper emailMsgHelper = new MimeMessageHelper(msg, true);
			
			for (String to : tos) {
				emailMsgHelper.addTo(to);
			}
			
			emailMsgHelper.setFrom("do_not_reply@i-med.com.au");
			emailMsgHelper.setBcc("Hidehiro.Uehara@i-med.com.au");
			emailMsgHelper.setSubject(subject);
			emailMsgHelper.setText(content, true);
			emailMsgHelper.setSentDate(new Date());
			
			// Attach given streams with given file name
			int i = 0;
			for (InputStreamSource attachmentEntry : dataSources) {
				emailMsgHelper.addAttachment(attachmentFileName.get(i), attachmentEntry);
				i++;
			}
			mailSender.send(msg);
	}
	
	/**
	 * Send email with attachment from the given stream
	 *
	 * @param tos
	 * @param subject
	 * @param content
	 * @param writers
	 * @param attachmentFileName
	 * @throws MessagingException 
	 */
	public void sendWithStreamAsAttachment(final List<String> tos, final String subject, final String content,
			InputStreamSource dataSource, String attachmentFileName) throws MessagingException {
		List<InputStreamSource> tempDatasourceList = new ArrayList<InputStreamSource>();
		tempDatasourceList.add(dataSource);
		List<String> tempFileNameList = new ArrayList<String>();
		tempFileNameList.add(attachmentFileName);
		sendWithStreamsAsAttachment(tos, subject, content, tempDatasourceList, tempFileNameList);		
	}

	public void sendAddPractice(final AddPractice practice, final DetailedLdapUserDetails detail) {
		final String NL = "\n";
    StringBuffer sb = new StringBuffer();
    sb.append("Referrer userid:");
    sb.append(detail.getUsername());
    sb.append(NL);
    sb.append("Referrer first name:");
    sb.append(detail.getGivenName());
    sb.append(NL);
    sb.append("Referrer last name:");
    sb.append(detail.getSn());
    sb.append(NL);
    sb.append("Referrer email:");
    sb.append(detail.getEmail());
    sb.append(NL);
    sb.append("Referrer mobile:");
    sb.append(detail.getMobile());
    sb.append(NL);
    sb.append(NL);

    sb.append("Practice provider number:");
    sb.append(practice.getProviderNumber());
    sb.append(NL);
    sb.append("Practice name:");
    sb.append(practice.getName());
    sb.append(NL);
    sb.append("Practice phone:");
    sb.append(practice.getPhone());
    sb.append(NL);
    sb.append("Practice fax:");
    sb.append(practice.getFax());
    sb.append(NL);
    sb.append("Practice street:");
    sb.append(practice.getStreet());
    sb.append(NL);
    sb.append("Practice suburb:");
    sb.append(practice.getSuburb());
    sb.append(NL);
    sb.append("Practice state:");
    sb.append(practice.getState());
    sb.append(NL);
    sb.append("Practice postcode:");
    sb.append(practice.getPostcode());
    sb.append(NL);
    
    sendMail("referrer@i-med.com.au", "I-MED Online : Referrer New Practice", sb.toString());   
	}
	
  public void emailSupportTeamNewUser(ExternalUser user) {
    sendMail(SUPPORT_ADDRESS, "New User Created - " + user.getUserid(), UserMessageUtil.getNewAccountCreatedBody(user));
  }
}