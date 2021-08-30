package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.ldap.templates.AdLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.ldap.templates.BaseLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.ldap.templates.ReferrerLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.service.LdapUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.query.SearchScope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private ReferrerLdapTemplate referrerLdapTemplate;

    @Autowired
    private AdLdapTemplate adLdapTemplate;

    @Value("${spring.profiles.active}")
    private String ACTIVE_PROFILE;

    public boolean tryLogin(String username, String password) {
        var authenticated = referrerLdapTemplate.authenticate(username, password);
        if (!authenticated && !ACTIVE_PROFILE.equalsIgnoreCase("local")) {
            authenticated = adLdapTemplate.authenticate(username, password);
        }
        return authenticated;
    }

    public List<String> getAccountGroups(final String userName) throws Exception {
        if (ACTIVE_PROFILE.equalsIgnoreCase("local")) {
            return Collections.emptyList();
        }
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
}
