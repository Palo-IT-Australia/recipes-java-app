package au.com.imed.portal.referrer.referrerportal.rest.practice.controller.v2;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.AddPractice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = { "profiles.active=prod" })
public class PracticesControllerTest {
    @Mock
    private ReferrerAccountService referrerAccountService;

    @Mock
    private ReferrerMailService mailService;

    @InjectMocks
    private final PracticesController controller = new PracticesController("prod");

    @Test
    public void shouldSendEmailToAddPractice() {
        AddPractice practice = getAddPractice();

        AccountDetail accountDetail = new AccountDetail();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("jackson");
        when(referrerAccountService.getReferrerAccountDetail("jackson")).thenReturn(accountDetail);

        var response = controller.addPractice(practice, auth);

        verify(mailService).sendAddPractice(null, practice, accountDetail);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test(expected = Exception.class)
    public void shouldReturn4xxWhenEmailSendFails() {
        AddPractice practice = getAddPractice();

        AccountDetail accountDetail = new AccountDetail();
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("jackson");
        when(referrerAccountService.getReferrerAccountDetail("jackson")).thenReturn(accountDetail);
        // TODO: Figure out why this prevent the class from being used
        doThrow(new Exception("")).when(mailService).sendAddPractice(null, practice, accountDetail);

        var response = controller.addPractice(practice, auth);

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    private AddPractice getAddPractice() {
        AddPractice practice = new AddPractice();
        practice.setName("Practice 1");
        practice.setFax("Fax");
        practice.setPhone("0421042100");
        practice.setPostcode("2020");
        practice.setProviderNumber("Provider#1");
        practice.setState("NSW");
        practice.setStreet("York");
        practice.setSuburb("Sydney");
        return practice;
    }
}
