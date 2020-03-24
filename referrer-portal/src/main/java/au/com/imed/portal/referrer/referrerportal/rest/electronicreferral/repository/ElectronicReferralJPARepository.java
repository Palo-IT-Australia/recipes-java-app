package au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import au.com.imed.portal.referrer.referrerportal.rest.electronicreferral.model.ElectronicReferralForm;

@Repository
public interface ElectronicReferralJPARepository extends JpaRepository<ElectronicReferralForm, Integer> {

}
