package com.example.blogs.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenHelper {

    private static final Logger logger = LogManager.getLogger(JwtTokenHelper.class);

    private final String SECRET_KEY = "e3cbac02bbf968f70eba5f5dea3634f7887e0bf621abbf6ff2b2bb4b0da48561";

    public String extractEmail(String token) {
        logger.info("Extracting email from token");
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails login) {
        logger.info("Validating token for email: {}", login.getUsername());
        String email = extractEmail(token);
        boolean isValid = email.equals(login.getUsername()) && !isTokenExpired(token);
        if (isValid) {
            logger.info("Token is valid for email: {}", login.getUsername());
        } else {
            logger.warn("Token is invalid for email: {}", login.getUsername());
        }
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        logger.info("Checking if token is expired");
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        logger.info("Extracting expiration date from token");
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        logger.info("Extracting claims from token");
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.info("Extracting all claims from token");
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(String email) {
        logger.info("Generating token for email: {}", email);
        String token = Jwts
                .builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 5 * 60 * 60 * 1000))
                .signWith(getSignKey())
                .compact();
        logger.info("Token generated successfully for email: {}", email);
        return token;
    }

    private SecretKey getSignKey() {
        logger.info("Generating signing key");
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
