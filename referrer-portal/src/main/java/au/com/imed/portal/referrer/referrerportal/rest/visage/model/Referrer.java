package au.com.imed.portal.referrer.referrerportal.rest.visage.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Referrer {
  private String uri;
  private String name;
  private String email;
  private String mobile;
  private Practice [] practices;
  
  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public Practice[] getPractices() {
    return practices;
  }

  public void setPractices(Practice[] practices) {
    this.practices = practices;
  }

  @JsonIgnoreProperties(ignoreUnknown=true)
  public static class Practice {    
    private String uri;
    private String fullName;
    private String practiceName;
    private String providerNumber;
    private String speciality;
    private String phone1;
    private String phone2;
    private String mobile;
    private String email;
    private String fax;
    private String businessUnit;
    private Address address;
    
    public String getUri() {
      return uri;
    }

    public void setUri(String uri) {
      this.uri = uri;
    }

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

    public String getPracticeName() {
      return practiceName;
    }

    public void setPracticeName(String practiceName) {
      this.practiceName = practiceName;
    }

    public String getProviderNumber() {
      return providerNumber;
    }

    public void setProviderNumber(String providerNumber) {
      this.providerNumber = providerNumber;
    }

    public String getSpeciality() {
      return speciality;
    }

    public void setSpeciality(String speciality) {
      this.speciality = speciality;
    }

    public String getPhone1() {
      return phone1;
    }

    public void setPhone1(String phone1) {
      this.phone1 = phone1;
    }

    public String getPhone2() {
      return phone2;
    }

    public void setPhone2(String phone2) {
      this.phone2 = phone2;
    }

    public String getMobile() {
      return mobile;
    }

    public void setMobile(String mobile) {
      this.mobile = mobile;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getFax() {
      return fax;
    }

    public void setFax(String fax) {
      this.fax = fax;
    }

    public String getBusinessUnit() {
      return businessUnit;
    }

    public void setBusinessUnit(String businessUnit) {
      this.businessUnit = businessUnit;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class Address {
      private String line1;
      private String line2;
      private String line3;
      private String city;
      private String state;
      private String postcode;
      private String country;
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
      public String getCity() {
        return city;
      }
      public void setCity(String city) {
        this.city = city;
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
      public String getCountry() {
        return country;
      }
      public void setCountry(String country) {
        this.country = country;
      }
      
    }
  }

}
