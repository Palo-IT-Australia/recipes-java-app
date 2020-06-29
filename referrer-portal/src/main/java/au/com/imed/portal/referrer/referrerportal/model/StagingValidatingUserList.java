package au.com.imed.portal.referrer.referrerportal.model;

import java.util.List;

public class StagingValidatingUserList {
	private List<StageUser> stagings;
  private List<StageUser> validatings;
  
	public List<StageUser> getStagings() {
		return stagings;
	}
	public void setStagings(List<StageUser> stagings) {
		this.stagings = stagings;
	}
	public List<StageUser> getValidatings() {
		return validatings;
	}
	public void setValidatings(List<StageUser> validatings) {
		this.validatings = validatings;
	}  
}
