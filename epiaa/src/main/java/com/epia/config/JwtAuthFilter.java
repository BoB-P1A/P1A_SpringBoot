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

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

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

        String uri = req.getRequestURI();
        System.out.println("🔍 [JwtAuthFilter] URI: " + uri);

        try {
            String h = req.getHeader("Authorization");

            System.out.println("🔍 [JwtAuthFilter] URI: " + req.getRequestURI());
            System.out.println("🔍 [JwtAuthFilter] Method: " + req.getMethod());
            System.out.println("🔍 [JwtAuthFilter] Authorization Header: " + (h != null ? h.substring(0, Math.min(h.length(), 30)) + "..." : "null"));

            if (!StringUtils.hasText(h) || !h.startsWith("Bearer ")) {
                System.out.println("❌ [JwtAuthFilter] Authorization 헤더 없음 또는 잘못됨");
                sendErrorResponse(res, 401, "인증 필요");
                return;
                //throw new ApiException(401, "인증 필요");
            }

            Claims claims = jwt.getClaims(h.substring(7));
            System.out.println("✅ [JwtAuthFilter] 토큰 검증 성공: " + claims.get("username"));

            String role = claims.get("role", String.class);
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER"))
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("✅ [JwtAuthFilter] SecurityContext에 인증 정보 등록 완료");

            req.setAttribute("user", claims);

            fc.doFilter(req, res);

        } catch (ApiException e) {
            System.out.println("❌ [JwtAuthFilter] ApiException: " + e.getMessage());
            sendErrorResponse(res, e.status, e.getMessage());
            //throw e;
        } catch (Exception e) {
            System.out.println("❌ [JwtAuthFilter] Exception: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(res, 401, "토큰이 유효하지 않습니다.");
            //throw new ApiException(401, "토큰이 유효하지 않습니다.");
        }
    }

    private void sendErrorResponse(HttpServletResponse res, int status, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"error\":\"" + message + "\",\"status\":" + status + "}");
    }
}