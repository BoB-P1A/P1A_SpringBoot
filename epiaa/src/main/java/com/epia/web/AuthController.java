package com.epia.web;

import com.epia.auth.*; 
import com.epia.domain.*;
import com.epia.repo.*;
import com.epia.support.ApiException;
import org.springframework.web.bind.annotation.*;
import java.util.*; 

@RestController @RequestMapping("/auth")
public class AuthController {
  private final CompanyRepo companyRepo;
  private final PasswordService pw;
  private final JwtService jwt;
  
  public AuthController(CompanyRepo r,PasswordService p,JwtService j){ 
	  this.companyRepo=r;
	  this.pw=p;
	  this.jwt=j;
  }

  // 로그인: { "id": "<username>", "password": "..." }
  @PostMapping("/login")
  public Map<String,Object> login(@RequestBody Map<String,String> b){
    String username=b.get("id"), password=b.get("password");
    Company c = companyRepo.findByAccountsLoginId(username).orElseThrow(()->new ApiException(401,"사용자 없음"));
    Account acc = c.accounts.stream().filter(a->username.equals(a.loginId)).findFirst().orElseThrow(()->new ApiException(401,"사용자 없음"));
    if(!pw.match(password, acc.passwordHash)) throw new ApiException(401,"비밀번호 불일치");
    Map<String,Object> user = Map.of(
      "id", acc.id, "username", acc.loginId, "name", acc.name, "role", acc.role, "company", c.name);
    String token = jwt.create(new HashMap<>(user));
    return Map.of("token", token, "user", user);
  }

  @PostMapping("/logout") public Map<String,String> logout(){ return Map.of("message","로그아웃되었습니다"); }

  @GetMapping("/me")
  public Map<String,Object> me(@RequestHeader("Authorization") String auth){
    var claims = jwt.parse(auth.substring(7)).getBody();
    return Map.of("id",claims.get("id"),"username",claims.get("username"),"name",claims.get("name"),"role",claims.get("role"),"company",claims.get("company"));
  }
}