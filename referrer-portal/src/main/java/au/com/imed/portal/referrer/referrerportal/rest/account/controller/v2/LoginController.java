package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountTokenResponse;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountUidPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${imed.api-v2.prefix}/portal/")
public class LoginController {

    @Autowired
    private GlobalAccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<AccountTokenResponse> login(@RequestBody AccountUidPassword user) {
        if (accountService.checkPasswordForReferrer(user.getUid(), user.getPassword())) {
            try {
                var token = AuthenticationUtil.createAccessToken(user.getUid(), accountService.getAccountGroups(user.getUid()));
                return ResponseEntity.ok(new AccountTokenResponse("Bearer", token));
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
