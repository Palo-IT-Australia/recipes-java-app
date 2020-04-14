package au.com.imed.portal.referrer.referrerportal.jpa.audit.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CRM_POSTCODE")
public class CrmPostcodeEntity {
	@Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "name")
  private String name;
  
  @Column(name = "postcode")
  private String postcode;

  @Column(name = "suburb")
  private String suburb;

  @Column(name = "bu")
  private String bu;
  
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

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getBu() {
		return bu;
	}

	public void setBu(String bu) {
		this.bu = bu;
	}
  
  
}
