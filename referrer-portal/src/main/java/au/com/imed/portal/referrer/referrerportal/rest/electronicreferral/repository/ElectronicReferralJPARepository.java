package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;

@Repository
public interface ElectronicReferralJPARepository extends JpaRepository<ElectronicReferralForm, Integer> {
	List<ElectronicReferralForm> findBySubmittedTimeBetweenOrderByIdAsc(Date startTime, Date endTime);
}
