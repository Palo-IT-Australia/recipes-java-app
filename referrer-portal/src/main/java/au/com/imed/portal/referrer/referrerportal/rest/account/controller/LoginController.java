package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.LdapAccountCheckerService;
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

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam("username") String username, @RequestParam("password") String password) {

        if (accountChecker.login(username, password)) {
            try {
                return ResponseEntity.ok(AuthenticationUtil.createAccessToken(username, accountService.getAccountGroups(username)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } else return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
