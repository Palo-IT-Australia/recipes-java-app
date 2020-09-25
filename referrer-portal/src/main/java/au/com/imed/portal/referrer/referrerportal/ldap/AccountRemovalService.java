package au.com.imed.portal.referrer.referrerportal.ldap;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.rest.cleanup.model.GlobalLdapAccount;

@Service
public class AccountRemovalService extends ABasicAccountService {
	private Logger logger = LoggerFactory.getLogger(AccountRemovalService.class);

	// canRemove() use this order
	private final static String [] REMOVABLE_OUS = new String [] {
			PortalConstant.DOMAIN_IMED_PACS_USERS.split(",")[0], PortalConstant.DOMAIN_PACS_USERS.split(",")[0],
			PortalConstant.DOMAIN_REFERRER.split(",")[0], PortalConstant.DOMAIN_STAGING_PACS_USERS.split(",")[0]
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
				.attributes("cn", "uid", "givenName", "sn", "mail", "adDn")
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
			if(dn.contains(REMOVABLE_OUS[i]) && !acnt.getMail().contains("@i-med.com.au")) 
			{
				if(i < 2) {
					can = acnt.getAddn().endsWith(ADDN_REFERRER);
					if(can) {
						break;
					}
				} else {
					can = true;
					break;
				}
			}
		}
		return can;
	}
	
	protected String toType(final String dn) {
		String type = "Others";
		for(int i = 0; i < REMOVABLE_OUS.length; i ++) {
			if(dn.contains(REMOVABLE_OUS[i])) {
				type = DOMAIN_NAMES[i];
				break;
			}
		}
		return type;
	}
	
	
}
