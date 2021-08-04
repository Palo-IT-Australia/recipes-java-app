package au.com.imed.portal.referrer.referrerportal.ldap.templates;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public abstract class BaseLdapTemplate {

    abstract String getBaseDomain();

    abstract String getUserDn();

    abstract String getLdapPassword();

    abstract String getLdapUrl();

    public LdapTemplate getLdapTemplate() {
        return new LdapTemplate(getContextSource());
    }

    public abstract String getSearchQuery(String uid);

    public LdapContextSource getContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setBase(getBaseDomain());
        contextSource.setUserDn(getUserDn());
        contextSource.setPassword(getLdapPassword());
        contextSource.setUrl(getLdapUrl());
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    public boolean authenticate(String username, String password) {
        var isAuth = false;
        try {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                isAuth = getLdapTemplate().authenticate("", getSearchQuery(username), password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isAuth;
    }
}
