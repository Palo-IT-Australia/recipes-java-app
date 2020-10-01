package au.com.imed.portal.referrer.referrerportal.ldap;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAutoValidationEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.VisageRequestAuditEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerActivationJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerAutoValidationRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.ReferrerProviderJpaRepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.UserPreferencesJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.VisageRequestAuditJPARepository;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.PatientHistoryEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.history.repository.PatientHistoryJPARepository;
import au.com.imed.portal.referrer.referrerportal.rest.cleanup.model.GlobalLdapAccount;

@Service
public class AccountDeactivationService extends ABasicAccountService {
	private Logger logger = LoggerFactory.getLogger(AccountDeactivationService.class);

	@Autowired
	private ReferrerProviderJpaRepository referrerProviderJpaRepository;

	@Autowired
	private ReferrerAutoValidationRepository autoValidationRepository;

	@Autowired
	private ReferrerActivationJpaRepository activationRepository;
	
	@Autowired
	private UserPreferencesJPARepository preferencesRespository;
	
	@Autowired
	private PatientHistoryJPARepository patientHistoryRepository;
	
	@Autowired
	private VisageRequestAuditJPARepository auditRepository;
	
	public final static String INACTIVE_PREFIX = "inactive__";
	public final static String DOMAIN_INACTIVE = "ou=Inactive,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au";
	private static final String ROOT_DN_POSTFIX = ",dc=mia,dc=net,dc=au";
	private final static String OU_REFERRER = PortalConstant.DOMAIN_REFERRER.split(ROOT_DN_POSTFIX)[0];
	// canRemove() use this order
	private final static String [] REMOVABLE_OUS = new String [] {
			PortalConstant.DOMAIN_IMED_PACS_USERS.split(ROOT_DN_POSTFIX)[0], PortalConstant.DOMAIN_PACS_USERS.split(ROOT_DN_POSTFIX)[0],
			OU_REFERRER, PortalConstant.DOMAIN_STAGING_PACS_USERS.split(ROOT_DN_POSTFIX)[0],
			DOMAIN_INACTIVE.split(ROOT_DN_POSTFIX)[0]
	};
	// Order must match REMOVABLE_DOMAINS
	private final static String [] DOMAIN_NAMES = new String [] {
			"IMED PACS", "PACS",
			"Referrer", "Placeholder",
			"Deactivated"
	};
	private static final String ADDN_REFERRER = "ou=referrers,ou=portal,ou=applications,dc=mia,dc=net,dc=au";
	
	public void deactivate(List<GlobalLdapAccount> list) throws Exception {
		for(GlobalLdapAccount acnt : list) {
			String dn = acnt.getDn();
			for(int i = 0; i < REMOVABLE_OUS.length; i ++) {
				// non internal account and not under validation
				if(dn.contains(REMOVABLE_OUS[i])) 
				{
					if(i < 2) { // PACSes
						deactivateMail(acnt);
					} else if(i == 2) { // Referrer 
						deactivateReferrerAccount(acnt);
					} else if(i == 3) { // Placeholder
						removeGlobalAccount(dn);
					}
				}
			}
		}
	}
	
	protected void removeGlobalAccount(final String dn) throws Exception {
		logger.info("removeGlobalAccount() {} ", dn);
		getGlobalLdapTemplate().unbind(dn);
	}
	
	private void deactivateReferrerAccount(final GlobalLdapAccount acnt) throws Exception {
			LdapTemplate ldapTemplate = getGlobalLdapTemplate();
			List<ModificationItem> moditemList = new ArrayList<>(2);
			Attribute finAttr = new BasicAttribute("mail", INACTIVE_PREFIX + acnt.getMail());
			ModificationItem finItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, finAttr);
			moditemList.add(finItem);
			Attribute ahpraAttr = new BasicAttribute("AHPRA", INACTIVE_PREFIX + acnt.getAhpra());
			ModificationItem ahpraItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, ahpraAttr);
			moditemList.add(ahpraItem);
			ldapTemplate.modifyAttributes(acnt.getDn(), moditemList.toArray(new ModificationItem[moditemList.size()]));
			logger.info("deactivateReferrerAccount {}", acnt.getDn());
			
			final String newUid = "" + (new Random().nextInt((99999 - 10000) + 1) + 10000) + "_" + acnt.getUid();
			logger.info("deactivateReferrerAccount() moving " + acnt.getDn() + " into inactive with uid: " + newUid);
			ldapTemplate.rename(acnt.getDn(), "uid=" + newUid + "," + DOMAIN_INACTIVE.split(ROOT_DN_POSTFIX)[0]);
			
			updateDb(acnt.getUid(), newUid);
	}
	
	private void updateDb(final String uid, final String newUid) {
		// On deactivating referrer account
		List<VisageRequestAuditEntity> audits = auditRepository.findByUsername(uid);
		logger.info("updateDb() updating audit uid");
		for(VisageRequestAuditEntity entity : audits) {
			entity.setUsername(newUid);
			auditRepository.saveAndFlush(entity);
		}
		
		List<ReferrerProviderEntity> provs = referrerProviderJpaRepository.findByUsername(uid);
		logger.info("updateDb() updating providers");
		for(ReferrerProviderEntity prov : provs) {
			prov.setUsername(newUid);
			referrerProviderJpaRepository.saveAndFlush(prov);
		}

		List<ReferrerAutoValidationEntity> autos = autoValidationRepository.findByUidAndValidationStatus(uid, PortalConstant.VALIDATION_STATUS_NOTIFIED);
		logger.info("updateDb() updating auto validations");
		for(ReferrerAutoValidationEntity auto : autos) {
			auto.setUid(newUid);
			auto.setEmail(INACTIVE_PREFIX + auto.getEmail());
			auto.setAhpra(INACTIVE_PREFIX + auto.getAhpra());
			autoValidationRepository.saveAndFlush(auto);
		}

		List<ReferrerActivationEntity> activations = activationRepository.findByUid(uid);
		if(activations.size() > 0) {
			logger.info("updateDb() removing activations");
			activationRepository.delete(activations.get(0));
		}

		List<UserPreferencesEntity> prefs = preferencesRespository.findByUsername(uid);
		if(prefs.size() > 0) {
			logger.info("updateDb() removing preferences");
			preferencesRespository.delete(prefs.get(0));
		}

		List<PatientHistoryEntity> hists = patientHistoryRepository.findByUsername(uid);
		if(hists.size() > 0) {
			logger.info("updateDb() removing histories");
			patientHistoryRepository.delete(hists.get(0));
		}
	}
		
	private void deactivateMail(final GlobalLdapAccount acnt) throws Exception {
		List<ModificationItem> moditemList = new ArrayList<>(1);
		Attribute finAttr = new BasicAttribute("mail", INACTIVE_PREFIX + acnt.getMail());
		ModificationItem finItem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, finAttr);
		moditemList.add(finItem);
		getGlobalLdapTemplate().modifyAttributes(acnt.getDn(), moditemList.toArray(new ModificationItem[moditemList.size()]));
		logger.info("deactivatePacsEmail {}", acnt.getDn());
	}
		
	public List<GlobalLdapAccount> searchGlobalAccounts(final String word) throws Exception {
		LdapQuery query = query()
				.attributes("cn", "uid", "givenName", "sn", "mail", "AHPRA",  "adDn", PortalConstant.PARAM_ATTR_FINALIZING_PAGER)
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
				acnt.setAhpra(atrString(attrs.get("AHPRA")));
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
			val =  (atr != null && atr.get(0) != null) ? atr.get(0).toString() : "";	
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
			if(dn.contains(REMOVABLE_OUS[i]) && !acnt.getMail().contains("@i-med.com.au") && StringUtil.isBlank(acnt.getStage())) 
			{
				if(i < 2) { // PACSes
					can = acnt.getAddn().endsWith(ADDN_REFERRER) && !acnt.getMail().startsWith(INACTIVE_PREFIX);
				} else if(i == 2) { // Referrer 
					List<ReferrerAutoValidationEntity> autos = autoValidationRepository.findByUidAndValidationStatusNot(acnt.getUid(), PortalConstant.VALIDATION_STATUS_INVALID);
					if(autos.size() > 0) { // should be only one valid status
						String sts = autos.get(0).getValidationStatus();
						logger.info("canRemove() non-invalid validation status " + sts); 
						// Notified status can be removed
						can = PortalConstant.VALIDATION_STATUS_NOTIFIED.equalsIgnoreCase(sts);
					} else { // completed account			
						can = true;
					}
				} else if (i == 4) { // Inactive already
					can = false;
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
}
