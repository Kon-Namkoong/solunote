package com.vol.solunote.security;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat 멀티 커넥터 설정 (HTTP/HTTPS 동시 지원)
 */
@Configuration
public class ServletConfig {

    @Value("${server.http.port:8080}") // 기본값 8080 설정으로 안정성 확보
    private int serverPortHttp;


    /**
     * HTTP 전용 커넥터를 생성합니다.
     */
    private Connector createStandardConnector() {
        // 프로토콜 문자열을 상수로 사용하거나 명확하게 기술
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(serverPortHttp);
        connector.setSecure(false); // HTTP이므로 보안 연결 해제 명시
        
        return connector;
    }
    /**
     * Tomcat 서블릿 컨테이너 설정을 커스터마이징합니다.
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        
        // 추가 HTTP 커넥터 등록
        tomcat.addAdditionalTomcatConnectors(createStandardConnector());
        
        return tomcat;
    }

}