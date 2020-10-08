package au.com.imed.portal.referrer.referrerportal.rest.cleanup.controller;

import java.util.Collections;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.portal.referrer.referrerportal.ldap.AccountDeactivationService;
import au.com.imed.portal.referrer.referrerportal.rest.cleanup.model.GlobalLdapAccount;
import au.com.imed.portal.referrer.referrerportal.rest.cleanup.model.RemoveList;

@RestController
@RequestMapping("/cleanuprest")
public class AccountCleanupRestController {
	private Logger logger = LoggerFactory.getLogger(AccountCleanupRestController.class);

	@Autowired
	private AccountDeactivationService deactivationService;
//	@Autowired
//	private AccountRemovalService removalService;

	@GetMapping("/find")
	public ResponseEntity<List<GlobalLdapAccount>> find(@RequestParam("word") String word) {
		List<GlobalLdapAccount> list;
		try {
			list = deactivationService.searchGlobalAccounts(word);
		} catch (Exception ex) {
			list = Collections.emptyList();
			ex.printStackTrace();
		}
		return ResponseEntity.ok(list);
	}

	@SuppressWarnings("unchecked")
	@PostMapping("/remove")
	public ResponseEntity<JSONObject> remove(@RequestBody() RemoveList removeList) {
		JSONObject resultPayload = new JSONObject();
		HttpStatus sts = HttpStatus.OK;
		try {
			deactivationService.deactivate(removeList.getList());
			resultPayload.put("msg", "Deactivated accounts. Please make sure to handle PACS and Visage account accordingly.");
		} catch (Exception ex) {
			sts = HttpStatus.BAD_REQUEST;
			resultPayload.put("msg", "Failed to deactivated accounts");			
			ex.printStackTrace();
		}
		return ResponseEntity.status(sts).body(resultPayload);
	}
}