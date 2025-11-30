package com.sena.sistemaintegralsena.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Importación necesaria
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
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
                // Dejas CSRF deshabilitado, lo cual evita problemas de tokens en POST
                .csrf(csrf -> csrf.disable()) 
                .authorizeHttpRequests(auth -> auth

                        // Recursos estáticos
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

                        // Login y registro (GET)
                        .requestMatchers("/login", "/registro").permitAll()
                        
                        // 🔑 AJUSTE CRÍTICO: Permitir el POST para la URL de guardado del registro
                        .requestMatchers(HttpMethod.POST, "/registro/guardar").permitAll() 
                        
                        // CORRECCIÓN VITAL: Permite acceso a /error para romper el bucle de redirección
                        .requestMatchers("/error").permitAll() 

                        // Dashboard accesible para cualquier usuario autenticado
                        .requestMatchers("/dashboard").authenticated()

                        // Roles estandarizados: Usar hasRole()
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // Rutas comunes para ADMIN, PSICOLOGA y T_SOCIAL (usar hasAnyRole)
                        .requestMatchers("/fichas/**").hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")
                        .requestMatchers("/aprendices/**").hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")
                        .requestMatchers("/comite/**").hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")
                        .requestMatchers("/atencion/**").hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")
                        .requestMatchers("/talleres/**").hasAnyRole("ADMIN", "PSICOLOGA", "T_SOCIAL")

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