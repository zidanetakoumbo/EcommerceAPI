package com.zizou.EcommerceAPI.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig {

	private final UserDetailsService userDetailsService;

	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	// cette classe doit obligatoirement etre dans le meme package ou classe que le
	// springbootApplication pour fonctionner
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> {
		}).csrf(csrf -> csrf.disable()) // desactive les token csrf
				.authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers("/api/connexion/**", "/api/user/register", "/swagger-ui/**", "/v3/api-docs/**")
						.permitAll().anyRequest().authenticated()) // autorise toutes les requettes //test
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authProvider(userDetailsService, passwordEncoder()))
				.httpBasic(basic -> basic.disable())
				// desactiver la protection d'affichage des frames (utilisé pour h2 via web
				// interface)
				.headers(headers -> headers.frameOptions(frame -> frame.disable())).formLogin(form -> form.disable());

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public AuthenticationProvider authProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

		authProvider.setPasswordEncoder(passwordEncoder);

		return authProvider;
	}

	// pernet de definir les règles d'accès pour les requettes venat du front-end
	// angular , cette methode est la porte d'entrée 

	public void addCorsMapping(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
