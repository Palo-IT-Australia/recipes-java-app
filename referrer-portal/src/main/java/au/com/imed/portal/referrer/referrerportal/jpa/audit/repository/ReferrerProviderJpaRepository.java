package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.ReferrerProviderEntity;

public interface ReferrerProviderJpaRepository extends JpaRepository<ReferrerProviderEntity, Integer> {
  public List<ReferrerProviderEntity> findByUsername(String username);
  public List<ReferrerProviderEntity> findByProviderNumber(String providerNumber);
}
