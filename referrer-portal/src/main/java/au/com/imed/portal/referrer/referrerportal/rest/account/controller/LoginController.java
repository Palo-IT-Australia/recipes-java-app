package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.LdapAccountCheckerService;
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
    public ResponseEntity<String> login(@RequestBody AccountUidPassword user) {

        if (accountChecker.login(user.getUid(), user.getPassword())) {
            try {
                return ResponseEntity.ok(AuthenticationUtil.createAccessToken(user.getUid(), accountService.getAccountGroups(user.getUid())));
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
