package com.zizou.EcommerceAPI.Configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

/**
 * Configuration de la sécurité Spring Security.
 * Gère la chaîne de filtres JWT, CORS, et les règles d'accès aux endpoints.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Chaîne de filtres principale.
     * - CORS configuré pour Angular (localhost:4200)
     * - CSRF désactivé (API REST stateless)
     * - Routes publiques : inscription, connexion, docs Swagger, console H2
     * - Toutes les autres routes nécessitent un token JWT valide
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Toujours public
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/connexion/**", "/api/user/register").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                // Catalogue : lecture publique, modifications réservées ADMIN
                .requestMatchers(HttpMethod.GET, "/api/livres/**", "/api/autheurs/**", "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/livres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/livres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/livres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/autheurs/**", "/api/categories/**").hasRole("ADMIN")

                // Commandes : endpoints réservés à l'admin
                .requestMatchers(HttpMethod.GET, "/api/commandes/statut/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/commandes/non-livrees").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/commandes/*/statut").hasRole("ADMIN")

                // Paiement : webhook public (vérifié par signature Stripe), remboursement ADMIN
                // Le webhook DOIT être public : Stripe ne peut pas envoyer un JWT !
                // La sécurité du webhook est assurée par la vérification de signature dans PaiementService
                .requestMatchers(HttpMethod.POST, "/api/paiement/webhook").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/paiement/rembourser/**").hasRole("ADMIN")

                // Utilisateurs : liste réservée ADMIN
                .requestMatchers(HttpMethod.GET, "/api/user/all").hasRole("ADMIN")

                // Tout le reste : utilisateur connecté (USER ou ADMIN)
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider(userDetailsService, passwordEncoder()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(basic -> basic.disable())
            // Nécessaire pour afficher la console H2 dans un iframe
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .formLogin(form -> form.disable());

        return http.build();
    }

    /** Encodeur BCrypt pour les mots de passe */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Gestionnaire d'authentification exposé pour être injecté dans les controllers */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Fournisseur d'authentification DAO.
     * Utilise CustomUserDetailsService pour charger l'utilisateur par email,
     * et BCrypt pour vérifier le mot de passe.
     */
    @Bean
    public AuthenticationProvider authProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(passwordEncoder);
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    /**
     * Configuration CORS pour autoriser les requêtes depuis le front-end Angular.
     * Autorise les méthodes HTTP nécessaires à une API REST classique.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
