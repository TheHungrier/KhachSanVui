package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.DangKyDTO;
import com.khachsanvui.khachsanvui.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;


@Controller
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("pageTitle", "Đăng nhập");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("dto", new DangKyDTO());
        model.addAttribute("pageTitle", "Đăng ký");
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("dto") DangKyDTO dto,
                           BindingResult result,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("error", result.getAllErrors().getFirst().getDefaultMessage());
            return "redirect:/register";
        }
        try {
            authService.dangKyKhachHang(dto);
            redirect.addFlashAttribute("success", "Đăng ký thành công. Bạn có thể đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
}
