package au.com.imed.portal.referrer.referrerportal.ldap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Primary
@Service
public class GlobalAccountService extends ABasicAccountService {

    public static final String AUTH_ADMIN = "ROLE_ADMIN";
    public static final String AUTH_EDITOR = "ROLE_EDITOR";
    public static final String AUTH_HOSPITAL = "ROLE_HOSPITAL";
    public static final String AUTH_CLEANUP = "ROLE_CLEANUP";
    public static final String AUTH_CRM_ADMIN = "ROLE_CRM_ADMIN";

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

    public boolean checkPasswordForReferrer(String username, String password) {
        var isAuth = false;
        try {
            if (username != null && username.length() > 0 && password != null && password.length() > 0) {
                var filter = new AndFilter();
                filter.and(new EqualsFilter("uid", username));
                isAuth = getReferrerLdapTemplate().authenticate("", filter.toString(), password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isAuth;
    }

    private SearchControls getSimpleSearchControls() {
        var searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(30000);
        String[] attrIDs = {"memberOf"};
        searchControls.setReturningAttributes(attrIDs);
        return searchControls;
    }

    public List<String> getAccountGroups(final String userName) throws Exception {
        return getGroupNames(userName, getLdapGroups(userName));
    }

    private ArrayList<String> getLdapGroups(String userName) throws Exception {
        var groups = new ArrayList<String>();
        var filter = new AndFilter();
        filter.and(new EqualsFilter("cn", userName));
        getGlobalLdapTemplate().search("", filter.encode(), getSimpleSearchControls(), attributes -> {
            var name = attributes.getName();
            var groupMatcher = Pattern.compile(".*cn=(([\\w-\\s])+).*").matcher(name);
            if (groupMatcher.matches()) {
                groups.add(groupMatcher.group(1));
            }
        });
        return groups;
    }

    private List<String> getGroupNames(String username, List<String> ldapGroups) {
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

    private boolean hasAuthority(List<String> groups, String[] targetGroup) {
        if (groups != null && !groups.isEmpty()) {
            return groups.stream().anyMatch(s -> Arrays.asList(targetGroup).contains(s));
        }
        return false;
    }
}