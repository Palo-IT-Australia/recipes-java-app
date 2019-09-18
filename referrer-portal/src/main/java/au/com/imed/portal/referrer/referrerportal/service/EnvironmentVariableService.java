package au.com.imed.portal.referrer.referrerportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.EnvironmentVariableEntity;
import au.com.imed.portal.referrer.referrerportal.jpa.audit.repository.EnvironmentVariableRepository;

@Service
public class EnvironmentVariableService {

	@Autowired
	EnvironmentVariableRepository environmentVariableRepository;

	public String getValue(String key) {
		EnvironmentVariableEntity environmentVariableEntity = environmentVariableRepository.findByName(key);
		String value = environmentVariableEntity != null ? environmentVariableEntity.getValue() : null;
		return value;
	}

}
