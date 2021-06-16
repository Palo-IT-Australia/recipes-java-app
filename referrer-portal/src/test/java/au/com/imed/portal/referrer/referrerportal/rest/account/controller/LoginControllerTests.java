package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ReferrerPortalApplication;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = ReferrerPortalApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-local.properties")
public class LoginControllerTests {

    @Mock
    private ReferrerCreateAccountService referrerCreateAccountService;

    @InjectMocks
    private LoginController loginController = new LoginController();

    @Test
    public void shouldTryRegisterUserAndSucceed() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_SUCCESS_MSG, "data");
        var mockExternalUser = new ExternalUser();
        when(referrerCreateAccountService.createAccount(mockExternalUser)).thenReturn(mockReturn);
        var result = loginController.register(mockExternalUser);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertSame(mockReturn, result.getBody());
    }

    @Test
    public void shouldTryRegisterUserAndFail() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_ERROR_MSG, "data");
        var mockExternalUser = new ExternalUser();
        when(referrerCreateAccountService.createAccount(mockExternalUser)).thenReturn(mockReturn);
        var result = loginController.register(mockExternalUser);
        assertTrue(result.getStatusCode().is4xxClientError());
        assertSame(mockReturn, result.getBody());
    }

}


