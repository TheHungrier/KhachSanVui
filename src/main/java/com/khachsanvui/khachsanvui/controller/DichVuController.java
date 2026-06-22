package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.DichVuDTO;
import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.DichVu;
import com.khachsanvui.khachsanvui.model.HoSoLuuTru;
import com.khachsanvui.khachsanvui.model.PhieuSuDungDV;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.DichVuRepository;
import com.khachsanvui.khachsanvui.repository.PhieuSuDungDVRepository;
import com.khachsanvui.khachsanvui.service.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/dich-vu")
public class DichVuController {

    private final DichVuService dichVuService;
    private final AuthService authService;
    private final LuuTruService luuTruService;
    private final PhieuSuDungDVRepository phieuSuDungDVRepository;
    private final VNPayService vnpayService;
    private final DichVuRepository dichVuRepository;
    private final GenericSearchService genericSearchService;

    public DichVuController(DichVuService dichVuService,
                            AuthService authService,
                            LuuTruService luuTruService,
                            PhieuSuDungDVRepository phieuSuDungDVRepository,
                            VNPayService vnpayService,
                            DichVuRepository dichVuRepository,
                            GenericSearchService genericSearchService) {
        this.dichVuService = dichVuService;
        this.authService = authService;
        this.luuTruService = luuTruService;
        this.phieuSuDungDVRepository = phieuSuDungDVRepository;
        this.vnpayService = vnpayService;
        this.dichVuRepository = dichVuRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping({"", "/"})
    public String danhSach(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("dsTatCa", dichVuService.search(keyword));
            model.addAttribute("dsNoiBat", List.of());
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("dsNoiBat", dichVuService.getDichVuNoiBat());
            model.addAttribute("dsTatCa", dichVuService.getAll());
        }
        return "dichvu/danh-sach";
    }

    @PostMapping("/dat-nhieu-dich-vu")
    public String datNhieuDichVu(
            @RequestParam("maLuuTru") Integer maLuuTru,
            @ModelAttribute com.khachsanvui.khachsanvui.dto.DichVuFormDTO formDTO,
            HttpServletRequest request,
            RedirectAttributes redirect) {

        TaiKhoan user = authService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (formDTO == null || formDTO.getItems() == null || formDTO.getItems().isEmpty()) {
            redirect.addFlashAttribute("error", "Vui lòng chọn ít nhất một dịch vụ trước khi thanh toán.");
            return "redirect:/dich-vu?maLuuTru=" + maLuuTru;
        }

        try {
            HoSoLuuTru hoSo = luuTruService.getById(maLuuTru);
            if (hoSo == null) {
                redirect.addFlashAttribute("error", "Hồ sơ lưu trú không tồn tại.");
                return "redirect:/dich-vu?maLuuTru=" + maLuuTru;
            }

            BigDecimal tongTienCumForm = BigDecimal.ZERO;
            String thoiGianNhom = String.valueOf(System.currentTimeMillis() / 1000);

            for (com.khachsanvui.khachsanvui.dto.DichVuFormDTO.CartItem item : formDTO.getItems()) {
                DichVu dv = dichVuService.getById(item.getMaDichVu());
                if (dv != null) {
                    PhieuSuDungDV phieu = new PhieuSuDungDV();
                    phieu.setHoSoLuuTru(hoSo);
                    phieu.setDichVu(dv);
                    phieu.setSoLuong(item.getSoLuong());
                    phieu.setDonGia(dv.getDonGia());

                    phieu.setTrangThai("Chờ thanh toán_" + thoiGianNhom);
                    phieu.setThoiGianSuDung(LocalDateTime.now());

                    phieuSuDungDVRepository.save(phieu);

                    BigDecimal thanhTienItem = dv.getDonGia().multiply(BigDecimal.valueOf(item.getSoLuong()));
                    tongTienCumForm = tongTienCumForm.add(thanhTienItem);
                }
            }

            String paymentUrl = vnpayService.taoUrlThanhToanDatNhieuDichVu(maLuuTru, tongTienCumForm, thoiGianNhom, request);

            return "redirect:" + paymentUrl;

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Đã xảy ra lỗi trong quá trình xử lý: " + e.getMessage());
            return "redirect:/dich-vu?maLuuTru=" + maLuuTru;
        }
    }

    @GetMapping("/admin")
    public String adminDanhSach(@RequestParam(required = false, defaultValue = "") String keyword,
                                @RequestParam(required = false, defaultValue = "") String sortBy,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";

        List<String> searchFields = List.of("tenDichVu", "moTa");

        PageResponseDTO<DichVu> response = genericSearchService.search(
                dichVuRepository, keyword, searchFields, sortBy, page, 10, "maDichVu"
        );

        model.addAttribute("dsDichVu", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/dichvu/danh-sach";
    }

    @GetMapping("/admin/them")
    public String adminThem(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        model.addAttribute("dto", new DichVuDTO());
        model.addAttribute("formTitle", "Thêm dịch vụ");
        return "admin/dichvu/form";
    }

    @GetMapping("/admin/sua/{id}")
    public String adminSua(@PathVariable Integer id, Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        model.addAttribute("dto", dichVuService.toDTO(dichVuService.getById(id)));
        model.addAttribute("formTitle", "Sửa dịch vụ");
        return "admin/dichvu/form";
    }

    @PostMapping("/admin/luu")
    public String adminLuu(@ModelAttribute("dto") DichVuDTO dto,
                           @RequestParam("fileAnh") MultipartFile fileAnh,
                           RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            dichVuService.saveWithFile(dto, fileAnh);
            redirect.addFlashAttribute("success", "Lưu dịch vụ thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dich-vu/admin";
    }

    @PostMapping("/admin/xoa/{id}")
    public String adminXoa(@PathVariable Integer id, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            DichVu dv = dichVuService.getById(id);
            dv.setTrangThai("Ngừng hoạt động");
            dichVuService.saveWithFile(dichVuService.toDTO(dv), null);
            redirect.addFlashAttribute("success", "Cập nhật trạng thái ngừng hoạt động của dịch vụ thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Xảy ra lỗi trong tiến trình xử lý nghiệp vụ.");
        }
        return "redirect:/dich-vu/admin";
    }
}
