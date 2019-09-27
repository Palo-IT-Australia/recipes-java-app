package au.com.imed.portal.referrer.referrerportal.jpa.clinic.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import au.com.imed.portal.referrer.referrerportal.jpa.clinic.entity.RadiologistEntity;

public interface RadiologistRepository extends JpaRepository<RadiologistEntity, Integer> {
  public List<RadiologistEntity> findByTitleLike(String title);
  public List<RadiologistEntity> findByName(String name);
  public List<RadiologistEntity> findTop4ByRegionIn(Collection<String> regions);
  public List<RadiologistEntity> findTop4ByRegion(String region);
  
  @Query(value ="SELECT * FROM RADIOLOGIST as RD order by RD.title offset ?1 rows fetch next ?2 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedAll(int start, int num);
  
  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.title like ?1 order by RD.title offset ?2 rows fetch next ?3 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedTitleLike(String titleLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.title like ?1 AND region not in ?2 order by RD.title offset ?3 rows fetch next ?4 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedTitleLikeRegionNotIn(String titleLike, Collection<String> regions, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.title like ?1 AND (RD.region like %?2% OR RD.region like %?3% OR RD.region like %?4% OR RD.region like %?5% OR RD.region like %?6% OR RD.region like %?7% OR RD.region like %?8% OR RD.region like %?9% OR RD.region like %?10% OR RD.region like %?11%) order by RD.title offset ?12 rows fetch next ?13 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedTitleLikeRegionMultiLike(String titleLike, String regionZero, String regionOne, String regionTwo, String regionThree, String regionFour, String regionFive, String regionSix, String regionSeven, String regionEight, String regionNine, int start, int num);
  
  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.title like ?1 AND (RD.region like %?2% OR RD.region like %?3% OR RD.region like %?4% OR RD.region like %?5% OR RD.region like %?6% OR RD.region like %?7% OR RD.region like %?8% OR RD.region like %?9% OR RD.region like %?10% OR RD.region like %?11%) order by RD.region, RD.title offset ?12 rows fetch next ?13 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedTitleLikeRegionMultiLikeOrderByRegionTitle(String titleLike, String regionZero, String regionOne, String regionTwo, String regionThree, String regionFour, String regionFive, String regionSix, String regionSeven, String regionEight, String regionNine, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region like ?1 AND RD.title like ?2 order by RD.title offset ?3 rows fetch next ?4 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionLikeTitleLike(String regionLike, String titleLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region like ?1 AND RD.title like ?2 AND RD.speciality like ?3 order by RD.title offset ?4 rows fetch next ?5 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionLikeTitleLikeSpecialityLike(String regionLike, String titleLike, String specialityLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region like ?1 AND RD.title like ?2 AND RD.skills like ?3 order by RD.title offset ?4 rows fetch next ?5 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionLikeTitleLikeSkillsLike(String regionLike, String titleLike, String skillsLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region = ?1 AND RD.title like ?2 order by RD.title offset ?3 rows fetch next ?4 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionTitleLike(String region, String titleLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region = ?1 AND RD.keyword like ?2 AND RD.title like ?3 order by RD.title offset ?4 rows fetch next ?5 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionKeywowrdLikeTitleLike(String region, String keywordLike, String titleLike, int start, int num);

  @Query(value ="SELECT * FROM RADIOLOGIST as RD where RD.region = ?1 AND RD.title like ?2 AND RD.skills like ?3 order by RD.title offset ?4 rows fetch next ?5 rows only", nativeQuery = true)
  public List<RadiologistEntity> getPagedByRegionTitleLikeSkillsLike(String region, String titleLike, String skillsLike, int start, int num);
}
