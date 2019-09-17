package au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "CLINIC_CONTENT")
public class ClinicContentSummaryEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private int id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "path", nullable = false)
  private String path;

  @Column(name = "address")
  private String address;

  @Column(name = "suburb")
  private String suburb;

  @Column(name = "state")
  private String state;

  @Column(name = "postcode")
  private String postcode;

  @Column(name = "region")
  private String region;

  @Column(name = "phone")
  private String phone;

  @Column(name = "lon")
  private String lon;

  @Column(name = "lat")
  private String lat;
  
  @Column(name = "mapurl")
  private String mapurl;
  
  @Column(name = "procedures")
  private String procedures;

  @Column(name = "hours")
  private String hours;
  
  @Column(name = "fax")
  private String fax;
  
  @Column(name = "infofilename")
  private String infofilename;
  
  @Column(name ="bookurl")
  private String bookurl;
  
  @Transient
  private double distance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getMapurl() {
		return mapurl;
	}

	public void setMapurl(String mapurl) {
		this.mapurl = mapurl;
	}

	public String getProcedures() {
		return procedures;
	}

	public void setProcedures(String procedures) {
		this.procedures = procedures;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getInfofilename() {
		return infofilename;
	}

	public void setInfofilename(String infofilename) {
		this.infofilename = infofilename;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public String getBookurl() {
		return bookurl;
	}

	public void setBookurl(String bookurl) {
		this.bookurl = bookurl;
	}

}
