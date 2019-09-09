package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.model.StagingUserList;

@RestController
@RequestMapping("/adminrest/approver")
public class AdminAccountApproverRestController {
	@Autowired
	private ReferrerAccountService referrerAccountService;
	
	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;
	
	@GetMapping("/getStageUsers")
	public ResponseEntity<StagingUserList> getStagingUserList() {
		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), HttpStatus.OK);
	}
	
	private StagingUserList getCurrentStagingUserList() {
		StagingUserList sul = new StagingUserList();
		// Stage with providers
		List<StageUser> stagings = referrerAccountService.getStageUserList();
		putProviders(stagings);
		sul.setStagings(stagings);
		// Finalizing with providers
		List<StageUser> finalisings = referrerAccountService.getFinalisingUserList();
		putProviders(finalisings);
		sul.setFinalisings(finalisings);
		return sul;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/checkNewuid")
	public ResponseEntity<JSONObject> checkNiewUid(@RequestParam("newuid") String uid) {
		JSONObject resultPayload = new JSONObject();
		// Check referrer only as PACS ones can be as existing user type and Stage is current one
		if(referrerAccountService.GetReferrerDnListByAttr("uid", uid).size() > 0 ||
				new ImedActiveDirectoryLdapManager().findByUid(uid).size() > 0) 
		{		
			resultPayload.put("msg", "This User ID is Unavailable");
			resultPayload.put("error", true);
		}
		return ResponseEntity.status(200).body(resultPayload);
	}
	
  private void putProviders(List<StageUser> list) {
    int size = list.size();
    for(int i = 0;  i < size; i++) {
      List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByUsername(list.get(i).getUid());
      list.get(i).setProviders(providers);
    }
  }
  
  @PostMapping("/approveUser")
  public ResponseEntity<StagingUserList> postApproveUser() {
		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), HttpStatus.OK);
  }
  
  @PostMapping("/finaliseUser")
  public ResponseEntity<StagingUserList> postFinaliseUser() {
		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), HttpStatus.OK);
  }
  
  @PostMapping("/declineUser")
  public ResponseEntity<StagingUserList> postDeclineUser() {
		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), HttpStatus.OK);
  }
  
  @PostMapping("/sendMessage")
  public ResponseEntity<String> postSentMessage() {
		return new ResponseEntity<String>("Not Yet Implemented", HttpStatus.OK);
  }
  
  private void switchProviderUid(final String uid, final String newuid) {
    if(uid != null && newuid != null && newuid.length() > 4 && !uid.equalsIgnoreCase(newuid)) {
      List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByUsername(uid);
      for(ReferrerProviderEntity entity : providers) {
        entity.setUsername(newuid);
        referrerProviderJpaRepository.saveAndFlush(entity);
      }
    }
  }
}
