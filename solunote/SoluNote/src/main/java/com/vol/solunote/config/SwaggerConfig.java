package com.vol.solunote.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @EnableSwagger2는 더 이상 필요 없으므로 제거합니다.
public class SwaggerConfig {

    private static final String API_NAME = "Meeting Minutes API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "회의록 API";

    /**
     * Springdoc-openapi를 사용하기 위한 GroupedOpenApi 설정
     * * group: API 그룹명 (기존 groupName 대체)
     * packagesToScan: 컨트롤러 패키지 경로 (기존 RequestHandlerSelectors.basePackage 대체)
     * pathsToMatch: 문서화할 경로 (기존 PathSelectors.ant 대체)
     */
    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group(API_VERSION)
                // 기존 패키지 경로가 "com.sgsas.api"였으나 프로젝트 설정에 맞춰 "com.vol.solunote" 등으로 확인 필요
                .packagesToScan("com.vol.solunote") 
                .pathsToMatch("/API/meet/**")
                .build();
    }

    /**
     * API의 일반적인 정보(제목, 버전, 설명)를 설정
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION));
    }
}

