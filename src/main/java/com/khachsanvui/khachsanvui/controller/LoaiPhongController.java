package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.LoaiPhong;
import com.khachsanvui.khachsanvui.repository.LoaiPhongRepository;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import com.khachsanvui.khachsanvui.service.LoaiPhongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/loai-phong/admin")
public class LoaiPhongController {

    private final LoaiPhongService loaiPhongService;
    private final LoaiPhongRepository loaiPhongRepository;
    private final GenericSearchService genericSearchService;

    public LoaiPhongController(LoaiPhongService loaiPhongService, GenericSearchService genericSearchService, LoaiPhongRepository loaiPhongRepository) {
        this.loaiPhongService = loaiPhongService;
        this.genericSearchService = genericSearchService;
        this.loaiPhongRepository = loaiPhongRepository;
    }

    @GetMapping
    public String index(@RequestParam(required = false, defaultValue = "") String keyword,
                        @RequestParam(required = false, defaultValue = "") String sortBy,
                        @RequestParam(required = false, defaultValue = "1") int page,
                        Model model) {

        List<String> searchFields = List.of("tenLoaiPhong", "moTa");

        PageResponseDTO<LoaiPhong> response = genericSearchService.search(
                loaiPhongRepository, keyword, searchFields, sortBy, page, 10, "maLoaiPhong"
        );

        model.addAttribute("dsLoaiPhong", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/loaiphong/danh-sach";
    }

    @GetMapping("/them")
    public String showAddForm(Model model) {
        model.addAttribute("formTitle", "Thêm loại phòng nghỉ mới");
        model.addAttribute("loaiPhong", new LoaiPhong());
        return "admin/loaiphong/form";
    }

    @GetMapping("/sua/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        LoaiPhong lp = loaiPhongService.getById(id);
        if (lp == null) {
            ra.addFlashAttribute("error", "Không tồn tại danh mục loại phòng nghỉ yêu cầu.");
            return "redirect:/loai-phong/admin";
        }
        model.addAttribute("formTitle", "Chỉnh sửa loại phòng nghỉ");
        model.addAttribute("loaiPhong", lp);
        return "admin/loaiphong/form";
    }

    @PostMapping("/luu")
    public String save(@ModelAttribute LoaiPhong loaiPhong, RedirectAttributes ra) {
        try {
            loaiPhongService.save(loaiPhong);
            ra.addFlashAttribute("success", "Cập nhật dữ liệu cấu trúc danh mục loại phòng thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Xảy ra lỗi trong tiến trình xử lý lưu trữ dữ liệu.");
        }
        return "redirect:/loai-phong/admin";
    }

    @PostMapping("/xoa/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            LoaiPhong lp = loaiPhongService.getById(id);
            if (lp != null) {
                lp.setTrangThai("Ngừng hoạt động");
                loaiPhongService.save(lp);
            }
            ra.addFlashAttribute("success", "Cập nhật trạng thái ngừng hoạt động của loại phòng thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Xảy ra lỗi trong tiến trình xử lý lưu trữ dữ liệu.");
        }
        return "redirect:/loai-phong/admin";
    }
}