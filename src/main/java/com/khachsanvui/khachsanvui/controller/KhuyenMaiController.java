package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.KhuyenMaiDTO;
import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.KhuyenMai;
import com.khachsanvui.khachsanvui.repository.KhuyenMaiRepository;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import com.khachsanvui.khachsanvui.service.KhuyenMaiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/khuyen-mai")
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final GenericSearchService genericSearchService;

    public KhuyenMaiController(KhuyenMaiService khuyenMaiService, KhuyenMaiRepository khuyenMaiRepository, GenericSearchService genericSearchService) {
        this.khuyenMaiService = khuyenMaiService;
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping
    public String khuyenMaiPage(Model model) {
        List<KhuyenMaiDTO> khuyenMai = khuyenMaiService.toDTOList(khuyenMaiRepository.findAll());
        model.addAttribute("khuyenmai", khuyenMai);
        return "khuyenmai/danh-sach";
    }

    @GetMapping("/api/flash-sales")
    @ResponseBody
    public List<KhuyenMaiDTO> getActiveFlashSales() {
        return khuyenMaiService.toDTOList(khuyenMaiService.getActiveFlashSales());
    }

    @GetMapping("/admin")
    public String index(@RequestParam(required = false, defaultValue = "") String keyword,
                        @RequestParam(required = false, defaultValue = "") String sortBy,
                        @RequestParam(required = false, defaultValue = "1") int page,
                        Model model) {

        List<String> searchFields = List.of("maCode", "tenKhuyenMai", "loaiGiamGia");

        PageResponseDTO<KhuyenMai> response = genericSearchService.search(
                khuyenMaiRepository, keyword, searchFields, sortBy, page, 10, "maKhuyenMai"
        );

        model.addAttribute("dsKhuyenMai", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/khuyenmai/danh-sach";
    }

    @GetMapping("/admin/them")
    public String showAddForm(Model model) {
        model.addAttribute("formTitle", "Khởi tạo chiến dịch khuyến mãi mới");
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "admin/khuyenmai/form";
    }

    @GetMapping("/admin/sua/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        return khuyenMaiRepository.findById(id).map(km -> {
            model.addAttribute("formTitle", "Chỉnh sửa chiến dịch khuyến mãi");
            model.addAttribute("khuyenMai", km);
            return "admin/khuyenmai/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Không tồn tại chương trình khuyến mãi yêu cầu.");
            return "redirect:/khuyen-mai/admin";
        });
    }

    @PostMapping("/admin/luu")
    public String save(@ModelAttribute KhuyenMai khuyenMai, RedirectAttributes ra) {
        try {
            if (khuyenMai.getApDungChoPhong() == null) khuyenMai.setApDungChoPhong(false);
            if (khuyenMai.getApDungChoDichVu() == null) khuyenMai.setApDungChoDichVu(false);
            if (khuyenMai.getTrangThai() == null || khuyenMai.getTrangThai().isBlank()) {
                khuyenMai.setTrangThai("Đang diễn ra");
            }
            khuyenMaiRepository.save(khuyenMai);
            ra.addFlashAttribute("success", "Dữ liệu cấu hình voucher khuyến mãi đã được lưu trữ thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Xảy ra lỗi trong tiến trình xử lý nghiệp vụ.");
        }
        return "redirect:/khuyen-mai/admin";
    }

    @PostMapping("/admin/xoa/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            khuyenMaiRepository.findById(id).ifPresent(km -> {
                km.setTrangThai("Đã hủy");
                khuyenMaiRepository.save(km);
            });
            ra.addFlashAttribute("success", "Gỡ bỏ mã chương trình khuyến mãi khỏi hệ thống thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể gỡ bỏ do mã voucher đã phát sinh dữ liệu hóa đơn giao dịch.");
        }
        return "redirect:/khuyen-mai/admin";
    }
}