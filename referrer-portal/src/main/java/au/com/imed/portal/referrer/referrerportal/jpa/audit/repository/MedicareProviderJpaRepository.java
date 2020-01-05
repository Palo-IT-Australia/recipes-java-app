package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.MedicareProviderEntity;

public interface MedicareProviderJpaRepository extends JpaRepository<MedicareProviderEntity, Integer> {
	List<MedicareProviderEntity> findByProviderNumber(String providerNumber);
	List<MedicareProviderEntity> findByFirstNameAndLastName(String firstName, String lastName);
}
