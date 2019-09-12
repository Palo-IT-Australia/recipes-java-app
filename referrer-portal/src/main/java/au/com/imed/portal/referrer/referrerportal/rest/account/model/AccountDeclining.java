package au.com.imed.portal.referrer.referrerportal.rest.account.model;

public class AccountDeclining {
	private String uid ;
	private String reason;
	private String step;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
}
