package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.VisageRequestAuditEntity;

public interface VisageRequestAuditJPARepository extends JpaRepository<VisageRequestAuditEntity, Integer> {
	// where audit_at >= '2016-11-09' AND audit_at < DATEADD(d,1,'2016-11-09')
	@Query(value = "SELECT * from VISAGE_REQUEST_AUDIT as VRA where VRA.audit_at >= ?1 and VRA.audit_at < DATEADD(d,1,?2)", nativeQuery = true)
	public List<VisageRequestAuditEntity> getBetween(String startDate, String endDate);

	public Integer countByUsernameAndAuditAtGreaterThan(String username, Date from);
	
	@Query(value = "SELECT DISTINCT username from VISAGE_REQUEST_AUDIT as VRA where VRA.audit_at >= ?1 and VRA.audit_at < ?2", nativeQuery = true)
	public List<String> getDistinctUsernamesBetween(Date from, Date to);
}
