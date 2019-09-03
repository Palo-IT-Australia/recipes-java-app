package au.com.imed.portal.referrer.referrerportal.rest.account.model;

import java.io.Serializable;

public class UniquenessModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private boolean isAvailable;

  public boolean isAvailable() {
    return isAvailable;
  }

  public void setAvailable(boolean isAvailable) {
    this.isAvailable = isAvailable;
  }
  
}
