package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${imed.api-v2.prefix}/password")
@Slf4j
public class PasswordController {

    @Autowired
    private PortalAccountService portalAccountService;

    @PostMapping("/change")
    public ResponseEntity<String> changePassword(@RequestBody AccountPassword accountPassword) throws Exception {
        log.info("/account/change_password" + accountPassword.toString());
        try {
            var user = AuthenticationUtil.getAuthenticatedUserName(null);
            portalAccountService.updateReferrerPassword(user, accountPassword);
            return ResponseEntity.ok("Successfully changed password");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
