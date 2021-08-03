package au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates;

import au.com.imed.portal.referrer.referrerportal.common.PortalConstant;
import au.com.imed.portal.referrer.referrerportal.ldap.adapter.ImolAdLdapAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

@Component
public class AdLdapTemplate extends ImolAdLdapAdapter implements BaseLdapTemplate {

    @Override
    public String getBaseDomain() {
        return PortalConstant.DOMAIN_BUSINESS_UNITS;
    }

    @Override
    public LdapContextSource getContextSource(String baseDomain) {
        return super.getContextSource(baseDomain);
    }

    @Override
    public LdapTemplate getLdapTemplate() {
        return new LdapTemplate(getContextSource(getBaseDomain()));
    }

    @Override
    public String getSearchQuery() {
        return "sAMAccountName=";
    }
}
