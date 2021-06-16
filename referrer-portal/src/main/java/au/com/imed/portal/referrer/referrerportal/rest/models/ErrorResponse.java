package au.com.imed.portal.referrer.referrerportal.rest.models;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private int code;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
