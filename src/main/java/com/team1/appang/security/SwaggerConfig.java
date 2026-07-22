package com.team1.appang.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//인증 토큰 헤더를 붙일 수 있게 설정
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        // Swagger UI에서 "Authorize" 팝업에 표시될 인증 방식 이름표
        String jwtSchemeName = "JWT";

        // 이 문서 전체에 jwtSchemeName인증 방식을 적용하겠다는 요구사항 객체
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 실제 인증 방식 정의
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)              // 스킴 이름
                        .type(SecurityScheme.Type.HTTP)   // 인증 타입
                        .scheme("bearer")                 // Authorization 헤더에 "Bearer {토큰}" 형태로 보낸다는 뜻
                        .bearerFormat("JWT"));             // UI 표시용. 실제 검증방식에는 영향 X

        return new OpenAPI()
                // Swagger UI 최상단에 표시되는 문서 정보
                .info(new Info()
                        .title("Appang API")           // 페이지 제목
                        .description("API 명세서")      // 페이지 설명
                        .version("v1"))                 // API 버전 표시
                // 위에서 만든 요구사항을 전체 API에 적용
                .addSecurityItem(securityRequirement)
                .components(components); //정의 등록
    }
}