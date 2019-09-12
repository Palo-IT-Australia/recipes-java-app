package au.com.imed.portal.referrer.referrerportal.ldap;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;

import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;

public abstract class ABasicAccountService {
	@Value("${imed.ldap.url}")
	private String LDAP_URL;

	//
	// LDAP
	//
	protected LdapTemplate getReferrerLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getReferrerStagingLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Staging,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getApplicationsLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Applications,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getGlobalLdapTemplate() throws Exception {
		return getLdapTemplate("dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getBusinessUnitLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Business Units,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getImedPacsLdapTemplate() throws Exception {
		return getLdapTemplate("ou=IMED PACS Users,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getPacsLdapTemplate() throws Exception {
		return getLdapTemplate("ou=PACS Users,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getStagePacsLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Staging PACS Users,dc=mia,dc=net,dc=au");
	}

	private LdapTemplate getLdapTemplate(final String baseDomain) throws Exception {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setBase(baseDomain);
		contextSource.setUserDn("cn=root");
		contextSource.setPassword("794bo3HAST");
		contextSource.setUrl(LDAP_URL);
		contextSource.afterPropertiesSet();

		return new LdapTemplate(contextSource);
	}

	protected class PersonContextMapper extends AbstractContextMapper<Name> {
		public Name doMapFromContext(DirContextOperations context) {
			return context.getDn();
		}
	}

	protected class AccountDetailAttributeMapper implements AttributesMapper<AccountDetail> {
		@Override
		public AccountDetail mapFromAttributes(Attributes attrs) throws NamingException {
			AccountDetail detail = new AccountDetail();
			detail.setEmail(attrs.get("mail") != null ? attrs.get("mail").get(0).toString() : "");
			detail.setName(attrs.get("cn") != null ? attrs.get("cn").get(0).toString() : "");
			detail.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get(0).toString() : "");
			detail.setUid(attrs.get("uid") != null ? attrs.get("uid").get(0).toString() : "");
			return detail;
		}
	}

	protected class StageUserAttributeMapper implements AttributesMapper<StageUser> {
		@Override
		public StageUser mapFromAttributes(Attributes attrs) throws NamingException {
			StageUser detail = new StageUser();
			detail.setEmail(attrs.get("mail") != null ? attrs.get("mail").get(0).toString() : "");
			detail.setGivenName(attrs.get("givenName") != null ? attrs.get("givenName").get(0).toString() : "");
			detail.setSurname(attrs.get("sn") != null ? attrs.get("sn").get(0).toString() : "");
			detail.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get(0).toString() : "");
			detail.setUid(attrs.get("uid") != null ? attrs.get("uid").get(0).toString() : "");
			detail.setCn(attrs.get("cn") != null ? attrs.get("cn").get(0).toString() : "");
			detail.setAhpra(attrs.get("ahpra") != null ? attrs.get("ahpra").get(0).toString() : "");
			detail.setAccountType(attrs.get("employeeType") != null ? attrs.get("employeeType").get(0).toString() : "");
			detail.setBusinessUnit(
					attrs.get("BusinessUnit") != null ? attrs.get("BusinessUnit").get(0).toString() : "");
			return detail;
		}
	}
}
