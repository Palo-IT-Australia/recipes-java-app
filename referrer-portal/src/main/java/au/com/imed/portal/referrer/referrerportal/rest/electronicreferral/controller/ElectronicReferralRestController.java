package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;

@RestController
@RequestMapping("/electronicreferralrest")
public class ElectronicReferralRestController {

	private Logger logger = LoggerFactory.getLogger(ElectronicReferralRestController.class);

	@Autowired
	private ElectronicReferralService electronicReferralService;
	
	@Autowired
	private AuditService auditService;

	@PostMapping("/referral")
	public ResponseEntity<JSONObject> postEreferral(@RequestBody() ElectronicReferralForm referral,
			Authentication authentication) {
		logger.info("Received referral request : " + referral.toString());
		JSONObject resp = new JSONObject();
		
//		if(authentication!=null && StringUtils.isNotEmpty(authentication.getName()) && authentication.getName().equals("alchau")) {
		
		try {
			boolean isReferrerLoggedIn = authentication!=null && StringUtils.isNotEmpty(authentication.getName());
			if(StringUtils.isEmpty(referral.getDoctorAhpra()) && !isReferrerLoggedIn) {
				resp.put("msg", "AHPRA number is missing for non logged in user");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
			} else {
				electronicReferralService.save(referral, isReferrerLoggedIn);
				resp.put("msg", "Saved successfully");
				resp.put("referralId", referral.getId());
				doAudit((authentication==null || StringUtils.isEmpty(authentication.getName()))?"":authentication.getName(), referral);
				return ResponseEntity.status(HttpStatus.OK).body(resp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.put("msg", "There was a problem occured");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
		}
		
//		} else {
//			resp.put("msg", "The user is not allowed to send electronic referral");
//			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(resp);
//		}

//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
	}
	
	private void doAudit(String username, ElectronicReferralForm referral) {
		try {
			Map<String, String> params = new HashMap<>();
			params.put("id", "" + referral.getId());
			params.put("patientName", referral.getPatientName());
			params.put("patientDob", referral.getPatientDob());
			auditService.doAudit("ElectronicReferral", username, params);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
