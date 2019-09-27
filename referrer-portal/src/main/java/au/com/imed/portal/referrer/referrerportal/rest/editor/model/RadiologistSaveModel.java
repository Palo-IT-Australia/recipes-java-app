package au.com.imed.portal.referrer.referrerportal.rest.editor.model;

import java.io.Serializable;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.RadiologistEntity;

public class RadiologistSaveModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private RadiologistEntity radiologist;
  private String imgstr;
  
  public RadiologistEntity getRadiologist() {
    return radiologist;
  }
  public void setRadiologist(RadiologistEntity radiologist) {
    this.radiologist = radiologist;
  }
  public String getImgstr() {
    return imgstr;
  }
  public void setImgstr(String imgstr) {
    this.imgstr = imgstr;
  }
 
}
