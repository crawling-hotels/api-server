package com.example.demo.util.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder().encode("user1Pass"))
                .roles("USER")
                .build();
        UserDetails user2 = User.withUsername("user2")
                .password(passwordEncoder().encode("user2Pass"))
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("adminPass"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }

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
                    .requestMatchers("/test/**").permitAll()
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .anyRequest().authenticated()
            );
//            .formLogin(formLogin -> formLogin
//                    .loginPage("/login")
//                    .loginProcessingUrl("/login/process")
//                    .defaultSuccessUrl("/index")
//                    .permitAll()
//                    .failureUrl("/login.html?error=true")
//                    .failureHandler(authenticationFailureHandler())
//                    .logoutUrl("/logout")
//                    .deleteCookies("JSESSIONID")
//                    .logoutSuccessHandler(logoutSuccessHandler())
//            )
//            .rememberMe(Customizer.withDefaults());

        return http.build();
    }
}
