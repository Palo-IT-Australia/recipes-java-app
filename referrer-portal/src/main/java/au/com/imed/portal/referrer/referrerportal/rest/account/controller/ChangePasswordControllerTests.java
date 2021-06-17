package au.com.imed.portal.referrer.referrerportal.rest.account.controller;

import au.com.imed.portal.referrer.referrerportal.ReferrerPortalApplication;
import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ChangePasswordControllerTests {
    @Mock
    private PortalAccountService portalAccountService;

    @Mock
    AuthenticationUtil authenticationUtil;

    @InjectMocks
    private ChangePasswordController changePasswordController;

    @Test
    public void shouldSuccessfullyChangePassword() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_SUCCESS_MSG, "");
        var mockUser = "Mr. Grumpy";
        var mockAccountPassword = new AccountPassword();
        mockAccountPassword.setOld_password("old pass");
        mockAccountPassword.setNew_password("new pass");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        var result = changePasswordController.changePassword("Bearer token", mockAccountPassword);

        verify(portalAccountService).updateReferrerPassword(mockUser, mockAccountPassword);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldUnsuccessfullyChangePassword() throws Exception {
    }
}
