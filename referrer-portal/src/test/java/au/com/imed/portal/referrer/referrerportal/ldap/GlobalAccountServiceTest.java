package au.com.imed.portal.referrer.referrerportal.ldap;

import au.com.imed.portal.referrer.referrerportal.ldap.templates.AdLdapTemplate;
import au.com.imed.portal.referrer.referrerportal.ldap.templates.ReferrerLdapTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlobalAccountServiceTest {

    @Mock
    private ReferrerLdapTemplate referrerLdapTemplate;
    @Mock
    private AdLdapTemplate adLdapTemplate;

    @InjectMocks
    private GlobalAccountService accountService;

    @Test
    public void shouldAuthenticateTemplates() {
        accountService.tryLogin("username", "password");

        verify(adLdapTemplate).authenticate("username", "password");
        verify(referrerLdapTemplate).authenticate("username", "password");
    }

    @Test
    public void shouldNotAuthenticate() {
        when(adLdapTemplate.authenticate("username", "password")).thenReturn(false);
        when(referrerLdapTemplate.authenticate("username", "password")).thenReturn(false);

        var result = accountService.tryLogin("username", "password");

        assertFalse(result);
    }

    @Test
    public void shouldFindGroups() throws Exception {
        var adTemplateMock = Mockito.mock(LdapTemplate.class);
        var templateMock = Mockito.mock(LdapTemplate.class);

        when(adLdapTemplate.getLdapTemplate()).thenReturn(adTemplateMock);
        when(referrerLdapTemplate.getLdapTemplate()).thenReturn(templateMock);

        when(adLdapTemplate.getSearchQuery("username")).thenReturn("");
        when(referrerLdapTemplate.getSearchQuery("username")).thenReturn("");

        var roles = new HashSet<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ADMIN"));

        when(templateMock.search(anyString(), anyString(), anyInt(), any(ContextMapper.class))).thenReturn(Collections.singletonList(roles));

        var result = accountService.getAccountGroups("username");

        assertEquals("ADMIN", result.get(0));
    }
}
