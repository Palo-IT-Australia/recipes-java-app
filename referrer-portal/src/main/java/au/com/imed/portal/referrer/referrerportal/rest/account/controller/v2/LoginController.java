package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountTokenResponse;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountUidPassword;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;

@RestController
@Slf4j
@RequestMapping("${imed.api-v2.prefix}/portal/")
public class LoginController {

    @Autowired
    private GlobalAccountService accountService;

    @Autowired
    private ReferrerCreateAccountService referrerAccountService;

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


    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody ExternalUser imedExternalUser) {
        log.info("/portal/register" + imedExternalUser.toString());
        try {
            var response = referrerAccountService.createAccount(imedExternalUser);
            if (response.containsKey(MODEL_KEY_SUCCESS_MSG)){
                return ResponseEntity.ok(response);
            }
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
