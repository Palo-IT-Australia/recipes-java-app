package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.LdapUserDetails;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.AuditService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReferrerPortalAccountRestControllerTest {

    private final String emailReceiver = "test@test.com";
    @Mock
    private ReferrerCreateAccountService accountService;
    @Mock
    private ReferrerMailService emailService;
    @InjectMocks
    private ReferrerPortalAccountRestController controller;
    @Mock
    private AuditService auditService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(controller, "emailReceiver", emailReceiver);
    }

    @Test
    public void shouldRecoverUid() throws Exception {
        var email = "test@test.com";
        var ahpra = "APHRA";


        var ldapUser = new LdapUserDetails();
        ldapUser.setUid("UID");
        when(accountService.findReferrerAccountsByEmailAndAhpra(email, ahpra)).thenReturn(singletonList(ldapUser));

        var result = controller.recoverUID(email, ahpra);
        verify(emailService).emailRetrieved(emailReceiver, ldapUser);
        verify(auditService).doAudit(eq("UserID"), eq(ldapUser.getUid()), anyMap());
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldReturnBadRequestWhenMoreThanOneAccountFound() throws Exception {
        var email = "test@test.com";
        var ahpra = "APHRA";


        var ldapUser = new LdapUserDetails();
        ldapUser.setUid("UID");
        when(accountService.findReferrerAccountsByEmailAndAhpra(email, ahpra)).thenReturn(Arrays.asList(ldapUser, ldapUser));

        var result = controller.recoverUID(email, ahpra);
        verify(emailService).emailFailedRetrieveAttempt(new String[]{emailReceiver}, email, ahpra);
        assertTrue(result.getStatusCode().is4xxClientError());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().getMessage().contains("Multiple accounts are found"));
    }
}
