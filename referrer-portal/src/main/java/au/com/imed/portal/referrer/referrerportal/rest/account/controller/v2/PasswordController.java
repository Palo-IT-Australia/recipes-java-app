package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PasswordController {

    @Autowired
    private PortalAccountService portalAccountService;

    @PostMapping("/change_password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestHeader("Authorization") String token, @RequestBody AccountPassword accountPassword) throws Exception {
        log.info("/portal/register" + accountPassword.toString());
        try {
            var user = AuthenticationUtil.getAuthenticatedUserName("");
            portalAccountService.updateReferrerPassword(user, accountPassword);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
