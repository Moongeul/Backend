package com.core.book.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // Access Token 쿠키 기반 인증 스키마 설정
        SecurityScheme accessTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("accessToken");

        // Refresh Token 쿠키 기반 인증 스키마 설정
        SecurityScheme refreshTokenScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refreshToken");

        // SecurityRequirement 설정 - 인증 요구사항 추가
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("accessToken")
                .addList("refreshToken");

        Server server = new Server();
        server.setUrl("https://moongeul.kro.kr");

        return new OpenAPI()
                .info(new Info()
                        .title("코어 프로젝트 API")
                        .description("독서 커뮤니티 REST API Document- Backend Developer : 태근, 주현")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes("accessToken", accessTokenScheme)
                        .addSecuritySchemes("refreshToken", refreshTokenScheme))
                .addSecurityItem(securityRequirement);
    }
}
