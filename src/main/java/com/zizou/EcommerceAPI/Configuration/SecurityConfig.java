package com.zizou.EcommerceAPI.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

		// cette classe doit obligatoirement etre dans le meme package ou classe que le 
		// springbootApplication pour fonctionner 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // desactive les token csrf
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                
            ) // autorise toutes les requettes //test 
            .httpBasic(basic -> basic.disable())
            //desactiver la protection d'affichage des frames (utilisé pour h2 via web interface) 
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .formLogin(form -> form.disable());

        return http.build();
    }
}
