package au.com.imed.portal.referrer.referrerportal.jpa.history.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.RequestAuditEntity;


public interface RequestAuditJPARepository extends JpaRepository<RequestAuditEntity, Integer> {
  // where audit_at >= '2016-11-09' AND audit_at < DATEADD(d,1,'2016-11-09')      
  @Query(value = "SELECT * from VISAGE_REQUEST_AUDIT as VRA where VRA.audit_at >= ?1 and VRA.audit_at < DATEADD(d,1,?2)", nativeQuery = true)
  public List<RequestAuditEntity> getBetween(String startDate, String endDate);
  
  public Integer countByUsernameAndAuditAtGreaterThan(String username, Date from);
  
  public List<RequestAuditEntity> findByUsernameAndCommand(String usermane, String command);
}
