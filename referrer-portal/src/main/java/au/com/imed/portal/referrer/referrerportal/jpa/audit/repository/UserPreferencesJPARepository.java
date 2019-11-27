package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.UserPreferencesEntity;


public interface UserPreferencesJPARepository extends JpaRepository<UserPreferencesEntity, Integer> {
	public List<UserPreferencesEntity> findByUsername(String username);
	public List<UserPreferencesEntity> findByAutoimgOrNotify(String autoimg, String notify);
}
