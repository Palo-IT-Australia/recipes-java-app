package au.com.imed.portal.referrer.referrerportal.rest.forms.controller;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.rest.forms.model.InstallSupport;
import au.com.imed.portal.referrer.referrerportal.rest.forms.service.InstallSupportFormService;

@RestController
@RequestMapping("/formrest")
public class FormsRestController {
	private Logger logger = LoggerFactory.getLogger(FormsRestController.class);
	
	@Autowired
	private InstallSupportFormService installSupportFormService;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/installsupport")
	public ResponseEntity<JSONObject> postInstallSupport(@RequestBody InstallSupport reqobj) {
		logger.info("request: " + reqobj);
		installSupportFormService.processRequest(reqobj);
		JSONObject resp = new JSONObject();
		resp.put("msg", "Form has been submitted successfully");
		return ResponseEntity.ok().body(resp);
	}
}
