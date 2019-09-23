package au.com.imed.portal.referrer.referrerportal.mvc.controller;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ACTION_STATUS;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_FORM_MODEL;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.AddPractice;
import au.com.imed.portal.referrer.referrerportal.model.ChangeModel;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.ResetConfirmModel;
import au.com.imed.portal.referrer.referrerportal.model.ResetModel;
import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.service.ConfirmProcessDataService;
import au.com.imed.portal.referrer.referrerportal.service.EnvironmentVariableService;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;
import au.com.imed.portal.referrer.referrerportal.utils.SmsPasscodeHashUtil;
import au.com.imed.portal.referrer.referrerportal.utils.UrlCodeAes128Util;

@Controller
public class ReferrerPortalMvcController {
	private Logger logger = LoggerFactory.getLogger(ReferrerPortalMvcController.class);
	
	private static final String PARAM_IMED_EXTERNAL_USER = "imedExternalUser";

	@Value("${imed.portal.sare.base.url}")
	private String SHARE_REPORT_BASE_URL;

	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Value("${imed.application.url}")
	private String APPLICATION_URL;

	@Value("${imed.application.version}")
	private String vnum;

	@Autowired
	private ReferrerCreateAccountService accountService;
	
	@Autowired
	private ReferrerMailService emailService;
	
	@Autowired
	private ConfirmProcessDataService confirmProcessDataService;
	
	@Autowired
	private GoFaxSmsService smsService;
	
	@Autowired
	EnvironmentVariableService environmentVariableService;

	@GetMapping("/login")
	public ModelAndView getLogin() {
		ModelAndView loginModelAndView = new ModelAndView("login");
		loginModelAndView.addObject("message", environmentVariableService.getValue("login_page_alert_text"));
		return loginModelAndView;
	}
	
	@GetMapping("/apply")
	public String getApply() {
		return "apply";
	}
	
	@PostMapping("/apply")
	public String postApply(@ModelAttribute(PARAM_IMED_EXTERNAL_USER) ExternalUser imedExternalUser, Model model) {
		logger.info("/apply " + imedExternalUser.toString());
		model.addAllAttributes(accountService.createAccount(imedExternalUser));
		return "apply";
	}

	@GetMapping("/results")
	public String getResults() {
		return "results";
	}

	@GetMapping("/hospital")
	public String getHospital() {
		return "hospital";
	}

	@GetMapping("/admin")
	public String getAdmin() {
		return "admin";
	}
	
	@GetMapping("/admin/approve")
	public String getAdminApprove() {
		return "approve";
	}	
	
	@GetMapping("/admin/account")
	public String getAdminAccout() {
		return "account";
	}
	
	@GetMapping("/book")
	public String getBook() {
		return "book";
	}

	@GetMapping("/profile")
	public String getProfile(Model model, Authentication authentication) {
		model.addAttribute("ChangeModel", new ChangeModel());
		model.addAttribute("AddPractice", new AddPractice());
		model.addAttribute("DetailModel", getPopulatedDetailModel(authentication));
		return "profile";
	}

	private DetailModel getPopulatedDetailModel(final Authentication authentication) {
		DetailModel model = new DetailModel();
		if(authentication != null)
		{	
			AccountDetail detail = accountService.getReferrerAccountDetail(authentication.getName());
			if(detail != null)
			{
				model.setEmail(detail.getEmail());
				model.setMobile(detail.getMobile());
				model.setDisplayName(detail.getName());
			}
		}
		return model;
	} 

	@PostMapping("/detail")
	public String postUpdate(@ModelAttribute DetailModel detailModel, Model model, Authentication authentication) {
		final String uid = authentication.getName();
		if(uid != null)
		{
			if(ModelUtil.sanitizeModel(detailModel, true)) {
				try {
					Map<String, String> resultMap = accountService.updateReferrerAccountDetail(uid, detailModel);
					model.addAllAttributes(resultMap);
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute(MODEL_KEY_ERROR_MSG, "Failed to change details.");
				}
			}
			else
			{
				model.addAttribute(MODEL_KEY_ERROR_MSG, "Failed to change details. Invalid charactor input found.");      
			}
		}
		model.addAttribute("ChangeModel", new ChangeModel());
		model.addAttribute("AddPractice", new AddPractice());
		model.addAttribute("DetailModel", getPopulatedDetailModel(authentication));
		return "profile";
	}

	@PostMapping("/change")
	public String postChange(@ModelAttribute ChangeModel changeModel, Model model, Authentication authentication) {
		logger.info(changeModel.toString());
		if(ModelUtil.sanitizeModel(changeModel)) {
			final String uid = authentication.getName();
			if(uid != null && 
					changeModel.getNewPassword().length() >= 8 && 
					changeModel.getNewPassword().equals(changeModel.getConfirmPassword())) {
				try {
					accountService.updateReferrerPassword(uid, changeModel);
					model.addAttribute(MODEL_KEY_SUCCESS_MSG, "Your password has been changed.");
				} catch (Exception e) {
					e.printStackTrace();
					model.addAttribute(MODEL_KEY_ERROR_MSG, "Failed to change password. Current passwod is wrong.");
				}
			}
			else {
				logger.info("actionChange() password unmatch or uid null.");
				model.addAttribute(MODEL_KEY_ERROR_MSG, "Failed to change password. Password is too short.");
			}
		}
		else
		{
			model.addAttribute(MODEL_KEY_ERROR_MSG, "Failed to change password. Invalid charactor input found.");      
		}
		model.addAttribute("ChangeModel", new ChangeModel());
		model.addAttribute("AddPractice", new AddPractice());
		model.addAttribute("DetailModel", getPopulatedDetailModel(authentication));
		return "profile";
	}
	
	@PostMapping("/addpractice")
	public String postAddpractice(@ModelAttribute AddPractice practice, Model model, Authentication authentication) {
		logger.info("postAddpractice()", practice.toString());
		logger.info(authentication.getPrincipal().toString());
		DetailedLdapUserDetails principal = (DetailedLdapUserDetails) authentication.getPrincipal();
		if("prod".equals(ACTIVE_PROFILE)) {
			emailService.sendAddPractice(practice, principal);
		}
		model.addAttribute("ChangeModel", new ChangeModel());
		model.addAttribute("AddPractice", new AddPractice());
		model.addAttribute("DetailModel", getPopulatedDetailModel(authentication));
		model.addAttribute("practiceSuccessMsg", "Requested to add practice successfully. It takes up to two business days to complete the process.");
		return "profile";
	}
	
	@GetMapping("/reset")
	public String getReset(Model model) {
		model.addAttribute(MODEL_KEY_FORM_MODEL, new ResetModel());
		return "reset";
	}
	
	@PostMapping("/reset")
	public String postReset(@ModelAttribute ResetModel resetModel, Model model) {
		logger.info(resetModel.toString());
		if(ModelUtil.sanitizeModel(resetModel))
		{
			try
			{
				AccountDetail userDetails = accountService.getReferrerAccountDetailByEmail(resetModel.getUsername());
				if(userDetails != null) {
					final String email = userDetails.getEmail();
					final String mobile = userDetails.getMobile();
					if(email != null && email.length() > 0 && mobile != null && mobile.length() > 0) {
						if(mobile.startsWith("04")) {
							try {
								final String  passcode = SmsPasscodeHashUtil.randomString(8);
								ReferrerPasswordResetEntity saved = confirmProcessDataService.savePasswordReset(userDetails.getName(), passcode);
								final String confirmParam = URLEncoder.encode(UrlCodeAes128Util.encrypt(saved.getUrlCode()), "UTF-8");
								if("prod".equals(ACTIVE_PROFILE)) {
									emailService.sendPasswordResetHtml(new String[] {email}, APPLICATION_URL + "/resetconfirm?secret=" + confirmParam);
									smsService.send(new String [] {mobile}, "I-MED Radiology Network : Your account reset passcode is " + passcode);
								}else {
									emailService.sendPasswordResetHtml(new String[] {"Hidehiro.Uehara@i-med.com.au"}, APPLICATION_URL + "/resetconfirm?secret=" + confirmParam);
									smsService.send(new String [] {"0437118213"}, "I-MED Radiology Network : Your account reset passcode is " + passcode);
								}
								model.addAttribute(MODEL_KEY_SUCCESS_MSG, "You have successfully completed the password reset process. Shortly you will receive an email with a link and SMS with a code to finalise your new password.");
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								model.addAttribute(MODEL_KEY_ERROR_MSG, "Error : Failed to send SMS or email.");                         
							}
						}
						else
						{
							model.addAttribute(MODEL_KEY_ERROR_MSG, "Error : Your registered mobile number is invalid in Australia. Please update on My Account page.");      
						}
					}
					else
					{
						model.addAttribute(MODEL_KEY_ERROR_MSG, "Error : We don't have your email address or mobile number.");              
					}
				}
				else
				{
					model.addAttribute(MODEL_KEY_ERROR_MSG, "Email address is invalid");					
				}
			}
			catch(IllegalArgumentException iae) {
				model.addAttribute(MODEL_KEY_ERROR_MSG, "Email address is incorrect");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				model.addAttribute(MODEL_KEY_ERROR_MSG, "Unexpected error occured");
			}
		}
		else
		{
			model.addAttribute(MODEL_KEY_ERROR_MSG, "Invalid charactor input found");			
		}
		model.addAttribute(MODEL_KEY_FORM_MODEL, new ResetModel());
		return "reset";
	}
	
	@GetMapping("/resetconfirm")
	public String getResetConfirm(ModelMap modelMap, HttpServletRequest request) {
		String secret = request.getParameter("secret");
		if(secret != null && secret.length() > 0 && 
				confirmProcessDataService.getReferrerPasswordResetEntityBySecret(secret) != null) {
			ResetConfirmModel confirm = new ResetConfirmModel();
			confirm.setSecret(secret);
			modelMap.put(MODEL_KEY_FORM_MODEL, confirm);
			modelMap.put(MODEL_KEY_ACTION_STATUS, "normal");			
		}
		else
		{
			modelMap.put(MODEL_KEY_FORM_MODEL, new ResetConfirmModel());
			modelMap.put(MODEL_KEY_ACTION_STATUS, "invalid");			
		}
		return "resetconfirm";
	}
	
	@PostMapping("/resetconfirm")
	public String postResetConfirm(@ModelAttribute ResetConfirmModel confirmModel, Model model) {
		logger.info(confirmModel.toString());
		if(ModelUtil.sanitizeModel(confirmModel))
		{
			final String passcode = confirmModel.getPasscode();
			final String password = confirmModel.getPassword();
			final String secret = confirmModel.getSecret();
			if(passcode != null && passcode.trim().length() > 0 &&
					password != null && password.length() >= 8 &&
					secret != null && secret.length() > 0) {
				ReferrerPasswordResetEntity entity = confirmProcessDataService.getReferrerPasswordResetEntityBySecret(secret); 
				if(entity != null) {					
					try {
						if(SmsPasscodeHashUtil.validatePassword(passcode, entity.getPasscodeHash(), entity.getPasscodeSalt())) {
						  accountService.resetReferrerPassword(entity.getUid(), password);
						  confirmProcessDataService.setPasswordResetActive(entity);
						  model.addAttribute(MODEL_KEY_ACTION_STATUS, "success");						  
						}
						else
						{
							logger.info("postResetConfirm() Passcode wrong"); 
							confirmProcessDataService.incrementPasswordResetFailures(entity);
							model.addAttribute(MODEL_KEY_ACTION_STATUS, "error");															
						}
					} catch (Exception e) {
						e.printStackTrace();
						model.addAttribute(MODEL_KEY_ACTION_STATUS, "error");								
					}
				}
				else
				{
					model.addAttribute(MODEL_KEY_ACTION_STATUS, "error");								
				}
			}
			else
			{
				model.addAttribute(MODEL_KEY_ACTION_STATUS, "error");			
			}
		}
		else
		{
			model.addAttribute(MODEL_KEY_ACTION_STATUS, "error");						
		}
		ResetConfirmModel confirm = new ResetConfirmModel();
		confirm.setSecret(confirmModel.getSecret());
		model.addAttribute(MODEL_KEY_FORM_MODEL, confirm); 
		return "resetconfirm";
	}

	@GetMapping("/quick-report")
	public String getQuickReport() {
		return "quickreport";
	}
	
	@GetMapping("/")
	public String getHome(Model model) {
		return "home";
	}
	
	@GetMapping("/about")
	public String getAbout(Model model) {
		return "about";
	}
	
	@GetMapping("/support")
	public String getSupport(Model model) {
		return "support";
	}
	
	@GetMapping("/privacy")
	public String getPrivacy(Model model) {
		return "privacy";
	}
	
	@GetMapping("/termsofuse")
	public String getTermsOfUse(Model model) {
		return "termsofuse";
	}
	
	@GetMapping("/mycrm")
	public String getMyCrm(Model model) {
		return "mycrm";
	}
	
	@GetMapping("/feedback")
	public String getFeedback(Model model) {
		return "feedback";
	}
}
