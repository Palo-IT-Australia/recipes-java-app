package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerPasswordResetEntity;

public interface ReferrerPasswordResetRepository extends JpaRepository<ReferrerPasswordResetEntity, Integer> {
  List<ReferrerPasswordResetEntity> findByUid(String uid);
  
  List<ReferrerPasswordResetEntity> findByUrlCodeAndFailuresLessThanAndExpiredAtAfterAndActivatedAtIsNull(String urlCode, byte failures, Date now); // confirm url check
  List<ReferrerPasswordResetEntity> findByUidAndFailuresLessThanAndExpiredAtAfterAndActivatedAtIsNull(String uid, byte failures, Date now);  // available for uid
}
