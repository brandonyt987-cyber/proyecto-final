package com.sena.sistemaintegralsena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity 
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) 
                .authorizeHttpRequests(auth -> auth

                        // Recursos estáticos
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

                        // Login y registro (GET)
                        .requestMatchers("/login", "/registro").permitAll()
                        
                        // Permitir el POST para guardar registro
                        .requestMatchers(HttpMethod.POST, "/registro/guardar").permitAll() 
                        
                        // Error page
                        .requestMatchers("/error").permitAll() 

                        // Dashboard
                        .requestMatchers("/dashboard").authenticated()

                        // MÓDULO EXCLUSIVO DE ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // MÓDULOS COMPARTIDOS (ADMIN, PSICOLOGA, T_SOCIAL)
                        // Aquí agregamos las nuevas rutas permitidas para todos
                        .requestMatchers(
                            "/fichas/**", 
                            "/aprendices/**", 
                            "/comite/**", 
                            "/atencion/**", 
                            "/talleres/**",
                            "/coordinaciones/**", 
                            "/instructores/**",   
                            "/voceros/**"         
                        ).hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")

                        // Todo lo demás requiere login
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}