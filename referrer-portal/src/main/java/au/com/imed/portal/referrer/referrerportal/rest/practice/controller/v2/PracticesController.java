package au.com.imed.portal.referrer.referrerportal.rest.practice.controller.v2;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AddPractice;
import au.com.imed.portal.referrer.referrerportal.rest.models.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${imed.api-v2.prefix}/practice")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class PracticesController {

    @Autowired
    private ReferrerMailService emailService;

    @Autowired
    private ReferrerAccountService referrerAccountService;

    @PostMapping("/add")
    public ResponseEntity<ErrorResponse> addPractice(@RequestBody AddPractice practice, final Authentication authentication) {
        log.info("/practice/add" + practice.toString());
        log.info(authentication.toString());
        try {
            var accountDetail = referrerAccountService.getReferrerAccountDetail((String) authentication.getPrincipal());
            emailService.sendAddPractice(practice, accountDetail);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}

