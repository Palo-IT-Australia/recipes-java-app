package au.com.imed.portal.referrer.referrerportal.rest.account.service;

import au.com.imed.portal.referrer.referrerportal.email.ReferrerMailService;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;
import au.com.imed.portal.referrer.referrerportal.model.AccountDetail;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.EmailException;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.SmsException;
import au.com.imed.portal.referrer.referrerportal.service.ConfirmProcessDataService;
import au.com.imed.portal.referrer.referrerportal.sms.GoFaxSmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

        userAccountService.confirmPasswordReset(accountDetail);

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

        userAccountService.confirmPasswordReset(accountDetail);

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

        userAccountService.confirmPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowExceptionOnEmptyPhoneNumber() {
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(null);

        userAccountService.confirmPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowEmailExceptionOnEmptyEmail() {
        var phone1 = "04040404041";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(null);
        accountDetail.setEmail(null);
        accountDetail.setMobile(phone1);

        userAccountService.confirmPasswordReset(accountDetail);
    }

    @Test(expected = EmailException.class)
    public void shouldThrowEmailExceptionOnNonExistingEmail() {
        var phone1 = "04040404041";
        var email1 = "test.test@palo-it.com";
        AccountDetail accountDetail = new AccountDetail();
        accountDetail.setUid(email1);
        accountDetail.setEmail(email1);
        accountDetail.setMobile(phone1);

        userAccountService.confirmPasswordReset(accountDetail);
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
        userAccountService.confirmPasswordReset(accountDetail);
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
        userAccountService.confirmPasswordReset(accountDetail);
    }
}
