package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.controller;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;
import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.service.ElectronicReferralService;

@RestController
@RequestMapping("/electronicreferralrest")
public class ElectronicReferralRestController {

	private Logger logger = LoggerFactory.getLogger(ElectronicReferralRestController.class);

	@Autowired
	ElectronicReferralService electronicReferralService;

	@PostMapping("/referral")
	public ResponseEntity<JSONObject> postEreferral(@RequestBody() ElectronicReferralForm referral,
			Authentication authentication) {
		logger.info("Received referral request : " + referral.toString());
		JSONObject resp = new JSONObject();
		try {
			electronicReferralService.save(referral);
			resp.put("msg", "Saved successfully");
			resp.put("referralId", referral.getId());
			return ResponseEntity.status(HttpStatus.OK).body(resp);
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("msg", "There was a problem occured");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
		}

//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
	}
}
