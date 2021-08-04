package au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.naming.Name;

public interface BaseLdapTemplate {

    String getBaseDomain();

    LdapContextSource getContextSource(String baseDomain);

    LdapTemplate getLdapTemplate();

    String getSearchQuery(String uid);

    Name buildSearchDn(String uid);
}
