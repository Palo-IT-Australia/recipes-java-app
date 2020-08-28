package au.com.imed.portal.referrer.referrerportal.ldap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.IMED_TEMPORAL_PASSWORD;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_MSG_ACCOUNT_CREATED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_MSG_MANUAL_CREATEION;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_INVALID;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_NOTIFIED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_PASSED;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.VALIDATION_STATUS_VALID;
import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Name;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.ahpra.AhpraBotService;
import au.com.imed.portal.referrer.referrerportal.ahpra.AhpraDetails;
import au.com.imed.portal.referrer.referrerportal.audit.CrmAdminAuditService;
import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.Aes128StringEncodeUtil;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.filetoaccount.VisageCheckerService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.MedicareProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAutoValidationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmPostcodeJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.CrmProfileJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.MedicareProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerActivationJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerAutoValidationRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.model.AutoValidationResult;
import au.com.imed.portal.referrer.referrerportal.model.ExternalPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;

@Service
public class ReferrerCreateAccountService extends ReferrerAccountService {
	private Logger logger = LoggerFactory.getLogger(ReferrerCreateAccountService.class);

	@Autowired
	private VisageCheckerService visageCheckerService;

	@Autowired
	private MedicareProviderJpaRepository medicareProviderJpaRepository;

	@Autowired
	private AhpraBotService ahpraBotService;

	@Autowired
	private ReferrerAutoValidationRepository referrerAutoValidationRepository;

	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;

	@Value("${imed.shared.folder.mnt}")
	private String SHARED_MOUNTED_FOLDER;

	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;

	@Autowired
	private CrmPostcodeJpaRepository crmPostcodeRepository;

	@Autowired
	private CrmProfileJpaRepository crmProfileRepository;

	@Autowired
	private ReferrerMailService emailService;

	@Autowired
	private ReferrerActivationJpaRepository referrerActivationEntityJapRepository;

	@Autowired
	private CrmAdminAuditService auditService;

	private static final String MSG_APPLIED_SUCCESSFULLY = "Thank you for registering for I-MED Online 2.0! Your application will be processed within one business day. You will receive and email confirmation with your username once complete.";

	public Map<String, String> createAccount(ExternalUser imedExternalUser) {
		Map<String, String> resultMap = new HashMap<>(1);

		if(ModelUtil.sanitizeExternalUserModel(imedExternalUser))
		{
			if (imedExternalUser.getPassword().equals(imedExternalUser.getConfirmPassword())) {
				if(imedExternalUser.getAccountType().startsWith("COMRAD")) {
					// Comrad no password first, set temporal one
					imedExternalUser.setPassword(IMED_TEMPORAL_PASSWORD);
					imedExternalUser.setConfirmPassword(IMED_TEMPORAL_PASSWORD);
				}

				// Auto Validation process (QLD only at this moment)
				boolean isAutoValidationTarget = isAutoValidationTarget(imedExternalUser);

				final String proposedUid = imedExternalUser.getUserid();
				if(autoUidRequired(imedExternalUser))
				{
					imedExternalUser.setUserid(generateUsername(imedExternalUser));
				}

				AutoValidationResult result = null;
				if(isAutoValidationTarget) {
					result = validateOnFormSubmission(imedExternalUser);
				}
				if(isAutoValidationTarget && result.isValid()) {
					saveProviders(imedExternalUser);
					resultMap.put(MODEL_KEY_SUCCESS_MSG, MSG_APPLIED_SUCCESSFULLY);
				} else {
					// Conventional approver procedure
					try {
						createPortalStagingUser(imedExternalUser, proposedUid);
						saveProviders(imedExternalUser);
						resultMap.put(MODEL_KEY_SUCCESS_MSG, MSG_APPLIED_SUCCESSFULLY);
						if("prod".equals(ACTIVE_PROFILE)) {
							emailService.emailAutoValidatedReferrerAccount(ReferrerMailService.SUPPORT_ADDRESS, imedExternalUser, true, result);
						}else {
							emailService.emailAutoValidatedReferrerAccount("Hidehiro.Uehara@i-med.com.au", imedExternalUser, true, result);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						resultMap.put(MODEL_KEY_ERROR_MSG, "Failed to register the user. Please check password includes only valid characters.");
					}
				}
			} else {
				resultMap.put(MODEL_KEY_ERROR_MSG, "Failed to register the user. The passwords do not match");
			}
		}else{
			resultMap.put(MODEL_KEY_ERROR_MSG, "Failed to register the user. Invalid input detected.");      
		}

		return resultMap;
	}

	public void createPortalStagingUser(final ExternalUser imedExternalUser, final String proposedUid) throws Exception {
		createReferrerUser(getReferrerStagingLdapTemplate(), imedExternalUser, proposedUid, "true", null);
	}

	/**
	 * Admin tool can create referrer account without approval process
	 * @param imedExternalUser
	 * @param proposedUid
	 * @throws Exception
	 */
	public void createPortalReferrerUser(final ExternalUser imedExternalUser, final String proposedUid) throws Exception {
		createReferrerUser(getReferrerLdapTemplate(), imedExternalUser, proposedUid, "false", null);
	}

	private void createReferrerUser(LdapTemplate ldapTemplate, final ExternalUser imedExternalUser, final String proposedUid, final String lock, final String businessUnit) throws Exception {
		Name dn = LdapNameBuilder
				.newInstance()
				.add("uid", imedExternalUser.getUserid())
				.build();
		DirContextAdapter context = new DirContextAdapter(dn);

		context.setAttributeValues(
				"objectclass",
				new String[]
						{ "top",
								"person",
								"organizationalPerson",
						"inetorgPerson" });
		context.setAttributeValue("cn", imedExternalUser.getFirstName() + " " + imedExternalUser.getLastName());
		context.setAttributeValue("sn", imedExternalUser.getLastName());
		context.setAttributeValue("givenName", imedExternalUser.getFirstName());
		context.setAttributeValue("userPassword", imedExternalUser.getPassword());
		context.setAttributeValue("mail", imedExternalUser.getEmail());
		context.setAttributeValue("mobile", imedExternalUser.getMobile());
		context.setAttributeValue("employeeType", imedExternalUser.getAccountType());
		context.setAttributeValue("carLicense", proposedUid);
		context.setAttributeValue("AHPRA", imedExternalUser.getAhpraNumber());
		context.setAttributeValue("homePhone", imedExternalUser.getPreferredPhone());
		if(businessUnit != null) {
			context.setAttributeValue("BusinessUnit", businessUnit);
		}
		List<ExternalPractice> practices = imedExternalUser.getPractices();
		if(practices != null && practices.size() > 0) {
			context.setAttributeValue("physicalDeliveryOfficeName", practices.get(0).getPracticeAddress());
			context.setAttributeValue("RISid", practices.get(0).getProviderNumber());
		} else {
			context.setAttributeValue("physicalDeliveryOfficeName", imedExternalUser.getPreferredPhone());
		}
		context.setAttributeValue(PortalConstant.PARAM_ATTR_ACC_LOCKED, lock);

		ldapTemplate.bind(context);
	}

	public void createPlaceholderUser(final String uid) throws Exception {
		Name dn = LdapNameBuilder
				.newInstance()
				.add("uid", uid)
				.build();
		DirContextAdapter context = new DirContextAdapter(dn);

		context.setAttributeValues(
				"objectclass",
				new String[]
						{ "top",
								"person",
								"organizationalPerson",
						"inetorgPerson" });
		context.setAttributeValue("cn", "Placeholder account");
		context.setAttributeValue("sn", "Placeholder");
		context.setAttributeValue(PortalConstant.PARAM_ATTR_ACC_LOCKED, "true");

		getStagePacsLdapTemplate().bind(context);
	}

	private void saveProviders(final ExternalUser user) {
		List<ExternalPractice> practices = user.getPractices();
		for (ExternalPractice practice : practices) {
			logger.info(practice.toString());
			saveReferrerProvider(practice, user);
		}    
	}

	private void saveReferrerProvider(ExternalPractice imedExternalPractice, ExternalUser imedExternalUser) {
		ReferrerProviderEntity practice = new ReferrerProviderEntity();
		practice.setUsername(imedExternalUser.getUserid());
		practice.setProviderNumber(imedExternalPractice.getProviderNumber());
		practice.setPracticeName(imedExternalPractice.getPracticeName());
		practice.setPracticePhone(imedExternalPractice.getPracticePhone());
		practice.setPracticeFax(imedExternalPractice.getPracticeFax());
		practice.setPracticeAddress(imedExternalPractice.getPracticeAddress());

		practice.setPracticeStreet(imedExternalPractice.getPracticeStreet());
		practice.setPracticeSuburb(imedExternalPractice.getPracticeSuburb());
		practice.setPracticeState(imedExternalPractice.getPracticeState());
		practice.setPracticePostcode(imedExternalPractice.getPracticePostcode());
		practice.setPracticeType(imedExternalPractice.getPracticeType());

		referrerProviderJpaRepository.save(practice);
	}

	private boolean autoUidRequired(final ExternalUser imedExternalUser) {
		boolean req = false;
		if("PACS".equalsIgnoreCase(imedExternalUser.getAccountType())) {
			req = false;
		}
		else if(imedExternalUser.getAccountType().startsWith("COMRAD")) {
			req = true;
		}
		if(imedExternalUser.getUserid() == null || imedExternalUser.getUserid().length() < 4)
		{
			req = true;
		}
		return req;
	}

	private final static String NEW_NAME_PREFIX = "im";
	private String generateUsername(ExternalUser imedExternalUser) {
		String initName = NEW_NAME_PREFIX +
				(imedExternalUser.getFirstName().toLowerCase().substring(0, 1) + imedExternalUser
						.getLastName().toLowerCase()).replaceAll("[^a-zA-Z]","");

		if (initName.length() > 20) initName = initName.substring(0,20);

		String userName = initName;

		logger.info("Initial username: " + initName);

		userName = generateUsernameInner(initName);
		userName = generateUsernameNotInVisage(userName);

		logger.info("Generated username: " + userName);

		return userName;
	}

	private String generateUsernameNotInVisage(final String candidateName) {
		String finalName = candidateName;
		if(finalName != null && finalName.length() > 0) {
			int i = 1;
			// Visage has no fuzzy search call each time
			while(visageCheckerService.isUsernameTaken(finalName)) {
				finalName = finalName + (i++);
			}
		}
		logger.info("generateUsernameNotInVisage() finalName = " + finalName);
		return finalName;
	}

	private String generateUsernameInner(final String initName) {
		String result = initName;

		try {
			Set<String> uniqueNames = new HashSet<String>();
			uniqueNames.addAll(new ImedActiveDirectoryLdapManager().findByUidStartsWith(initName).stream().map(u -> u.getUid()).collect(Collectors.toList()));
			uniqueNames.addAll(getAccountDetailListLike(getApplicationsLdapTemplate(), "uid", initName + "*").stream().map(u -> u.getUid()).collect(Collectors.toList()));
			uniqueNames.addAll(getAccountDetailListLike(getStagePacsLdapTemplate(), "uid", initName + "*").stream().map(u -> u.getUid()).collect(Collectors.toList()));
			uniqueNames.addAll(getAccountDetailListLike(getImedPacsLdapTemplate(), "cn", initName + "*").stream().map(u -> u.getName()).collect(Collectors.toList()));
			uniqueNames.addAll(getAccountDetailListLike(getPacsLdapTemplate(), "cn", initName + "*").stream().map(u -> u.getName()).collect(Collectors.toList()));
			uniqueNames.addAll(referrerAutoValidationRepository.findByUidLikeAndValidationStatusNot(initName + "%", VALIDATION_STATUS_INVALID).stream().map(u -> u.getUid()).collect(Collectors.toList()));

			if(uniqueNames.size() > 0) {
				int i = 0;
				String newName = initName + (++i);
				while (uniqueNames.contains(newName)) {
					newName = initName + (++i);
				}
				if (newName.length() <= 20) result = newName;
				else result = generateUsernameInner(initName.substring(0,initName.length() - 1));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		return result;
	}

	//
	// Auto validation methods
	//
	/**
	 *
	 * @param imedExternalUser
	 * @return
	 */
	private boolean isAutoValidationTarget(final ExternalUser imedExternalUser) {
		boolean isAuto = false;
		if("NEW".equalsIgnoreCase(imedExternalUser.getAccountType())) {  
			isAuto = true;
		}
		return isAuto;
	}

	/**
	 * Validate all data except for AHPRA# due to possible bot blocker.
	 *
	 * @param imedExternalUser uid and ahpra should already be unique
	 * @return
	 */
	private AutoValidationResult validateOnFormSubmission(ExternalUser imedExternalUser) {
		logger.info("validateOnFormSubmission() " + imedExternalUser);

		boolean isAccepted = false;
		String msg = "Ready to validate AHPRA details";
		AutoValidationResult result = new AutoValidationResult(isAccepted, msg);

		if(imedExternalUser != null && isAutoValidationTarget(imedExternalUser)) {
			if(findGlobalByAtr("AHPRA", imedExternalUser.getAhpraNumber()).size() > 0) {
				msg = "AHPRA " + imedExternalUser.getAhpraNumber() + " already in LDAP";
			}else if(findGlobalByAtr("mail", imedExternalUser.getEmail()).size() > 0) {
				msg = "email address " + imedExternalUser.getEmail() + " already in LDAP";
			}else if(visageCheckerService.isUsernameTaken(imedExternalUser.getUserid())) {
				msg = "username already in Visage";
			}else if(visageCheckerService.isAhpraTaken(imedExternalUser.getUserid())) {
				msg = "AHPRA " + imedExternalUser.getAhpraNumber() + " already in Visage";
			}else if(poviderNumbersTakenInVisage(imedExternalUser).size() > 0) {
				msg = "Provider number already in Visage";
			}else if(!validMedicareProviders(imedExternalUser)) {
				msg = "Practice details do not match to medicare provider DB";
			}else if(pacsAccountsExist(imedExternalUser)) {
				msg = "The applicant may already has PACS account";
			}else {
				isAccepted = true;
			}

			// Create database entity and save with status and message
			ReferrerAutoValidationEntity entity = new ReferrerAutoValidationEntity();
			entity.setAccountType(imedExternalUser.getAccountType());
			entity.setAhpra(imedExternalUser.getAhpraNumber());
			entity.setApplyAt(new Date());
			entity.setBusinessUnit("MIAV");
			entity.setContactAdvanced(imedExternalUser.getContactAdvanced());
			entity.setEmail(imedExternalUser.getEmail());
			entity.setFilmless(imedExternalUser.getFilmless());
			entity.setFirstName(imedExternalUser.getFirstName());
			entity.setLastName(imedExternalUser.getLastName());
			entity.setMobile(imedExternalUser.getMobile());
			entity.setPasswordEncoded(Aes128StringEncodeUtil.encrypt(imedExternalUser.getPassword()));
			entity.setPhone(imedExternalUser.getPreferredPhone());
			entity.setUid(imedExternalUser.getUserid());
			updateValidationStatus(entity, isAccepted ? VALIDATION_STATUS_PASSED : VALIDATION_STATUS_INVALID, msg);

			logger.info("validateOnFormSubmission() msg : " + msg + ", accepted ? " + isAccepted);
			result.setMsg(msg);
			result.setValid(isAccepted);
		}
		else
		{
			logger.info("Not auto validation target, skipping all process.");
			result.setMsg("Not auto validation target");
			result.setValid(false);
		}

		return result;  // Returning false proceeds to ordinal manual approver
	}

	private boolean pacsAccountsExist(final ExternalUser imedExternalUser) {
		boolean isConflict = false;

		try {
			List<Name> pnames = findPacsUsersDnListByFirstAndLastNames(imedExternalUser.getFirstName(), imedExternalUser.getLastName());
			if(pnames.size() > 0) {
				logger.info("pacsAccountsExist() Pacs Users May conflict " + pnames.get(0));
				isConflict = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			List<Name> ipnames = findImedPacsUsersDnListByFirstAndLastNames(imedExternalUser.getFirstName(), imedExternalUser.getLastName());
			if(ipnames.size() > 0) {
				logger.info("pacsAccountsExist() I-MED Pacs Users May conflict " + ipnames.get(0));
				isConflict = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for(ExternalPractice practice : imedExternalUser.getPractices()) {
			List<Name> risnames = GetPacsDnListByAttr("RISid", practice.getProviderNumber());
			if(risnames.size() > 0) {
				logger.info("pacsAccountsExist() RISid " + practice.getProviderNumber() + " already taked by " + risnames.get(0));
				isConflict = true;
			}
		}

		return isConflict;
	}

	private boolean validMedicareProviders(final ExternalUser imedExternalUser) {
		boolean isValid = false;
		for(ExternalPractice practice : imedExternalUser.getPractices()) {
			List<MedicareProviderEntity> list = medicareProviderJpaRepository.findByProviderNumberAndPracticeNot(practice.getProviderNumber(), "LEFT PRACTICE");
			if(list.size() > 0) {  // Should be only one
				MedicareProviderEntity entity = list.get(0);
				// As DB has some empty or funny suburbs, check postcode and state only
				isValid = compareIngoringSpaces(entity.getFirstName(), imedExternalUser.getFirstName()) &&
						compareIngoringSpaces(entity.getLastName(), imedExternalUser.getLastName()) &&
						compareIngoringSpaces(entity.getPostcode(), practice.getPracticePostcode()) &&
						compareIngoringSpaces(entity.getState(), practice.getPracticeState());
				;
				if(!isValid) {
					logger.info("Provider NOT match DB : " + practice);
					break;
				}
			}
			else
			{
				logger.info("Active provider not in DB : " + practice);
				isValid = false;
			}
		}
		return isValid;
	}

	private boolean compareIngoringSpaces(final String first, final String second) {
		boolean isEqual = false;
		if(first != null && second != null && first.length() > 0 && second.length() > 0) {
			isEqual = first.replaceAll(" ", "").equalsIgnoreCase(second.replaceAll(" ", ""));
		}
		if(!isEqual) {
			logger.info("compareIngoringSpaces() unmatch [{}] [{}]", first, second);
		}
		return isEqual;
	}

	private boolean containsIgnoringSpaces(final String first, final String second) {
		boolean isContain = false;
		if(first != null && second != null && first.length() > 0 && second.length() > 0) {
			isContain = first.replaceAll(" ", "").toUpperCase().contains(second.replaceAll(" ", "").toUpperCase());
		}
		if(!isContain) {
			logger.info("containsIgnoringSpaces() [{}] not contain [{}]", first, second);
		}
		return isContain;
	}

	private void updateValidationStatus(ReferrerAutoValidationEntity entity, String status, String msg) {
		entity.setValidationStatus(status);
		entity.setValidationMsg(msg);
		referrerAutoValidationRepository.saveAndFlush(entity);
		logger.info("updateValidationStatus() Updated " + status + ", " + msg);
	}

	/**
	 * final validation on AHPRA on ahpra.gov.au, night scheduler task will call
	 */
	public List<ReferrerAutoValidationEntity> validateOnDb(List<ReferrerAutoValidationEntity> list) {
		logger.info("validateOnDb() # of target referrers " + list.size());
		List<ReferrerAutoValidationEntity> created = new ArrayList<>();

		for(ReferrerAutoValidationEntity entity : list) {
			boolean isValid = false;
			List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(entity.getUid());
			String ahpra = entity.getAhpra();
			AhpraDetails [] ahpras = this.ahpraBotService.findByNumberRetry(ahpra);
			AhpraDetails detail = null;
			if(ahpras.length > 0) {
				detail = ahpras[0]; // Should be one
				isValid = validAhpra(entity, detail, provs);
				logger.info("validateOnDb() AHPRA details valid ? " + isValid);
			} else {
				logger.info("validateOnDb() ahpra not available in ahpra.gov.au : " + ahpra);
			}

			ExternalUser imedExternalUser = toExternalUser(entity, provs);

			if(isValid)
			{
				// create referrer account directly
				try {
					createReferrerUser(getReferrerLdapTemplate(), imedExternalUser, entity.getUid(), "false", entity.getBusinessUnit());
					// CRMCreate case
					setCrmAdminCreate(entity.getOrigin(), entity.getUid(), false);
					saveActivationDb(imedExternalUser);
					entity.setAccountAt(new Date());
					updateValidationStatus(entity, VALIDATION_STATUS_VALID, VALIDATION_MSG_ACCOUNT_CREATED);
					created.add(entity);
					if("prod".equals(ACTIVE_PROFILE)) {
						emailService.emailAutoValidatedReferrerAccount(ReferrerMailService.SUPPORT_ADDRESS, imedExternalUser, false, null);
					} else {
						emailService.emailAutoValidatedReferrerAccount("Hidehiro.Uehara@i-med.com.au", imedExternalUser, false, null);
					}

				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			else
			{
				// create staging account
				try {
					createPortalStagingUser(imedExternalUser, entity.getUid());
					// CRMCreate case
					setCrmAdminCreate(entity.getOrigin(), entity.getUid(), true);
					String msg = "AHPRA details don't match to ahpra.gov.au";
					AutoValidationResult result = new AutoValidationResult(false, msg);
					if("prod".equals(ACTIVE_PROFILE)) {
						emailService.emailAutoValidatedReferrerAccount(ReferrerMailService.SUPPORT_ADDRESS, imedExternalUser, true, result);
					} else {
						emailService.emailAutoValidatedReferrerAccount("Hidehiro.Uehara@i-med.com.au", imedExternalUser, true, result);
					}
					updateValidationStatus(entity, VALIDATION_STATUS_INVALID, msg);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		logger.info("validateOnDb() # of created accounts " + created.size());
		return created;
	}

	private void saveActivationDb(ExternalUser imedExternalUser) {
		ReferrerActivationEntity rae = new ReferrerActivationEntity();
		rae.setUid(imedExternalUser.getUserid());
		rae.setAhpra(imedExternalUser.getAhpraNumber());
		rae.setEmail(imedExternalUser.getEmail());
		rae.setMobile(imedExternalUser.getMobile());
		rae.setFirstName(imedExternalUser.getFirstName());
		rae.setLastName(imedExternalUser.getLastName());
		rae.setActivatedAt(new Date());
		referrerActivationEntityJapRepository.saveAndFlush(rae);
		logger.info("saveActivationDb() saved " + imedExternalUser.getUserid());
	}

	private List<File> getVisageCsvFiles() {
		List<File> csvs = new ArrayList<>(0);
		logger.info("getVisageCsvFiles() getting csvs from " + SHARED_MOUNTED_FOLDER + "/from");
		try {
			csvs = Files.walk(Paths.get(SHARED_MOUNTED_FOLDER + "/from"))
					.filter(Files::isRegularFile)
					.map(Path::toFile)
					.filter(f -> f.getName().endsWith(".csv"))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("getVisageCsvFiles() # of files = " + csvs.size());
		return csvs;
	}

	public void notifyNewAccountByVisageCsvFiles() {
		List<File> csvs = getVisageCsvFiles();
		List<ReferrerAutoValidationEntity> listSuccess = new ArrayList<>();
		List<ReferrerAutoValidationEntity> listError = new ArrayList<>();

		if(csvs.size() > 0) {
			// Collect entries for errors and successes
			for(File csv : csvs) {
				String fname = csv.getName();
				if(fname.contains("success")) {
					logger.info("Visage success file " + fname);
					List<ReferrerAutoValidationEntity> slist = csvToCreatedReferrers(csv);
					// moved to another scheduler to check visage account notifyNewAccounts(slist);
					listSuccess.addAll(slist);
				} else if(fname.contains("error")) {
					logger.info("Visage error file " + fname);
					List<ReferrerAutoValidationEntity> elist = csvToCreatedReferrers(csv);
					listError.addAll(elist);
				}
			}
			
			// Welcome if success only, set manual creation if any error
			if(listError.size() > 0) {
				logger.info("notifyNewAccountByVisageCsvFiles() There are error cases : " + listError);
				logger.info("notifyNewAccountByVisageCsvFiles() Updating DB with " + VALIDATION_MSG_MANUAL_CREATEION);
				for(ReferrerAutoValidationEntity eent : listError) {
					eent.setNotifyAt(new Date());
					updateValidationStatus(eent, VALIDATION_STATUS_NOTIFIED, VALIDATION_MSG_MANUAL_CREATEION);
				}
				logger.info("notifyNewAccountByVisageCsvFiles() listSuccess before removing listError " + listSuccess);
				List<String> errorUids = listError.stream().map(e -> e.getUid()).collect(Collectors.toList());
				listSuccess = listSuccess.stream().filter(s -> !errorUids.contains(s.getUid())).collect(Collectors.toList());
				logger.info("notifyNewAccountByVisageCsvFiles() Removed to " + listSuccess);
			}
			logger.info("notifyNewAccountByVisageCsvFiles() Full auto success cases for welcome email : " + listSuccess);
			notifyNewAccounts(listSuccess);

			// Notify support team
			try {
				sendVisageCsvFilesEmail(csvs, listSuccess, listError);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Move files into processed folder
			for(File from : csvs) {
				try {
					logger.info("Moving file to processed foler : " + from.getName());
					from.renameTo(new File(SHARED_MOUNTED_FOLDER + "/processed/" + from.getName()));
					from.delete();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void buildReferrerCsvFilesContent(PrintWriter referrerWriter, PrintWriter providerWriter, List<ReferrerAutoValidationEntity> list) {
		referrerWriter.println("uid,firstname,lastname,email,AHPRA#,BusinessUnit,phone,mobile,Contact,Filmless");
		providerWriter.println("uid,provider#,practiceName,phone,fax,street,suburb,state,postcode");
		for(ReferrerAutoValidationEntity entity : list) {
			referrerWriter.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
					entity.getUid(), nonQuote(entity.getFirstName()), nonQuote(entity.getLastName()), nonQuote(entity.getEmail()),
					nonQuote(entity.getAhpra()), entity.getBusinessUnit(), nonQuote(entity.getPhone()), nonQuote(entity.getMobile()),
					nonQuote(entity.getContactAdvanced()), nonQuote(entity.getFilmless()) ));
			List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(entity.getUid());
			for(ReferrerProviderEntity provider : provs) {
				providerWriter.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
						provider.getUsername(), nonQuote(provider.getProviderNumber()), nonQuote(provider.getPracticeName()), nonQuote(provider.getPracticePhone()), nonQuote(provider.getPracticeFax()),
						nonQuote(provider.getPracticeStreet()), nonQuote(provider.getPracticeSuburb()),nonQuote(provider.getPracticeState()), nonQuote(provider.getPracticePostcode()) ));
			}
		}
	}
	
	private static final int MAX_ACCOUNTS_IN_POSTFIX = 5;
	private String buildResultCsvEmailSubjectPostFix(final List<ReferrerAutoValidationEntity> listSuccess, final List<ReferrerAutoValidationEntity> listError) { 
		String ps = " - ";
		try {
			ps += listSuccess.size() > MAX_ACCOUNTS_IN_POSTFIX ? listSuccess.size() + " Accounts" : String.join(",", listSuccess.stream().map(e -> e.getUid()).collect(Collectors.toList()));
			ps += " : ";
			ps += listError.size() > MAX_ACCOUNTS_IN_POSTFIX ? listError.size() + " Accounts" : String.join(",", listError.stream().map(e -> e.getUid()).collect(Collectors.toList()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return ps;
	}

	private void sendVisageCsvFilesEmail(List<File> csvs, List<ReferrerAutoValidationEntity> listSuccess, List<ReferrerAutoValidationEntity> listError) throws Exception {
		Map<String, File> fileMap = new HashMap<>(6);
		for(File file : csvs) {
			fileMap.put(file.getName(), file);
		}

		File referrerFile = null;
		File providerFile = null;
		if(listSuccess.size() > 0) {
			referrerFile = File.createTempFile("referrerssuccess-", ".csv");
			providerFile = File.createTempFile("providerssuccess-", ".csv");
			PrintWriter referrerWriter = new PrintWriter(referrerFile);
			PrintWriter providerWriter = new PrintWriter(providerFile);
			buildReferrerCsvFilesContent(referrerWriter, providerWriter, listSuccess);
			referrerWriter.close();
			providerWriter.close();  
			fileMap.put("referrers_success.csv", referrerFile);
			fileMap.put("providers_success.csv", providerFile);
		}

		File referrerErrorFile = null;
		File providerErrorFile = null;
		if(listError.size() > 0) {
			referrerErrorFile = File.createTempFile("referrererror-", ".csv");
			providerErrorFile = File.createTempFile("providererror-", ".csv");
			PrintWriter referrerErrorWriter = new PrintWriter(referrerErrorFile);
			PrintWriter providerErrorWriter = new PrintWriter(providerErrorFile);
			buildReferrerCsvFilesContent(referrerErrorWriter, providerErrorWriter, listError);
			referrerErrorWriter.close();
			providerErrorWriter.close();  
			fileMap.put("referrers_error.csv", referrerErrorFile);
			fileMap.put("providers_error.csv", providerErrorFile);
		}

		// Append uids into subject
		String postfix = buildResultCsvEmailSubjectPostFix(listSuccess, listError);
		if("prod".equals(ACTIVE_PROFILE)) {
			emailService.sendWithFileMap(new String[] {ReferrerMailService.SUPPORT_ADDRESS},
					"I-MED Online 2.0 Visage account creation result Csv files" + postfix, "Please find attached csv files", fileMap);
		} else {
			emailService.sendWithFileMap(new String[] {"Hidehiro.Uehara@i-med.com.au", "georgia.wood@i-med.com.au", "erick.doust@i-med.com.au"},
					"I-MED Online 2.0 Visage account creation result Csv files" + postfix, "Please find attached csv files", fileMap);
		}

		if(listSuccess.size() > 0) {
			referrerFile.delete();
			providerFile.delete();
		}
		if(listError.size() > 0) {
			referrerErrorFile.delete();
			providerErrorFile.delete();
		}
	}

	public void makeAndShareVisageCsvFile(List<ReferrerAutoValidationEntity> created) throws Exception {
		logger.info("makeAndShareVisageCsvFile() list = " + created);
		if(created.size() > 0) {
			Date now = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");  
			logger.info("makeAndShareVisageCsvFile() creating csv to visage folder of " + SHARED_MOUNTED_FOLDER);
			File referrerFile = new File(SHARED_MOUNTED_FOLDER + "/to/referrer_user_export_" + formatter.format(now) + ".csv");
			referrerFile.createNewFile();
			// Read permission to all
			Set<PosixFilePermission> perms = new HashSet<>();
			perms.add(PosixFilePermission.OWNER_READ);
			perms.add(PosixFilePermission.OWNER_WRITE);
			perms.add(PosixFilePermission.OTHERS_READ);
			perms.add(PosixFilePermission.OTHERS_WRITE);
			perms.add(PosixFilePermission.GROUP_READ);
			perms.add(PosixFilePermission.GROUP_WRITE);
			Files.setPosixFilePermissions(referrerFile.toPath(), perms);

			PrintWriter referrerWriter = new PrintWriter(referrerFile);
			referrerWriter.println("username,role,email_address,family_name,given_names,middle_name,preferred_name,title,area_code,number,short_id,previous_names,provider_number");

			for(ReferrerAutoValidationEntity entity : created) {
				List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(entity.getUid());
				// Join provider #s with pipe
				String pnumstr = "";
				String postcode = "";
				if(provs.size() > 0) {
					List<String> pnums = provs.stream().map(p -> p.getProviderNumber()).collect(Collectors.toList());
					pnumstr = String.join("|", pnums);
					postcode = provs.get(0).getPracticePostcode();
				}
				String row = String.format("%s,Referrer,%s,%s,%s,,,,,%s,%s,,%s",
						entity.getUid(), nonQuote(entity.getEmail()), nonQuote(entity.getLastName()), nonQuote(entity.getFirstName()),
						nonQuote(entity.getMobile()), entity.getUid(), pnumstr);
				logger.info(row);
				referrerWriter.println(row);
			}

			referrerWriter.close();
		}
		else
		{
			logger.info("No account for visage csv");
		}
	}

	public void makeAndSendCsvEmails(List<ReferrerAutoValidationEntity> created) throws Exception {
		File referrerFile = File.createTempFile("referrers-", ".csv");
		File providerFile = File.createTempFile("providers-", ".csv");
		PrintWriter referrerWriter = new PrintWriter(referrerFile);
		PrintWriter providerWriter = new PrintWriter(providerFile);
		referrerWriter.println("uid,firstname,lastname,email,AHPRA#,BusinessUnit,phone,mobile,Contact,Filmless");
		providerWriter.println("uid,provider#,practiceName,practiceType,phone,fax,street,suburb,state,postcode");
		List<String> postfix = new ArrayList<>();
		for(ReferrerAutoValidationEntity entity : created) {
			postfix.add(entity.getUid());
			referrerWriter.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
					entity.getUid(), nonQuote(entity.getFirstName()), nonQuote(entity.getLastName()), nonQuote(entity.getEmail()),
					nonQuote(entity.getAhpra()), entity.getBusinessUnit(), nonQuote(entity.getPhone()), nonQuote(entity.getMobile()),
					nonQuote(entity.getContactAdvanced()), nonQuote(entity.getFilmless()) ));
			List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(entity.getUid());
			for(ReferrerProviderEntity provider : provs) {
				providerWriter.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
						provider.getUsername(), nonQuote(provider.getProviderNumber()), nonQuote(provider.getPracticeName()), provider.getPracticeType(), nonQuote(provider.getPracticePhone()), nonQuote(provider.getPracticeFax()),
						nonQuote(provider.getPracticeStreet()), nonQuote(provider.getPracticeSuburb()),nonQuote(provider.getPracticeState()), nonQuote(provider.getPracticePostcode()) ));
			}
		}
		referrerWriter.close();
		providerWriter.close();  

		Map<String, File> fileMap = new HashMap<>(2);
		fileMap.put("providers.csv", providerFile);
		fileMap.put("referrers.csv", referrerFile);

		String subject = "I-MED Online 2.0 New Referrer and Providers Csv files - " + postfix.stream().collect(Collectors.joining(", "));		
		if("prod".equals(ACTIVE_PROFILE)) {
			emailService.sendWithFileMap(new String[] {ReferrerMailService.SUPPORT_ADDRESS}, 
					subject, "Please find attached csv files", fileMap);
		} else {
			emailService.sendWithFileMap(new String[] {"Hidehiro.Uehara@i-med.com.au", "julie-ann.evans@i-med.com.au", "erick.doust@i-med.com.au", "georgia.wood@i-med.com.au"}, 
					subject, "Please find attached csv files", fileMap);
		}

		referrerFile.delete();
		providerFile.delete();
	}

	// TODO midnight task
	private void putCsvToSharedFolder(List<File> files) {
		// try {
		// JSch jsch = new JSch();
		// Session session = null;
		// session = jsch.getSession("huehara","imedtsweb01",22);
		// session.setPassword("Hello0123");
		// session.setConfig("StrictHostKeyChecking", "no");
		// session.connect();
		// ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
		// channel.connect();
		// channel.cd("/tmp");
		// for(File f : files) {
		// channel.put(new FileInputStream(f), f.getName()); // Name may common format
		// }
		// channel.disconnect();
		// session.disconnect();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}

	// TODO daily task csvToCreatedReferrers() notifyNewAccounts() real folder
	private File getCsvFromSharedFolder() {
		File file = null;
		// try {
		// JSch jsch = new JSch();
		// Session session = null;
		// session = jsch.getSession("huehara","imedtsweb01",22);
		// session.setPassword("Hello0123");
		// session.setConfig("StrictHostKeyChecking", "no");
		// session.connect();
		// ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
		// channel.connect();
		// channel.cd("/tmp");
		// file = channel.get("referrer.csv");
		// channel.disconnect();
		// session.disconnect();
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
		return file;
	}

	private List<ReferrerAutoValidationEntity> csvToCreatedReferrers(File file) {
		List<List<String>> records = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				records.add(Arrays.asList(values));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		List<ReferrerAutoValidationEntity> created = new ArrayList<>();
		for(List<String> column : records) {
			try {
				String uid = column.get(0);
				String [] splits = uid.split("row:");
				if(splits.length >= 2) {
					uid = splits[1].trim();
					logger.info("error csv format extracted uid");
				}
				logger.info("Retrieving uid with valid status from DB " + uid);
				ReferrerAutoValidationEntity entity = referrerAutoValidationRepository.findByUidAndValidationStatus(uid, VALIDATION_STATUS_VALID).get(0);
				created.add(entity);
			} catch (Exception ex) {
				logger.info("Skipping this referrer as not in valid list.");
				ex.printStackTrace();
			}
		}
		logger.info("csvToCreatedReferrers() size = " + created.size());
		return created;
	}

	private String nonQuote(String original) {
		return original != null ? original.replaceAll("\"", " ") : "";
	}

	private ExternalUser toExternalUser(ReferrerAutoValidationEntity entity, List<ReferrerProviderEntity> provs)
	{
		ExternalUser imedExternalUser = new ExternalUser();
		imedExternalUser.setAccountType(entity.getAccountType());
		imedExternalUser.setAhpraNumber(entity.getAhpra());
		final String password = Aes128StringEncodeUtil.decrypt(entity.getPasswordEncoded());
		imedExternalUser.setConfirmPassword(password);
		imedExternalUser.setPassword(password);
		imedExternalUser.setContactAdvanced(entity.getContactAdvanced());
		imedExternalUser.setEmail(entity.getEmail());
		imedExternalUser.setFilmless(entity.getFilmless());
		imedExternalUser.setFirstName(entity.getFirstName());
		imedExternalUser.setLastName(entity.getLastName());
		imedExternalUser.setMobile(entity.getMobile());
		imedExternalUser.setPreferredPhone(entity.getPhone());
		imedExternalUser.setUserid(entity.getUid());

		List<ExternalPractice> praclist = new ArrayList<>();
		for(ReferrerProviderEntity ent : provs) {
			ExternalPractice pra = new ExternalPractice();
			pra.setPracticeAddress(ent.getPracticeAddress());
			pra.setPracticeFax(ent.getPracticeFax());
			pra.setPracticeName(ent.getPracticeName());
			pra.setPracticePhone(ent.getPracticePhone());
			pra.setPracticePostcode(ent.getPracticePostcode());
			pra.setPracticeState(ent.getPracticeState());
			pra.setPracticeStreet(ent.getPracticeStreet());
			pra.setPracticeSuburb(ent.getPracticeSuburb());
			pra.setProviderNumber(ent.getProviderNumber());
			pra.setPracticeType(ent.getPracticeType());
			praclist.add(pra);
		}
		imedExternalUser.setPractices(praclist);
		return imedExternalUser;
	}

	private boolean validAhpra(final ReferrerAutoValidationEntity entity, final AhpraDetails ahpraDetails, final List<ReferrerProviderEntity> provs) {
		boolean isValid = false;

		isValid = compareIngoringSpaces("Registered", ahpraDetails.getRegistrationDetails().getRegistrationStatus()) &&
				containsIgnoringSpaces(ahpraDetails.getName(), entity.getFirstName()) &&
				containsIgnoringSpaces(ahpraDetails.getName(), entity.getLastName());

		if(isValid) {
			isValid = false; // Clear for next validation
			if(provs.size() > 0) {
				for(ReferrerProviderEntity provider : provs) {
					if(compareIngoringSpaces(ahpraDetails.getPrincipalPlaceOfPractice().getState(), provider.getPracticeState())) {
						isValid = true;
						logger.info("Found matching provider to AHPRA. Provider# is " + provider.getProviderNumber());
						break;
					}
				}
				logger.info("AHPRA and provider match ? " + isValid);
			}
			else
			{
				logger.info("No prover in DB");
			}
		}
		else
		{
			logger.info("Applicant name is wrong or AHPRA status is not Registered");
		}

		return isValid;
	}
	
	
	/**
	 * Check account and providers are created
	 */
	public List<ReferrerAutoValidationEntity> filterToVisageAccoutCompleted(List<ReferrerAutoValidationEntity> list) {
		logger.info("filterToVisageAccoutCompleted() original valid list size " + list.size());
		List<ReferrerAutoValidationEntity> visageList = new ArrayList<>();
		for(ReferrerAutoValidationEntity entity : list) {
			String username = entity.getUid();
			List<ReferrerProviderEntity> providers = referrerProviderJpaRepository.findByUsername(username);
			List<String> pnums = providers.stream().map(p -> p.getProviderNumber()).collect(Collectors.toList());
			if(visageCheckerService.isAccountWithProviders(username, pnums)) {
				logger.info("filterToVisageAccoutCompleted() visage account has been completed for {} {}", username, pnums);
				visageList.add(entity);
			}
		}
		logger.info("filterToVisageAccoutCompleted() visageList size " + visageList.size());
		return visageList;
	}

	/**
	 * Welcome email to referrer and notify email to CRM, called by scheduler with 1 day delay or send by scheduler
	 * @return
	 */
	public void notifyNewAccounts(List<ReferrerAutoValidationEntity> list) {
		logger.info("notifyNewAccounts() # of target list is " + list.size());
		for(ReferrerAutoValidationEntity entity : list) {
			// Check temporal password by CRMCreate
			String temppswd = "";
			if(PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE.equals(entity.getOrigin())) {
				temppswd = auditService.getRawPasswordForCrmCreate(entity.getUid());
				logger.info("CRMCreate got temporal password for welcome email");
			}
			// Welcome email
			if("prod".equals(ACTIVE_PROFILE)) {
				emailService.emailAccountApproved(entity.getFirstName(), entity.getLastName(), entity.getUid(), entity.getEmail(), temppswd);
			} else {
				emailService.emailAccountApproved(entity.getFirstName(), entity.getLastName(), entity.getUid(), "Hidehiro.Uehara@i-med.com.au", temppswd);
			}

			List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(entity.getUid());
			if(provs.size() > 0) {
				CrmProfileEntity crm = getCrm(provs.get(0).getPracticePostcode());
				logger.info("notifyNewAccounts() Send email to CRM " + crm);
				if("prod".equals(ACTIVE_PROFILE)) {
					try {
						String [] toCrm = crm != null ? new String [] {crm.getEmail()} : new String [0];
						emailService.emailNotifyNewReferrer(toCrm,
								new String [] {"Julie-Ann.Evans@i-med.com.au"}, toExternalUser(entity, provs));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					emailService.emailNotifyNewReferrer(new String [] {"Hidehiro.Uehara@i-med.com.au"},
							new String [] {"Hidehiro.Uehara@i-med.com.au"}, toExternalUser(entity, provs));
				}
			}
			entity.setNotifyAt(new Date());
			updateValidationStatus(entity, VALIDATION_STATUS_NOTIFIED, "Notified " + entity.getEmail());
		}
	}

	private CrmProfileEntity getCrm(final String postcode) {
		CrmProfileEntity profile = null;
		if(postcode != null && postcode.length() > 0) {
			List<CrmPostcodeEntity> plist = crmPostcodeRepository.findByPostcode(postcode);
			if(plist.size() > 0) {
				List<CrmProfileEntity> crmList = crmProfileRepository.findByName(plist.get(0).getName());
				profile = crmList.size() > 0 ? crmList.get(0) : null;
			}
		}
		logger.info("getCrm() Found crm for postcode {} ? {}", postcode, profile);
		return profile;
	}

	private List<String> poviderNumbersTakenInVisage(final ExternalUser externalUser) {
		List<String> takens = new ArrayList<>(0);
		for(ExternalPractice practice : externalUser.getPractices()) {
			if(visageCheckerService.isProviderNumberTaken(practice.getProviderNumber())) {
				logger.info("Provider number " + practice.getProviderNumber() + " is in Visage");
				takens.add(practice.getProviderNumber());
			}
		}
		return takens;
	}

	//
	// TODO CRM Admin methods to port around csv exchange as well
	//
	/**
	 * Called by /create crm admin cntler
	 * @return
	 */
	public int markCrmCreate(final String referrerUid) {
		int validationId = 0;
		boolean isSet = updateReferrerCrmActionIfStating(referrerUid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
		if(!isSet) {
			List<ReferrerAutoValidationEntity> list = referrerAutoValidationRepository.findByUidAndValidationStatus(referrerUid, PortalConstant.VALIDATION_STATUS_PASSED);
			if(list.size() > 0) {
				// Should be only one
				ReferrerAutoValidationEntity entity = list.get(0);
				validationId = entity.getId();
				logger.info("Auto validation table id found " + validationId);
				entity.setOrigin(PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
				referrerAutoValidationRepository.saveAndFlush(entity);
			} else {
				logger.info("referrer is not auto validation table as passed status.");
			}
		}
		return validationId;
	}

	/**
	 * Called on validateOnDb() isValid or not after create account stage/live
	 * @param origin
	 * @param uid
	 * @param isStage
	 * @throws Exception
	 */
	protected void setCrmAdminCreate(final String origin, final String uid, boolean isStage) throws Exception {
		if(PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE.equals(origin)) {
			if(isStage) {
				updateReferrerCrmActionIfStating(uid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
			} else {
				updateReferrerCrmAction(uid, PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_CREATE);
			}
		}
	}

	public AutoValidationResult validateOnLdapStaging(final String uid, final String newuid, final String bu) throws Exception {
		// Find details
		LdapQuery query = query()
				.attributes("ibm-pwdAccountLocked", "cn", "uid", "givenName", "sn", "mail", "ahpra", "createTimeStamp", "BusinessUnit", "employeeType", "homePhone", "mobile", "physicalDeliveryOfficeName")
				.where("uid").is(uid);
		List<LdapUserDetails> list = getReferrerStagingLdapTemplate().search(query, new LdapUserDetailsUserAttributeMapper());

		AutoValidationResult results = new AutoValidationResult(false, "");
		if(list.size() > 0) {
			List<ReferrerProviderEntity> pracs = referrerProviderJpaRepository.findByUsername(list.get(0).getUid());
			ExternalUser externalUser = toExternalUser(list.get(0), pracs);
			// Validate all
			results = validateAll(externalUser, pracs);

			if(results.isValid()) {
				logger.info("Creating auto validatin DB entry with valid status");
				approveUser(uid, newuid, bu);
				finaliseUser(StringUtils.isBlank(newuid) ? uid : newuid);
				saveActivationDb(externalUser);
				// Need to adjust to csv exchange future TODO
				ReferrerAutoValidationEntity entity = toAutoValidationEntity(externalUser, bu);
				entity.setAccountAt(new Date());
				updateValidationStatus(entity, VALIDATION_STATUS_VALID, VALIDATION_MSG_ACCOUNT_CREATED);
				makeAndSendCsvEmails(Collections.singletonList(entity));
			} else {
				logger.info("Just putting validating flag into LDAP account");
				updateCrmValidating(uid, bu, true);
			}

		} else {
			logger.info("Not a staging user " + uid);
		}
		logger.info("validateOnLdapStaging() result = " + results.isValid() + ", " + results.getMsg());
		return results;
	}

	protected ReferrerAutoValidationEntity toAutoValidationEntity(final ExternalUser externalUser, final String bu) {
		ReferrerAutoValidationEntity entity = new ReferrerAutoValidationEntity();
		entity.setAccountAt(new Date());
		entity.setAccountType(externalUser.getAccountType());
		entity.setAhpra(externalUser.getAhpraNumber());
		entity.setApplyAt(new Date());
		entity.setBusinessUnit(bu);
		entity.setContactAdvanced(externalUser.getContactAdvanced());
		entity.setEmail(externalUser.getEmail());
		entity.setFilmless(externalUser.getFilmless());
		entity.setFirstName(externalUser.getFirstName());
		entity.setLastName(externalUser.getLastName());
		entity.setMobile(externalUser.getMobile());
		entity.setPasswordEncoded(externalUser.getPassword());
		entity.setPhone(externalUser.getPreferredPhone());
		entity.setUid(externalUser.getUserid());
		entity.setOrigin(PortalConstant.PARAM_ATTR_VALUE_CRM_ACTION_APPROVE);
		return entity;
	}

	protected ExternalUser toExternalUser(final LdapUserDetails details, List<ReferrerProviderEntity> pracs) {
		// This part is common logic TODO
		List<ExternalPractice> praclist = new ArrayList<>();
		for(ReferrerProviderEntity ent : pracs) {
			ExternalPractice pra = new ExternalPractice();
			pra.setPracticeAddress(ent.getPracticeAddress());
			pra.setPracticeFax(ent.getPracticeFax());
			pra.setPracticeName(ent.getPracticeName());
			pra.setPracticePhone(ent.getPracticePhone());
			pra.setPracticePostcode(ent.getPracticePostcode());
			pra.setPracticeState(ent.getPracticeState());
			pra.setPracticeStreet(ent.getPracticeStreet());
			pra.setPracticeSuburb(ent.getPracticeSuburb());
			pra.setProviderNumber(ent.getProviderNumber());
			pra.setPracticeType(ent.getPracticeType());
			praclist.add(pra);
		}

		ExternalUser user = new ExternalUser();
		user.setAccountType(details.getAccountType());
		user.setAhpraNumber(details.getAhpra());
		user.setConfirmPassword(details.getPassword());
		user.setPassword(details.getPassword());
		user.setContactAdvanced("Yes");
		user.setEmail(details.getEmail());
		user.setFilmless("Yes");
		user.setFirstName(details.getGivenName());
		user.setLastName(details.getSurname());
		user.setMobile(details.getMobile());
		user.setOs("");
		user.setPractices(praclist);
		user.setPreferredPhone(details.getMobile());
		user.setUserid(details.getUid());
		logger.info("toExternalUser() " + user);
		return user;
	}

	protected AutoValidationResult validateAll(ExternalUser imedExternalUser, List<ReferrerProviderEntity> pracs) {
		String msg = "";
		boolean isValid = false;

		// This part is similar but checks in ldap to skip self
		if(visageCheckerService.isUsernameTaken(imedExternalUser.getUserid())) {
			msg = "username already in Visage";
		}else if(visageCheckerService.isAhpraTaken(imedExternalUser.getUserid())) {
			msg = "AHPRA " + imedExternalUser.getAhpraNumber() + " already in Visage";
		}else if(poviderNumbersTakenInVisage(imedExternalUser).size() > 0) {
			msg = "Provider number already in Visage";
		}else if(!validMedicareProviders(imedExternalUser)) {
			msg = "Practice details do not match to medicare provider DB";
		}else if(pacsAccountsExist(imedExternalUser)) {
			msg = "The applicant may already has PACS account";
		}else {
			isValid = true;
		}

		if(isValid) {
			// This part is common as validate on DB() TODO
			AhpraDetails [] ahpras = this.ahpraBotService.findByNumberRetry(imedExternalUser.getAhpraNumber());
			if(ahpras.length > 0) {
				AhpraDetails ahpraDetails = ahpras[0]; // Should be one
				isValid = compareIngoringSpaces("Registered", ahpraDetails.getRegistrationDetails().getRegistrationStatus()) &&
						containsIgnoringSpaces(ahpraDetails.getName(), imedExternalUser.getFirstName()) &&
						containsIgnoringSpaces(ahpraDetails.getName(), imedExternalUser.getLastName());

				if(isValid) {
					isValid = false; // Clear for next validation
					if(pracs.size() > 0) {
						for(ReferrerProviderEntity provider : pracs) {
							if(compareIngoringSpaces(ahpraDetails.getPrincipalPlaceOfPractice().getPostcode(), provider.getPracticePostcode()) &&
									compareIngoringSpaces(ahpraDetails.getPrincipalPlaceOfPractice().getState(), provider.getPracticeState())) {
								isValid = true;
								logger.info("Found matching provider to AHPRA. Provider# is " + provider.getProviderNumber());
								break;
							}
						}
						logger.info("AHPRA and provider match ? " + isValid);
					}
					else
					{
						logger.info("No prover in DB");
					}
				}
				else
				{
					msg = "AHPRA details don't match to ahpra.gov.au";
					logger.info("Applicant name is wrong or AHPRA status is not Registered");
				}
			} else {
				msg = "AHPRA details don't match to ahpra.gov.au";
				logger.info("validateOnDb() ahpra not available in ahpra.gov.au : " + imedExternalUser.getAhpraNumber());
			}
		}

		return new AutoValidationResult(isValid, msg);
	}
}