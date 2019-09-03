package au.com.imed.portal.referrer.referrerportal.jpa.clinic;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "au.com.imed.portal.referrer.referrerportal.jpa.clinic",
entityManagerFactoryRef = "clinicEntityManagerFactory", 
transactionManagerRef = "clinicTransactionManager")
public class ClinicJpaConfig {
  @Bean(name="clinicEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean clinicEntityManagerFactory(EntityManagerFactoryBuilder builder) {
  	return builder
  			.dataSource(clinicDataSource())
  			.packages("au.com.imed.portal.referrer.referrerportal.jpa.clinic")
  			.persistenceUnit("IMED_CLINICDB")
  			.build();
  }
	
	@Bean
	@ConfigurationProperties("sqlserver2.datasource")
	public DataSourceProperties clinicDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource clinicDataSource() {
		return clinicDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class).build();
	}

  @Bean(name="clinicTransactionManager")
  public PlatformTransactionManager clinicTransactionManager(@Qualifier("clinicEntityManagerFactory")
  EntityManagerFactory clinicEntityManagerFactory) {
  	return new JpaTransactionManager(clinicEntityManagerFactory);
  }

  @Bean(name="clinicExceptionTranslation")
  public PersistenceExceptionTranslationPostProcessor clinicExceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}

