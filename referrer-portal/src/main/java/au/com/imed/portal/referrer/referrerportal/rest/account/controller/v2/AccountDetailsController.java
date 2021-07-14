package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountDetail
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("${imed.api-v2.prefix}/account")
@PreAuthorize("isAuthenticated()")
public class AccountDetailsController {

    @Autowired
    private ReferrerAccountService accountService;

    @Autowired
    private GetReferrerService getReferrerService;

    @Autowired
    private PortalAccountService portalAccountService;

    private DetailModel getPopulatedDetailModel(final Authentication authentication) {
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/details")
    public ResponseEntity<Referrer> getReferrer(
            @RequestHeader(value = PortalConstant.HEADER_AUTHENTICATION, required = false) String authentication) {
        Map<String, String> internalParams = new HashMap<String, String>(1);
        String userName = AuthenticationUtil.getAuthenticatedUserName(authentication);
        internalParams.put(GetReferrerService.PARAM_CURRENT_USER_NAME, userName);
        ResponseEntity<Referrer> entity = getReferrerService.doRestGet(userName, internalParams, Referrer.class);
        if (HttpStatus.OK.equals(entity.getStatusCode())) {
            AccountDetail detail = portalAccountService.getReferrerAccountDetail(userName);
            if (detail != null) {
                Referrer ref = entity.getBody();
                ref.setEmail(detail.getEmail());
                ref.setName(detail.getName());
                ref.setMobile(detail.getMobile());
                log.info("/user overwriting with LDAP information");
                entity = new ResponseEntity<>(ref, HttpStatus.OK);
            }
        }
        return entity;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/details")
    public ResponseEntity<DetailModel> updateContactDetails(@RequestBody DetailModel detailModel, Authentication authentication) throws ResponseStatusException {
        final String uid = authentication.getName();
        try {
            if (uid == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to change details. User does not exist.");
            }
            if (!ModelUtil.sanitizeModel(detailModel, true)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to change details. Invalid character input found.");
            }
            Map<String, String> resultMap = accountService.updateReferrerAccountDetail(uid, detailModel);
            if (resultMap.containsKey(MODEL_KEY_SUCCESS_MSG)) {
                return new ResponseEntity<>(getPopulatedDetailModel(authentication), HttpStatus.OK);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, resultMap.get(MODEL_KEY_ERROR_MSG));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
