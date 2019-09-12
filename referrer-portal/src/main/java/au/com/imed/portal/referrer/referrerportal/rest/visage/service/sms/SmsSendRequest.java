package au.com.imed.portal.referrer.referrerportal.rest.visage.service.sms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
{
  "To": [
    "string"
  ],
  "From": "string",
  "ReplyTo": "string",
  "ClientReference": "string",
  "Body": "string"
} *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SmsSendRequest {
  private String [] To;
  private String From;
  private String ReplyTo;
  private String ClientReference;
  private String Body;
  
  public String[] getTo() {
    return To;
  }
  public void setTo(String[] to) {
    To = to;
  }
  public String getFrom() {
    return From;
  }
  public void setFrom(String from) {
    From = from;
  }
  public String getReplyTo() {
    return ReplyTo;
  }
  public void setReplyTo(String replyTo) {
    ReplyTo = replyTo;
  }
  public String getClientReference() {
    return ClientReference;
  }
  public void setClientReference(String clientReference) {
    ClientReference = clientReference;
  }
  public String getBody() {
    return Body;
  }
  public void setBody(String body) {
    Body = body;
  }
  
}
