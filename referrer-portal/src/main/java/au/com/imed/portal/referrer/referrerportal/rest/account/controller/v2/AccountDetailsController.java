package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountDetailsResponse;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;


import java.util.Map;

@RestController
@RequestMapping("${imed.api-v2.prefix}/account")
@PreAuthorize("isAuthenticated()")
public class AccountDetailsController {

    @Autowired
    private ReferrerAccountService accountService;

    private DetailModel getPopulatedDetailModel(final Authentication authentication){
        DetailModel model = new DetailModel();
        if (authentication != null) {
            AccountDetail detail = accountService.getReferrerAccountDetail(authentication.getName());
            if (detail != null) {
                model.setEmail(detail.getEmail());
                model.setMobile(detail.getMobile());
                model.setDisplayName(detail.getName());
            }
        }
        return model;
    }

    private Referrer.Practice[] getListedPracticesModel(final Authentication authentication) {
        Referrer.Practice[] mockPractices;
        mockPractices = new Referrer.Practice[3];
        var mockPractice1 = new Referrer.Practice();
        mockPractice1.setPracticeName("Sydney Medical Center");
        var mockPractice2 = new Referrer.Practice();
        mockPractice2.setPracticeName("Prince Louie Private Hospital");
        var mockPractice3 = new Referrer.Practice();
        mockPractice3.setPracticeName("Barton Medical Center");
        mockPractices[0] = mockPractice1;
        mockPractices[1] = mockPractice2;
        mockPractices[2] = mockPractice3;

        var mockReferrer = new Referrer();
        mockReferrer.setPractices(mockPractices);
        return mockReferrer.getPractices();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/details")
    public ResponseEntity<AccountDetailsResponse> accountDetails(Authentication authentication) {
        try {
            var details = getPopulatedDetailModel(authentication);
            var practices = getListedPracticesModel(authentication);
            return ResponseEntity.ok(new AccountDetailsResponse(details.getEmail(), details.getMobile(), details.getDisplayName(), practices));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/details")
    public ResponseEntity<AccountDetailsResponse> info(@RequestBody DetailModel detailModel, Authentication authentication) {
        final String uid = authentication.getName();
        try {
            if (uid == null) {
                throw new Exception("Failed to change details. User does not exist.");
            }
            if (!ModelUtil.sanitizeModel(detailModel, true)) {
                throw new Exception("Failed to change details. Invalid character input found.");
            }
            Map<String, String> resultMap = accountService.updateReferrerAccountDetail(uid, detailModel);
            if (resultMap.containsKey(MODEL_KEY_SUCCESS_MSG)) {
                return new ResponseEntity(getPopulatedDetailModel(authentication), HttpStatus.OK);
            }
            throw new Exception(resultMap.get(MODEL_KEY_ERROR_MSG));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
