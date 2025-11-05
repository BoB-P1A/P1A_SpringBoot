package com.epia.config;

import com.epia.support.ApiException;
import com.epia.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest r) {
        String p = r.getRequestURI();
        // 로그인, 헬스체크, CORS preflight 는 제외
        return p.startsWith("/auth/") || p.startsWith("/actuator") || "OPTIONS".equals(r.getMethod());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain fc
    ) throws ServletException, IOException {

        try {
            String h = req.getHeader("Authorization");

            if (!StringUtils.hasText(h) || !h.startsWith("Bearer ")) {
                throw new ApiException(401, "인증 필요");
            }

            Claims claims = jwt.getClaims(h.substring(7));

            req.setAttribute("user", claims);

            fc.doFilter(req, res);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(401, "토큰이 유효하지 않습니다.");
        }
    }
}