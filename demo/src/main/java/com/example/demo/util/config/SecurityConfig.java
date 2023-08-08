package com.example.demo.util.config;

import com.example.demo.user.handler.CustomAuthenticationFailureHandler;
import com.example.demo.user.handler.CustomAuthenticationSuccessHandler;
import com.example.demo.user.handler.CustomLogoutSuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(){
        return new CustomLogoutSuccessHandler();
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user1 = User.withUsername("user1")
//                .password(passwordEncoder().encode("user1Pass"))
//                .roles("USER")
//                .build();
//        UserDetails user2 = User.withUsername("user2")
//                .password(passwordEncoder().encode("user2Pass"))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder().encode("adminPass"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1, user2, admin);
//    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

            .csrf(csrf -> csrf
                    .ignoringRequestMatchers(PathRequest.toH2Console())
                    .disable()
                    //.httpBasic(Customizer.withDefaults())
            )

            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/custom-login-page", "/login/**", "/index", "/custom-login-page.html?error=true").permitAll()
                    .requestMatchers("/search/**").permitAll()
                    .requestMatchers("/detail/**").permitAll()
                    .requestMatchers("/api/only-test/s3/**").permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
//                    .loginPage("/custom-login-page")
                    .loginProcessingUrl("/login/process")
                    .defaultSuccessUrl("/index")
                    .successHandler(authenticationSuccessHandler())
                    .failureUrl("/custom-login-page.html?error=true")
                    .failureHandler(authenticationFailureHandler())
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
            )

            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessHandler(logoutSuccessHandler())
            )

            .rememberMe(Customizer.withDefaults());

        return http.build();
    }
}
