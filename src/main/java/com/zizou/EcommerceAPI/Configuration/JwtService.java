package com.zizou.EcommerceAPI.Configuration;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String SECRET_KEY;

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	@PostConstruct
	public void init() {
		System.out.println("Clé chargée au démarrage : " + SECRET_KEY);
	}

	// 🔐 Générer token
	public String generateToken(String username) {

		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	// 📤 Extraire username
	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	// ✅ Vérifier validité
	public boolean isTokenValid(String token, String username) {
		String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	// ⏳ Vérifier expiration
	private boolean isTokenExpired(String token) {
		Date expiration = Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody()
				.getExpiration();

		return expiration.before(new Date());
	}
}
