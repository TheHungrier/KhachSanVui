package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.NhanVien;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.NhanVienRepository;
import com.khachsanvui.khachsanvui.service.AuthService;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import com.khachsanvui.khachsanvui.service.NhanVienService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/nhan-vien")
public class NhanVienController {

    private final NhanVienService nhanVienService;
    private final AuthService authService;
    private final NhanVienRepository nhanVienRepository;
    private final GenericSearchService genericSearchService;

    public NhanVienController(NhanVienService nhanVienService,
                              AuthService authService,
                              NhanVienRepository nhanVienRepository,
                              GenericSearchService genericSearchService) {
        this.nhanVienService = nhanVienService;
        this.authService = authService;
        this.nhanVienRepository = nhanVienRepository;
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

        List<String> searchFields = List.of("hoTen", "soDienThoai", "email", "chucVu");

        PageResponseDTO<NhanVien> response = genericSearchService.search(
                nhanVienRepository, keyword, searchFields, sortBy, page, 10, "maNV"
        );

        model.addAttribute("dsNhanVien", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/nhanvien/danh-sach";
    }

    @PostMapping("/admin/xoa/{id}")
    public String adminXoa(@PathVariable Integer id, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) {
            return "redirect:/login";
        }
        try {
            nhanVienService.delete(id);
            redirect.addFlashAttribute("success", "Xử lý tối ưu hồ sơ: Đã chấm dứt hợp đồng và vô hiệu hóa quyền truy cập dự án của nhân sự thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Hệ thống gặp lỗi khi xử lý chấm dứt dữ liệu nhân sự: " + e.getMessage());
        }
        return "redirect:/nhan-vien/admin";
    }
}