package com.sena.sistemaintegralsena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.sena.sistemaintegralsena.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())   // Mientras desarrollamos
            .headers(headers -> headers.frameOptions(frame -> frame.disable())) // Para H2-console
            .authorizeHttpRequests(auth -> auth
                // Archivos públicos
                .requestMatchers(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/favicon.ico",
                        "/registro",
                        "/login",
                        "/error",
                        "/h2-console/**"
                ).permitAll()

                // Rutas por rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/psico/**").hasRole("PSICOLOGA")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )

            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
