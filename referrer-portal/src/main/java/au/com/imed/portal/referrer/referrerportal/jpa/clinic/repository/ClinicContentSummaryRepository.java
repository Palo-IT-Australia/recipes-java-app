package au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentSummaryEntity;

public interface ClinicContentSummaryRepository extends JpaRepository<ClinicContentSummaryEntity, Integer> {
	List<ClinicContentSummaryEntity> findByName(String name);

	List<ClinicContentSummaryEntity> findByPath(String path);

	List<ClinicContentSummaryEntity> findByRegionIn(Collection<String> regions);

	List<ClinicContentSummaryEntity> findByRegion(String region);

	// Locator with region like and procedure containing
	List<ClinicContentSummaryEntity> findByRegionLike(String regionLike);

	List<ClinicContentSummaryEntity> findByRegionLikeAndProceduresContaining(String regionLike, String procedure);

	List<ClinicContentSummaryEntity> findByProceduresContaining(String procedure);
}
