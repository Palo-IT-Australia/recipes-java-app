package au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.web.client.RestTemplate;

import au.com.imed.portal.referrer.referrerportal.common.util.IgnoreCertFactoryUtil;

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
		return getLdapTemplate("ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
	}

	protected LdapTemplate getPatientLdapTemplate() throws Exception {
		return getLdapTemplate("ou=Patients,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
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

	// VueMotion base dn for email uniqueness
	protected LdapTemplate getPacsUsersLdapTemplate() throws Exception {
		return getLdapTemplate("OU=PACS Users,dc=mia,dc=net,dc=au");
	}

	private LdapTemplate getLdapTemplate(final String baseDomain) throws Exception {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setBase(baseDomain);
		contextSource.setUserDn(ldapUserDn);
		contextSource.setPassword(ldapPassword);
		contextSource.setUrl(ldapUrl);
		contextSource.afterPropertiesSet();

		return new LdapTemplate(contextSource);
	}
}
