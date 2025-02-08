package com.emr.www.config.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
	}

	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.disable()) // CORS 설정을 비활성화한다.
				.csrf(csrf -> csrf.disable()); // CSRF 보호를 비활성화한다.

		// 세션 비활성화 (JWT 기반 인증으로 상태 없는 방식 사용)
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// URL 접근 제어 설정
		http.authorizeHttpRequests(auth -> auth.requestMatchers("/ws/**").permitAll() // 웹소켓 경로를 인증 없이 허용
				.requestMatchers("/css/**", "/js/**", "/img/**", "/images/**").permitAll() // 정적 리소스에 대한 접근 허용
				.requestMatchers("/loginMain", "/signup", "/registration_form", "/Login", "/inactivity-logout", "/logout").permitAll() // 로그인, 회원가입 페이지 및 리소스 접근 허용
				.requestMatchers("/WEB-INF/views/login/**").permitAll() // WEB-INF 내부의 로그인 페이지에 대한 모든 접근 허용
				.anyRequest().authenticated() // 나머지 요청도 인증된 사용자만 접근 가능하게 설정
		);

		// JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		// Custom AuthenticationEntryPoint 설정
		http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint));

		// 로그아웃 설정
		http.logout(logout -> logout.logoutUrl("/logout") // 로그아웃 URL 설정
				.invalidateHttpSession(true) // 세션 무효화
				.deleteCookies("JSESSIONID", "jwtToken") // 쿠키 삭제
				.logoutSuccessUrl("/loginMain")); // 로그아웃 후 리디렉트

		return http.build();
	}

	// 세션 이벤트를 감지하고 처리하는 데 사용
	@Bean
	protected HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}
}
