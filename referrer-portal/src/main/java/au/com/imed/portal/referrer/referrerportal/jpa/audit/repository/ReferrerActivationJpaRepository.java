package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerActivationEntity;

public interface ReferrerActivationJpaRepository extends JpaRepository<ReferrerActivationEntity, Integer> {
	List<ReferrerActivationEntity> findByUid(String uid);
	List<ReferrerActivationEntity> findByActivatedAtBetween(Date from, Date to);
}
