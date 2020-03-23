package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.controller;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;

@RestController
@RequestMapping("/electronicreferralrest")
public class ElectronicReferralRestController {
	@PostMapping("/referral")
	public ResponseEntity<JSONObject> postEreferral(@RequestBody() ElectronicReferralForm referral, Authentication authentication) {
		JSONObject reps = new JSONObject();
		reps.put("msg", "success");
		return ResponseEntity.status(HttpStatus.OK).body(reps);
	}
}
