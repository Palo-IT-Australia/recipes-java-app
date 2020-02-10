package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerAutoValidationEntity;

public interface ReferrerAutoValidationRepository extends JpaRepository<ReferrerAutoValidationEntity, Integer> 
{
	public List<ReferrerAutoValidationEntity> findByUid(String uid);
	public List<ReferrerAutoValidationEntity> findByUidAndValidationStatus(String uid, String status);
	public List<ReferrerAutoValidationEntity> findByUidAndValidationStatusNot(String uid, String status);
	public List<ReferrerAutoValidationEntity> findByEmailAndValidationStatusNot(String email, String status);
	public List<ReferrerAutoValidationEntity> findByAhpraAndValidationStatusNot(String ahpra, String status);
	public List<ReferrerAutoValidationEntity> findByUidLikeAndValidationStatusNot(String uidLike, String status);
	public List<ReferrerAutoValidationEntity> findByApplyAtBetween(Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByAccountAtBetween(Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByNotifyAtBetween(Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByValidationStatusAndApplyAtBetween(String status, Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByValidationStatusAndAccountAtBetween(String status, Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByValidationStatusAndAccountAtBetweenAndNotifyAtIsNull(String status, Date from, Date to);
	public List<ReferrerAutoValidationEntity> findByValidationStatusAndNotifyAtBetween(String status, Date from, Date to);
}
