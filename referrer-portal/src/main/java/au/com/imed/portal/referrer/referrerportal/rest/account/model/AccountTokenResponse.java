package au.com.imed.portal.referrer.referrerportal.rest.account.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountTokenResponse {

    private String type;
    private String token;
}
