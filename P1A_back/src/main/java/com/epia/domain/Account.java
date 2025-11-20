package com.epia.domain;

public class Account {
    public String id;            // 내부적으로 unique key (String)
    public String loginId;       // 로그인용 ID (프론트의 "id" 입력값)
    public String passwordHash;  // BCrypt 해시
    public String name;          // 실명
    public String role;          // admin | developer | privacy-team | planning-team
}