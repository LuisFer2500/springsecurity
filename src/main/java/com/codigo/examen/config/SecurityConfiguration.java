package com.codigo.examen.config;

import com.codigo.examen.entity.Rol;
import com.codigo.examen.repository.RolRepository;
import com.codigo.examen.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RolRepository rolRepository;
    private final UsuarioService usuarioService;

    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/ms-examen/v1/autentication/**")
                        .permitAll()
                        .requestMatchers("/ms-examen/v1/admin/**").hasAnyAuthority(getRoleByName("ADMIN"))
                        .requestMatchers("/ms-examen/v1/usuarios/**").hasAnyAuthority(getRoleByName("USER"))
                        .anyRequest().hasAnyAuthority())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //que no se va a mantener una sesion siempre
                .authenticationProvider(authenticationProvider()).addFilterBefore(  //VAlidad y autentica el token
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return httpSecurity.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(usuarioService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passWordEncoder());
        return authenticationProvider;
    }

    @Bean
    private PasswordEncoder passWordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    private AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    private String getRoleByName(String roleName) {
        Optional<Rol> roleOptional = rolRepository.findByNombreRol(roleName);
        return roleOptional.map(Rol::getNombreRol).orElse(roleName);
    }

}
