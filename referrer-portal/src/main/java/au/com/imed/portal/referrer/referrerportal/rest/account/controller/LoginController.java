package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.LdapAccountCheckerService;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountTokenResponse;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountUidPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal")
public class LoginController {

    @Autowired
    private LdapAccountCheckerService accountChecker;

    @Autowired
    private GlobalAccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<AccountTokenResponse> login(@RequestBody AccountUidPassword user) {
        if (accountChecker.login(user.getUid(), user.getPassword())) {
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
