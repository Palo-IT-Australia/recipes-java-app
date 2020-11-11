package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAccountDeactivationAuditEneity;

public interface ReferrerAccountDeactivationAuditJpaRepository extends JpaRepository<ReferrerAccountDeactivationAuditEneity, Integer> {
	public List<ReferrerAccountDeactivationAuditEneity> findByUsername(String username);
	public List<ReferrerAccountDeactivationAuditEneity> findByAuditAtBetween(Date from, Date to);
}
