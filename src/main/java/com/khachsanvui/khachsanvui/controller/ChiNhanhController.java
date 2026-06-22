package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.ChiNhanh;
import com.khachsanvui.khachsanvui.service.ChiNhanhService;
import com.khachsanvui.khachsanvui.repository.ChiNhanhRepository;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/chi-nhanh/admin")
public class ChiNhanhController {

    private final ChiNhanhRepository chiNhanhRepository;
    private final GenericSearchService genericSearchService;

    public ChiNhanhController(ChiNhanhRepository chiNhanhRepository, GenericSearchService genericSearchService) {
        this.chiNhanhRepository = chiNhanhRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping
    public String index(@RequestParam(required = false, defaultValue = "") String keyword,
                        @RequestParam(required = false, defaultValue = "") String sortBy,
                        @RequestParam(required = false, defaultValue = "1") int page,
                        Model model) {

        List<String> searchFields = List.of("tenChiNhanh", "diaChi", "soDienThoai");

        PageResponseDTO<ChiNhanh> response = genericSearchService.search(
                chiNhanhRepository, keyword, searchFields, sortBy, page, 10, "maChiNhanh"
        );

        model.addAttribute("dsChiNhanh", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/chinhanh/danh-sach";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("formTitle", "Thêm chi nhánh cơ sở mới");
        model.addAttribute("chiNhanh", new ChiNhanh());
        return "admin/chinhanh/form";
    }

    @GetMapping("/sua/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        return chiNhanhRepository.findById(id).map(cn -> {
            model.addAttribute("formTitle", "Chỉnh sửa thông tin chi nhánh");
            model.addAttribute("chiNhanh", cn);
            return "admin/chinhanh/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Không tìm thấy thông tin cơ sở chi nhánh.");
            return "redirect:/chi-nhanh/admin";
        });
    }

    @PostMapping("/luu")
    public String save(@ModelAttribute ChiNhanh chiNhanh, RedirectAttributes ra) {
        try {
            chiNhanhRepository.save(chiNhanh);
            ra.addFlashAttribute("success", "Cấu trúc dữ liệu chi nhánh đã được cập nhật thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Tiến trình lưu trữ thông tin cơ sở xảy ra lỗi.");
        }
        return "redirect:/chi-nhanh/admin";
    }

    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            chiNhanhRepository.findById(id).ifPresent(cn -> {
                cn.setTrangThai("Ngừng hoạt động");
                chiNhanhRepository.save(cn);
            });
            ra.addFlashAttribute("success", "Cập nhật trạng thái ngừng hoạt động của chi nhánh thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Tiến trình lưu trữ thông tin cơ sở xảy ra lỗi.");
        }
        return "redirect:/chi-nhanh/admin";
    }
}