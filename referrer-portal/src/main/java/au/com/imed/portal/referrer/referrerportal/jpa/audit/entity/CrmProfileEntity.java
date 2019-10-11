package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CRM_PROFILE")
public class CrmProfileEntity {
	@Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "name")
  private String name;
  @Column(name = "region")
  private String region;
  @Column(name = "phone")
  private String phone;
  @Column(name = "email")
  private String email;
  
  @JsonIgnore
  @Column(name = "imgstr")
  private String imgstr;
  
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getImgstr() {
		return imgstr;
	}
	public void setImgstr(String imgstr) {
		this.imgstr = imgstr;
	}
  
  
}
