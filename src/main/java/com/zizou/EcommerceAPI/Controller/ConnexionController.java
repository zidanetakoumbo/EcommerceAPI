package com.zizou.EcommerceAPI.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Configuration.JwtService;
import com.zizou.EcommerceAPI.Service.AppUserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Contrôleur gérant l'authentification des utilisateurs.
 * Expose les endpoints de connexion et déconnexion.
 */
@RestController
@RequestMapping("/api/connexion")
@CrossOrigin(origins = "http://localhost:4200")
public class ConnexionController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AppUserService userService;

    public ConnexionController(AuthenticationManager auth, AppUserService userServ, JwtService jwt) {
        this.authenticationManager = auth;
        this.userService = userServ;
        this.jwtService = jwt;
    }

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     * Le token est valable 3 heures.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Vérification des credentials via Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Génération du token JWT avec l'email comme sujet
        String token = jwtService.generateToken(request.getEmail());
        String userName = userService.getUserByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"))
            .getNom();

        return ResponseEntity.ok(new LoginResponse(token, request.getEmail(), userName));
    }

    /**
     * Déconnexion côté client : le token JWT est invalidé par le front-end.
     * Côté serveur (stateless), aucune action n'est requise.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        return ResponseEntity.ok("Déconnexion réussie");
    }
}
