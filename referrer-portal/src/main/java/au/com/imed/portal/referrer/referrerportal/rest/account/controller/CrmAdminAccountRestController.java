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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.html2pdf.jsoup.helper.StringUtil;

import au.com.imed.portal.referrer.referrerportal.audit.CrmAdminAuditService;
import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.ForceResetPasswordAes128Util;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.AccountStatus;
import au.com.imed.portal.referrer.referrerportal.model.AutoValidationResult;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.model.StagingValidatingUserList;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer.Practice;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;

@RestController
@RequestMapping("/crmadminrest/account")
@PreAuthorize("hasAuthority('ROLE_CRM_ADMIN')")
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
	private CrmAdminAuditService auditService;

	@Autowired
	private ReferrerMailService emailService;

	@SuppressWarnings("unchecked")
	@PostMapping("/create")
	public ResponseEntity<JSONObject> postCreate(@RequestBody ExternalUser imedExternalUser, Authentication authentication) {
		logger.info("/create " + imedExternalUser);
		// Temporal password
		final String temppswd = ForceResetPasswordAes128Util.randomString(16);
		imedExternalUser.setPassword(temppswd);
		imedExternalUser.setConfirmPassword(temppswd);
		Map<String, String> resultsMap = referrerAccountService.createAccount(imedExternalUser);
		JSONObject reps = new JSONObject();
		if(resultsMap.containsKey(PortalConstant.MODEL_KEY_SUCCESS_MSG)) {
			try {
				final String referrerUid = imedExternalUser.getUserid();
				logger.info("/create temporal pswd = " + temppswd);
				logger.info("/create with uid = " + referrerUid);
				int validationId = referrerAccountService.markCrmCreate(referrerUid);
				auditService.auditCreate(authentication.getName(), imedExternalUser, validationId);
				reps.put("msg", "Account applied successfully");
				return new ResponseEntity<>(reps, HttpStatus.OK);
			} catch (Exception ex) {
				ex.printStackTrace();
				reps.put("msg", "Failed to create account.");
				return new ResponseEntity<>(reps, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			reps.put("msg", resultsMap.get(PortalConstant.MODEL_KEY_ERROR_MSG));
			return new ResponseEntity<>(reps, HttpStatus.BAD_REQUEST);
		}
	}

//	private int markCrmCreate(final String referrerUid) {
//		int validationId = 0;
//		boolean isSet = referrerAccountService.updateReferrerCrmActionIfStating(referrerUid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
//		if(!isSet) {
//			List<ReferrerAutoValidationEntity> list = autoValidationRepository.findByUidAndValidationStatus(referrerUid, PortalConstant.VALIDATION_STATUS_PASSED);
//			if(list.size() > 0) {
//				// Should be only one
//				ReferrerAutoValidationEntity entity = list.get(0);
//				validationId = entity.getId();
//				logger.info("Auto validation table id found " + validationId);
//			} else {
//				logger.info("referrer is not auto validation table as passed status.");
//			}
//		}
//		return validationId;
//	}

	@SuppressWarnings("unchecked")
	@PostMapping("/reset")
	public ResponseEntity<JSONObject> postReset(@RequestBody JSONObject resetUid, Authentication authentication)
	{
		String crm = authentication.getName();

		boolean isSuccess = false;
		JSONObject reps = new JSONObject();

		String uid = (String) resetUid.get("uid");
		logger.info("/reset for uid : " + uid + " by crm " + crm);

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
			referrerAccountService.resetReferrerPassword(uid, temppswd);
			referrerAccountService.updateReferrerCrmAction(uid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_RESET);
			logger.info("/reset updated password and action");
			if("prod".equals(ACTIVE_PROFILE)) {
				emailService.sendPasswordResetByCrmHtml(new String[] {email}, temppswd);
			} else {
				emailService.sendPasswordResetByCrmHtml(new String[] {"Hidehiro.Uehara@i-med.com.au"}, temppswd);
			}
			// Save to DB
			auditService.auditReset(crm, userDetails, temppswd);
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
	public ResponseEntity<List<AccountStatus>> getInquire(@RequestParam("provider") String provider, @RequestParam("name") String name, Authentication authentication) {
		logger.info("/inquire parameter provider: {}, name: {}", provider, name);
		List<AccountStatus> list = new ArrayList<>(0);
		HttpStatus sts = HttpStatus.OK;
		if(!StringUtil.isBlank(provider) && !StringUtil.isBlank(name)) {
			try {
				List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByProviderNumber(provider);
				if(providers.size() > 0) {
					logger.info("Provider# in DB.");
					for(int idx = 0; idx < providers.size(); idx++) {
						String uid = providers.get(idx).getUsername();
						logger.info("uid from DB provider : " + uid);
						if(!StringUtil.isBlank(uid)) {
							// Check name match
							String trimmedUppderName = name.replaceAll(" ", "").toUpperCase();
							boolean isMatch = providers.get(idx).getPracticeName().replaceAll(" ", "").toUpperCase().contains(trimmedUppderName);
							if(!isMatch) {
								List<LdapUserDetails> refs = referrerAccountService.findReferrerAccountsByUid(uid);
								if(refs.size() > 0) {
									LdapUserDetails ref = refs.get(0);
									isMatch = trimmedUppderName.contains(ref.getGivenName().replaceAll(" ", "").toUpperCase()) ||
											trimmedUppderName.contains(ref.getSurname().replaceAll(" ", "").toUpperCase());
								}
							} else {
								logger.info("Practice name matched.");
							}
							logger.info("Name matched to practice name or referrer name? " + isMatch);

							if(isMatch) {
								AccountStatus accountStatus = new AccountStatus();
								accountStatus.setUid(uid);
								accountStatus.setProviders(Collections.singletonList(providers.get(idx)));
								accountStatus.setVisage(getVisageReferrer(GetReferrerService.PARAM_CURRENT_USER_NAME, uid) != null);
								accountStatus.setPacs(referrerAccountService.GetPacsDnListByAttr("cn", uid).size() > 0);
								accountStatus.setImedpacs(referrerAccountService.GetImedPacsDnListByAttr("cn", uid).size() > 0);
								accountStatus.setPortal(referrerAccountService.GetReferrerDnListByAttr("uid", uid).size() > 0);
								list.add(accountStatus);
							}
						} else {
							logger.info("uid is emply for provider#");
						}
					}
				} else {
					logger.info("Not in provider DB, finding visage");
					Referrer referrer = getVisageReferrer(GetReferrerService.PARAM_PROVIDER_NUMBER, provider);
					if(referrer != null) {
						String visName = referrer.getName();
						logger.info("Visage referrer name : " + visName + " vs " + name);
						boolean isReferrerNameMatch = visName.toLowerCase().contains(name.toLowerCase());
						List<Practice> visPracs = Arrays.asList(referrer.getPractices()).stream().filter(p -> p.getPracticeName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());

						if(isReferrerNameMatch || visPracs.size() > 0) {
							String visUid = referrer.getUri().replaceAll("/user/", "").trim();
							logger.info("Visage user name " + visUid);
							AccountStatus accountStatus = new AccountStatus();
							accountStatus.setUid(visUid);
							accountStatus.setProviders(toProviderEntities(referrer.getPractices()));
							accountStatus.setVisage(true);
							accountStatus.setPacs(visUid.length() > 0 ? referrerAccountService.GetPacsDnListByAttr("cn", visUid).size() > 0 : false);
							accountStatus.setImedpacs(visUid.length() > 0 ? referrerAccountService.GetImedPacsDnListByAttr("cn", visUid).size() > 0 : false);
							accountStatus.setPortal(visUid.length() > 0 ? referrerAccountService.GetReferrerDnListByAttr("uid", visUid).size() > 0 : false);
							list.add(accountStatus);
						} else {
							logger.info("Name does not match in visage");
						}
					}
				}
				auditService.auditInquire(authentication.getName(), provider, name);
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

	private List<ReferrerProviderEntity> toProviderEntities(final Practice [] visPracs) {
		List<ReferrerProviderEntity> list = new ArrayList<ReferrerProviderEntity>(1);
		for(Practice vis : visPracs) {
			ReferrerProviderEntity prov = new ReferrerProviderEntity();
			prov.setProviderNumber(vis.getProviderNumber());
			prov.setPracticeAddress(vis.getAddress().getLine1());
			prov.setPracticeFax(vis.getFax());
			prov.setPracticeName(vis.getPracticeName());
			prov.setPracticePhone(vis.getPhone1());
			prov.setPracticePostcode(vis.getAddress().getPostcode());
			prov.setPracticeState(vis.getAddress().getState());
			prov.setPracticeStreet(vis.getAddress().getLine1());
			prov.setPracticeSuburb(vis.getAddress().getCity());
			prov.setPracticeType(vis.getSpeciality());
			list.add(prov);
		}
		return list;
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
	public ResponseEntity<StagingValidatingUserList> postApprove(@RequestBody JSONObject reqObj, Authentication authentication) {
		ResponseEntity<StagingValidatingUserList> entity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		try {
			String uid = (String) reqObj.get("uid");
			String bu = (String) reqObj.get("bu");
			String newuid = ""; //(String)reqObj.get("newuid");
			logger.info("/approve for uid {}, bu {}", uid, bu);
			AutoValidationResult result = referrerAccountService.validateOnLdapStaging(uid, newuid, bu);
			auditService.auditApprove(authentication.getName(), uid, newuid);
			JSONObject reps = new JSONObject();
			reps.put("msg", result.isValid() ? "Created" : "Validating");
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
