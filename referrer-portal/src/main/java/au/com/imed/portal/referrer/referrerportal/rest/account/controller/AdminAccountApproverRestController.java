package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.Date;
import java.util.List;

import org.jose4j.json.internal.json_simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerActivationJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.model.StagingUserList;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountApproving;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountDeclining;

@RestController
@RequestMapping("/adminrest/approver")
public class AdminAccountApproverRestController {
	private Logger logger = LoggerFactory.getLogger(AdminAccountApproverRestController.class);
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Autowired
	private ReferrerAccountService referrerAccountService;
	
	@Autowired
	private ReferrerMailService emailService;
	
	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;
	
	@Autowired
	private ReferrerActivationJpaRepository referrerActivationEntityJapRepository;
	
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
		if(uid != null) {
			// Check referrer only as PACS ones can be as existing user type and Stage is current one
			if(referrerAccountService.GetReferrerDnListByAttr("uid", uid).size() > 0 ||
					new ImedActiveDirectoryLdapManager().findByUid(uid).size() > 0) 
			{		
				resultPayload.put("msg", "This User ID is Unavailable");
				resultPayload.put("error", true);
			}
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
  public ResponseEntity<StagingUserList> postApproveUser(@RequestBody AccountApproving aa) {
  	String uid = aa.getUid();
    String newuid = aa.getNewuid();
    String bu = aa.getBu();
    
    HttpStatus sts = HttpStatus.BAD_REQUEST;
    if(uid != null && !uid.isBlank()) {
    	try {
				referrerAccountService.approveUser(uid, newuid, bu);
				switchProviderUid(uid, newuid);
				sts = HttpStatus.OK;
			} catch (Exception e) {
				e.printStackTrace();
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
    }

		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), sts);
  }
  
  /**
   * Unlock user and clear finalizing flag attrs
   */
  @PostMapping("/finaliseUser")
  public ResponseEntity<StagingUserList> postFinaliseUser(@RequestBody AccountApproving aa) {
  	String uid = aa.getUid();
  	HttpStatus sts = HttpStatus.BAD_REQUEST;
  	if(uid != null && !uid.isBlank()) {
  		try {
  			StageUser user = referrerAccountService.finaliseUser(uid);
  			if(!"prod".equals(ACTIVE_PROFILE)) {
  				user.setEmail("Hidehiro.Uehara@i-med.com.au");
  			}
  			emailService.emailAccountApproved(user);
  			List<LdapUserDetails> details = referrerAccountService.findReferrerAccountsByUid(uid);
  			if(details.size() > 0) {
  				LdapUserDetails userDetail = details.get(0);
  				ReferrerActivationEntity rae = new ReferrerActivationEntity();
  				rae.setUid(userDetail.getUid());
  				rae.setAhpra(userDetail.getAhpra());
  				rae.setEmail(userDetail.getEmail());
  				rae.setMobile(userDetail.getMobile());
  				rae.setFirstName(userDetail.getGivenName());
  				rae.setLastName(userDetail.getSurname());
  				rae.setActivatedAt(new Date());
  				referrerActivationEntityJapRepository.saveAndFlush(rae);
  				logger.info("Saved to activation DB : " + userDetail);
  			}
  			else
  			{
  				logger.info("Failed to retrieve stageUser for " + uid);
  			}
  			sts = HttpStatus.OK;
  		} catch (Exception e) {
  			e.printStackTrace();
  			sts = HttpStatus.INTERNAL_SERVER_ERROR;
  		}
  	}
  	return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), sts);
  }
  
  @PostMapping("/declineUser")
  public ResponseEntity<StagingUserList> postDeclineUser(@RequestBody AccountDeclining ad) {
  	String uid = ad.getUid();
    String reason = ad.getReason();
    String step = ad.getStep();
    
    logger.info("postDeclineUser() {} {} {}", uid, reason, step);
    		    
    HttpStatus sts = HttpStatus.BAD_REQUEST;
    if(uid != null && !uid.isBlank()) {
    	try {
				referrerAccountService.declineUser(uid, step);
				deleteProviders(uid);
				sts = HttpStatus.OK;
			} catch (Exception e) {
				e.printStackTrace();
  			sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
    }
		return new ResponseEntity<StagingUserList>(getCurrentStagingUserList(), sts);
  }
  
  @PostMapping("/sendMessage")
  public ResponseEntity<String> postSentMessage() {
		return new ResponseEntity<String>("Not Yet Implemented", HttpStatus.OK);
  }
  
  private void switchProviderUid(final String uid, final String newuid) {
    if(uid != null && newuid != null && newuid.length() > 0 && !uid.equalsIgnoreCase(newuid)) {
      List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByUsername(uid);
      for(ReferrerProviderEntity entity : providers) {
        entity.setUsername(newuid);
        referrerProviderJpaRepository.saveAndFlush(entity);
      }
    }
  }
  
  private void deleteProviders(final String uid) {
    List<ReferrerProviderEntity> list = referrerProviderJpaRepository.findByUsername(uid);
    for(ReferrerProviderEntity entity : list) {
    	referrerProviderJpaRepository.delete(entity);
    }
    referrerProviderJpaRepository.flush();
  }
}
