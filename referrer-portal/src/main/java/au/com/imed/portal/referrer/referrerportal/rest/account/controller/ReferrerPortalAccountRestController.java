package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ldap.LdapAccountCheckerService;
import au.com.imed.portal.referrer.referrerportal.provider.MedicareProviderCheckerService;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.UniquenessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/referreraccount")
public class ReferrerPortalAccountRestController {
    private Logger logger = LoggerFactory.getLogger(ReferrerPortalAccountRestController.class);

    @Autowired
    private LdapAccountCheckerService accountChecker;

    @Autowired
    private MedicareProviderCheckerService providerCheckerService;


    @GetMapping("/isEmailAvailable")
    public ResponseEntity<UniquenessModel> isEmailAvailable(@RequestParam("email") String email) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailable(email));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isEmailAvailableForUser")
    public ResponseEntity<UniquenessModel> isEmailAvailable(@RequestParam("email") String email, @RequestParam("uid") String uid) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailableForUser(email, uid));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isEmailAvailablePortal")
    public ResponseEntity<UniquenessModel> isEmailAvailablePortal(@RequestParam("email") String email) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isEmailAvailablePortal(email));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isUidAvailable")
    public ResponseEntity<UniquenessModel> isUidAvailable(@RequestParam("uid") String uid) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isUserIdAvailable(uid));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isAhpraAvailable")
    public ResponseEntity<UniquenessModel> isAhpraAvailable(@RequestParam("ahpra") String ahpra) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(accountChecker.isAhpraAvailable(ahpra));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }

    @GetMapping("/isProviderNumberValid")
    public ResponseEntity<UniquenessModel> isProviderNumberValid(@RequestParam("provider") String provider) {
        UniquenessModel um = new UniquenessModel();
        um.setAvailable(providerCheckerService.isProviderNumberValid(provider));
        return new ResponseEntity<UniquenessModel>(um, HttpStatus.OK);
    }
}
