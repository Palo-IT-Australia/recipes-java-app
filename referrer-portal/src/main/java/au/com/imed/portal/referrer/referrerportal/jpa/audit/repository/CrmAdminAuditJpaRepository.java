package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmAdminAuditEntity;

public interface CrmAdminAuditJpaRepository extends JpaRepository<CrmAdminAuditEntity, Integer> {
	public List<CrmAdminAuditEntity> findByCommandAndReferrerOrderByAuditAtDesc(String command, String referrer);
	public List<CrmAdminAuditEntity> findByCommandAndReferrerAndValidationIdOrderByAuditAtDesc(String command, String referrer, int validationId);
	public List<CrmAdminAuditEntity> findByAuditAtBetween(Date from, Date to);
}
