package au.com.imed.portal.referrer.referrerportal.model;

public class AddPractice {
	private String providerNumber;
	private String name;
	private String phone;
	private String fax;
	private String street;
	private String suburb;
	private String state;
	private String postcode;

	public String getProviderNumber() {
		return providerNumber;
	}
	public void setProviderNumber(String providerNumber) {
		this.providerNumber = providerNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getSuburb() {
		return suburb;
	}
	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	@Override
	public String toString() {
		return "AddPractice [providerNumber=" + providerNumber + ", name=" + name + ", phone=" + phone + ", fax=" + fax
				+ ", street=" + street + ", suburb=" + suburb + ", state=" + state + ", postcode=" + postcode + "]";
	}
}
