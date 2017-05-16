package com.daou.go.config.application;

import com.daou.go.common.properties.ERPDatabaseProperties;
import com.daou.go.common.util.secure.Constants;
import com.daou.go.common.util.secure.SecureUtil;
import com.daou.go.common.util.secure.SymmetricCrypt;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;



@Configuration //해당 클래스가 스프링 설정으로 사용된다는 것을 지정한다. 
@EnableTransactionManagement
public class ERPDataSourceConfig {
    private static final int MINUTE_3 = 300;

    protected  static final Logger logger = LoggerFactory.getLogger(ERPDataSourceConfig.class);

    @Autowired
    Environment environment;

    @Autowired
    ERPDatabaseProperties erpDbProperties;


    @Bean(name = "ERPJdbcTemplate")
    @Autowired
    public NamedParameterJdbcTemplate jdbcTemplate(@Qualifier("ERPDataSource") DataSource dataSource){
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate;
    }

    @Bean(destroyMethod = "close", name = "ERPDataSource")
    public DataSource customDataSource(){
        ComboPooledDataSource pool = new ComboPooledDataSource();
        try {
            pool.setDriverClass(erpDbProperties.getDriver());
            pool.setJdbcUrl(getJdbcUrl());
            pool.setUser(decrypt(erpDbProperties.getUsername()));
            pool.setPassword(decrypt(erpDbProperties.getPassword()));
            pool.setInitialPoolSize(erpDbProperties.getInitialPoolSize());
            pool.setMinPoolSize(erpDbProperties.getMinPoolSize());
            pool.setMaxPoolSize(erpDbProperties.getMaxPoolSize());
            pool.setIdleConnectionTestPeriod(MINUTE_3);
            pool.setPreferredTestQuery("SELECT 1 FROM DUAL");
            pool.setCheckoutTimeout(erpDbProperties.getCheckoutTimeout());
            pool.setUnreturnedConnectionTimeout(erpDbProperties.getUnreturnedConnectionTimeout());
            return pool;
        } catch (PropertyVetoException e) {
            throw new DataSourceConfigException(e);
        }
    }

    private String decrypt(String value){

        String decryptVal = "";
        try {
            decryptVal = SecureUtil.decrypt(SymmetricCrypt.AES, Constants.DEFAULT_AES_DECRYPTION_KEY, value);
        } catch (Exception e) {
            decryptVal = value;
        }

        return decryptVal;
    }

    private String getJdbcUrl() {
        StringBuffer url = new StringBuffer();

        url.append(erpDbProperties.getProtocol());
        url.append("://");
        url.append(erpDbProperties.getHost());
        url.append(':');
        url.append(erpDbProperties.getPort());
        url.append(";DatabaseName=");
        url.append(erpDbProperties.getDbName());
        
        return url.toString();
    }
}
