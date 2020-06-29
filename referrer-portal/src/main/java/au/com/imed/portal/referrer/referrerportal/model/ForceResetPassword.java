package au.com.imed.portal.referrer.referrerportal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ForceResetPassword {
	private String uid;
	private String temp;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	
	@Override
	public String toString() {
		return "ForceResetPassword [uid=" + uid + ", temp=" + temp + "]";
	}
	
}
