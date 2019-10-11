package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.CrmProfileEntity;

public interface CrmProfileJpaRepository extends JpaRepository<CrmProfileEntity, Integer> {
	public List<CrmProfileEntity> findByName(String name);
}
