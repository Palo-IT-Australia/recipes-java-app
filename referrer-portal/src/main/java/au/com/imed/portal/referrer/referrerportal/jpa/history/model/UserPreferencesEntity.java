package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VISAGE_USER_PREFERENCES", catalog = "dbo")
public class UserPreferencesEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;

  @Column(name = "help")
  private String help;

  @Column(name = "query")
  private String query;
  
  @Column(name = "autoimg")
  private String autoimg;
  
  @Column(name = "notify")
  private String notify;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getAutoimg() {
    return autoimg;
  }

  public void setAutoimg(String autoimg) {
    this.autoimg = autoimg;
  }

  public String getNotify() {
    return notify;
  }

  public void setNotify(String notify) {
    this.notify = notify;
  }

}

