package com.epia.web;

import com.epia.auth.JwtService;
import com.epia.domain.Account;
import com.epia.domain.Company;
import com.epia.repo.CompanyRepo;
import com.epia.support.ApiException;
import io.jsonwebtoken.Claims;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CompanyRepo companyRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(CompanyRepo companyRepo, PasswordEncoder encoder, JwtService jwt) {
        this.companyRepo = companyRepo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    private static String toStringId(Object id) {
        if (id == null) return null;
        if (id instanceof ObjectId) return ((ObjectId) id).toHexString();
        return String.valueOf(id);
    }

    @GetMapping(value = "/hash/{raw}", produces = "text/plain")
    public String makeHash(@PathVariable String raw) { return encoder.encode(raw); }

    @GetMapping("/check")
    public String check(@RequestParam String raw, @RequestParam String hash) {
        try {
            return encoder.matches(raw, hash) ? "MATCH" : "NO MATCH";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        final String loginId = body.get("loginId");
        final String rawPassword = body.get("password");

        Company company = companyRepo.findByAccountLoginId(loginId)
                .orElseThrow(() -> new ApiException(401, "사용자 없음"));

        Account acc = company.accounts.stream()
                .filter(a -> loginId.equals(a.loginId))
                .findFirst()
                .orElseThrow(() -> new ApiException(401, "사용자 없음"));

        // 현재는 평문 비교 (해시 복귀 시: encoder.matches(rawPassword, acc.passwordHash))
        if (!rawPassword.equals(acc.passwordHash)) {
            throw new ApiException(401, "비밀번호 불일치");
        }

        String companyId = toStringId(company.id);

        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", acc.id);
        user.put("username", acc.loginId);
        user.put("name", acc.name);
        user.put("role", acc.role);
        user.put("companyId", companyId);
        user.put("company", company.name);

        String token = jwt.create(new LinkedHashMap<>(user));

        return Map.of("token", token, "user", user);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ApiException(401, "인증 필요");
        }
        Claims claims = jwt.getClaims(auth.substring(7));

        return Map.of(
                "id", claims.get("id"),
                "username", claims.get("username"),
                "name", claims.get("name"),
                "role", claims.get("role"),
                "companyId", claims.get("companyId"), 
                "company", claims.get("company")
        );
    }

    @PostMapping("/logout")
    public Map<String, String> logout() {
        return Map.of("message", "로그아웃되었습니다");
    }
}