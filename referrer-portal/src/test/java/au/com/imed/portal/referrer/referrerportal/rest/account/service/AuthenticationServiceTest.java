package au.com.imed.portal.referrer.referrerportal.rest.account.service;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.RefreshToken;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.RefreshTokenRepository;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.AuthenticationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    @InjectMocks
    private AuthenticationService service;

    @Test
    public void shouldCreateRefreshToken() throws Exception {
        try (MockedStatic<AuthenticationUtil> authUtilMock = Mockito.mockStatic(AuthenticationUtil.class)) {
            authUtilMock.when(() -> AuthenticationUtil.createImolRefreshToken("uid")).thenReturn("refresh-token");

            var result = service.createRefreshToken("uid");
            assertEquals("refresh-token", result);
        }
    }

    @Test
    public void shouldInvalidatePreviousTokensOnNewTokenCreate() throws Exception {
        try (MockedStatic<AuthenticationUtil> authUtilMock = Mockito.mockStatic(AuthenticationUtil.class)) {
            ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
            authUtilMock.when(() -> AuthenticationUtil.createImolRefreshToken("uid")).thenReturn("refresh-token");

            var previousTokens = singletonList(new RefreshToken());
            when(repository.findAllByUserIdAndValid("uid", true)).thenReturn(previousTokens);

            service.createRefreshToken("uid");

            verify(repository, times(2)).save(tokenCaptor.capture());
            assertFalse(tokenCaptor.getAllValues().get(0).isValid());
        }
    }

    @Test
    public void shouldCheckRefreshToken() {
        try (MockedStatic<AuthenticationUtil> authUtilMock = Mockito.mockStatic(AuthenticationUtil.class)) {
            authUtilMock.when(() -> AuthenticationUtil.checkRefreshToken("Bearer token")).thenReturn("uid");

            when(repository.findByRefreshToken(anyString())).thenReturn(new RefreshToken("uid", "token"));

            var userId = service.checkRefreshToken("token");

            assertEquals("uid", userId);
        }
    }

    @Test(expected = AuthenticationException.class)
    public void shouldNotValidateOldRefreshToken() {
        try (MockedStatic<AuthenticationUtil> authUtilMock = Mockito.mockStatic(AuthenticationUtil.class)) {
            authUtilMock.when(() -> AuthenticationUtil.checkRefreshToken("Bearer token")).thenReturn("uid");

            var token = new RefreshToken("uid", "token");
            token.setValid(false);
            when(repository.findByRefreshToken(anyString())).thenReturn(token);

            service.checkRefreshToken("token");
        }
    }

    @Test(expected = AuthenticationException.class)
    public void shouldNotValidateIfRefreshTokenNotFoundFromRepo() {
        try (MockedStatic<AuthenticationUtil> authUtilMock = Mockito.mockStatic(AuthenticationUtil.class)) {
            authUtilMock.when(() -> AuthenticationUtil.checkRefreshToken("Bearer token")).thenReturn("uid");

            when(repository.findByRefreshToken(anyString())).thenReturn(null);

            service.checkRefreshToken("token");
        }
    }
}
