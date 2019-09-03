package au.com.imed.portal.referrer.referrerportal.model;

public class ResetConfirmModel {
	private String secret;
	private String password;
	private String passcode;
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasscode() {
		return passcode;
	}
	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}
	@Override
	public String toString() {
		return "ResetConfirmModel [secret=" + secret + ", password=" + password + ", passcode=" + passcode + "]";
	}
	
}
