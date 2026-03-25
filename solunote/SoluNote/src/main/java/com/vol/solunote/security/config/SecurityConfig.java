package com.vol.solunote.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.vol.solunote.security.AuthorizationChecker;
import com.vol.solunote.security.handler.CustomAppEntryPointHandler;
import com.vol.solunote.security.handler.CustomLoginFailureHandler;
import com.vol.solunote.security.service.CustomUserDetailsService;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 생성자 주입 자동화
public class SecurityConfig {

    private final AuthenticationSuccessHandler successHandler;

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return new CustomLoginFailureHandler();
    }

    @Bean
    public AuthenticationEntryPoint entryPointHandler() {
        return new CustomAppEntryPointHandler();
    }

    /**
     * 정적 리소스 보안 무시 설정
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowBackSlash(true);
        
        return (web) -> web.httpFirewall(firewall)
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()) // 기본 정적 리소스 (css, js 등)
                .requestMatchers("/font/**", "/lib/**", "/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 및 보안 설정 (Lambda 스타일)
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. 권한 설정 (requestMatchers 사용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/myinfo").hasRole("MEMBER")              
                // Swagger (Springdoc) 및 API 허용 경로
                .requestMatchers(
                    "/denied", "/API/**", "/RECORD/**", "/doc", 
                    "/swagger-ui/**", "/v3/api-docs/**", "/webjars/**", "/swagger-resources/**"
                ).permitAll()
                .requestMatchers("/", "/main").authenticated()
                // Custom Authorization Checker
                .anyRequest().access((authentication, context) -> 
                    new org.springframework.security.authorization.AuthorizationDecision(
                        ((AuthorizationChecker) org.springframework.web.context.support.WebApplicationContextUtils
                            .getRequiredWebApplicationContext(context.getRequest().getServletContext())
                            .getBean("authorizationChecker"))
                            .check(context.getRequest(), authentication.get())
                    )
                )
            )

            // 3. 로그인 설정
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/user/loginProcess")
                .successHandler(successHandler)
                .failureHandler(failureHandler())
                .permitAll()
            )

            // 4. 세션 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // 5. 로그아웃 설정
            .logout(logout -> logout
            	.logoutUrl("/user/logout") 
            	.logoutSuccessUrl("/user/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // 6. 예외 처리
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(entryPointHandler())
                .accessDeniedPage("/denied")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}