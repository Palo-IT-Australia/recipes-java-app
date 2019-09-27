package au.com.imed.portal.referrer.referrerportal.rest.editor.model;

import java.io.Serializable;

public class SimpleResultModel implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private String msg;
  private String sts;
  
  public SimpleResultModel() {
  }
  public SimpleResultModel(String msg, String sts) {
    this.msg = msg;
    this.sts = sts;
  }
  
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
  public String getSts() {
    return sts;
  }
  public void setSts(String sts) {
    this.sts = sts;
  }
  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
