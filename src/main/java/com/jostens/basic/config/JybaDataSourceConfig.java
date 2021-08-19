package com.jostens.basic.config;

import com.jostens.basic.mybatis.CachingSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "jyba.datasource")
public class JybaDataSourceConfig extends HikariConfig {

	@Value(value = "classpath:sqlmap/config.xml")
	private Resource jybaSqlMapConfigXML;

	@Primary
	@Bean(name = "jybaDataSource")
	public DataSource jybaDataSource() {
		return new HikariDataSource(this);
	}

	@Bean
	public CachingSqlSessionFactoryBean jybaSqlSessionFactory() {
		CachingSqlSessionFactoryBean factory = new CachingSqlSessionFactoryBean();
		factory.setDataSource(jybaDataSource());
		factory.setConfigLocation(jybaSqlMapConfigXML);
		return factory;
	}

	@Bean
	public SqlSessionTemplate jybaSqlSessionTemplate() throws Exception {
		return new SqlSessionTemplate(jybaSqlSessionFactory().getObject());
	}

	@Bean
	public PlatformTransactionManager jybaTransactionManager() {
		return new DataSourceTransactionManager(jybaDataSource());
	}

}
