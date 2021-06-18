package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.DetailModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.HashMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;



// @ TODO change the name when the proper class will be rewritten with Mockito Runner
@RunWith(MockitoJUnitRunner.class)
public class PostAccountDetailsControllerTests {


    @Mock
    private ReferrerAccountService accountService;

    @InjectMocks
    private AccountDetailsController accountDetailsController;

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
    @Test
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
