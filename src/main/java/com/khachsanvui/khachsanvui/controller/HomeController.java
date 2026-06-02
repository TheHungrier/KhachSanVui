package com.khachsanvui.khachsanvui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/phong/1"; // Tự động chuyển hướng về trang chi tiết phòng 101
    }
}