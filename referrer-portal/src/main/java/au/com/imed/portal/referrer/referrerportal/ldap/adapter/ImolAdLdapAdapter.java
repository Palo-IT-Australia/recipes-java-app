package au.com.imed.portal.referrer.referrerportal.ldap.adapter;

import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.BaseLdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

public abstract class ImolAdLdapAdapter implements BaseLdapTemplate {

    public LdapContextSource getContextSource(String baseDomain) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setBase(baseDomain);
        contextSource.setUserDn("CN=IWS_LDAP_BIND_Prod,OU=ServiceAccounts,DC=mia,DC=net,DC=au");
        contextSource.setPassword("7Z79531seI32j49pu96m3172563435OKe65oA8g33y29mM3k6w85B80EH5EXZb7t");
        contextSource.setUrl("ldap://10.100.120.10:389");
        contextSource.afterPropertiesSet();
        return contextSource;
    }
}
