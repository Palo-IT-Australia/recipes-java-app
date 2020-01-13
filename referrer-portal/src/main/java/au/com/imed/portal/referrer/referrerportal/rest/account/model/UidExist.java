package au.com.imed.portal.referrer.referrerportal.rest.account.model;

public class UidExist {
	private boolean pacs;
	private boolean imedPacs;
	private boolean visage;
	
	public boolean isPacs() {
		return pacs;
	}
	public void setPacs(boolean pacs) {
		this.pacs = pacs;
	}
	public boolean isImedPacs() {
		return imedPacs;
	}
	public void setImedPacs(boolean imedPacs) {
		this.imedPacs = imedPacs;
	}
	public boolean isVisage() {
		return visage;
	}
	public void setVisage(boolean visage) {
		this.visage = visage;
	}
	
}
