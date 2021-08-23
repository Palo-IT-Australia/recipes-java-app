package au.com.imed.portal.referrer.referrerportal;

import au.com.imed.portal.referrer.referrerportal.jwt.JwtTokenFilter;
import au.com.imed.portal.referrer.referrerportal.security.PortalLogoutSuccessHandler;
import au.com.imed.portal.referrer.referrerportal.service.LdapUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;

import static au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService.*;

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

    @Value("${imed.ldap.userdn}")
    private String ldapUserDn;

    @Value("${imed.ldap.password}")
    private String ldapPassword;

    @Value("${imed.frontend.url}")
    private String[] frontendUrls;

    @Value("${spring.profiles.active}")
    private String ACTIVE_PROFILE;

    @Autowired
    private LdapUserMapper ldapUserMapper;

    @Autowired
    private PortalLogoutSuccessHandler portalLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
        http.authorizeRequests()
                .antMatchers(anonUrls).permitAll()
                .antMatchers(adminUrls).hasAuthority(AUTH_ADMIN)
                .antMatchers(cleanupUrls).hasAuthority(AUTH_CLEANUP)
                .antMatchers(crmAdminUrls).hasAuthority(AUTH_CRM_ADMIN)
                .antMatchers("/hospital").hasAnyAuthority(AUTH_HOSPITAL)
                .anyRequest().fullyAuthenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/results", true)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(portalLogoutSuccessHandler)
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and().cors().and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(nocsrfs)
                .and()
                .addFilterAfter(new JwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
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
                .userSearchFilter("(uid={0})")
                .contextSource(contextSource)
                .userDetailsContextMapper(ldapUserMapper);

        if (!ACTIVE_PROFILE.equals("local")) {
            LdapContextSource contextSourceAd = new LdapContextSource();
            contextSourceAd.setBase("ou=Business Units,dc=mia,dc=net,dc=au");
            contextSourceAd.setUserDn("CN=IWS_LDAP_BIND_Prod,OU=ServiceAccounts,DC=mia,DC=net,DC=au");
            contextSourceAd.setPassword("7Z79531seI32j49pu96m3172563435OKe65oA8g33y29mM3k6w85B80EH5EXZb7t");
            contextSourceAd.setUrl("ldap://10.100.120.10:389");
            contextSourceAd.afterPropertiesSet();

            auth
                    .ldapAuthentication()
                    .userSearchFilter("(sAMAccountName={0})")
                    .contextSource(contextSourceAd)
                    .userDetailsContextMapper(ldapUserMapper);
        }
    }

    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return ldapUserMapper;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(frontendUrls));  //set access from all domains
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
