package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.service.AuthService;
import com.khachsanvui.khachsanvui.service.BaoCaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final BaoCaoService baoCaoService;
    private final AuthService authService;

    public AdminController(BaoCaoService baoCaoService, AuthService authService) {
        this.baoCaoService = baoCaoService;
        this.authService = authService;
    }

    @GetMapping({"/admin", "/admin/dashboard", "/bao-cao/doanh-thu"})
    public String dashboard(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        model.addAttribute("dashboard", baoCaoService.dashboard());
        return "admin/dashboard";
    }
}