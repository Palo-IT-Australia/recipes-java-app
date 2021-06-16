package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.LdapAccountCheckerService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.provider.MedicareProviderCheckerService;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.UniquenessModel;
import au.com.imed.portal.referrer.referrerportal.rest.models.ErrorResponse;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/referreraccount")
public class ReferrerPortalAccountRestController {
    private Logger logger = LoggerFactory.getLogger(ReferrerPortalAccountRestController.class);

    @Autowired
    private ReferrerCreateAccountService accountService;

    @Autowired
    private ReferrerMailService emailService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private LdapAccountCheckerService accountChecker;

    @Autowired
    private MedicareProviderCheckerService providerCheckerService;

    @Value("${spring.profiles.active}")
    private String ACTIVE_PROFILE;

    @Value("${imed.email.reciever}")
    private String emailReceiver;

    @GetMapping("/isEmailAvailable")
    public ResponseEntity<UniquenessModel> isEmailAvailable(@RequestParam("email") String email) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailable(email));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isEmailAvailableForUser")
    public ResponseEntity<UniquenessModel> isEmailAvailable(@RequestParam("email") String email, @RequestParam("uid") String uid) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailableForUser(email, uid));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isEmailAvailablePortal")
    public ResponseEntity<UniquenessModel> isEmailAvailablePortal(@RequestParam("email") String email) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailablePortal(email));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isUidAvailable")
    public ResponseEntity<UniquenessModel> isUidAvailable(@RequestParam("uid") String uid) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isUserIdAvailable(uid));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isAhpraAvailable")
    public ResponseEntity<UniquenessModel> isAhpraAvailable(@RequestParam("ahpra") String ahpra) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isAhpraAvailable(ahpra));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isProviderNumberValid")
    public ResponseEntity<UniquenessModel> isProviderNumberValid(@RequestParam("provider") String provider) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(providerCheckerService.isProviderNumberValid(provider));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/recoverUID")
    public ResponseEntity<ErrorResponse> recoverUID(@RequestParam("email") String email, @RequestParam("ahpra") String ahpra) {
        logger.info("/recoverUID {}", email);
        try {
            if (StringUtil.isBlank(ahpra) || StringUtil.isBlank(email)) {
                return new ResponseEntity<>(new ErrorResponse("Invalid AHPRA number or email address."), HttpStatus.BAD_REQUEST);
            } else {
                List<LdapUserDetails> list = accountService.findReferrerAccountsByEmailAndAhpra(email, ahpra);
                if (list.size() != 1) {
                    logger.info("/recoverUID failed attempt. # of account " + list.size() + ", attempted with " + email);
                    emailService.emailFailedRetrieveAttempt(new String[]{emailReceiver}, email, ahpra);
                    return new ResponseEntity<>(getErrorMessageForRecoverUid(list.size()), HttpStatus.BAD_REQUEST);
                }

                LdapUserDetails referrer = list.get(0);
                logger.info("/retrieve successfully found UID : " + referrer.getUid());
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
