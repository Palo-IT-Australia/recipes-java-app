package au.com.imed.portal.referrer.referrerportal.jpa.audit.repository;


import org.springframework.data.repository.CrudRepository;

import au.com.imed.portal.referrer.referrerportal.jpa.audit.entity.EnvironmentVariableEntity;

public interface EnvironmentVariableRepository extends CrudRepository<EnvironmentVariableEntity, Integer> {
	EnvironmentVariableEntity findByName(String name);
}
