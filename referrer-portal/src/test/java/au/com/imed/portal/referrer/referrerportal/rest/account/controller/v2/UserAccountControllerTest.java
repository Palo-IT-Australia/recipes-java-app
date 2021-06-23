package au.com.imed.portal.referrer.referrerportal.rest.account.controller.v2;

import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.ExternalUser;
import au.com.imed.portal.referrer.referrerportal.model.ResetConfirmModel;
import au.com.imed.portal.referrer.referrerportal.model.ResetModel;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.IMedGenericException;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.SmsException;
import au.com.imed.portal.referrer.referrerportal.rest.account.service.UserAccountService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_ERROR_MSG;
import static au.com.imed.portal.referrer.referrerportal.common.PortalConstant.MODEL_KEY_SUCCESS_MSG;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountControllerTest {

    @Mock
    private UserAccountService service;

    @Mock
    private ReferrerCreateAccountService referrerAccountService;

    @InjectMocks
    private UserAccountController controller;

    @Test
    public void shouldReturn2xxOnSuccessfulReset() {
        var email1 = "email@palo-it.com";
        var resetModel = new ResetModel();
        resetModel.setUsername(email1);

        var accountDetail = mock(AccountDetail.class);
        when(referrerAccountService.getReferrerAccountDetailByEmail(email1)).thenReturn(accountDetail);

        var response = controller.resetPassword(resetModel);

        verify(service).requestPasswordReset(accountDetail);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldReturn4xxOnUnSuccessfulReset() {
        var email1 = "email@palo-it.com";
        var resetModel = new ResetModel();
        resetModel.setUsername(email1);

        var accountDetail = mock(AccountDetail.class);
        when(referrerAccountService.getReferrerAccountDetailByEmail(email1)).thenReturn(accountDetail);

        doThrow(new SmsException("Wrong phone number")).when(service).requestPasswordReset(accountDetail);

        Assertions.assertThrows(ResponseStatusException.class, () -> controller.resetPassword(resetModel));
    }

    @Test
    public void shouldTryRegisterUserAndSucceed() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_SUCCESS_MSG, "data");
        var mockExternalUser = new ExternalUser();
        when(referrerAccountService.createAccount(mockExternalUser)).thenReturn(mockReturn);
        var result = controller.register(mockExternalUser);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        assertSame(mockReturn, result.getBody());
    }

    @Test
    public void shouldTryRegisterUserAndFail() throws Exception {
        var mockReturn = new HashMap<String, String>();
        mockReturn.put(MODEL_KEY_ERROR_MSG, "data");
        var mockExternalUser = new ExternalUser();
        when(referrerAccountService.createAccount(mockExternalUser)).thenReturn(mockReturn);
        var result = controller.register(mockExternalUser);
        assertTrue(result.getStatusCode().is4xxClientError());
        assertSame(mockReturn, result.getBody());
    }

    @Test
    public void shouldReturn2xxOnSuccessfulPasswordResetConfirmation() {
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword("pass");
        resetConfirmModel.setSecret("secret");
        resetConfirmModel.setPasscode("1021");
        Mockito.doNothing().when(service).confirmPasswordReset(resetConfirmModel);
        var response = controller.postResetConfirm(resetConfirmModel);
        verify(service).confirmPasswordReset(resetConfirmModel);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void shouldReturn4xxOnFailedPasswordResetConfirmation() {
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword("pass");
        resetConfirmModel.setSecret("secret");
        resetConfirmModel.setPasscode("1021");
        Mockito.doThrow(new IMedGenericException("")).when(service).confirmPasswordReset(resetConfirmModel);
        Assertions.assertThrows(ResponseStatusException.class, () -> controller.postResetConfirm(resetConfirmModel));
    }



}
