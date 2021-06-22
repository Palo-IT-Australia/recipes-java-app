package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;


import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.rest.models.ErrorResponse;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController("ReferrerPortalAccountRestControllerV2")
    @RequestMapping("${imed.api-v2.prefix}/referreraccount")
public class ReferrerPortalAccountRestController {

    @Autowired
    private ReferrerCreateAccountService accountService;

    @Autowired
    private ReferrerMailService emailService;

    @Autowired
    private AuditService auditService;

    @Value("${spring.profiles.active}")
    private String ACTIVE_PROFILE;

    @Value("${imed.email.reciever}")
    private String emailReceiver;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recoverUID")
    public ResponseEntity<ErrorResponse> recoverUID(@RequestParam("email") String email, @RequestParam("ahpra") String ahpra) {
        log.info("/recoverUID {}", email);
        try {
            if (StringUtil.isBlank(ahpra) || StringUtil.isBlank(email)) {
                return new ResponseEntity<>(new ErrorResponse("Invalid AHPRA number or email address."), HttpStatus.BAD_REQUEST);
            } else {
                List<LdapUserDetails> list = accountService.findReferrerAccountsByEmailAndAhpra(email, ahpra);
                if (list.size() != 1) {
                    log.info("/recoverUID failed attempt. # of account " + list.size() + ", attempted with " + email);
                    emailService.emailFailedRetrieveAttempt(new String[]{emailReceiver}, email, ahpra);
                    return new ResponseEntity<>(getErrorMessageForRecoverUid(list.size()), HttpStatus.BAD_REQUEST);
                }

                LdapUserDetails referrer = list.get(0);
                log.info("/retrieve successfully found UID : " + referrer.getUid());
                String to = "prod".equals(ACTIVE_PROFILE) ? referrer.getEmail() : emailReceiver;
                emailService.emailRetrieved(to, referrer);
                // Audit
                Map<String, String> params = new HashMap<>(2);
                params.put("email", referrer.getEmail());
                params.put("ahpra", referrer.getAhpra());
                auditService.doAudit("UserID", referrer.getUid(), params);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            return new ResponseEntity<>(new ErrorResponse("Unexpected exception occurred."), HttpStatus.BAD_REQUEST);
        }
    }

    private ErrorResponse getErrorMessageForRecoverUid(int size) {
        String reason = size > 1 ? "Multiple accounts are found" : "No account is found";
        String message = " with this AHPRA # and email address. Please contact our Service Desk on 1300 147 852 or email:  IT.Servicedesk@i-med.com.au for assistance.";
        return new ErrorResponse(reason + message);
    }
}
