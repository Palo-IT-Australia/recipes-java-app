package au.com.imed.portal.referrer.referrerportal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.security.PortalLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter  {  
	@Value("#{'${imed.portal.pages.urls.anon}' + ',' + '${imed.portal.pages.urls.anon.swagger}'}")
	private String[] anonUrls;

	@Value("${imed.portal.pages.rest.methods.nocsrf}")
	private String[] nocsrfs;

	@Value("${imed.ldap.url}")
	private String LDAP_URL;
	
	@Value("${imed.portal.pages.urls.admin}")
	private String [] adminUrls;

	@Value("${imed.portal.pages.urls.crmadmin}")
	private String [] crmAdminUrls;

	@Value("${imed.portal.pages.urls.editor}")
	private String [] editorUrls;

	@Value("${imed.portal.auth.groups.admin}")
	private String [] adminGroups;

	@Value("${imed.portal.auth.groups.crmadmin}")
	private String [] crmAdminGroups;

	@Value("${imed.portal.auth.groups.editor}")
	private String [] editorGroups;
	
	@Value("${imed.portal.auth.groups.hospital}")
	private String [] hospitalGroups;
		
	@Autowired 
	ReferrerAccountService accountService;
	
	@Autowired
	private AuditService auditService;
	
	@Autowired
	private PortalLogoutSuccessHandler portalLogoutSuccessHandler;
	
	private static final String AUTH_ADMIN = "ROLE_ADMIN";
	private static final String AUTH_EDITOR = "ROLE_EDITOR";
	private static final String AUTH_HOSPITAL = "ROLE_HOSPITAL";
	private static final String AUTH_CRM_ADMIN = "ROLE_CRM_ADMIN";

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers(anonUrls).permitAll()
		.antMatchers(adminUrls).hasAuthority(AUTH_ADMIN)
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
		.and()
		.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).ignoringAntMatchers(nocsrfs);  // Angular 403 ajax post/put
		;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		LdapContextSource contextSource = new LdapContextSource();
		contextSource.setBase("ou=Referrers,ou=Portal,ou=Applications,dc=mia,dc=net,dc=au");
		contextSource.setUserDn("cn=root");
		contextSource.setPassword("794bo3HAST");
		contextSource.setUrl(LDAP_URL);
		contextSource.afterPropertiesSet(); 		

		auth
		.ldapAuthentication()
		.userSearchFilter("(uid={0})")
		.contextSource(contextSource)
		.userDetailsContextMapper(userDetailsContextMapper());
		
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
		.userDetailsContextMapper(userDetailsContextMapper());		
	}

	@Bean
	public UserDetailsContextMapper userDetailsContextMapper() {
		return new LdapUserDetailsMapper() {
			@Override
			public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
				Set<SimpleGrantedAuthority> auths = new HashSet<>(1);
				
				final String CN = "CN=";				
				Object[] groups = ctx.getObjectAttributes("memberOf");
				if(groups != null && groups.length > 0) {
					if(Arrays.stream(groups).map(o -> o.toString()).filter(s -> {
						return Arrays.stream(adminGroups).filter(g -> s.startsWith(CN + g)).count() > 0;
					}).count() > 0) {
						auths.add(new SimpleGrantedAuthority(AUTH_ADMIN));
					}
					if(Arrays.stream(groups).map(o -> o.toString()).filter(s -> {
						return Arrays.stream(editorGroups).filter(g -> s.startsWith(CN + g)).count() > 0;
					}).count() > 0) {
						auths.add(new SimpleGrantedAuthority(AUTH_EDITOR));
					}
					if(Arrays.stream(groups).map(o -> o.toString()).filter(s -> {
						return Arrays.stream(crmAdminGroups).filter(g -> s.startsWith(CN + g)).count() > 0;
					}).count() > 0) {
						auths.add(new SimpleGrantedAuthority(AUTH_CRM_ADMIN));
					}

				}
				
				// Hospital access both AD and LDAP groups
				boolean isHospitalAuth = false;
				if(accountService.isHospitalAccess(username)) {
					isHospitalAuth = true;
				} else if (groups != null && groups.length > 0) {
					if(Arrays.stream(groups).map(o -> o.toString()).filter(s -> {
						return Arrays.stream(hospitalGroups).filter(g -> s.startsWith(CN + g)).count() > 0;
					}).count() > 0) {
						isHospitalAuth = true;
					}
				}
				if(isHospitalAuth) {
					auths.add(new SimpleGrantedAuthority(AUTH_HOSPITAL));
				}
				
				// Audit login
		    auditService.doAudit("Login", username);
				
				UserDetails details = super.mapUserFromContext(ctx, username, auths);
				return new DetailedLdapUserDetails((LdapUserDetails) details, 
						ctx.getStringAttribute("sn"),
						ctx.getStringAttribute("givenName"),
						ctx.getStringAttribute("mobile"),
						ctx.getStringAttribute("mail"));
			}
		};
	}
}