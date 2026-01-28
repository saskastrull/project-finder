package org.example.projectfinder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Permit all for testing
                        .requestMatchers("/h2-console/**").permitAll()

                        // Admin only
                        .requestMatchers(HttpMethod.DELETE, "/api/keywords/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/keywords").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/keywords/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/projects").hasRole("ADMIN")

                        // User + admin endpoints
                        .requestMatchers(HttpMethod.GET, "/api/keywords").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/keywords/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/projects/{id}").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf // disable CSRF
                        .ignoringRequestMatchers("/h2-console/**",
                                "/api/**"))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
                .withUsername("user")
                .password("{noop}user")
                .roles("USER")
                .build();

        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
