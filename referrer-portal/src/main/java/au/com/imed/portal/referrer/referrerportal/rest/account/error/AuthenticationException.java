package au.com.imed.portal.referrer.referrerportal.rest.account.error;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
