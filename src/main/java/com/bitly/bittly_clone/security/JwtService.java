package com.bitly.bittly_clone.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    @Value("${jwt.expiration.time}")
    private long EXPIRATION_TIME;

    public long getExpirationTime() {
        return EXPIRATION_TIME;
    }

    public String extractUsername(String token) {
        // Logic to extract username from the token
        return extractClaims(token, Claims::getSubject);
    }

    public Boolean isValidToken(String token, UserDetails userDetails) {
        // Logic to validate the token
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        // TODO Auto-generated method stub
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        // TODO Auto-generated method stub
        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        // Logic to extract all claims from the token
        // This method should handle the parsing of the JWT token and return the claims
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails user) {
        return generateToken(user, new HashMap<>());
    }

    public String generateToken(UserDetails user, HashMap<String, Object> extraClaims) {
        return buildToken(extraClaims, user, EXPIRATION_TIME);
    }

    private String buildToken(HashMap<String, Object> extraClaims, UserDetails user, long jwtExpiration) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 3600 * 1000))
                .signWith(getSigningKey(), io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

    }

    private Key getSigningKey() {
        // Logic to get the signing key
        // This method should return the key used to sign the JWT tokens

        byte[] key = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(key);
    }

}
