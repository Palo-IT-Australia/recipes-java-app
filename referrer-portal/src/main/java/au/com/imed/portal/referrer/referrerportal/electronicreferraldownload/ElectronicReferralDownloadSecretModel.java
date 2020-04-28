package au.com.imed.portal.referrer.referrerportal.electronicreferraldownload;

public class ElectronicReferralDownloadSecretModel {
	private int tableId;
	private String mode;
	private String date;
	
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "ElectronicReferralDownloadSecretModel [tableId=" + tableId + ", mode=" + mode + ", date=" + date + "]";
	}
	
}
