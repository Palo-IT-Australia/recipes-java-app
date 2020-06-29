package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.itextpdf.html2pdf.jsoup.helper.StringUtil;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.ForceResetPasswordAes128Util;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.AccountStatus;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.model.StagingValidatingUserList;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer.Practice;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;

@RestController
@RequestMapping("/crmadminrest/account")
public class CrmAdminAccountRestController {
	private Logger logger = LoggerFactory.getLogger(CrmAdminAccountRestController.class);
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Value("${imed.application.url}")
	private String APPLICATION_URL;
	
	@Autowired
	private ReferrerCreateAccountService referrerAccountService;
	
	@Autowired
	private GetReferrerService visageReferrerService;
	
	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;
	
	@Autowired
	private ReferrerMailService emailService;
	
	@SuppressWarnings("unchecked")
	@PostMapping("/create")
	public ResponseEntity<JSONObject> postCreate(@RequestBody ExternalUser imedExternalUser) {
		logger.info("/create " + imedExternalUser);
		// Temporal password
		final String temppswd = ForceResetPasswordAes128Util.randomString(16);
		imedExternalUser.setPassword(temppswd);
		imedExternalUser.setConfirmPassword(temppswd);
		Map<String, String> resultsMap = referrerAccountService.createAccount(imedExternalUser);
		//final String secret = ForceResetPasswordAes128Util.getSecretParameterValue(resultMap.uid, temppswd);
		//referrerAccountService.updateReferrerCrmAction(uid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
		//logger.info("/reset secret = " + secret);
		JSONObject reps = new JSONObject();
		reps.put("msg", "Account applied successfully");
		return new ResponseEntity<>(reps, HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/reset")
	public ResponseEntity<JSONObject> postReset(@RequestBody JSONObject resetUid) 
	{
		boolean isSuccess = false;
		JSONObject reps = new JSONObject();

		String uid = (String) resetUid.get("uid");
		logger.info("/reset for uid : " + uid);
		
		try
		{
			if(StringUtil.isBlank(uid)) {
				throw new IllegalArgumentException("Empty uid");
			}
			
			AccountDetail userDetails = referrerAccountService.getReferrerAccountDetail(uid);
			if(userDetails == null) {
				throw new IllegalArgumentException("Not referrer");
			}
				
			final String email = userDetails.getEmail();
			if(StringUtil.isBlank(email)) {
				throw new IllegalArgumentException("Email not registered");
			}
			
			final String mobile = userDetails.getMobile();
			if(StringUtil.isBlank(mobile) || !mobile.startsWith("04")) {
				throw new IllegalArgumentException("Email unregistered or non australian number.");
			}
			logger.info("/reset email {} mobile {} ", email, mobile);
			
			// Temporal password
			final String temppswd = ForceResetPasswordAes128Util.randomString(16);
			final String secret = ForceResetPasswordAes128Util.getSecretParameterValue(uid, temppswd);
			logger.info("/reset secret = " + secret);
			// TODO save to crm audit DB
			// Mobile number without spaces is confirm passcode
			//ReferrerPasswordResetEntity saved = confirmProcessDataService.savePasswordReset(userDetails.getUid(), mobile.replaceAll(" ", ""));
			//final String confirmParam = URLEncoder.encode(UrlCodeAes128Util.encrypt(saved.getUrlCode()), "UTF-8");
			referrerAccountService.resetReferrerPassword(uid, temppswd);
			referrerAccountService.updateReferrerCrmAction(uid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_RESET);
			logger.info("/reset updated password and action");
			if("prod".equals(ACTIVE_PROFILE)) {
				emailService.sendPasswordResetByCrmHtml(new String[] {email}, temppswd);
			} else {
				emailService.sendPasswordResetByCrmHtml(new String[] {"Hidehiro.Uehara@i-med.com.au"}, temppswd);
			}
			reps.put("msg", "Password reset successfully");		
			isSuccess = true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			reps.put("msg", "Failed to reset password - " + ex.getMessage());
		}
		
		return new ResponseEntity<>(reps, isSuccess ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/find")
	public ResponseEntity<List<LdapUserDetails>> getFind(@RequestParam("word") String word) {
		List<LdapUserDetails> list;
		HttpStatus sts = HttpStatus.OK;
		if(word != null && word.length() >= 3) {
			try {
				list = referrerAccountService.findFuzzyReferrerAccounts(word);
			} catch (Exception e) {
				e.printStackTrace();
				list = new ArrayList<>(0);
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			list = new ArrayList<>(0);
			sts = HttpStatus.BAD_REQUEST;
		}
		logger.info("Crm Admin /find word:{} size:{}", word, list.size());
		return new ResponseEntity<List<LdapUserDetails>>(list, sts);
	}
	
	@GetMapping("/inquire")
	public ResponseEntity<List<AccountStatus>> getInquire(@RequestParam("provider") String provider, @RequestParam("name") String name) {
		List<AccountStatus> list = new ArrayList<>(0);
		HttpStatus sts = HttpStatus.OK;
		if(!StringUtil.isBlank(provider) && !StringUtil.isBlank(name)) {
			try {
				List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByProviderNumber(provider);
				if(providers.size() > 0) {
					for(int idx = 0; idx < providers.size(); idx++) {
						String uid = providers.get(idx).getUsername();
						logger.info("uid from DB provider : " + uid);
						if(!StringUtil.isBlank(uid)) { // TODO check with practice or referrer name
							AccountStatus accountStatus = new AccountStatus();
							accountStatus.setUid(uid);
							accountStatus.setProviders(Collections.singletonList(providers.get(idx)));
							accountStatus.setVisage(getVisageReferrer(GetReferrerService.PARAM_CURRENT_USER_NAME, uid) != null);
							accountStatus.setPacs(referrerAccountService.GetPacsDnListByAttr("cn", uid).size() > 0);
							accountStatus.setImedpacs(referrerAccountService.GetImedPacsDnListByAttr("cn", uid).size() > 0);
							accountStatus.setPortal(referrerAccountService.GetReferrerDnListByAttr("uid", uid).size() > 0);				
							list.add(accountStatus);
						}
					}
				} else {					
					Referrer referrer = getVisageReferrer(GetReferrerService.PARAM_PROVIDER_NUMBER, provider);
					if(referrer != null) {
						// TODO double check with practice or referrer name
						String visName = referrer.getName();
						logger.info("Visage referrer name : " + visName + " vs " + name);
						boolean isReferrerNameMatch = visName.toLowerCase().contains(name.toLowerCase());
						List<Practice> visPracs = Arrays.asList(referrer.getPractices()).stream().filter(p -> p.getPracticeName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
						
						String visUid = referrer.getUri().replaceAll("/user/", "").trim();
						logger.info("Visage user name " + visUid);
						AccountStatus accountStatus = new AccountStatus();
						accountStatus.setUid(visUid);
						accountStatus.setProviders(null); // TODO convert to provider entity format or just prov#?
						accountStatus.setVisage(true);
						accountStatus.setPacs(visUid.length() > 0 ? referrerAccountService.GetPacsDnListByAttr("cn", visUid).size() > 0 : false);
						accountStatus.setImedpacs(visUid.length() > 0 ? referrerAccountService.GetImedPacsDnListByAttr("cn", visUid).size() > 0 : false);
						accountStatus.setPortal(visUid.length() > 0 ? referrerAccountService.GetReferrerDnListByAttr("uid", visUid).size() > 0 : false);				
						list.add(accountStatus);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			sts = HttpStatus.BAD_REQUEST;
		}
		logger.info("Crm Admin /inquire provider:{} size:{}", provider, list.size());
		return new ResponseEntity<List<AccountStatus>>(list, sts);
	}
	
	private Referrer getVisageReferrer(final String key, final String value) {
		Referrer referrer = null;
		ResponseEntity<Referrer> ve = visageReferrerService.doRestGet(PortalConstant.REP_VISAGE_USER, Collections.singletonMap(key, value), Referrer.class);
		logger.info("Referrer response {} {}", ve.getStatusCode(), ve.getBody());
		if(HttpStatus.OK.equals(ve.getStatusCode())) {
			Referrer ref = ve.getBody();
			if(ref.getName().length() > 0) {
				referrer = ve.getBody();
			}
		}
		return referrer;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/approve")
	public ResponseEntity<StagingValidatingUserList> postApprove(@RequestBody JSONObject reqObj) {
		ResponseEntity<StagingValidatingUserList> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			String uid = (String) reqObj.get("uid");
			String bu = (String) reqObj.get("bu");
			logger.info("/approve for uid {}, bu {}", uid, bu);
			// TODO validate ahpra, prov# etc. then DB or LDAP
			referrerAccountService.updateCrmValidating(uid, bu, true);
			JSONObject reps = new JSONObject();
			reps.put("msg", "Approved");
			entity = ResponseEntity.ok(getCurrentStagingUserList());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return entity;
	}
	
	@GetMapping("/getStageUsers")
	public ResponseEntity<StagingValidatingUserList> getStagingUserList() {
		return new ResponseEntity<StagingValidatingUserList>(getCurrentStagingUserList(), HttpStatus.OK);
	}
	
	private StagingValidatingUserList getCurrentStagingUserList() {
		StagingValidatingUserList sul = new StagingValidatingUserList();
		// Stage with providers
		List<StageUser> stagings = referrerAccountService.getStageNewUserList();
		putProviders(stagings);
		sul.setStagings(stagings);
		// Finalizing with providers
		List<StageUser> validatings = referrerAccountService.getStageValidatingUserList();
		putProviders(validatings);
		sul.setValidatings(validatings);
		return sul;
	}
	
  private void putProviders(List<StageUser> list) {
    int size = list.size();
    for(int i = 0;  i < size; i++) {
      List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByUsername(list.get(i).getUid());
      list.get(i).setProviders(providers);
    }
  }
}
