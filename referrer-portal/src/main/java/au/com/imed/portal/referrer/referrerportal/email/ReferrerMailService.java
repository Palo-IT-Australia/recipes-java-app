package au.com.imed.portal.referrer.referrerportal.email;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.model.AddPractice;
import au.com.imed.portal.referrer.referrerportal.model.AutoValidationResult;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;

@Service
public class ReferrerMailService {
	private Logger logger = LoggerFactory.getLogger(ReferrerMailService.class);
	
	private static final String FROM_ADDRESS = "do_not_reply@i-med.com.au";
	public static final String SUPPORT_ADDRESS = "referrer@i-med.com.au";
	private static final ClassPathResource IMED_LOGO = new ClassPathResource("static/images/public/imed.jpg");
	private static final String LOGO_KEY = "imed.jpg";
	private static final String INLINE_LOGO = "<br/><img src=\"cid:imed.jpg\"></img><br/>";
  private final static String SUBJECT_APPROVED = "Welcome to the new I-MED Online 2.0!";
  private final static String TEMPLATE_APPROVED = "tempApproveEmail.html";
  private final static String TEMPLATE_MIDDLE_APPROVED = "tempApproveEmailMiddle.html";
  private final static String TEMPLATE_END_APPROVED = "tempApproveEmailEnd.html";
  private final static String NL = "\n";
	private static final ClassPathResource IMED_BANNER = new ClassPathResource("static/images/public/banner.jpg");
	private static final ClassPathResource IMED_REQUEST_FOR_IMG = new ClassPathResource("static/images/public/Request_for_Imaging.png");
	private static final String BANNER_KEY = "banner.jpg";
	private static final String INLINE_BANNER = "<br/><img src=\"cid:banner.jpg\"></img><br/><br/>";
	private static final String REQUEST_FOR_IMAGE_BANNER_KEY = "request_for_image_banner.png";
	private static final String INLINE_REQUEST_FOR_IMAGE_BANNER = "<br/><img src=\"cid:request_for_image_banner.png\"></img><br/><br/>";
	private static final ClassPathResource REQUEST_FOR_IMAGE_IMED_LOGO = new ClassPathResource("static/images/public/ER_Footer.png");

  
  private final static Map<String, String> IMG_CID_MAP_APPROVED = new HashMap<>();
  static {
    IMG_CID_MAP_APPROVED.put("banner", "static/images/public/banner.jpg");
    IMG_CID_MAP_APPROVED.put("apple", "static/images/public/apple.jpg");
    IMG_CID_MAP_APPROVED.put("google", "static/images/public/google.jpg");
    IMG_CID_MAP_APPROVED.put("sign", "static/images/public/sign.jpg");
  }
  
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
	
	public void sendMailWithCc(String [] tos, String [] ccs, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(tos);
		message.setCc(ccs);
		message.setFrom(FROM_ADDRESS);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}

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

	public void sendHtmlMail(final String[] toEmails, final String subject, final String content) throws Exception {
		this.sendHtmlMail(toEmails, subject, content, null);
	}
	
	// Send html with imed logo
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
	
	//Send html with imed logo header and footer
	public void sendImoHtmlMail(final String[] toEmails, final String [] ccEmails, final String subject, final String content) throws Exception 
	{
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");
		helper.addAttachment(LOGO_KEY, IMED_LOGO);
		helper.addAttachment(BANNER_KEY, IMED_BANNER);
		helper.setText(INLINE_BANNER + "<div style='font-family: Arial;'>" + content + "</div>" + INLINE_LOGO, true);
		helper.setTo(toEmails);
		helper.setCc(ccEmails);
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

			mailSender.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendWithFileMap(final String [] tos, final String subject, final String content, Map<String, File> fileMap) {
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");
      msg.setFrom("do_not_reply@i-med.com.au");

      msg.setSubject(subject, "UTF-8");
      msg.setText(content, "UTF-8");

      msg.setSentDate(new Date());
      for(String to : tos) {
      	msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
      }
      msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("Hidehiro.Uehara@i-med.com.au", false));
      
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(content);
      
      Multipart multipart = new MimeMultipart();
      multipart.addBodyPart(messageBodyPart);
      
      for(String fname : fileMap.keySet()) {
      	BodyPart fileBodyPart = new MimeBodyPart();   
      	fileBodyPart.setDataHandler(new DataHandler(new FileDataSource(fileMap.get(fname))));
      	fileBodyPart.setFileName(fname);
      	multipart.addBodyPart(fileBodyPart);
      }
      
      // Send the complete message parts
      msg.setContent(multipart);
      
      mailSender.send(msg);  
    }
    catch(Exception ex) {
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
	 * Send email with attachment from the given array of streams
	 *
	 * @param tos
	 * @param subject
	 * @param content
	 * @param dataSources
	 * @param attachmentFileName
	 * @throws MessagingException 
	 */
	public void sendWithStreamsAsAttachmentWithHeaderFooter(final List<String> tos, final String subject, final String content,
			List<InputStreamSource> dataSources, List<String> attachmentFileName, String headerImgLoc, String footerImgLoc) throws MessagingException {
		
			MimeMessage msg = mailSender.createMimeMessage();

			MimeMessageHelper emailMsgHelper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "utf-8");

			emailMsgHelper.addAttachment(LOGO_KEY, (new ClassPathResource(footerImgLoc)));
			emailMsgHelper.addAttachment(REQUEST_FOR_IMAGE_BANNER_KEY, (new ClassPathResource(headerImgLoc)));
			
			for (String to : tos) {
				emailMsgHelper.addTo(to);
			}
			
			emailMsgHelper.setFrom("do_not_reply@i-med.com.au");
			emailMsgHelper.setSubject(subject);
			emailMsgHelper.setText("<table width=\"600\"><tr><td>" + INLINE_REQUEST_FOR_IMAGE_BANNER + "</td></tr><tr><td><div style='font-family: Arial;'>" + content + "</div></td></tr><tr><td>" + INLINE_LOGO + "</td></tr></table>", true);
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
	public void sendWithStreamAsAttachmentWithHeaderFooter(final List<String> tos, final String subject, final String content,
			InputStreamSource dataSource, String attachmentFileName, String headerImgLoc, String footerImgLoc) throws MessagingException {
		List<InputStreamSource> tempDatasourceList = new ArrayList<InputStreamSource>();
		tempDatasourceList.add(dataSource);
		List<String> tempFileNameList = new ArrayList<String>();
		tempFileNameList.add(attachmentFileName);
		sendWithStreamsAsAttachmentWithHeaderFooter(tos, subject, content, tempDatasourceList, tempFileNameList, headerImgLoc, footerImgLoc);		
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
	
  public void sendReportHtml(final String [] toEmails, final String url) throws Exception 
  {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
    mimeMessage.setContent("Please click <a href=\"" + url + "\">here</a> to open report download page.<br/>This link is valid for 24 hours.", "text/html");
    helper.setTo(toEmails);  
    helper.setSubject("I-MED Radiology Network : Access to your report");
    helper.setFrom(FROM_ADDRESS);
    mailSender.send(mimeMessage);
  }

  public void sendHtmlMail(String to, String from, String subject, String htmlBody, Map<String, String> imgmap) {
    try
    {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
      
      // TEXT
      MimeMultipart multipart = new MimeMultipart("related");
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(htmlBody, "text/html; charset=UTF-8");
      multipart.addBodyPart(messageBodyPart);
      
      // IMGS
      if(imgmap != null) {
        for(String key : imgmap.keySet()) {
          MimeBodyPart imagePart = new MimeBodyPart();
          imagePart.setHeader("Content-ID", "<" + key + ">");
          imagePart.setDisposition(MimeBodyPart.INLINE);
          ClassPathResource cpr = new ClassPathResource(imgmap.get(key));
          imagePart.attachFile(cpr.getFile());
          multipart.addBodyPart(imagePart);
        }
      }
      
      mimeMessage.setContent(multipart);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setFrom(from);
      mailSender.send(mimeMessage);
      System.out.println("sendHtmlMail() sent email to " + to);
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
   
  private String readMailTemplate(final String fname) {
    String temp = "";
    try {
      ClassPathResource cpr = new ClassPathResource("static/files/" + fname);
      byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
      temp = new String(bdata, StandardCharsets.UTF_8);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("template length = " + temp.length());
    return temp;
  }

  public void emailNewUser(ExternalUser user) {
    emailSupportTeamNewUser(user);
  }

  public void emailPasswordChange(StageUser user) {
    try {
      sendMail(user.getEmail(), "I-MED Online - Change of your password notification", UserMessageUtil.getPasswordChangedBody(user.getUid()));
    }
    catch (MailSendException ex) {
      emailSupportTeamMailIssue(user.getUid());
    }
  }

  public void emailPasswordReset(StageUser user, String password) {
    try {
      sendMail(user.getEmail(), "I-MED Online - Password reset", UserMessageUtil.getResetPasswordBody(user,password));
    }
    catch (MailSendException ex) {
      emailSupportTeamMailIssue(user.getUid());
    }
  }

  public void emailDetailsChanged(StageUser user) {
    try {
      sendMail(user.getEmail(), "I-MED Online - Change of your details", UserMessageUtil.getDetailsChangedBody(user));
    }
    catch (MailSendException ex) {
      emailSupportTeamMailIssue(user.getUid());
    }
  }

  public void emailAccountApproved(StageUser user) {
    emailAccountApproved(user.getGivenName(), user.getSurname(), user.getUid(), user.getEmail());
  }
  
  public void emailAccountApproved(String firstName, String lastName, String uid, String email) {
    String template = this.readMailTemplate(TEMPLATE_APPROVED);
    String tempmiddle = this.readMailTemplate(TEMPLATE_MIDDLE_APPROVED);
    String tempend = this.readMailTemplate(TEMPLATE_END_APPROVED);
    final String htmlBody = template + " " + firstName + " " + lastName + tempmiddle + uid + tempend;
    logger.info("emailAccountApproved() body = " + htmlBody); 
    String em = email;
    if(em != null && em.length() > 3) {
      this.sendHtmlMail(em, FROM_ADDRESS, SUBJECT_APPROVED, htmlBody, IMG_CID_MAP_APPROVED);
    }
  }
  
  public void emailCrmNotify(StageUser user) {
    StringBuffer sb = new StringBuffer();
    sb.append("To whom it may concern, ");
    sb.append(NL);
    sb.append(NL);
    sb.append(NL);
    sb.append("User ID: ");
    sb.append(user.getUid());
    sb.append(NL);
    sb.append("AHPRA #: ");
    sb.append(user.getAhpra());
    sb.append(NL);
    sb.append("First Name: ");
    sb.append(user.getGivenName());
    sb.append(NL);
    sb.append("Last Name: ");
    sb.append(user.getSurname());
    sb.append(NL);
    sb.append("Mobile: ");
    sb.append(user.getMobile());
    sb.append(NL);
    sb.append("Email: ");
    sb.append(user.getEmail());
    sb.append(NL);
    sb.append(NL);
    sb.append("<Practices>");
    sb.append(NL);    
    for(ReferrerProviderEntity prov : user.getProviders()) {
      sb.append("Name: ");
      sb.append(prov.getPracticeName());
      sb.append(NL);
      sb.append("Address: ");
      sb.append(prov.getPracticeAddress());
      sb.append(NL);
      sb.append("Provider #: ");
      sb.append(prov.getProviderNumber());
      sb.append(NL);
      sb.append("Phone: ");
      sb.append(prov.getPracticePhone());
      sb.append(NL);
      sb.append("Fax: ");
      sb.append(prov.getPracticeFax());
      sb.append(NL);
      sb.append(NL);
      sb.append(NL);
    }
    sendMail("Christian.Galloway@i-med.com.au", "Referrer Account Creation IMO2.0 Regional Imaging", sb.toString());
  }

  public void emailAccountDeclined(StageUser user, String reason) {
    // Below action disabled on JE request - enable when advised
    /*try {
      sendMail(user.getEmail(), "I-MED Online - Account Application Declined",UserMessageUtil.getDeclinedBody(reason));
    }
    catch (MailSendException ex) {
      emailSupportTeamMailIssue(user.getUid());
    }*/
  }

  public void emailAccountMessage(String userId,String email, String message) {
    try {
      sendMail(email, "I-MED Online - Message about your account application",UserMessageUtil.getAccountMessage(message),SUPPORT_ADDRESS);
    }
    catch (MailSendException ex) {
      emailSupportTeamMailIssue(userId);
    }
  }

  private void emailSupportTeamMailIssue(String user) {
    sendMail(SUPPORT_ADDRESS, "User Account Issue",UserMessageUtil.getAccountMailIssueBody(user));
  }

  private void emailSupportTeamNewUser(ExternalUser user) {
    sendMail(SUPPORT_ADDRESS, "New User Created - " + user.getUserid(), UserMessageUtil.getNewAccountCreatedBody(user));
  }

  private static final String FMT_AUTOVALIDATION_REUSLT = "This New Referrer application has failed validation. Reason of failure : %s\n\n"; 
  private static final String FMT_PLEASE_APPROVE = "A new account application for user: %s has been created. Please browse to Portal User Approval to action this request.\n\n";
  public void emailAutoValidatedReferrerAccount(final String email, ExternalUser user, boolean isStaging, AutoValidationResult result) {
  	String hl = "";
  	String va = "";
  	String ap = "";
  	if(user.getPractices() != null && user.getPractices().size() > 1) {
  		hl = "Note: This referrer has more than one practice. The first provider number " + 
  				user.getPractices().get(0).getProviderNumber() + " has been assigned in the RISid attribute on the LDAP account. Please ensure the additional provider details are updated in PACS.\n\n";
  	}
  	if(isStaging && result != null) {
  		va = String.format(FMT_AUTOVALIDATION_REUSLT, result.getMsg());
  	}
  	if(isStaging) {
  		ap = String.format(FMT_PLEASE_APPROVE, user.getUserid());
  	}
    sendMail(email, "I-MED Online 2.0 Referrer Account Created " + (isStaging ? "for Approval" : "") + " - " + user.getUserid(), hl + va + ap + UserMessageUtil.getNewAccountCreatedBody(user));
  }

  public void emailNotifyNewReferrer(String [] tos, String [] ccs, ExternalUser user) {
  	sendMailWithCc(tos, ccs, "I-MED Online 2.0 Referrer Account Created - " + user.getUserid(), UserMessageUtil.buildReferrerAccountContent(user));
  }  

  public void emailSupportTeamRegistrationError(Exception e, String stage, ExternalUser user) {
    sendMail(UserMessageUtil.ADMIN_USER_EMAIL, "Account Registration Error", UserMessageUtil.getRegistrationErrorBody(e,stage,user));
  }

}