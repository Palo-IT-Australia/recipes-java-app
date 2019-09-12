package au.com.imed.portal.referrer.referrerportal.jpa.history.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.ReportNotificationEntity;

public interface ReportNotificationJPARepository extends JpaRepository<ReportNotificationEntity, Integer> {
	public List<ReportNotificationEntity> findByUid(String uid);

	public List<ReportNotificationEntity> findByMessageAtGreaterThanEqual(Date from);

	public List<ReportNotificationEntity> findByUidAndMessageAtGreaterThanEqual(String uid, Date from);
}
