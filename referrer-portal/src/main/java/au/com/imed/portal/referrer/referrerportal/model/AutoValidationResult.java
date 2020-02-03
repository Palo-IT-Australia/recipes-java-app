package au.com.imed.portal.referrer.referrerportal.model;

public class AutoValidationResult {
	private boolean isValid;
	private String msg;
	
	public AutoValidationResult(boolean isValid, String msg) {
		this.isValid = isValid;
		this.msg = msg;
	}
	
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
