package au.com.imed.portal.referrer.referrerportal.ldap.adapter;

import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.BaseLdapTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.LdapContextSource;

public abstract class ImolLdapAdapter implements BaseLdapTemplate {

    @Value("${imed.ldap.url}")
    private String ldapUrl;

    @Value("${imed.ldap.userdn}")
    private String ldapUserDn;

    @Value("${imed.ldap.password}")
    private String ldapPassword;

    @Override
    public LdapContextSource getContextSource(String baseDomain) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setBase(baseDomain);
        contextSource.setUserDn(ldapUserDn);
        contextSource.setPassword(ldapPassword);
        contextSource.setUrl(ldapUrl);
        contextSource.afterPropertiesSet();
        return contextSource;
    }
}
