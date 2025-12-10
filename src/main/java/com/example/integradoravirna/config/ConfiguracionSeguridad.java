package com.example.integradoravirna.config;

import com.example.integradoravirna.servicio.ServicioAutenticacion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    private final ServicioAutenticacion servicioAutenticacion;

    // Constructor que inyecta el servicio de autenticacion personalizado
    public ConfiguracionSeguridad(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    // Bean para codificar contrasenas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura el AuthenticationManager de Spring Security
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);

        // Configura el servicio personalizado y el codificador de contrasenas
        auth.userDetailsService(servicioAutenticacion)
                .passwordEncoder(passwordEncoder());

        return auth.build();
    }

    // Configura las reglas de seguridad de la aplicacion
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // Configura autorizacion de rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas publicas (sin autenticacion)
                        .requestMatchers(
                                "/", "/login", "/registro", "/registrar",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        // Todas las demas rutas requieren autenticacion
                        .anyRequest().authenticated()
                )

                // Configura el formulario de login
                .formLogin(login -> login
                        .loginPage("/login")           // Pagina personalizada de login
                        .loginProcessingUrl("/login")  // URL donde Spring procesa el login
                        .usernameParameter("username") // Nombre del campo usuario
                        .passwordParameter("password") // Nombre del campo contrasena
                        .defaultSuccessUrl("/", true)  // Redirige aqui tras login exitoso
                        .failureUrl("/login?error=true") // Redirige aqui si falla el login
                        .permitAll()                   // Permite acceso a todos
                )

                // Configura el logout
                .logout(logout -> logout
                        .logoutUrl("/logout")              // URL para logout
                        .logoutSuccessUrl("/login?logout=true") // Redirige aqui tras logout
                        .permitAll()                      // Permite acceso a todos
                )
                // Desactiva CSRF (Cross-Site Request Forgery) para simplificar
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}