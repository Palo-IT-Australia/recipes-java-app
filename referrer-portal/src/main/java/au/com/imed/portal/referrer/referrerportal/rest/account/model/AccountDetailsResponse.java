package au.com.imed.portal.referrer.referrerportal.rest.account.model;

import au.com.imed.portal.referrer.referrerportal.rest.visage.model.Referrer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountDetailsResponse {
    private final String email;
    private final String mobile;
    private final String displayName;
    private final Referrer.Practice[] practices;
}
