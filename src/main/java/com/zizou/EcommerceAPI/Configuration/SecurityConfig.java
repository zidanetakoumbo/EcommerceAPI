package com.zizou.EcommerceAPI.Configuration;

import java.util.Arrays;

import org.apache.tomcat.util.file.ConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {

	private final UserDetailsService userDetailsService;
	private JwtAuthenticationFilter jwtAuthFilter ; 

	public SecurityConfig(UserDetailsService userDetailsService , JwtAuthenticationFilter jwtauth) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthFilter = jwtauth ; 
	}

	// cette classe doit obligatoirement etre dans le meme package ou classe que le
	// springbootApplication pour fonctionner
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // responsable des origins
			.csrf(csrf -> csrf.disable()) // desactive les token csrf
			.authorizeHttpRequests(auth -> auth.requestMatchers("/h2-console/**").permitAll()
						.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/api/livres/**","/api/categories/**","/api/connexion/**", "/api/user/register", "/swagger-ui/**", "/v3/api-docs/**")
						.permitAll()
						.anyRequest().authenticated()) // autorise toutes les requettes //test
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authenticationProvider(authProvider(userDetailsService, passwordEncoder()))
				// 2. AJOUTER LE FILTRE JWT ICI (Indispensable pour le STATELESS)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
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

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// 1. Autorise ton front-end Angular
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));

		// 2. Autorise les méthodes HTTP nécessaires
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

		// 3. Autorise les Headers (Content-Type pour le JSON, Authorization pour ton
		// JWT)
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));

		// 4. Permet l'envoi de cookies ou credentials si besoin
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Applique à toutes les routes
		return source;
	}

}
