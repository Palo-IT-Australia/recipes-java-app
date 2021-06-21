package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class AccountDetailsControllerTests {

    @Mock
    private ReferrerAccountService referrerAccountService;

    @InjectMocks
    private AccountDetailsController accountDetailsController;

    @Test
    public void shouldRequestAccountDetailsAndSucceed() throws Exception {
        var mockReturn = new AccountDetail();
        mockReturn.setMobile("jackson");
        mockReturn.setEmail("jackson@palo-it.com");
        mockReturn.setName("jackson");
        mockReturn.setUid("0404040404");
        lenient().when(referrerAccountService.getReferrerAccountDetail(Mockito.any(String.class))).thenReturn(mockReturn);

        var authentication = mock(Authentication.class);

        var result = accountDetailsController.accountDetails(authentication);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldRequestAccountDetailsAndFail() throws Exception {
        var mockReturn = new AccountDetail();
        mockReturn.setMobile("");
        mockReturn.setEmail("");
        mockReturn.setName("");
        mockReturn.setUid("");
        var authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("Jackson");
        when(referrerAccountService.getReferrerAccountDetail("Jackson")).thenThrow(new RuntimeException());
        var result = accountDetailsController.accountDetails(authentication);

        assertTrue(result.getStatusCode().is4xxClientError());
    }
}
