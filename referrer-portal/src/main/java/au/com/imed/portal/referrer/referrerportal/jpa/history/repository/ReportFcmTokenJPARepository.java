package au.com.imed.portal.referrer.referrerportal.jpa.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.ReportFcmTokenEntity;


public interface ReportFcmTokenJPARepository extends JpaRepository<ReportFcmTokenEntity, Integer> {
  public List<ReportFcmTokenEntity> findByDeviceId(String deviceId);
}
