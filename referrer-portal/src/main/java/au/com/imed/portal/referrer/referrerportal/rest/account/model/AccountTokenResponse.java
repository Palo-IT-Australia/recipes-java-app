package au.com.imed.portal.referrer.referrerportal.rest.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountTokenResponse {
    private final String type;
    private final String token;
    private final String refreshToken;
}
