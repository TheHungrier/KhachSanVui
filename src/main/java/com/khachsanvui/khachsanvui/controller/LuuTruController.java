package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.HoSoLuuTru;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.HoSoLuuTruRepository;
import com.khachsanvui.khachsanvui.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/luu-tru")
public class LuuTruController {

    private final LuuTruService luuTruService;
    private final PhongService phongService;
    private final KhachHangService khachHangService;
    private final DatPhongService datPhongService;
    private final AuthService authService;
    private final HoSoLuuTruRepository hoSoLuuTruRepository;
    private final GenericSearchService genericSearchService;

    public LuuTruController(LuuTruService luuTruService,
                            PhongService phongService,
                            KhachHangService khachHangService,
                            DatPhongService datPhongService,
                            AuthService authService,
                            HoSoLuuTruRepository hoSoLuuTruRepository,
                            GenericSearchService genericSearchService) {
        this.luuTruService = luuTruService;
        this.phongService = phongService;
        this.khachHangService = khachHangService;
        this.datPhongService = datPhongService;
        this.authService = authService;
        this.hoSoLuuTruRepository = hoSoLuuTruRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping("/admin")
    public String adminDanhSach(@RequestParam(required = false, defaultValue = "") String keyword,
                                @RequestParam(required = false, defaultValue = "") String sortBy,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }

        List<String> searchFields = List.of("trangThai", "ghiChu");

        PageResponseDTO<HoSoLuuTru> response = genericSearchService.search(
                hoSoLuuTruRepository, keyword, searchFields, sortBy, page, 10, "maLuuTru"
        );

        model.addAttribute("dsLuuTru", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/luutru/danh-sach";
    }

    @GetMapping("/admin/dang-luu-tru")
    public String adminDangLuuTru(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        model.addAttribute("dsLuuTru", luuTruService.getDangLuuTru());
        return "admin/luutru/danh-sach";
    }

    @GetMapping("/admin/nhan-phong")
    public String formNhanPhong(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        model.addAttribute("dsPhong", phongService.getPhongTrong());
        model.addAttribute("dsKhachHang", khachHangService.getAll());
        model.addAttribute("dsDatPhong", datPhongService.getAll());
        return "admin/luutru/nhan-phong";
    }

    @PostMapping("/admin/nhan-phong")
    public String nhanPhong(@RequestParam(required = false) Integer maDatPhong,
                            @RequestParam Integer maPhong,
                            @RequestParam Integer maKH,
                            RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            Integer maNV = user.getNhanVien() == null ? null : user.getNhanVien().getMaNV();
            luuTruService.nhanPhong(maDatPhong, maPhong, maKH, maNV);
            redirect.addFlashAttribute("success", "Nhận phòng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/luu-tru/admin";
    }

    @PostMapping("/admin/tra-phong/{maLuuTru}")
    public String traPhong(@PathVariable Integer maLuuTru,
                           RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            luuTruService.traPhong(maLuuTru);
            redirect.addFlashAttribute("success", "Trả phòng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/luu-tru/admin";
    }
}