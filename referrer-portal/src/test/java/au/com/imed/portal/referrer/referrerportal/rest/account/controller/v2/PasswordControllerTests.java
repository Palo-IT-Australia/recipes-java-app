package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.account.AccountPassword;
import au.com.imed.portal.referrer.referrerportal.rest.visage.service.dicom.account.PortalAccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PasswordControllerTests {
    @Mock
    private PortalAccountService portalAccountService;

    @InjectMocks
    private PasswordController changePasswordController;

    @Test
    public void shouldSuccessfullyChangePassword() throws Exception {
        var mockUser = "Mr. Grumpy";
        var mockAccountPassword = new AccountPassword();
        mockAccountPassword.setOld_password("old pass");
        mockAccountPassword.setNew_password("new pass");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(mockUser);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        var result = changePasswordController.changePassword(mockAccountPassword);

        verify(portalAccountService).updateReferrerPassword(mockUser, mockAccountPassword);

        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldUnsuccessfullyChangePassword() throws Exception {
        var mockAccountPassword = new AccountPassword();
        mockAccountPassword.setOld_password("old pass");
        mockAccountPassword.setNew_password("new pass");

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(null);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        var result = changePasswordController.changePassword(mockAccountPassword);

        assertTrue(result.getStatusCode().is4xxClientError());
    }
}
