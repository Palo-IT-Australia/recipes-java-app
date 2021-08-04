package au.com.imed.portal.referrer.referrerportal.ldap.templates;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdLdapTemplate extends BaseLdapTemplate {

    @Value("${imed.ldap.ad.url}")
    private String ldapUrl;

    @Value("${imed.ldap.ad.userDN}")
    private String ldapUserDn;

    @Value("${imed.ldap.ad.password}")
    private String ldapPassword;

    @Override
    String getBaseDomain() {
        return PortalConstant.DOMAIN_BUSINESS_UNITS;
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
        return String.format("samAccountName=%s", uid);
    }
}
