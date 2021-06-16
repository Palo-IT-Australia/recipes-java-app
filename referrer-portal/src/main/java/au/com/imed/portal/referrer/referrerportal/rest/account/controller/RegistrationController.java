package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    private Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private ReferrerCreateAccountService accountService;

    @PostMapping("/apply")
    public ResponseEntity<Map<String,String>> postApply(@RequestBody ExternalUser imedExternalUser) {
        logger.info("/registration/apply " + imedExternalUser.toString());
        try {
            var response = accountService.createAccount(imedExternalUser);
            if (response.containsKey(MODEL_KEY_SUCCESS_MSG)){
                return ResponseEntity.ok(response);
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            var response = new HashMap<String, String>();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
