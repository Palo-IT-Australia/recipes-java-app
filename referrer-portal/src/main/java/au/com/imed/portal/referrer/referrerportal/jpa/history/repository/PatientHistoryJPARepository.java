package au.com.imed.portal.referrer.referrerportal.jpa.history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import au.com.imed.portal.referrer.referrerportal.jpa.history.model.PatientHistoryEntity;

public interface PatientHistoryJPARepository extends JpaRepository<PatientHistoryEntity, Integer> {
	List<PatientHistoryEntity> findByUsernameAndPatientUri(String username, String patientUri);

	@Query(value = "SELECT TOP 20 * from VISAGE_PATIENT_HISTORY as HIS where HIS.username = ?1 order by HIS.modified_at desc", nativeQuery = true)
	List<PatientHistoryEntity> getHistories(String username);
}
