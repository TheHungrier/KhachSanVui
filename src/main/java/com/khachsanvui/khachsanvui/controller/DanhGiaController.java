package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.service.AuthService;
import com.khachsanvui.khachsanvui.service.DanhGiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/danh-gia")
public class DanhGiaController {
    private final DanhGiaService danhGiaService;
    private final AuthService authService;

    public DanhGiaController(DanhGiaService danhGiaService, AuthService authService) {
        this.danhGiaService = danhGiaService;
        this.authService = authService;
    }

    @PostMapping("/them")
    public String themDanhGia(@RequestParam Integer maPhong,
                              @RequestParam Integer soSao,
                              @RequestParam(required = false) String binhLuan,
                              HttpSession session,
                              RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (user == null || user.getKhachHang() == null) {
            redirect.addFlashAttribute("error", "Vui lòng đăng nhập để đánh giá.");
            return "redirect:/login";
        }
        try {
            danhGiaService.taoDanhGia(maPhong, user.getKhachHang().getMaKH(), null, soSao, binhLuan);
            redirect.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/phong/" + maPhong;
    }
}