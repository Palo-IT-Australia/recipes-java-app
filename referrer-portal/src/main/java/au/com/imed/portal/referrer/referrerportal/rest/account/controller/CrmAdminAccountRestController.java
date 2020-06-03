package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

import com.itextpdf.html2pdf.jsoup.helper.StringUtil;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountStatus;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import au.com.imed.portal.referrer.referrerportal.model.StagingValidatingUserList;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.GetReferrerService;

@RestController
@RequestMapping("/crmadminrest/account")
public class CrmAdminAccountRestController {
private Logger logger = LoggerFactory.getLogger(CrmAdminAccountRestController.class);
	
	@Autowired
	private ReferrerCreateAccountService referrerCreateAccountService;
	
	@Autowired
	private ReferrerAccountService referrerAccountService;
	
	@Autowired
	private GetReferrerService visageReferrerService;
	
	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;
	
	@PostMapping("/create")
	public ResponseEntity<String> postCreate(@RequestBody ExternalUser imedExternalUser) {
		logger.info("/create " + imedExternalUser);
		// TODO reply some json object for angular 
		return new ResponseEntity<String>("Applied account successfully", HttpStatus.OK);
	}

	@GetMapping("/find")
	public ResponseEntity<List<LdapUserDetails>> getFind(@RequestParam("word") String word) {
		List<LdapUserDetails> list;
		HttpStatus sts = HttpStatus.OK;
		if(word != null && word.length() >= 3) {
			try {
				list = referrerCreateAccountService.findFuzzyReferrerAccounts(word);
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
						if(!StringUtil.isBlank(uid)) { // TODO check with name
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
						String visUid = referrer.getUri().replaceAll("/user/", "").trim();
						logger.info("Visage user name " + visUid);
						// TODO double check with name
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
		List<StageUser> finalisings = referrerAccountService.getStageValidatingUserList();
		putProviders(finalisings);
		sul.setValidatings(finalisings);
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
