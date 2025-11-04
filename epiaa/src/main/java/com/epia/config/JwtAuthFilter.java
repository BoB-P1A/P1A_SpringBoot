package com.epia.config;
import com.epia.support.ApiException;
import com.epia.auth.JwtService;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.util.StringUtils;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwt; public JwtAuthFilter(JwtService jwt){ this.jwt=jwt; }
  
  @Override
  protected boolean shouldNotFilter(HttpServletRequest r){
    String p=r.getRequestURI();
    return p.startsWith("/auth/") || p.startsWith("/actuator") || (r.getMethod().equals("OPTIONS"));
  }
  
  @Override
  protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain fc){
    try{
      String h=req.getHeader("Authorization");
      if(!StringUtils.hasText(h) || !h.startsWith("Bearer ")) throw new ApiException(401,"인증 필요");
      jwt.parse(h.substring(7)); fc.doFilter(req,res);
    }catch(ApiException e){ throw e; }catch(Exception e){ throw new ApiException(401,"토큰이 유효하지 않습니다."); }
  }
}