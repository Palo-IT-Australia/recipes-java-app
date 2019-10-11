package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmPostcodeEntity;

public interface CrmPostcodeJpaRepository extends JpaRepository<CrmPostcodeEntity, Integer> {
	List<CrmPostcodeEntity> findByPostcodeOrSuburbLike(String postcode, String suburbLike);
	List<CrmPostcodeEntity> findByPostcode(String postcode);
	List<CrmPostcodeEntity> findBySuburbLike(String suburbLike);
}
