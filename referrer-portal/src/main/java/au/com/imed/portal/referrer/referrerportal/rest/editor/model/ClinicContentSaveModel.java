package au.com.imed.portal.referrer.referrerportal.rest.editor.model;

import java.io.Serializable;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;

public class ClinicContentSaveModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private ClinicContentEntity clinic;
  private String imgstr;
  private String infofilestr;
  
  public ClinicContentEntity getClinic() {
    return clinic;
  }
  public void setClinic(ClinicContentEntity clinic) {
    this.clinic = clinic;
  }
  public String getImgstr() {
    return imgstr;
  }
  public void setImgstr(String imgstr) {
    this.imgstr = imgstr;
  }
  public String getInfofilestr() {
    return infofilestr;
  }
  public void setInfofilestr(String infofilestr) {
    this.infofilestr = infofilestr;
  }
  
}
