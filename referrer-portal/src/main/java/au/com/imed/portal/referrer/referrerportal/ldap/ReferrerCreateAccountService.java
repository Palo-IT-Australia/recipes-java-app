package au.com.imed.portal.referrer.referrerportal.ldap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.IMED_TEMPORAL_PASSWORD;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import au.com.imed.common.active.directory.manager.ImedActiveDirectoryLdapManager;
import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.model.ExternalPractice;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.utils.ModelUtil;

@Service
public class ReferrerCreateAccountService extends ReferrerAccountService {
	private Logger logger = LoggerFactory.getLogger(ReferrerCreateAccountService.class);
	
	@Value("${spring.profiles.active}")
	private String ACTIVE_PROFILE;
	
	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;
	
	@Autowired
	private ReferrerMailService emailService;
	
	//@Autowired
	//private ReferrerMailService emailService;
	
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

				final String proposedUid = imedExternalUser.getUserid(); 
				if(autoUidRequired(imedExternalUser)) 
				{
					imedExternalUser.setUserid(generateUsername(imedExternalUser));
				}

				try {
					createPortalStagingUser(imedExternalUser, proposedUid);
					saveProviders(imedExternalUser);
					resultMap.put(MODEL_KEY_SUCCESS_MSG, "Thank you for registering for I-MED Online 2.0! Your application will be processed within one business day. You will receive and email confirmation with your username once complete.");
					if("prod".equals(ACTIVE_PROFILE)) {
						emailService.emailNewUser(imedExternalUser);
					}
				}
				catch (Exception ex) 
				{
					ex.printStackTrace();
					resultMap.put(MODEL_KEY_ERROR_MSG, "Failed to register the user. Please check password includes only valid characters.");
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
		createReferrerUser(getReferrerStagingLdapTemplate(), imedExternalUser, proposedUid);
	}
	
	/**
	 * Admin tool can create referrer account without approval process
	 * @param imedExternalUser
	 * @param proposedUid
	 * @throws Exception
	 */
	public void createPortalReferrerUser(final ExternalUser imedExternalUser, final String proposedUid) throws Exception {
		createReferrerUser(getReferrerLdapTemplate(), imedExternalUser, proposedUid);
	}
	
	private void createReferrerUser(LdapTemplate ldapTemplate, final ExternalUser imedExternalUser, final String proposedUid) throws Exception {
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
		List<ExternalPractice> practices = imedExternalUser.getPractices();
		if(practices != null && practices.size() > 0) {
			context.setAttributeValue("physicalDeliveryOfficeName", practices.get(0).getPracticeAddress());
		} else {
			context.setAttributeValue("physicalDeliveryOfficeName", imedExternalUser.getPreferredPhone());			
		}
		context.setAttributeValue(PortalConstant.PARAM_ATTR_ACC_LOCKED, "true");

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
		int i = 1;
		// Visage has no fuzzy search
		// TODO
//		while(visagenameistaken?) {
//			finalName = finalName + (i++);
//		}
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
}
