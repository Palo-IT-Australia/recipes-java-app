package au.com.imed.portal.referrer.referrerportal.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
{
  "Success": true,
  "Message": "string",
  "ValidationErrors": [
    "string"
  ],
  "Response": 0
} *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SmsSendResponse {
  private boolean Success;
  private String Message;
  private String [] ValidationErrors;
  private int Response;
  
  public boolean isSuccess() {
    return Success;
  }
  public void setSuccess(boolean success) {
    Success = success;
  }
  public String getMessage() {
    return Message;
  }
  public void setMessage(String message) {
    Message = message;
  }
  public String[] getValidationErrors() {
    return ValidationErrors;
  }
  public void setValidationErrors(String[] validationErrors) {
    ValidationErrors = validationErrors;
  }
  public int getResponse() {
    return Response;
  }
  public void setResponse(int response) {
    Response = response;
  }
}
