package au.com.imed.portal.referrer.referrerportal.jpa.audit;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"au.com.imed.portal.referrer.referrerportal.jpa.audit","au.com.imed.portal.referrer.referrerportal.jpa.history"},
entityManagerFactoryRef = "auditEntityManagerFactory", 
transactionManagerRef = "auditTransactionManager")
public class AuditJpaConfig {
	@Primary
  @Bean(name="auditEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean auditEntityManagerFactory(EntityManagerFactoryBuilder builder) {
  	return builder
  			.dataSource(auditDataSource())
  			.packages("au.com.imed.portal.referrer.referrerportal.jpa.audit", "au.com.imed.portal.referrer.referrerportal.jpa.history")
  			.persistenceUnit("IMED_AUDITDB")
  			.build();
  }

	@Bean
	@Primary
	@ConfigurationProperties("sqlserver1.datasource")
	public DataSourceProperties auditDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public HikariDataSource auditDataSource() {
		return auditDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class).build();
	}
	
  @Primary
  @Bean(name="auditTransactionManager")
  public PlatformTransactionManager auditTransactionManager(@Qualifier("auditEntityManagerFactory")
  EntityManagerFactory auditEntityManagerFactory) {
  	return new JpaTransactionManager(auditEntityManagerFactory);
  }

  @Primary
  @Bean(name="auditExceptionTranslation")
  public PersistenceExceptionTranslationPostProcessor auditExceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }
}

