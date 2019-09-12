package au.com.imed.portal.referrer.referrerportal.rest.account.model;

public class AccountLockUnlock {
	private String uid;
	private boolean lock;
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public boolean isLock() {
		return lock;
	}
	public void setLock(boolean lock) {
		this.lock = lock;
	}

}
