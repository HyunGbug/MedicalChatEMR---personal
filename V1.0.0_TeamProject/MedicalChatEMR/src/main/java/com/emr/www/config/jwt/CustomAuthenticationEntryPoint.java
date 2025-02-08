package com.emr.www.config.jwt;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // 로그인 페이지로 리디렉트하면서 alert 창을 띄우기 위한 스크립트를 작성한다.
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("<script>alert('로그인 후 이용해 주세요.'); location.href='/loginMain';</script>");
    }
}
