package com.jostens.basic.mybatis;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class CachingSqlSessionFactoryBean extends SqlSessionFactoryBean {
    //private static final Logger LOG = Logger.getLogger(CachingSqlSessionFactoryBean.class);
    private static final Logger LOG = LoggerFactory.getLogger(CachingSqlSessionFactoryBean.class);
    private Resource[] configLocations;
    private DataSource dataSource;
    private Properties configurationProperties;
    private CacheFactoryBean cacheFactoryBean;

    public CachingSqlSessionFactoryBean() {
    }

    public void setConfigLocation(Resource configLocation) {
        this.configLocations = configLocation != null ? new Resource[]{configLocation} : null;
    }

    public void setConfigLocations(Resource[] configLocations) {
        this.configLocations = configLocations;
    }

    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        if (dataSource instanceof TransactionAwareDataSourceProxy) {
            this.dataSource = ((TransactionAwareDataSourceProxy)dataSource).getTargetDataSource();
        } else {
            this.dataSource = dataSource;
        }

    }

    public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
        super.setConfigurationProperties(sqlSessionFactoryProperties);
        this.configurationProperties = sqlSessionFactoryProperties;
    }

    public void setCacheFactoryBean(CacheFactoryBean cacheFactoryBean) {
        this.cacheFactoryBean = cacheFactoryBean;
    }

    protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
        XMLConfigBuilder xmlConfigBuilder = null;
        Configuration configuration;
        if (this.configLocations != null && this.configLocations.length > 0) {
            xmlConfigBuilder = new XMLConfigBuilder(this.configLocations[0].getInputStream(), (String)null, this.configurationProperties);
            configuration = xmlConfigBuilder.getConfiguration();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Property 'configLocation' not specified, using default MyBatis Configuration");
            }

            configuration = new Configuration();
            configuration.setVariables(this.configurationProperties);
        }

        if (this.cacheFactoryBean != null) {
            List<Cache> caches = this.cacheFactoryBean.getCacheList();
            if (caches != null && !caches.isEmpty()) {
                Iterator var4 = caches.iterator();

                while(var4.hasNext()) {
                    Cache cache = (Cache)var4.next();
                    configuration.addCache(cache);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Added cache: '" + cache.getId() + "'");
                    }
                }
            }
        }

        if (xmlConfigBuilder != null) {
            try {
                xmlConfigBuilder.parse();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Parsed configuration file: '" + this.configLocations[0] + "'");
                }

                for(int i = 1; i < this.configLocations.length; ++i) {
                    xmlConfigBuilder = new XMLConfigBuilder(this.configLocations[i].getInputStream(), (String)null, this.configurationProperties);
                    Configuration innerconfiguration = xmlConfigBuilder.getConfiguration();
                    Collection<Cache> caches = configuration.getCaches();
                    if (caches != null) {
                        Iterator var6 = caches.iterator();

                        while(var6.hasNext()) {
                            Cache cache = (Cache)var6.next();
                            innerconfiguration.addCache(cache);
                        }
                    }

                    xmlConfigBuilder.parse();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Parsed configuration file: '" + this.configLocations[i] + "'");
                    }

                    this.mergeConfigurations(innerconfiguration, configuration);
                }
            } catch (Exception var11) {
                throw new NestedIOException("Failed to parse config resource: " + Arrays.toString(this.configLocations), var11);
            } finally {
                ErrorContext.instance().reset();
            }
        }

        Environment environment = new Environment(this.getClass().getSimpleName(), new SpringManagedTransactionFactory(), this.dataSource);
        configuration.setEnvironment(environment);
        return (new SqlSessionFactoryBuilder()).build(configuration);
    }

    private void mergeConfigurations(Configuration src, Configuration dest) {
        if (src != null && dest != null) {
            Collection<String> mappedStatements = src.getMappedStatementNames();
            if (mappedStatements != null) {
                Iterator var4 = mappedStatements.iterator();

                while(var4.hasNext()) {
                    String mappedStatement = (String)var4.next();
                    if (mappedStatement.indexOf(".") > 0) {
                        dest.addMappedStatement(src.getMappedStatement(mappedStatement));
                    }
                }
            }

        }
    }
}
