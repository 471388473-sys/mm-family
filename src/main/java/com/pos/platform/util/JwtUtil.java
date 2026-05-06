package com.pos.platform.util;

import com.pos.platform.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 生成小程序端Token
     */
    public String generateMiniToken(Long userId, String openid) {
        return generateToken(userId, openid, "mini", jwtProperties.getMiniExpireTime());
    }

    /**
     * 生成管理端Token
     */
    public String generateAdminToken(Long adminId, String username, String role) {
        return generateToken(adminId, username, role, jwtProperties.getAdminExpireTime());
    }

    /**
     * 生成Token
     */
    private String generateToken(Long userId, String subject, String type, Long expireTime) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("subject", subject);
        claims.put("type", type);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Token解析失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 验证Token
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取Token类型
     */
    public String getTokenType(String token) {
        Claims claims = parseToken(token);
        return claims.get("type", String.class);
    }

    /**
     * 获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}