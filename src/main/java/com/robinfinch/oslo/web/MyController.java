package com.robinfinch.oslo.web;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    @GetMapping("/")
    public String greeting(OAuth2AuthenticationToken authentication,
                            Model model) {

            String name = authentication.getPrincipal().getAttributes().get("name").toString();

            model.addAttribute("name", name);

            return "greeting";
    }
}
