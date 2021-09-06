package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.common.util.IgnoreCertFactoryUtil;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.model.StageUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.client.RestTemplate;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Map;

public abstract class ABasicAccountService {

	@Value("${imed.ldap.url}")
	private String ldapUrl;

	@Value("${imed.ldap.userdn}")
	private String ldapUserDn;

	@Value("${imed.ldap.password}")
	private String ldapPassword;

	//
	// Rest
	//
	public <S> ResponseEntity<S> doGet(String url, Map<String, String> requestParams, Class<S> clz) {
		HttpComponentsClientHttpRequestFactory factory = null;
		try {
			factory = IgnoreCertFactoryUtil.createFactory();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RestTemplate restTemplate = (factory == null) ? new RestTemplate() : new RestTemplate(factory);
		return restTemplate.getForEntity(url, clz, requestParams);
	}

	//
	// LDAP
	//
	protected LdapTemplate getReferrerLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_REFERRER);
	}

	protected LdapTemplate getReferrerStagingLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_STAGING);
	}

	protected LdapTemplate getPortalLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_PORTAL);
	}

	protected LdapTemplate getPatientLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_PATIENTS);
	}

	protected LdapTemplate getApplicationsLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_APPLICATIONS);
	}

	protected LdapTemplate getGlobalLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_GLOBAL);
	}

	protected LdapTemplate getBusinessUnitLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_BUSINESS_UNITS);
	}

	protected LdapTemplate getStagePacsLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_STAGING_PACS_USERS);
	}

	protected LdapTemplate getImedPacsLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_IMED_PACS_USERS);
	}

	protected LdapTemplate getPacsLdapTemplate() throws Exception {
		return getLdapTemplate(PortalConstant.DOMAIN_PACS_USERS);
	}

	private LdapTemplate getLdapTemplate(final String baseDomain) throws Exception {
		LdapContextSource contextSource = getLdapContextSource(baseDomain);

		return new LdapTemplate(contextSource);
	}

	private LdapContextSource getLdapContextSource(String baseDomain) {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setBase(baseDomain);
		contextSource.setUserDn(ldapUserDn);
		contextSource.setPassword(ldapPassword);
		contextSource.setUrl(ldapUrl);
		contextSource.afterPropertiesSet();
		return contextSource;
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
			detail.setLastName(attrs.get("sn") != null ? attrs.get("sn").get(0).toString() : "");
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

	protected class LdapUserDetailsUserAttributeMapper implements AttributesMapper<LdapUserDetails> {
		@Override
		public LdapUserDetails mapFromAttributes(Attributes attrs) throws NamingException {
			LdapUserDetails detail = new LdapUserDetails();
			detail.setEmail(attrs.get("mail") != null ? attrs.get("mail").get(0).toString() : "");
			detail.setGivenName(attrs.get("givenName") != null ? attrs.get("givenName").get(0).toString() : "");
			detail.setSurname(attrs.get("sn") != null ? attrs.get("sn").get(0).toString() : "");
			detail.setMobile(attrs.get("mobile") != null ? attrs.get("mobile").get(0).toString() : "");
			detail.setUid(attrs.get("uid") != null ? attrs.get("uid").get(0).toString() : "");
			detail.setCn(attrs.get("cn") != null ? attrs.get("cn").get(0).toString() : "");
			detail.setAhpra(attrs.get("ahpra") != null ? attrs.get("ahpra").get(0).toString() : "");
			detail.setLocked(attrs.get(PortalConstant.PARAM_ATTR_ACC_LOCKED) != null
					? "true".equals(attrs.get(PortalConstant.PARAM_ATTR_ACC_LOCKED).get(0).toString())
					: false);
			detail.setAccountType(attrs.get("employeeType") != null ? attrs.get("employeeType").get(0).toString() : "");
			detail.setBusinessUnit(
					attrs.get("BusinessUnit") != null ? attrs.get("BusinessUnit").get(0).toString() : "");
			return detail;
		}
	}
}
