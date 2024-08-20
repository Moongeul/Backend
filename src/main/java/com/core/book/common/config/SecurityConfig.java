package com.core.book.common.config;

import com.core.book.common.config.jwt.JwtConfig;
import com.core.book.common.config.oauth2.OAuth2Config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final OAuth2Config oAuth2Config;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(form -> form.disable()) // FormLogin 사용 X
                .httpBasic(basic -> basic.disable()) // httpBasic 사용 X
                .csrf(csrf -> csrf.disable()) // csrf 보안 사용 X
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers( "/api-doc", "/health","/v3/api-docs/**", "/swagger-resources/**","/swagger-ui/**", "/h2-console/**").permitAll()

                        .requestMatchers("/oauth2/authorization/kakao").permitAll() // 카카오 로그인 접근 가능
                        .anyRequest().authenticated() // 위의 경로 이외에는 모두 인증된 사용자만 접근 가능
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)) // 401 Unauthorized 반환
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/kakao")
                        .successHandler(oAuth2Config.getOAuth2LoginSuccessHandler()) // 동의하고 계속하기를 눌렀을 때 Handler 설정
                        .failureHandler(oAuth2Config.getOAuth2LoginFailureHandler()) // 소셜 로그인 실패 시 핸들러 설정
                        .clientRegistrationRepository(oAuth2Config.clientRegistrationRepository())
                        .authorizedClientService(oAuth2Config.authorizedClientService())
                        .tokenEndpoint(token -> token.accessTokenResponseClient(oAuth2Config.authorizationCodeTokenResponseClient()))
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2Config.getCustomOAuth2UserService())) // customUserService 설정
                );

        // JwtAuthenticationProcessingFilter를 추가하여 JWT 인증을 처리
        http.addFilterBefore(jwtConfig.jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager 설정 후 등록
     * FormLogin(기존 스프링 시큐리티 로그인)과 동일하게 DaoAuthenticationProvider 사용
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        return new ProviderManager(provider);
    }
}
