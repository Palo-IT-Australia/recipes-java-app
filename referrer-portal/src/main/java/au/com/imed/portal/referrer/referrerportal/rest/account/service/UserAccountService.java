package au.com.imed.portal.referrer.referrerportal.rest.account.service;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.EmailException;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.SmsException;
import au.com.imed.portal.referrer.referrerportal.service.ConfirmProcessDataService;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;
import au.com.imed.portal.referrer.referrerportal.utils.SmsPasscodeHashUtil;
import au.com.imed.portal.referrer.referrerportal.utils.UrlCodeAes128Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j
@Service
public class UserAccountService {

    private static final String SMS_BODY = "I-MED Radiology Network : Your account reset passcode is ";
    @Autowired
    private ReferrerMailService emailService;
    @Autowired
    private GoFaxSmsService smsService;
    @Autowired
    private ConfirmProcessDataService confirmProcessDataService;
    @Value("${imed.application.url}")
    private String applicationUrl;

    public void confirmPasswordReset(AccountDetail userDetails) {
        if (validateUserDetails(userDetails)) {
            final var passcode = SmsPasscodeHashUtil.randomString(8);
            final String confirmParam = getConfirmParam(confirmProcessDataService.savePasswordReset(userDetails.getUid(), passcode));

            sendEmail(userDetails.getEmail(), confirmParam);
            sendSms(userDetails.getMobile(), passcode);
        } else {
            throw new EmailException("Invalid user details");
        }
    }

    private boolean validateUserDetails(AccountDetail userDetails) {
        return userDetails != null &&
                StringUtils.isNotBlank(userDetails.getEmail()) &&
                StringUtils.isNotEmpty(userDetails.getMobile());
    }

    private void sendEmail(String email, String confirmParam) {
        if (confirmParam != null) {
            try {
                emailService.sendPasswordResetHtml(new String[]{email}, applicationUrl + "/resetconfirm?secret=" + confirmParam);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new EmailException("Error : Failed to send email.");
            }
        } else {
            throw new EmailException("Error : We don't have your email address.");
        }
    }

    private void sendSms(String mobile, String passcode) {
        if (mobile.startsWith("04")) {
            try {
                smsService.send(new String[]{mobile}, getSmsMessage(passcode));
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new SmsException("Error : Failed to send SMS.");
            }
        } else {
            throw new SmsException("Error : Your registered mobile number is invalid in Australia. Please update on My Account page.");
        }
    }

    private String getSmsMessage(String passcode) {
        return SMS_BODY + passcode;
    }

    private String getConfirmParam(ReferrerPasswordResetEntity resetPassword) {
        if (resetPassword != null) {
            return URLEncoder.encode(UrlCodeAes128Util.encrypt(resetPassword.getUrlCode()), StandardCharsets.UTF_8);
        } else return null;
    }
}
