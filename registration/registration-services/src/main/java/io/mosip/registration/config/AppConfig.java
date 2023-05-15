package io.github.tf-govstack.registration.config;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.tf-govstack.commons.packet.facade.PacketWriter;
import io.github.tf-govstack.kernel.auditmanager.config.AuditConfig;
import io.github.tf-govstack.kernel.core.logger.spi.Logger;

import io.github.tf-govstack.kernel.core.templatemanager.spi.TemplateManagerBuilder;
import io.github.tf-govstack.kernel.dataaccess.hibernate.repository.impl.HibernateRepositoryImpl;
import io.github.tf-govstack.kernel.logger.logback.appender.RollingFileAppender;
import io.github.tf-govstack.kernel.logger.logback.factory.Logfactory;
import io.github.tf-govstack.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Spring Configuration class for Registration-Service Module
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Configuration
@EnableAspectJAutoProxy
@Import({ DaoConfig.class, AuditConfig.class, TemplateManagerBuilderImpl.class })
@EnableJpaRepositories(basePackages = "io.github.tf-govstack.registration", repositoryBaseClass = HibernateRepositoryImpl.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
		".*IdObjectCompositeValidator",
		".*IdObjectMasterDataValidator",
		".*PacketDecryptorImpl",
		".*IdSchemaUtils",
		".*OnlinePacketCryptoServiceImpl"}),
		basePackages = { "io.github.tf-govstack.registration",
		"io.github.tf-govstack.kernel.idvalidator", "io.github.tf-govstack.kernel.ridgenerator", "io.github.tf-govstack.kernel.qrcode",
		"io.github.tf-govstack.kernel.crypto", "io.github.tf-govstack.kernel.jsonvalidator", "io.github.tf-govstack.kernel.idgenerator",
		"io.github.tf-govstack.kernel.virusscanner", "io.github.tf-govstack.kernel.transliteration", "io.github.tf-govstack.kernel.applicanttype",
		"io.github.tf-govstack.kernel.core.pdfgenerator.spi", "io.github.tf-govstack.kernel.pdfgenerator.itext.impl",
		"io.github.tf-govstack.kernel.idobjectvalidator.impl", "io.github.tf-govstack.kernel.biosdk.provider.impl",
		"io.github.tf-govstack.kernel.biosdk.provider.factory", "io.github.tf-govstack.commons.packet",
		"io.github.tf-govstack.registration.api.config" })
@PropertySource(value = { "classpath:spring.properties", "classpath:props/mosip-application.properties" })
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@EnableConfigurationProperties
@EnableRetry
public class AppConfig {

	@Autowired
	@Qualifier("dataSource")
	private DataSource datasource;

	public static Logger getLogger(Class<?> className) {
		return Logfactory.getSlf4jLogger(className);
	}

	@Bean
	@Primary
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RestTemplate selfTokenRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper mapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return mapper;
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("entities");
	}
}
