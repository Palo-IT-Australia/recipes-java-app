package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAutoValidationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerActivationJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerAutoValidationRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.UserPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.PatientHistoryEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.PatientHistoryJPARepository;
import au.com.imed.portal.referrer.referrerportal.rest.cleanup.model.GlobalLdapAccount;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class AccountRemovalService extends ABasicAccountService {
	private Logger logger = LoggerFactory.getLogger(AccountRemovalService.class);

	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;

	@Autowired
	private ReferrerAutoValidationRepository autoValidationRepository;

	@Autowired
	private ReferrerActivationJpaRepository activationRepository;

	@Autowired
	private UserPreferencesJPARepository preferencesRepository;

	@Autowired
	private PatientHistoryJPARepository patientHistoryRepository;

	private final static String OU_REFERRER = PortalConstant.DOMAIN_REFERRER.split(",")[0];
	// canRemove() use this order
	private final static String[] REMOVABLE_OUS = new String[]{
			PortalConstant.DOMAIN_IMED_PACS_USERS.split(",")[0], PortalConstant.DOMAIN_PACS_USERS.split(",")[0],
			OU_REFERRER, PortalConstant.DOMAIN_STAGING_PACS_USERS.split(",")[0]
	};
	// Order must match REMOVABLE_DOMAINS
	private final static String [] DOMAIN_NAMES = new String [] {
			"IMED PACS", "PACS",
			"Referrer", "Placeholder"
	};
	private static final String ADDN_REFERRER = "ou=referrers,ou=portal,ou=applications,dc=mia,dc=net,dc=au";

	public void removeGlobalAccount(final String dn) throws Exception {
		logger.info("removeAccount() {} ", dn);
		getGlobalLdapTemplate().unbind(dn);
	}

	public List<GlobalLdapAccount> findGlobalAccounts(final String word) throws Exception {
		LdapQuery query = query()
				.attributes("cn", "uid", "givenName", "sn", "mail", "adDn", PortalConstant.PARAM_ATTR_FINALIZING_PAGER)
				.where("uid").is(word)
				.or("mail").is(word);
		return getGlobalLdapTemplate().search(query, new GlobalAccountContextMapper());
	}

	protected class GlobalAccountContextMapper extends AbstractContextMapper<GlobalLdapAccount> {
		public GlobalLdapAccount doMapFromContext(DirContextOperations context) {
			GlobalLdapAccount acnt = new GlobalLdapAccount();
			try {
				Attributes attrs = context.getAttributes();
				acnt.setUid(atrString(attrs.get("uid")));
				acnt.setCn(atrString(attrs.get("cn")));
				acnt.setGivenName(atrString(attrs.get("givenName")));
				acnt.setSn(atrString(attrs.get("sn")));
				acnt.setMail(atrString(attrs.get("mail")));
				acnt.setAddn(atrString(attrs.get("adDn")));
				acnt.setStage(atrString(attrs.get(PortalConstant.PARAM_ATTR_FINALIZING_PAGER)));
				acnt.setDn(context.getDn().toString());
				acnt.setCanRemove(canRemove(acnt));
				acnt.setType(toType(acnt.getDn()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return acnt;
		}
	}

	protected String atrString(Attribute atr) {
		String val = "";
		try {
			val = (atr != null && atr.get(0) != null) ? atr.get(0).toString() : "";
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return val;
	}

	protected boolean canRemove(final GlobalLdapAccount acnt) {
		boolean can = false;
		String dn = acnt.getDn();
		for(int i = 0; i < REMOVABLE_OUS.length; i ++) {
			// non internal account and not under validation
			if (dn.contains(REMOVABLE_OUS[i]) && !acnt.getMail().contains("@i-med.com.au") && StringUtil.isBlank(acnt.getStage())) {
				if (i < 2) { // PACSes
					can = acnt.getAddn().endsWith(ADDN_REFERRER);
				} else if (i == 2) { // Referrer
					List<ReferrerAutoValidationEntity> autos = autoValidationRepository.findByUidAndValidationStatusNot(acnt.getUid(), PortalConstant.VALIDATION_STATUS_INVALID);
					if (autos.size() > 0) { // should be only one valid status
						String sts = autos.get(0).getValidationStatus();
						logger.info("canRemove() non-invalid validation status " + sts);
						// Notified status can be removed
						can = PortalConstant.VALIDATION_STATUS_NOTIFIED.equalsIgnoreCase(sts);
					} else { // completed account
						can = true;
					}
				} else {
					can = true;
				}
				break;
			}
		}
		return can;
	}

	protected String toType(final String dn) {
		String type = "Other/Validating";
		for(int i = 0; i < REMOVABLE_OUS.length; i ++) {
			if(dn.contains(REMOVABLE_OUS[i])) {
				type = DOMAIN_NAMES[i];
				break;
			}
		}
		return type;
	}

	public void cleanupDb(final GlobalLdapAccount acnt) {
		// On removing referrer account
		if(acnt.getDn().contains(OU_REFERRER) && acnt.isCanRemove()) {
			String uid = acnt.getUid();

			List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(uid);
			if(provs.size() > 0) {
				logger.info("cleanupDB() removing providers");
				referrerProviderJpaRepository.deleteAll(provs);
			}

			List<ReferrerAutoValidationEntity> autos = autoValidationRepository.findByUidAndValidationStatusNot(uid, PortalConstant.VALIDATION_STATUS_INVALID);
			if(autos.size() > 0) {
				logger.info("cleanupDB() removing auto validations");
				autoValidationRepository.delete(autos.get(0));
			}

			List<ReferrerActivationEntity> activations = activationRepository.findByUid(uid);
			if (activations.size() > 0) {
				logger.info("cleanupDB() removing activations");
				activationRepository.delete(activations.get(0));
			}

			List<UserPreferencesEntity> prefs = preferencesRepository.findByUsername(uid);
			if (prefs.size() > 0) {
				logger.info("cleanupDB() removing preferences");
				preferencesRepository.delete(prefs.get(0));
			}

			List<PatientHistoryEntity> hists = patientHistoryRepository.findByUsername(uid);
			if (hists.size() > 0) {
				logger.info("cleanupDB() removing histories");
				patientHistoryRepository.delete(hists.get(0));
			}
		}
	}

}
