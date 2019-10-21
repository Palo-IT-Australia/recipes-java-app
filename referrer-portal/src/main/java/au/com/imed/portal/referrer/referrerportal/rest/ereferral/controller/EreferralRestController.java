package au.com.imed.portal.referrer.referrerportal.rest.ereferral.controller;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.ereferral.model.EreferralEreferral;
import au.com.imed.portal.referrer.referrerportal.rest.ereferral.model.EreferralReauth;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v231.message.ADT_A01;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import ca.uhn.hl7v2.parser.Parser;

@RestController
@RequestMapping("/ereferralrest")
public class EreferralRestController {
	@Autowired
	private ReferrerAccountService referrerAccountService;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/reauth")
	public ResponseEntity<JSONObject> postReauth(@RequestBody() EreferralReauth reauth, Authentication authentication) {
		boolean authed = referrerAccountService.checkPassword(authentication.getName(), reauth.getPassword())
			|| new ImedActiveDirectoryLdapManager().checkPassword(authentication.getName(), reauth.getPassword());
		JSONObject reps = new JSONObject();
		reps.put("msg", authed ? "success" : "failure");
		return ResponseEntity.status(authed ? HttpStatus.OK : HttpStatus.UNAUTHORIZED).body(reps);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/referral")
	public ResponseEntity<JSONObject> postEreferral(@RequestBody() EreferralEreferral referral, Authentication authentication) {
		JSONObject reps = new JSONObject();
		if(authentication != null && authentication.getName() != null) {
			// TODO HL7 or API
			reps.put("msg", "success");
			try {
				reps.put("hlseven", processReferral(referral));
				return ResponseEntity.status(HttpStatus.OK).body(reps);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(reps);
	}

	private String processReferral(final EreferralEreferral referral) throws Exception {
		ADT_A01 adt = new ADT_A01();
		adt.initQuickstart("ADT", "A01", "P");

		// Populate the MSH Segment
		MSH mshSegment = adt.getMSH();
		mshSegment.getSendingApplication().getNamespaceID().setValue("IMOL");

		// Populate the PID Segment
		PID pid = adt.getPID(); 
		pid.getPatientName(0).getFamilyLastName().getFamilyName().setValue(referral.getPatientLastName());
		pid.getPatientName(0).getGivenName().setValue(referral.getPatientFirstName());
		pid.getPatientIdentifierList(0).getID().setValue("77.123456");
		pid.getDateTimeOfBirth().getTimeOfAnEvent().setValue(referral.getPatientDob());

		HapiContext context = new DefaultHapiContext();
		Parser parser = context.getPipeParser();
		String encodedMessage = parser.encode(adt);
		System.out.println("HL7 Encoded Message:");
		System.out.println(encodedMessage);
		return encodedMessage;
	}
}
