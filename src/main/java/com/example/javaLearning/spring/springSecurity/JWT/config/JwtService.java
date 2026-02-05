package com.example.javaLearning.spring.springSecurity.JWT.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  public static final String SECRET_KEY =
      "a1a4673ac483513bc46461059df519b0db00e8f5046d6723389b19320d31c06d";

  public String extractUsername(String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public boolean isTokenValid(String jwt, UserDetails userDetails) {
    final String username = extractUsername(jwt);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
  }

  private boolean isTokenExpired(String jwt) {
    return extractExpirationToken(jwt).before(new Date());
  }

  private Date extractExpirationToken(String jwt) {
    return extractClaim(jwt, Claims::getExpiration);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder().addClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
  }

  public <T> T extractClaim(String jwt, Function<Claims, T> claimsTFunction) {
    Claims claims = extractAllClaims(jwt);
    return claimsTFunction.apply(claims);
  }

  public Claims extractAllClaims(String jwt) {
    return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(jwt)
        .getBody();
  }

  private Key getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
