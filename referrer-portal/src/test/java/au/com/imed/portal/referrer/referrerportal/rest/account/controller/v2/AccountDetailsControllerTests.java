package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class AccountDetailsControllerTests {


    @Mock
    private ReferrerAccountService accountService;

    @InjectMocks
    private AccountDetailsController accountDetailsController;

    @Test
    public void shouldRequestAccountDetailsAndSucceed() throws Exception {
        var mockReturn = new AccountDetail();
        mockReturn.setMobile("jackson");
        mockReturn.setEmail("jackson@palo-it.com");
        mockReturn.setName("jackson");
        mockReturn.setUid("0404040404");
        Mockito.when(accountService.getReferrerAccountDetail(Mockito.any(String.class))).thenReturn(mockReturn);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("jackson");

        var result = accountDetailsController.accountDetails(authentication);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(authentication, times(1)).getName();
        assertSame(result.getBody().getMobile(), "jackson" );
    }

    @Test
    public void shouldUpdateAccountDetailsAndFailOnGetName() throws Exception {
        var mockDetail = new DetailModel();
        mockDetail.setMobile("123");
        mockDetail.setEmail("test@email.com");
        Authentication authentication = Mockito.mock(Authentication.class);
        var result = accountDetailsController.info(mockDetail, authentication);
        assertTrue(result.getStatusCode().is4xxClientError());
        verify(authentication, times(1)).getName();
    }

    @Test(expected = ResponseStatusException.class)
    public void shouldUpdateAccountDetailsAndFailOnUpdate() throws Exception {
        var mockDetail = new DetailModel();
        mockDetail.setMobile("123");
        mockDetail.setEmail("test@email.com");
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(accountService.updateReferrerAccountDetail("test", mockDetail)).thenThrow(new Exception(("Test Exception")));
        Mockito.when(authentication.getName()).thenReturn("test");
        var result = accountDetailsController.info(mockDetail, authentication);
        assertTrue(result.getStatusCode().is4xxClientError());
        verify(authentication, times(1)).getName();
        verify(accountService, times(1)).updateReferrerAccountDetail("test", mockDetail);
    }

    @Test
    public void shouldUpdateAccountDetailsAndSucceed() throws Exception {
        var mockDetail = new DetailModel();
        mockDetail.setMobile("123");
        mockDetail.setEmail("test@email.com");
        var mockAccountUpdateReturn = new HashMap<String, String>();
        mockAccountUpdateReturn.put(MODEL_KEY_SUCCESS_MSG, "works");
        var mockAccount = new AccountDetail();
        Authentication authentication = Mockito.mock(Authentication.class);

        Mockito.when(accountService.updateReferrerAccountDetail("test", mockDetail)).thenReturn(mockAccountUpdateReturn);
        Mockito.when(authentication.getName()).thenReturn("test");
        var result = accountDetailsController.info(mockDetail, authentication);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(authentication, times(2)).getName();
        verify(accountService, times(1)).updateReferrerAccountDetail("test", mockDetail);
    }
}
