package au.com.imed.portal.referrer.referrerportal.rest.ereferral.controller;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@SuppressWarnings("unchecked")
	@PostMapping("/reauth")
	public ResponseEntity<JSONObject> postReauth(@RequestBody() EreferralReauth reauth) {
		// TODO check pswd by ldaptemp
		JSONObject reps = new JSONObject();
		reps.put("msg", "success");
		return ResponseEntity.status(HttpStatus.OK).body(reps);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/referral")
	public ResponseEntity<JSONObject> postEreferral(@RequestBody() EreferralEreferral referral) {
		// TODO HL7 or API
		JSONObject reps = new JSONObject();
		reps.put("msg", "success");
		try {
			reps.put("hlseven", processReferral(referral));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body(reps);
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
