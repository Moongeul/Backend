package com.core.book.api.member.oauth2.handler;

import com.core.book.api.member.entity.Role;
import com.core.book.api.member.jwt.service.JwtService;
import com.core.book.api.member.oauth2.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if (oAuth2User.getRole() == Role.GUEST) {
                handleGuestLogin(response, oAuth2User);
            } else {
                handleUserLogin(response, oAuth2User);
            }
        } catch (Exception e) {
            log.error("OAuth2 로그인 처리 중 오류 발생: ", e);
            response.sendRedirect("/login?error");
        }
    }

    private void handleGuestLogin(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        jwtService.sendAccessToken(response, accessToken);
        //response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        //response.sendRedirect("https://www.google.com");

        String redirectUrl = "http://localhost:3000/onboarding";
        response.sendRedirect(redirectUrl);
    }

    private void handleUserLogin(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);

        //response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        //response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);
        //response.sendRedirect("https://www.naver.com");

        String redirectUrl = "http://localhost:3000/home";
        response.sendRedirect(redirectUrl);
    }
}
