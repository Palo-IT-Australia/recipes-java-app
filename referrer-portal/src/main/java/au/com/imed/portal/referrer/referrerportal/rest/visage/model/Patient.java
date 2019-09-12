package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Visage
   "address":    {
      "city": "GLEN WAVERLEY",
      "country": "",
      "line1": "5 Make Believe CT",
      "line2": "",
      "line3": "",
      "postcode": "3150",
      "state": "VIC"
   },
   "dateOfBirth": "1983-08-15",
   "email": "",
   "fax": "",
   "fullName": "TEST, Barney",
   "mobile": "0417909640",
   "patientId": "77.93973",
   "phone1": "",
   "phone2": "",
   "businessUnit": "Norcoray"
   
   Rest
  "name": "Barney Test",
  "dob": "1970-01-01",
  "id": "77.8765432",

  "city": "Sydney",
  "address": "123 Memory Lane",
  "postcode": "2000",
  "state": "NSW"
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Patient {
  private String fullName;
  private String dateOfBirth;
  private String patientId;
  private Address address;
  
  @JsonProperty("name")
  public String getFullName() {
    return fullName;
  }

  @JsonProperty("fullName")
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @JsonProperty("dob")
  public String getDateOfBirth() {
    return dateOfBirth;
  }

  @JsonProperty("dateOfBirth")
  public void setDateOfBirth(String dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  @JsonProperty("id")
  public String getPatientId() {
    return patientId;
  }

  @JsonProperty("patientId")
  public void setPatientId(String patientId) {
    this.patientId = patientId;
  }

  @JsonIgnore
  public Address getAddress() {
    return address;
  }

  @JsonProperty("address")
  public void setAddress(Address address) {
    this.address = address;
  }
  
  @JsonProperty("city")
  public String getAddressCiti() {
    return this.address.getCity();
  }

  @JsonProperty("address")
  public String getAddressLines() {
    String add = this.address.getLine1() + " " + this.address.getLine2() + " " + this.address.getLine3();
    return add.trim();
  }

  @JsonProperty("postcode")
  public String getAddressPostcode() {
    return this.address.getPostcode();
  }

  @JsonProperty("state")
  public String getAddressState() {
    return this.address.getState();
  }

  @JsonIgnoreProperties(ignoreUnknown=true)
  static class Address {
    private String city;
    private String line1;
    private String line2;
    private String line3;
    private String postcode;
    private String state;
    
    public String getCity() {
      return city;
    }
    public void setCity(String city) {
      this.city = city;
    }
    public String getLine1() {
      return line1;
    }
    public void setLine1(String line1) {
      this.line1 = line1;
    }
    public String getLine2() {
      return line2;
    }
    public void setLine2(String line2) {
      this.line2 = line2;
    }
    public String getLine3() {
      return line3;
    }
    public void setLine3(String line3) {
      this.line3 = line3;
    }
    public String getPostcode() {
      return postcode;
    }
    public void setPostcode(String postcode) {
      this.postcode = postcode;
    }
    public String getState() {
      return state;
    }
    public void setState(String state) {
      this.state = state;
    }
    
  }
}
