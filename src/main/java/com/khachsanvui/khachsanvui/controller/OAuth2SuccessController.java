package com.khachsanvui.khachsanvui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuth2SuccessController {

    @GetMapping("/oauth2/success")
    public String success() {
        return "redirect:/home";
    }
}