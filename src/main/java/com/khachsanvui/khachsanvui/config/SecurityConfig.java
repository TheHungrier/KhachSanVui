package com.khachsanvui.khachsanvui.config;

import com.khachsanvui.khachsanvui.service.CustomOAuth2UserService;
import com.khachsanvui.khachsanvui.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final FormLoginSuccessHandler formLoginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomOAuth2UserService oAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          FormLoginSuccessHandler formLoginSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.formLoginSuccessHandler = formLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        .requestMatchers("/", "/home", "/login", "/register", "/oauth2/**", "/error").permitAll()
                        .requestMatchers("/phong", "/phong/{id}").permitAll()
                        .requestMatchers("/dich-vu", "/dich-vu/{id}").permitAll()
                        .requestMatchers("/kham-pha/**", "/lien-he", "/gioi-thieu/**", "/tin-tuc/**", "/tuyen-dung/**").permitAll()
                        .requestMatchers("/api/khuyen-mai/active-flash-sales", "/api/newsletter/subscribe").permitAll()
                        .requestMatchers("/yeu-thich/**").hasRole("KHACHHANG")

                        .requestMatchers("/phong/admin/**", "/loai-phong/admin/**", "/chi-nhanh/admin/**").hasRole("ADMIN")
                        .requestMatchers("/khach-hang/admin/**", "/nhan-vien/admin/**", "/dich-vu/admin/**", "/khuyen-mai/admin/**").hasRole("ADMIN")

                        .requestMatchers("/admin/**", "/dat-phong/admin/**", "/luu-tru/admin/**", "/hoa-don/admin/**").hasAnyRole("ADMIN", "LETAN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("tenDangNhap")
                        .passwordParameter("matKhau")
                        .successHandler(formLoginSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .userInfoEndpoint(user -> user
                                .userService(oAuth2UserService)
                        )
                        .defaultSuccessUrl("/oauth2/success", true)
                        .successHandler(oAuth2LoginSuccessHandler)
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
