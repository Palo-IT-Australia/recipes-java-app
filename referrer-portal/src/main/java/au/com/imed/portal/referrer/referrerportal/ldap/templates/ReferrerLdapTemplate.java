package au.com.imed.portal.referrer.referrerportal.ldap.templates;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReferrerLdapTemplate extends BaseLdapTemplate {

    @Value("${imed.ldap.url}")
    private String ldapUrl;

    @Value("${imed.ldap.userdn}")
    private String ldapUserDn;

    @Value("${imed.ldap.password}")
    private String ldapPassword;

    @Override
    String getBaseDomain() {
        return PortalConstant.DOMAIN_REFERRER;
    }

    @Override
    String getUserDn() {
        return ldapUserDn;
    }

    @Override
    String getLdapPassword() {
        return ldapPassword;
    }

    @Override
    String getLdapUrl() {
        return ldapUrl;
    }


    @Override
    public String getSearchQuery(String uid) {
        return String.format("uid=%s", uid);
    }
}
