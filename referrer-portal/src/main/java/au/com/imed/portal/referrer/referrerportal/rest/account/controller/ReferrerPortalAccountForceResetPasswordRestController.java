package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.common.util.ForceResetPasswordAes128Util;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ForceResetPassword;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;

@RestController
@RequestMapping("/referreraccount/forceresetpassword")
public class ReferrerPortalAccountForceResetPasswordRestController {
	private Logger logger = LoggerFactory.getLogger(ReferrerPortalAccountForceResetPasswordRestController.class);

	@Autowired
	private ReferrerAccountService accountService;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/updateresetcrm")
	public ResponseEntity<JSONObject> postUpdateResetCrm(@RequestBody JSONObject obj) {
		ResponseEntity<JSONObject> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		try {
			String secret = (String) obj.get("secret");
			String mobile = (String) obj.get("mobile");
			String newPassword = (String ) obj.get("newPassword");
			if(!StringUtil.isBlank(secret) && !StringUtil.isBlank(mobile) && !StringUtil.isBlank(newPassword)) {
				ForceResetPassword frp = ForceResetPasswordAes128Util.getObjectFromSecretParameterValue(secret);
				List<LdapUserDetails> acnts = accountService.findReferrerAccountsByUid(frp.getUid());
				if(acnts.size() > 0) {
					LdapUserDetails referrer = acnts.get(0); // should be only one
					if(mobile.equals(referrer.getMobile())) {
						accountService.resetReferrerPassword(frp.getUid(), newPassword);
						JSONObject respo = new JSONObject();
						respo.put("uid", frp.getUid());
						entity = ResponseEntity.ok(respo);
					} else {
						logger.info("Mobile # mismatch");
					}
				} else {
					logger.info("Uid {} not referrer account." + frp.getUid());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return entity;
	}
}
