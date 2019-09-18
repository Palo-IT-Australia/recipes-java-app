package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReportAccessEntity;

public interface ReportAccessRepository extends JpaRepository<ReportAccessEntity, Integer> {
  List<ReportAccessEntity> findByUrlCode(String urlCode);
}
