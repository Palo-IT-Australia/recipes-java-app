package au.com.imed.portal.referrer.referrerportal.model;

import java.util.List;

public class StagingUserList {
  private List<StageUser> stagings;
  private List<StageUser> finalisings;
  
  public List<StageUser> getStagings() {
    return stagings;
  }
  public void setStagings(List<StageUser> stagings) {
    this.stagings = stagings;
  }
  public List<StageUser> getFinalisings() {
    return finalisings;
  }
  public void setFinalisings(List<StageUser> finalisings) {
    this.finalisings = finalisings;
  }
}
