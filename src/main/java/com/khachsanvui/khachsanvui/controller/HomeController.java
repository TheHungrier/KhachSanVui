package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PhongService phongService;
    private final DichVuService dichVuService;
    private final AuthService authService;
    private final KhuyenMaiService khuyenMaiService;
    private final DanhGiaService danhGiaService;
    private final DiaDiemDLService diaDiemDLService;
    private final ChiNhanhService chiNhanhService;

    public HomeController(PhongService phongService,
                          DichVuService dichVuService,
                          AuthService authService,
                          KhuyenMaiService khuyenMaiService,
                          DanhGiaService danhGiaService,
                          DiaDiemDLService diaDiemDLService,
                          ChiNhanhService chiNhanhService) {
        this.phongService = phongService;
        this.dichVuService = dichVuService;
        this.authService = authService;
        this.khuyenMaiService = khuyenMaiService;
        this.danhGiaService = danhGiaService;
        this.diaDiemDLService = diaDiemDLService;
        this.chiNhanhService = chiNhanhService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (authService.isAdminOrLeTan(user)) {
            return "redirect:/admin";
        }
        model.addAttribute("phongNoiBat", phongService.getPhongNoiBat());
        model.addAttribute("dsLoaiPhong", phongService.getLoaiPhong());
        model.addAttribute("dichVuNoiBat", dichVuService.getDichVuNoiBat());
        model.addAttribute("flashSales", khuyenMaiService.toDTOList(khuyenMaiService.getActiveFlashSales()));
        model.addAttribute("danhGiaNoiBat", danhGiaService.toDTOList(danhGiaService.getDanhGiaNoiBat()));
        model.addAttribute("diaDiemDLs", diaDiemDLService.findAll());
        model.addAttribute("dsChiNhanh", chiNhanhService.getChiNhanhHienThi());
        return "index";
    }
}