package com.zizou.EcommerceAPI.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zizou.EcommerceAPI.Dto.AppUserDto;
import com.zizou.EcommerceAPI.Entity.AppUser;
import com.zizou.EcommerceAPI.Service.AppUserService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class AppUserController {

    private final AppUserService userService;

    public AppUserController(AppUserService userService) {
        this.userService = userService;
    }

    /** POST /api/user/register — inscription, retourne le profil sans mot de passe */
    @PostMapping("/register")
    public ResponseEntity<AppUserDto> register(@RequestBody AppUser user) {
        return ResponseEntity.status(201).body(userService.register(user));
    }

    /** GET /api/user/all — liste tous les utilisateurs (ADMIN uniquement, protégé par SecurityConfig) */
    @GetMapping("/all")
    public ResponseEntity<List<AppUserDto>> getAll() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    /** GET /api/user/profil — retourne le profil de l'utilisateur connecté */
    @GetMapping("/profil")
    public ResponseEntity<AppUserDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userService.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }

    /** PUT /api/user/profil — met à jour nom, prénom, adresse, dateNaissance */
    @PutMapping("/profil")
    public ResponseEntity<AppUserDto> updateProfile(
            @RequestBody AppUserDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        AppUser user = userService.getUserByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return ResponseEntity.ok(userService.updateProfile(user.getId(), dto));
    }
}
