package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.HospitalPreferencesEntity;



public interface HospitalPreferencesJPARepository extends JpaRepository<HospitalPreferencesEntity, Integer> {
  public List<HospitalPreferencesEntity> findByUsername(String username);
}
