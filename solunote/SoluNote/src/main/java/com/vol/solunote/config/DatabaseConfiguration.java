package com.vol.solunote.config;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource("classpath:/application.yml")
public class DatabaseConfiguration {
	
    // application.yml에 db: type: mysql 처럼 설정되어 있어야 함
	@Value("${db.type}")
	private String SQL_TYPE;
	
    // 값이 없을 경우를 대비해 기본값 설정 ("null" 문자열보다 비어있는 것이 안전)
	@Value("${db.company:#{null}}")
	private String COMPANY_TYPE;
	
	@Autowired
	private ApplicationContext applicationContext;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    HikariConfig hikariConfig() {
		return new HikariConfig();
	}

    @Bean
    DataSource dataSource() {
		DataSource dataSource = new HikariDataSource(hikariConfig());
		log.info("datasource : {}", dataSource);
		return dataSource;
	}

    @Bean
    SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);		
        // Config 위치 설정
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:/mapper/mybatis-config.xml"));
		
		log.info("조회경로 : {}", "classpath:/mapper/" + SQL_TYPE + "/**/*.xml");
		// 기본 매퍼 리소스 가져오기
		Resource[] sqls = applicationContext.getResources("classpath:/mapper/" + SQL_TYPE + "/**/*.xml");	
		Resource[] array = null;
		

		
        // COMPANY_TYPE에 따른 동적 매퍼 병합 로직
		if (COMPANY_TYPE == null || "null".equals(COMPANY_TYPE)) {
			array = sqls;
		} else {
			Resource[] companies = applicationContext.getResources("classpath:/mapper/" + COMPANY_TYPE + "/**/*.xml"); 
			array = Stream.of(sqls, companies).flatMap(Stream::of).toArray(Resource[]::new);
		}
    	
		sqlSessionFactoryBean.setMapperLocations(array);
											
		return sqlSessionFactoryBean.getObject();
	}

    @Bean
    SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}