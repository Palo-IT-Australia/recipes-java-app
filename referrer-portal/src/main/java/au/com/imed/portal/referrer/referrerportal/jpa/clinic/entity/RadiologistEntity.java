package au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "RADIOLOGIST")
public class RadiologistEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private int id;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "imgurl")
  private String imgurl;

  @Column(name = "keyword")
  private String keyword;

  @Column(name = "region")
  private String region;

  @Column(name = "speciality")
  private String speciality;

  @Column(name = "description")
  private String description;

  @Column(name = "skills")
  private String skills;

  @Column(name = "intro")
  private String intro;

  @Column(name = "card")
  private String card;
  
  @JsonIgnore
  @Column(name = "imgbin")
  private byte [] imgbin;
  
  @Column(name = "position")
  private String position;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImgurl() {
    return imgurl;
  }

  public void setImgurl(String imgurl) {
    this.imgurl = imgurl;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getSpeciality() {
    return speciality;
  }

  public void setSpeciality(String speciality) {
    this.speciality = speciality;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getSkills() {
    return skills;
  }

  public void setSkills(String skills) {
    this.skills = skills;
  }

  public String getIntro() {
    return intro;
  }

  public void setIntro(String intro) {
    this.intro = intro;
  }

  public String getCard() {
    return card;
  }

  public void setCard(String card) {
    this.card = card;
  }

  public byte[] getImgbin() {
    return imgbin;
  }

  public void setImgbin(byte[] imgbin) {
    this.imgbin = imgbin;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }
}
