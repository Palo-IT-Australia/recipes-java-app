package au.com.imed.portal.referrer.referrerportal.rest.account.service;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;
import au.com.imed.portal.referrer.referrerportal.ldap.ReferrerCreateAccountService;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.model.ResetConfirmModel;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.EmailException;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.IMedGenericException;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.SmsException;
import au.com.imed.portal.referrer.referrerportal.service.ConfirmProcessDataService;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;
import au.com.imed.portal.referrer.referrerportal.utils.SmsPasscodeHashUtil;
import au.com.imed.portal.referrer.referrerportal.utils.UrlCodeAes128Util;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserAccountServiceTest {

    @Mock
    private GoFaxSmsService smsService;

    @Mock
    private ReferrerMailService mailService;

    @Mock
    private ConfirmProcessDataService confirmProcessDataService;

    @InjectMocks
    private UserAccountService userAccountService;

    @Test
    public void shouldSendSms() throws Exception {
        var phone1 = "04040404042";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);
        var refPasswordResetEntity = mock(ReferrerPasswordResetEntity.class);

        when(refPasswordResetEntity.getUrlCode()).thenReturn("fake-url-code");
        when(confirmProcessDataService.savePasswordReset(eq(email1), anyString())).thenReturn(refPasswordResetEntity);

        userAccountService.requestPasswordReset(accountDetail);

        verify(smsService).send(eq(new String[]{phone1}), anyString());
    }

    @Test
    public void shouldSendEmail() throws Exception {
        var phone1 = "04040404042";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);
        var refPasswordResetEntity = mock(ReferrerPasswordResetEntity.class);

        when(refPasswordResetEntity.getUrlCode()).thenReturn("fake-url-code");
        when(confirmProcessDataService.savePasswordReset(eq(email1), anyString())).thenReturn(refPasswordResetEntity);

        userAccountService.requestPasswordReset(accountDetail);

        verify(mailService).sendPasswordResetHtml(eq(new String[]{email1}), anyString());
    }

    @Test(expected = SmsException.class)
    public void shouldThrowSmsExceptionOnWrongPhoneNumber() {
        var phone1 = "55669883";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);

        var refPasswordResetEntity = mock(ReferrerPasswordResetEntity.class);

        when(refPasswordResetEntity.getUrlCode()).thenReturn("fake-url-code");
        when(confirmProcessDataService.savePasswordReset(eq(email1), anyString())).thenReturn(refPasswordResetEntity);

        userAccountService.requestPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowExceptionOnEmptyPhoneNumber() {
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(null);

        userAccountService.requestPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowEmailExceptionOnEmptyEmail() {
        var phone1 = "04040404041";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(null);
        accountDetail.setEmail(null);
        accountDetail.setMobile(phone1);

        userAccountService.requestPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowEmailExceptionOnNonExistingEmail() {
        var phone1 = "04040404041";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);

        userAccountService.requestPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldReThrowEmailExceptionFromEmailService() throws Exception {
        var phone1 = "04040404041";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);

        var refPasswordResetEntity = mock(ReferrerPasswordResetEntity.class);
        when(refPasswordResetEntity.getUrlCode()).thenReturn("fake-url-code");
        when(confirmProcessDataService.savePasswordReset(eq(email1), anyString())).thenReturn(refPasswordResetEntity);

        doThrow(new Exception()).when(mailService).sendPasswordResetHtml(eq(new String[]{email1}), anyString());
        userAccountService.requestPasswordReset(accountDetail);
    }

    @Test(expected = SmsException.class)
    public void shouldReThrowSmsExceptionFromSmsService() throws Exception {
        var phone1 = "04040404041";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);

        var refPasswordResetEntity = mock(ReferrerPasswordResetEntity.class);
        when(refPasswordResetEntity.getUrlCode()).thenReturn("fake-url-code");
        when(confirmProcessDataService.savePasswordReset(eq(email1), anyString())).thenReturn(refPasswordResetEntity);

        doThrow(new Exception()).when(smsService).send(eq(new String[]{phone1}), anyString());
        userAccountService.requestPasswordReset(accountDetail);
    }

    @Mock
    private ReferrerCreateAccountService referrerAccountService;

    @Test
    public void shouldConfirmPasswordSuccessfully() throws Exception {
        var secret = "secret";
        var pass = "pass";
        var code = "1021";
        var uid = "email@email.com";
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword(pass);
        resetConfirmModel.setSecret(secret);
        resetConfirmModel.setPasscode(code);

        // create testing entity
        ReferrerPasswordResetEntity entity = createReferrerPasswordResetEntity(code, uid);

        when(confirmProcessDataService.getReferrerPasswordResetEntityBySecret(secret)).thenReturn(entity);
        Mockito.doNothing().when(referrerAccountService).resetReferrerPassword(entity.getUid(), pass);
        Mockito.when(confirmProcessDataService.setPasswordResetActive(entity)).thenReturn(entity);

        userAccountService.confirmPasswordReset(resetConfirmModel);

        verify(referrerAccountService).resetReferrerPassword(entity.getUid(), pass);
        verify(confirmProcessDataService).setPasswordResetActive(entity);
    }


    @Test
    public void shouldFailWhenConfirmPasswordDueToWrongCode() throws Exception {
        var secret = "secret";
        var pass = "pass";
        var code = "1021";
        var wrongCode = "1022";
        var uid = "email@email.com";
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword(pass);
        resetConfirmModel.setSecret(secret);
        resetConfirmModel.setPasscode(wrongCode);

        // create testing entity
        ReferrerPasswordResetEntity entity = createReferrerPasswordResetEntity(code, uid);

        when(confirmProcessDataService.getReferrerPasswordResetEntityBySecret(secret)).thenReturn(entity);
        Assertions.assertThrows(IMedGenericException.class, () -> userAccountService.confirmPasswordReset(resetConfirmModel));
    }

    @Test
    public void shouldFailWhenConfirmPasswordDueToBlankCode() throws Exception {
        var secret = "secret";
        var pass = "pass";
        var code = "";
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword(pass);
        resetConfirmModel.setSecret(secret);
        resetConfirmModel.setPasscode(code);
        Assertions.assertThrows(IMedGenericException.class, () -> userAccountService.confirmPasswordReset(resetConfirmModel));
    }

    @Test
    public void shouldFailWhenConfirmPasswordDueToNullEntity() throws Exception {
        var secret = "#";
        var pass = "pass";
        var code = "1021";
        var resetConfirmModel = new ResetConfirmModel();
        resetConfirmModel.setPassword(pass);
        resetConfirmModel.setSecret(secret);
        resetConfirmModel.setPasscode(code);
        ReferrerPasswordResetEntity entity = null;
        when(confirmProcessDataService.getReferrerPasswordResetEntityBySecret(secret)).thenReturn(entity);
        Assertions.assertThrows(IMedGenericException.class, () -> userAccountService.confirmPasswordReset(resetConfirmModel));
    }


    private ReferrerPasswordResetEntity createReferrerPasswordResetEntity(String code, String uid) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var entity = new ReferrerPasswordResetEntity();
        entity.setUid(uid);
        entity.setExpiredAt(UrlCodeAes128Util.getExpiryDate()); // 24 Hours valid
        entity.setFailures((byte) 0);
        String generatedSecuredPasswordHash = SmsPasscodeHashUtil.generateStorngPasswordHash(code);
        String[] places = generatedSecuredPasswordHash.split(":");
        entity.setPasscodeSalt(places[0]);
        entity.setPasscodeHash(places[1]);
        final String urlCode = SmsPasscodeHashUtil.randomString(32);
        entity.setUrlCode(urlCode);
        return entity;
    }
}
