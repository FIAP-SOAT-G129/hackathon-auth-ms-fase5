package com.hackathon.application.usecase;

import com.hackathon.domain.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenUseCase {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    public String generateToken(User user) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        return buildToken(user, claims);
    }

    private String buildToken(User user, Map<String, Object> extraClaims) {
        return Jwts
                .builder()
                .subject(user.getId().toString())
                .claims(extraClaims)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
