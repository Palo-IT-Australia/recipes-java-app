package au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.ClinicContentEntity;

public interface ClinicContentRepository extends JpaRepository<ClinicContentEntity, Integer> {
	List<ClinicContentEntity> findByName(String name);

	List<ClinicContentEntity> findByPath(String path);

	List<ClinicContentEntity> findByRegionIn(Collection<String> regions);

	List<ClinicContentEntity> findByRegion(String region);

	// Locator with region like and procedure containing
	List<ClinicContentEntity> findByRegionLike(String regionLike);

	List<ClinicContentEntity> findByRegionLikeAndProceduresContaining(String regionLike, String procedure);

	List<ClinicContentEntity> findByProceduresContaining(String procedure);

//  @Query(value ="SELECT DISTINCT * FROM CLINIC_CONTENT as CC where CC.procedures like %?1% or CC.procedures like %?2% or CC.procedures like %?3% or CC.procedures like %?4% or CC.procedures like %?5%", nativeQuery = true)
//  List<ClinicContentEntity> getByModalityLikeIn(String modalityZero, String modalityOne, String modalityTwo, String modalityThree, String modalityFour);
}
