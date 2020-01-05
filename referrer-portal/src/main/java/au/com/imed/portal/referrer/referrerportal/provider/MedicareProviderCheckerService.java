package au.com.imed.portal.referrer.referrerportal.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.MedicareProviderJpaRepository;

@Service
public class MedicareProviderCheckerService {
	@Autowired
	private MedicareProviderJpaRepository medicareProviderRepository;
	
	public boolean isProviderNumberValid(final String providerNumber) {
		boolean isValid = false;
		if(providerNumber != null && providerNumber.length() > 0) {
			isValid = medicareProviderRepository.findByProviderNumber(providerNumber).size() > 0;
		}
		return isValid;
	}
}
