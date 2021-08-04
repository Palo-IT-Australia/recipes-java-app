package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.AdLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.BaseLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.ldap.adapter.templates.ReferrerLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.service.LdapUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.SearchControls;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Primary
@Service
public class GlobalAccountService extends ABasicAccountService {

    public static final String REFERRER = "ROLE_REFERRER";
    public static final String AUTH_ADMIN = "ROLE_ADMIN";
    public static final String AUTH_EDITOR = "ROLE_EDITOR";
    public static final String AUTH_HOSPITAL = "ROLE_HOSPITAL";
    public static final String AUTH_CLEANUP = "ROLE_CLEANUP";
    public static final String AUTH_CRM_ADMIN = "ROLE_CRM_ADMIN";

    @Autowired
    private LdapUserMapper ldapUserMapper;

    @Autowired
    private ReferrerAccountService accountService;

    @Value("${imed.portal.auth.groups.admin}")
    private String[] adminGroups;

    @Value("${imed.portal.auth.groups.crmadmin}")
    private String[] crmAdminGroups;

    @Value("${imed.portal.auth.groups.editor}")
    private String[] editorGroups;

    @Value("${imed.portal.auth.groups.cleanup}")
    private String[] cleanupGroup;

    @Value("${imed.portal.auth.groups.hospital}")
    private String[] hospitalGroups;

    @Autowired
    private ReferrerLdapTemplate referrerLdapTemplate;

    @Autowired
    private AdLdapTemplate adLdapTemplate;

    public boolean tryLogin(String username, String password) throws Exception {
        List<LdapTemplate> templates = Collections.singletonList(getReferrerLdapTemplate());
        var authenticated = templates.parallelStream().anyMatch(template -> checkPasswordForTemplate(template, username, password));
        if (!authenticated) {
            authenticated = checkPasswordForAd(getADAccountsLdapTemplate(), username, password);
        }
        return authenticated;
    }

    private boolean checkPasswordForAd(LdapTemplate template, String username, String password) {
        var isAuth = false;
        try {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                var filter = new AndFilter();
                filter.and(new EqualsFilter("sAMAccountName", username));
                isAuth = template.authenticate("", filter.toString(), password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isAuth;
    }

    private boolean checkPasswordForTemplate(LdapTemplate template, String username, String password) {
        var isAuth = false;
        try {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                var filter = new AndFilter();
                filter.and(new EqualsFilter("uid", username));
                isAuth = template.authenticate("", filter.toString(), password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isAuth;
    }

    public List<String> getAccountGroups(final String userName) throws Exception {
        return getLdapGroups(userName).stream().map(Object::toString).collect(Collectors.toList());
    }

    private Collection<? extends GrantedAuthority> getLdapGroups(String uid) {
        var result = new ArrayList<GrantedAuthority>();
        List<BaseLdapTemplate> templates = asList(referrerLdapTemplate, adLdapTemplate);

        templates.forEach(template -> {
            var authorities = template.getLdapTemplate().search("", template.getSearchQuery(uid), SearchScope.SUBTREE.getId(), new AbstractContextMapper<Set<SimpleGrantedAuthority>>() {

                @Override
                protected Set<SimpleGrantedAuthority> doMapFromContext(DirContextOperations dirContextOperations) {
                    return ldapUserMapper.getSimpleGrantedAuthorities(dirContextOperations, uid);
                }
            });

            result.addAll(authorities.stream().flatMap(Collection::stream).collect(Collectors.toList()));
        });

        return result;
    }

    private List<String> getGroupNames(String username, Collection<? extends GrantedAuthority> ldapGroups) {
        List<String> auths = new ArrayList<>(1);
        if (ldapGroups != null && !ldapGroups.isEmpty()) {
            if (hasAuthority(ldapGroups, adminGroups)) {
                auths.add(AUTH_ADMIN);
            }
            if (hasAuthority(ldapGroups, editorGroups)) {
                auths.add(AUTH_EDITOR);
            }
            if (hasAuthority(ldapGroups, cleanupGroup)) {
                auths.add(AUTH_CLEANUP);
            }
            if (hasAuthority(ldapGroups, crmAdminGroups)) {
                auths.add(AUTH_CRM_ADMIN);
            }

        }
        // Hospital access both AD and LDAP groups
        if (accountService.isHospitalAccess(username) || hasAuthority(ldapGroups, hospitalGroups)) {
            auths.add(AUTH_HOSPITAL);
        }

        return auths;
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> groups, String[] targetGroup) {
        if (groups != null && !groups.isEmpty()) {
            return groups.stream().anyMatch(s -> asList(targetGroup).contains(s));
        }
        return false;
    }
}
