package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.KhachHangRepository;
import com.khachsanvui.khachsanvui.service.AuthService;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import com.khachsanvui.khachsanvui.service.KhachHangService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/khach-hang")
public class KhachHangController {

    private final KhachHangService khachHangService;
    private final AuthService authService;
    private final KhachHangRepository khachHangRepository;
    private final GenericSearchService genericSearchService;

    public KhachHangController(KhachHangService khachHangService, AuthService authService, KhachHangRepository khachHangRepository, GenericSearchService genericSearchService) {
        this.khachHangService = khachHangService;
        this.authService = authService;
        this.khachHangRepository = khachHangRepository;
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

        List<String> searchFields = List.of("hoTen", "soDienThoai", "cccd", "email");

        PageResponseDTO<KhachHang> response = genericSearchService.search(
                khachHangRepository, keyword, searchFields, sortBy, page, 10, "maKH"
        );

        model.addAttribute("dsKhachHang", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/khachhang/danh-sach";
    }

    @GetMapping("/admin/them")
    public String adminThem(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        KhachHang kh = new KhachHang();
        kh.setGioiTinh("Nam");
        model.addAttribute("khachHang", kh);
        model.addAttribute("formTitle", "Thêm khách hàng");
        return "admin/khachhang/form";
    }

    @GetMapping("/admin/sua/{id}")
    public String adminSua(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            model.addAttribute("khachHang", khachHangService.getById(id));
            model.addAttribute("formTitle", "Sửa khách hàng");
            return "admin/khachhang/form";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/khach-hang/admin";
        }
    }

    @PostMapping("/admin/luu")
    public String adminLuu(@ModelAttribute("khachHang") KhachHang khachHang,
                           @RequestParam("fileAnh") MultipartFile fileAnh,
                           RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            khachHangService.saveWithFile(khachHang, fileAnh);
            redirect.addFlashAttribute("success", "Lưu thông tin khách hàng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/khach-hang/admin";
    }

    @PostMapping("/admin/xoa/{id}")
    public String adminXoa(@PathVariable Integer id, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            khachHangService.delete(id);
            redirect.addFlashAttribute("success", "Xử lý yêu cầu gỡ bỏ / đình chỉ hoạt động hồ sơ khách hàng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa do hồ sơ liên quan chặt chẽ đến hóa đơn quyết toán dòng tiền.");
        }
        return "redirect:/khach-hang/admin";
    }
}