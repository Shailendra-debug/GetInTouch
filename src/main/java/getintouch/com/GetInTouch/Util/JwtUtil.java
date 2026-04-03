package getintouch.com.GetInTouch.Util;

import getintouch.com.GetInTouch.Entity.User.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final long ACCESS_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /* ============================
       ACCESS TOKEN
    ============================ */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("type", "access") // 🔥 important
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ============================
       REFRESH TOKEN
    ============================ */
    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("type", "refresh") // 🔥 important
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ============================
       COMMON METHODS
    ============================ */
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token) {
        Object id = getClaims(token).get("userId");
        return id != null ? Long.parseLong(id.toString()) : null;
    }

    public String extractType(String token) {
        return getClaims(token).get("type", String.class);
    }

    /* ============================
       VALIDATION
    ============================ */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);

            // ✅ must be access token
            if (!"access".equals(claims.get("type"))) {
                return false;
            }

            return claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = getClaims(token);

            // ✅ must be refresh token
            if (!"refresh".equals(claims.get("type"))) {
                return false;
            }

            return claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public LocalDateTime getRefreshExpiry() {
        return LocalDateTime.now().plusDays(7);
    }

    private static int count(int arr[],int k){
        int num=0;
        Map<Integer,Integer>map=new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            int a=k-arr[i];
            if (map.containsKey(a)){
                num++;
            }else map.put(arr[i],i);
        }
        return num;
    }
}