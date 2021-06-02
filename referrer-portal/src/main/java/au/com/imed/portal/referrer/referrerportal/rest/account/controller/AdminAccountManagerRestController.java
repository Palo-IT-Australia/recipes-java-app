package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.ldap.HospitalGroupService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.reportaccess.ReportAccessService;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountDetails;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountLockUnlock;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountReportAccess;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountUid;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.AccountUidPassword;
import au.com.imed.portal.referrer.referrerportal.rest.account.model.UniquenessModel;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Order;

@RestController
@RequestMapping("/adminrest/account")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminAccountManagerRestController {
	private Logger logger = LoggerFactory.getLogger(AdminAccountManagerRestController.class);

	@Autowired
	private ReferrerCreateAccountService referrerCreateAccountService;

	@Autowired
	private HospitalGroupService hospitalGroupService;

	@Autowired
	private ReportAccessService reportAccessService;

	@PostMapping("/placeholder")
	public ResponseEntity<String> postPlaceholder(@RequestBody AccountUid au) {
		final String uid = au.getUid();
		HttpStatus sts = HttpStatus.OK;
		if(isUidAvailableForPlaceholder(uid)) {
			try {
				referrerCreateAccountService.createPlaceholderUser(uid);
			} catch (Exception ex) {
				ex.printStackTrace();
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			sts = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<String>(sts);
	}

	@GetMapping("/isUidAvailablePlaceholder")
	public ResponseEntity<UniquenessModel> isUidAvailablePlaceholder(@RequestParam("uid") String uid) {
		UniquenessModel model = new UniquenessModel();
		model.setAvailable(isUidAvailableForPlaceholder(uid));
		return new ResponseEntity<UniquenessModel>(model, HttpStatus.OK);
	}

	private boolean isUidAvailableForPlaceholder(final String uid) {
		return uid != null && uid.length() > 4 &&
				referrerCreateAccountService.findAccountsGlobalByAttr("uid", uid).size() == 0 &&
  			new ImedActiveDirectoryLdapManager().findByUid(uid).size() == 0;
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
		logger.info("Admin /find word:{} size:{}", word, list.size());
		return new ResponseEntity<List<LdapUserDetails>>(list, sts);
	}

	@PostMapping("/create")
	public ResponseEntity<String> postCreateAccount(@RequestBody ExternalUser imedExternalUser) {
		HttpStatus sts = HttpStatus.OK;
		try {
			referrerCreateAccountService.createPortalReferrerUser(imedExternalUser, imedExternalUser.getUserid());
		} catch (Exception e) {
			sts = HttpStatus.BAD_REQUEST;
			e.printStackTrace();
		}
		return new ResponseEntity<String>(sts);
	}

	@PostMapping("/detail")
	public ResponseEntity<String> postDetail(@RequestBody AccountDetails detail) {
		HttpStatus sts = HttpStatus.OK;
		try {
			DetailModel dm = new DetailModel();
			dm.setEmail(detail.getEmail());
			dm.setMobile(detail.getMobile());
			referrerCreateAccountService.updateReferrerAccountDetail(detail.getUid(), dm);
		} catch (Exception e) {
			sts = HttpStatus.BAD_REQUEST;
			e.printStackTrace();
		}
		return new ResponseEntity<String>(sts);
	}

	@PostMapping("/password")
	public ResponseEntity<String> postUpdatePassword(@RequestBody AccountUidPassword aup) {
		final String uid = aup.getUid();
		final String password = aup.getPassword();

		HttpStatus sts = HttpStatus.OK;
		if(uid != null && !uid.isBlank() && password != null && !password.isBlank()) {
			try {
				referrerCreateAccountService.resetReferrerPassword(uid, password);;
			} catch (Exception ex) {
				ex.printStackTrace();
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			sts = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<String>(sts);
	}

	@PostMapping("/lock")
	public ResponseEntity<String> postLockUnlock(@RequestBody AccountLockUnlock alu) {
		final String uid = alu.getUid();
		final boolean lock = alu.isLock();

		HttpStatus sts = HttpStatus.OK;
		if(uid != null && !uid.isBlank()) {
			try {
				logger.info("lockUnlock() {} {}", uid, lock);
				referrerCreateAccountService.lockUnlockReferrerAccount(uid, lock);
			} catch (Exception ex) {
				ex.printStackTrace();
				sts = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			sts = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<String>(sts);
	}

	//
  // HospitalGroupService
  //
  @GetMapping("/hospitalList")
  public ResponseEntity<List<AccountUid>> getHospitalMemberList() {
  	return new ResponseEntity<List<AccountUid>>(this.hospitalGroupService.list(), HttpStatus.OK);
  }

  @PostMapping("/hospitalAdd")
  public ResponseEntity<List<AccountUid>> addHospitalMember(@RequestBody AccountUid accountUid) {
  	if(this.hospitalGroupService.add(accountUid.getUid())) {
  		return new ResponseEntity<>(this.hospitalGroupService.list(), HttpStatus.OK);
  	}
  	else
  	{
  		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  	}
  }

  @PostMapping("/hospitalRemove")
  public ResponseEntity<List<AccountUid>> removeHospitalMember(@RequestBody AccountUid accountUid) {
  	if(this.hospitalGroupService.remove(accountUid.getUid())) {
  		return new ResponseEntity<>(this.hospitalGroupService.list(), HttpStatus.OK);
  	}
  	else
  	{
  		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  	}
  }


  // Report Access
	@GetMapping("/reportlist")
	public ResponseEntity<List<Order>> getList(@RequestParam("patientId") String patientId) {
		return new ResponseEntity<List<Order>>(reportAccessService.listOrders(patientId), HttpStatus.OK);
	}

	@PostMapping("/reportavailable")
	public ResponseEntity<JSONObject> postAvailable(@RequestBody AccountReportAccess ra) {
		logger.info("/available request body = " + ra);
		boolean is = reportAccessService.makeAvailable(ra.getReportUri(), ra.getOrderUri(), ra.getPatientUri());
		return ResponseEntity.status(is ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(new JSONObject());
	}
}
