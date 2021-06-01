package au.com.imed.portal.referrer.referrerportal;

import au.com.imed.portal.referrer.referrerportal.jwt.JwtTokenFilter;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import au.com.imed.portal.referrer.referrerportal.security.PortalLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("#{'${imed.portal.pages.urls.anon}' + ',' + '${imed.portal.pages.urls.anon.swagger}'}")
    private String[] anonUrls;

    @Value("${imed.portal.pages.rest.methods.nocsrf}")
    private String[] nocsrfs;

    @Value("${imed.ldap.url}")
    private String LDAP_URL;

    @Value("${imed.portal.pages.urls.admin}")
    private String[] adminUrls;

    @Value("${imed.portal.pages.urls.crmadmin}")
    private String[] crmAdminUrls;

    @Value("${imed.portal.pages.urls.editor}")
    private String[] editorUrls;

    @Value("${imed.portal.pages.urls.cleanup}")
    private String[] cleanupUrls;

    @Autowired
    private AuditService auditService;

    @Value("${imed.ldap.userdn}")
    private String ldapUserDn;

    @Value("${imed.ldap.password}")
    private String ldapPassword;


    @Autowired
    private PortalLogoutSuccessHandler portalLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/login", "/portal/login").permitAll()
                .and()
                .addFilterAfter(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/results", true)
                .permitAll();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setBase("ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
        contextSource.setUserDn(ldapUserDn);
        contextSource.setPassword(ldapPassword);
        contextSource.setUrl(LDAP_URL);
        contextSource.afterPropertiesSet();

        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=users,dc=mia,dc=net,dc=au")
                .userSearchFilter("(uid={0})")
                .userSearchBase("ou=users")
                .contextSource(contextSource);

        LdapContextSource contextSourceAd = new LdapContextSource();
        contextSourceAd.setBase("ou=Business Units,dc=mia,dc=net,dc=au");
        contextSourceAd.setUserDn("CN=IWS_LDAP_BIND_Prod,OU=ServiceAccounts,DC=mia,DC=net,DC=au");
        contextSourceAd.setPassword("7Z79531seI32j49pu96m3172563435OKe65oA8g33y29mM3k6w85B80EH5EXZb7t");
        contextSourceAd.setUrl("ldap://10.100.120.10:389");
        contextSourceAd.afterPropertiesSet();

        auth
                .ldapAuthentication()
                .userSearchFilter("(sAMAccountName={0})")
                .contextSource(contextSourceAd);
    }
}
