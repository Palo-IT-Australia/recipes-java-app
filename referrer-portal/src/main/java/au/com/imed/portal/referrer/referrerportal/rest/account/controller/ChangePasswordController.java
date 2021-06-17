package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${imed.api-v2.prefix}/account")
public class ChangePasswordController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private PortalAccountService portalAccountService;

    @PostMapping("/change_password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestHeader("Authorization") String token, @RequestBody AccountPassword accountPassword) throws Exception {
        logger.info("/portal/register" + accountPassword.toString());
        try {
            var user = AuthenticationUtil.getAuthenticatedUserName("");
            portalAccountService.updateReferrerPassword(user, accountPassword);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
