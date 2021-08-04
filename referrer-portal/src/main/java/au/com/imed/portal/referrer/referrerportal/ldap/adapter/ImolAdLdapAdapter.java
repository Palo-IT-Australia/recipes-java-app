package au.com.imed.portal.referrer.referrerportal.ldap.adapter;

import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.BaseLdapTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.support.LdapContextSource;

public abstract class ImolAdLdapAdapter implements BaseLdapTemplate {

    @Value("${imed.ldap.ad.url}")
    private String adLdapHost;

    public LdapContextSource getContextSource(String baseDomain) {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setBase(baseDomain);
        contextSource.setUserDn("CN=IWS_LDAP_BIND_Prod,OU=ServiceAccounts,DC=mia,DC=net,DC=au");
        contextSource.setPassword("7Z79531seI32j49pu96m3172563435OKe65oA8g33y29mM3k6w85B80EH5EXZb7t");
        contextSource.setUrl(adLdapHost);
        contextSource.afterPropertiesSet();
        return contextSource;
    }
}
