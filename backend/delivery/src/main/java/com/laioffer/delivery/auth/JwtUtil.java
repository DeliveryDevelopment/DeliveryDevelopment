package com.laioffer.delivery.auth;

import com.laioffer.delivery.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtUtil {

    // 这里的 Key 应该在 application.yml 里配置，这里为了方便直接生成一个
    // 生产环境一定要用 @Value("${jwt.secret}") 注入
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("${jwt.expiration:86400000}") // 默认 1 天
    private long expiration;

    /**
     * 1. 生成 Token
     * 使用 User ID (UUID) 作为 Token 的 Subject
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // 你可以在这里放入更多信息，比如 role, username
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());

        return createToken(claims, user.getId().toString());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // 这里存的是 UUID String
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 2. 验证 Token (这就是报错缺少的那个方法)
     */
    public boolean validateToken(String token, User user) {
        final UUID userId = extractUserId(token);
        // 验证两点：
        // 1. Token 里的 ID 和 User 对象的 ID 是否一致
        // 2. Token 是否过期
        return (userId.equals(user.getId()) && !isTokenExpired(token));
    }

    /**
     * 3. 提取 User ID (UUID)
     */
    public UUID extractUserId(String token) {
        String idString = extractClaim(token, Claims::getSubject);
        return UUID.fromString(idString);
    }

    // --- 辅助方法 ---

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}