package com.epia.config;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration 
public class SecurityConfig {
  private final JwtAuthFilter jwt; public SecurityConfig(JwtAuthFilter jwt){ this.jwt=jwt; }
  
  @Bean 
  public SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.csrf(csrf->csrf.disable()).authorizeHttpRequests(a->a
      .requestMatchers("/auth/**","/actuator/**").permitAll().anyRequest().authenticated());
    http.addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    http.cors(c->c.configurationSource(cors())); return http.build();
  }
  
  @Bean 
  public CorsConfigurationSource cors(){
    var c=new CorsConfiguration();
    c.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:3000"));
    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true);
    var s=new UrlBasedCorsConfigurationSource();
    s.registerCorsConfiguration("/**",c); return s;
  }
}