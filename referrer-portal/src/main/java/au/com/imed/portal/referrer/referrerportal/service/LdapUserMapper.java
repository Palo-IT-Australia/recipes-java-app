package au.com.imed.portal.referrer.referrerportal.service;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import au.com.imed.portal.referrer.referrerportal.security.DetailedLdapUserDetails;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static au.com.imed.portal.referrer.referrerportal.ldap.GlobalAccountService.*;

@Component
public class LdapUserMapper extends LdapUserDetailsMapper {

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
    private ReferrerAccountService accountService;

    @Autowired
    private AuditService auditService;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        // Check user id cases as ldap is insensitive and visage is sensitive
        String uid = ctx.getStringAttribute("uid");
        String acnt = ctx.getStringAttribute("sAMAccountName");
        if (!StringUtil.isBlank(uid) && !username.equals(uid)) {
            throw new UsernameNotFoundException("uid case mismatch LDAP");
        } else if (!StringUtil.isBlank(acnt) && !username.equals(acnt)) {
            throw new UsernameNotFoundException("sAMAccountName case mismatch AD");
        }

        Set<SimpleGrantedAuthority> auths = new HashSet<>(1);

        final String CN = "CN=";
        Object[] groups = ctx.getObjectAttributes("memberOf");
        if (groups != null && groups.length > 0) {
            if (Arrays.stream(groups).map(Object::toString).anyMatch(s -> Arrays.stream(adminGroups).anyMatch(g -> s.startsWith(CN + g)))) {
                auths.add(new SimpleGrantedAuthority(AUTH_ADMIN));
            }
            if (Arrays.stream(groups).map(Object::toString).anyMatch(s -> Arrays.stream(editorGroups).anyMatch(g -> s.startsWith(CN + g)))) {
                auths.add(new SimpleGrantedAuthority(AUTH_EDITOR));
            }
            if (Arrays.stream(groups).map(Object::toString).anyMatch(s -> Arrays.stream(cleanupGroup).anyMatch(g -> s.startsWith(CN + g)))) {
                auths.add(new SimpleGrantedAuthority(AUTH_CLEANUP));
            }
            if (Arrays.stream(groups).map(Object::toString).anyMatch(s -> Arrays.stream(crmAdminGroups).anyMatch(g -> s.startsWith(CN + g)))) {
                auths.add(new SimpleGrantedAuthority(AUTH_CRM_ADMIN));
            }

        }

        // Hospital access both AD and LDAP groups
        boolean isHospitalAuth = false;
        if (accountService.isHospitalAccess(username)) {
            isHospitalAuth = true;
        } else if (groups != null && groups.length > 0) {
            if (Arrays.stream(groups).map(Object::toString).anyMatch(s -> Arrays.stream(hospitalGroups).anyMatch(g -> s.startsWith(CN + g)))) {
                isHospitalAuth = true;
            }
        }
        if (isHospitalAuth) {
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
}
