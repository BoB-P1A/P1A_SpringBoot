package com.epia.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {
  private final Key key; private final long expireMs;
  private final ObjectMapper om=new ObjectMapper();
  
  public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expireMinutes}") long mins){
    this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expireMs=mins*60_000;
  }
  
  public String create(Map<String,Object> claims){
    long now=System.currentTimeMillis();
    return Jwts.builder().setClaims(claims).setIssuedAt(new Date(now)).setExpiration(new Date(now+expireMs))
      .signWith(key, SignatureAlgorithm.HS256).compact();
  }
  
  public Jws<Claims> parse(String token){
	  return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); }
}