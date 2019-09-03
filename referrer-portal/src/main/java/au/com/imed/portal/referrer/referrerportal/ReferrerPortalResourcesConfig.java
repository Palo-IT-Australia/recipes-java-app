package au.com.imed.portal.referrer.referrerportal;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class ReferrerPortalResourcesConfig {
	@Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost("smtp.i-med.com.au");
    javaMailSender.setPort(25);
    javaMailSender.setJavaMailProperties(getMailProperties());
    return javaMailSender;
  }

  private Properties getMailProperties() {
    Properties properties = new Properties();
    properties.setProperty("mail.transport.protocol", "smtp");
    properties.setProperty("mail.smtp.auth", "false");
    properties.setProperty("mail.smtp.starttls.enable", "false");
    properties.setProperty("mail.debug", "true");
    return properties;
  }
}
