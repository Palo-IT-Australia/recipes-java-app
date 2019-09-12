package au.com.imed.portal.referrer.referrerportal.jpa.history.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="VISAGE_REQUEST_AUDIT", catalog = "dbo")
public class RequestAuditEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  @Column(name = "ID", unique = true, nullable = false)
  private int id;
  
  @Column(name = "username")
  private String username;

  @Column(name = "command")
  private String command;

  @Column(name = "parameters")
  private String parameters;

  @Column(name = "break_glass")
  private String breakGlass;
  
  @Column(name = "audit_at")
  private Date auditAt;

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

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getBreakGlass() {
    return breakGlass;
  }

  public void setBreakGlass(String breakGlass) {
    this.breakGlass = breakGlass;
  }

  public Date getAuditAt() {
    return auditAt;
  }

  public void setAuditAt(Date auditAt) {
    this.auditAt = auditAt;
  }
  
  
}
