package com.zizou.EcommerceAPI.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zizou.EcommerceAPI.Repository.AppUserRepository;

/**
 * Service chargé par Spring Security pour récupérer un utilisateur à partir de son email.
 * Utilisé lors de la validation du token JWT à chaque requête.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    /**
     * Injection du repository via le constructeur.
     * Spring détecte automatiquement le constructeur unique et injecte le bean.
     */
    public CustomUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur par son email (utilisé comme "username" dans Spring Security).
     * Lance UsernameNotFoundException si l'email n'existe pas en base.
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email));
    }
}
