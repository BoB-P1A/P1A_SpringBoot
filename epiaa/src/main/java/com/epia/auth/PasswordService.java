package com.epia.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service public class PasswordService { private final BCryptPasswordEncoder enc=new BCryptPasswordEncoder();
  public String hash(String raw){ return enc.encode(raw); }
  public boolean match(String raw,String hash){ return enc.matches(raw,hash); } }