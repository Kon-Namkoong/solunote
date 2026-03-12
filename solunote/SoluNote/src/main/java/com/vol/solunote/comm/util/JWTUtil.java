package com.vol.solunote.comm.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;


public class JWTUtil {

    // Key를 SecretKey 객체로 변환하는 내부 메서드
    private SecretKey getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String keyJWT, String bodyStr) {
        SecretKey key = getSigningKey(keyJWT);
        
        long expiredTime = 1000L * 24 * 60 * 60; // 24시간
        Date expDate = new Date(System.currentTimeMillis() + expiredTime);

        return Jwts.builder()
                .header()
                    .add("type", "JWT")
                    .add("alg", "HS256")
                .and()
                .claim("data", bodyStr) // 구체적인 데이터를 claim에 담음
                .expiration(expDate)    // 만료 시간 설정
                .signWith(key)          // 최신 버전은 알고리즘을 자동으로 선택하거나 키에서 유추함
                .compact();
    }

    public Claims getTokenFromJwtString(String keyJWT, String jwtTokenString) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey(keyJWT)) // setSigningKey -> verifyWith 변경
                    .build()
                    .parseSignedClaims(jwtTokenString) // parseClaimsJws -> parseSignedClaims 변경
                    .getPayload();                     // getBody -> getPayload 변경
        } catch (JwtException | IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getTokenExpired(String keyJWT, String jwtTokenString) throws JSONException {
        JSONObject json = new JSONObject();
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey(keyJWT))
                    .build()
                    .parseSignedClaims(jwtTokenString);
            
            json.put("expired", false);
        } catch (ExpiredJwtException e) {
            json.put("expired", true);
            json.put("expiredType", "ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
            json.put("expired", true);
            json.put("expiredType", "UnsupportedJwtException");
        } catch (MalformedJwtException e) {
            json.put("expired", true);
            json.put("expiredType", "MalformedJwtException");
        } catch (SignatureException e) {
            json.put("expired", true);
            json.put("expiredType", "SignatureException");
        } catch (IllegalArgumentException e) {
            json.put("expired", true);
            json.put("expiredType", "IllegalArgumentException");
        }
        return json;
    }
}