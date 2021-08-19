package au.com.imed.portal.referrer.referrerportal.rest.account.service;

import au.com.imed.portal.referrer.referrerportal.common.util.AuthenticationUtil;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.RefreshToken;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.RefreshTokenRepository;
import au.com.imed.portal.referrer.referrerportal.rest.account.error.AuthenticationException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Log4j
@Service
public class AuthenticationService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String createRefreshToken(String uid) {
        try {
            var refreshToken = AuthenticationUtil.createImolRefreshToken(uid);
            invalidateActiveTokens(uid);
            refreshTokenRepository.save(new RefreshToken(uid, getTokenHash(refreshToken)));
            return refreshToken;
        } catch (Exception e) {
            throw new RuntimeException("Error creating refresh token");
        }
    }

    public String checkRefreshToken(String refreshToken) {
        try {
            var uid = AuthenticationUtil.checkRefreshToken("Bearer " + refreshToken);
            if (uid == null) {
                throw new AuthenticationException("Refresh token expired");
            }
            validateTokens(refreshToken, uid);
            return uid;
        } catch (Exception e) {
            throw new AuthenticationException("Could not validate refresh token");
        }
    }

    private void validateTokens(String refreshToken, String uid) throws NoSuchAlgorithmException {
        var tokenEntity = refreshTokenRepository.findByRefreshToken(getTokenHash(refreshToken));
        if (tokenEntity == null) {
            throw new AuthenticationException("Invalid refresh token");
        } else if (!tokenEntity.isValid()) {
            invalidateActiveTokens(uid);
            throw new AuthenticationException("Invalid refresh token");
        }
    }


    private void invalidateActiveTokens(String uid) {
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserIdAndValid(uid, true);
        tokens.forEach(token -> {
            token.setValid(false);
            refreshTokenRepository.save(token);
        });
    }

    private String getTokenHash(String refreshToken) throws NoSuchAlgorithmException {
        var messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(refreshToken.getBytes());
        var hexString = new StringBuilder();

        for (byte b : messageDigest.digest()) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString();
    }
}
