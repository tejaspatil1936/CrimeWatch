package com.crimewatch.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "citizen/home";
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        if (auth == null) return "redirect:/";
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (ga.getAuthority().equals("ROLE_ADMIN")) return "redirect:/admin";
            if (ga.getAuthority().equals("ROLE_OFFICER")) return "redirect:/dashboard";
        }
        return "redirect:/";
    }
}
