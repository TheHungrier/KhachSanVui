package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.DatPhongDTO;
import com.khachsanvui.khachsanvui.dto.HuyDonDTO;
import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.repository.PhieuDatPhongRepository;
import com.khachsanvui.khachsanvui.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dat-phong")
public class DatPhongController {
    private final DatPhongService datPhongService;
    private final PhongService phongService;
    private final HoaDonService hoaDonService;
    private final AuthService authService;
    private final KhuyenMaiService khuyenMaiService;
    private final SpringTemplateEngine templateEngine;
    private final PhieuDatPhongRepository phieuDatPhongRepository;
    private final GenericSearchService genericSearchService;

    public DatPhongController(DatPhongService datPhongService, PhongService phongService,
                              HoaDonService hoaDonService, AuthService authService,
                              KhuyenMaiService khuyenMaiService, SpringTemplateEngine templateEngine,
                              PhieuDatPhongRepository phieuDatPhongRepository, GenericSearchService genericSearchService) {
        this.datPhongService = datPhongService;
        this.phongService = phongService;
        this.hoaDonService = hoaDonService;
        this.authService = authService;
        this.khuyenMaiService = khuyenMaiService;
        this.templateEngine = templateEngine;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping("/phong/{maPhong}")
    public String formDatPhong(@PathVariable Integer maPhong, Model model) {
        DatPhongDTO dto = new DatPhongDTO();
        dto.setMaPhong(maPhong);

        TaiKhoan user = authService.getCurrentUser();
        if (user != null && user.getKhachHang() != null) {
            KhachHang kh = user.getKhachHang();
            dto.setHoTen(kh.getHoTen());
            dto.setSoDienThoai(kh.getSoDienThoai());
            dto.setEmail(kh.getEmail());
            dto.setCccd(kh.getCccd());
            dto.setDiaChi(kh.getDiaChi());
        }

        model.addAttribute("dto", dto);
        model.addAttribute("phong", phongService.getById(maPhong));
        return "datphong/dat-phong";
    }

    @PostMapping
    public String taoDatPhong(@ModelAttribute("dto") DatPhongDTO dto,
                              RedirectAttributes redirect) {
        try {
            HoaDon hoaDon = datPhongService.taoDatPhong(dto);
            redirect.addFlashAttribute("success", "Tạo đặt phòng thành công. Bạn có thể thanh toán để giữ phòng.");
            return "redirect:/thanh-toan/" + hoaDon.getMaHD();
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/dat-phong/phong/" + dto.getMaPhong();
        }
    }

    @GetMapping("/lich-su")
    public String lichSu(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (user == null || user.getKhachHang() == null) {
            return "redirect:/login";
        }
        var ds = datPhongService.lichSuTheoKhachHang(user.getKhachHang().getMaKH());
        model.addAttribute("dsDatPhong", ds);
        var hoaDonMap = new java.util.HashMap<Integer, HoaDon>();
        for (var d : ds) {
            try {
                var hd = hoaDonService.getByDatPhong(d.getMaDatPhong());
                if (hd != null) hoaDonMap.put(d.getMaDatPhong(), hd);
            } catch (Exception ignored) {
            }
        }
        model.addAttribute("hoaDonMap", hoaDonMap);
        return "datphong/lich-su";
    }

    @GetMapping("/{maDatPhong}")
    public String chiTiet(@PathVariable Integer maDatPhong, Model model) {
        model.addAttribute("datPhong", datPhongService.getById(maDatPhong));
        model.addAttribute("chiTiet", datPhongService.getChiTiet(maDatPhong));
        model.addAttribute("hoaDon", hoaDonService.getByDatPhong(maDatPhong));
        if (!model.containsAttribute("huyDonDTO")) {
            model.addAttribute("huyDonDTO", new HuyDonDTO());
        }
        return "datphong/chi-tiet";
    }

    @PostMapping("/huy/{maDatPhong}")
    public String huyDon(@PathVariable Integer maDatPhong,
                         @Valid @ModelAttribute("huyDonDTO") HuyDonDTO huyDonDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError("lyDoHuy") != null
                    ? bindingResult.getFieldError("lyDoHuy").getDefaultMessage()
                    : "Dữ liệu yêu cầu hủy không hợp lệ!";
            redirect.addFlashAttribute("error", errorMsg);
            return "redirect:/dat-phong/" + maDatPhong;
        }
        try {
            datPhongService.huyDon(maDatPhong, huyDonDTO);
            HoaDon hd = hoaDonService.getByDatPhong(maDatPhong);
            if (hd != null && "Đã thanh toán".equalsIgnoreCase(hd.getTrangThaiThanhToan())) {
                redirect.addFlashAttribute("success", "Gửi yêu cầu hủy đơn thành công. Vui lòng chờ bộ phận quản trị viên phê duyệt hoàn tiền.");
            } else {
                redirect.addFlashAttribute("success", "Hủy đơn đặt phòng thành công.");
            }
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dat-phong/" + maDatPhong;
    }

    @GetMapping("/xuat-pdf/{maDatPhong}")
    public ResponseEntity<byte[]> taiHoaDonPdf(@PathVariable Integer maDatPhong) {
        try {
            var dp = datPhongService.getById(maDatPhong);
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("datPhong", dp);
            context.setVariable("chiTiet", datPhongService.getChiTiet(maDatPhong));
            context.setVariable("hoaDon", hoaDonService.getByDatPhong(maDatPhong));

            String htmlContent = templateEngine.process("pdf/hoa-don-template", context);
            byte[] pdfBytes = datPhongService.convertHtmlToPdf(htmlContent);

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=HoaDon_DP-" + maDatPhong + ".pdf")
                    .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/admin")
    public String adminDanhSach(@RequestParam(required = false, defaultValue = "") String keyword,
                                @RequestParam(required = false, defaultValue = "") String sortBy,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";

        List<String> searchFields = List.of("trangThai", "ghiChu", "maGiamGia");

        PageResponseDTO<PhieuDatPhong> response = genericSearchService.search(
                phieuDatPhongRepository, keyword, searchFields, sortBy, page, 10, "maDatPhong"
        );

        model.addAttribute("dsDatPhong", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/datphong/danh-sach";
    }

    @PostMapping("/admin/trang-thai/{maDatPhong}")
    public String capNhatTrangThai(@PathVariable Integer maDatPhong,
                                   @RequestParam String trangThai,
                                   RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            datPhongService.capNhatTrangThaiVoiPhong(maDatPhong, trangThai);
            redirect.addFlashAttribute("success", "Cập nhật trạng thái đơn đặt phòng #" + maDatPhong + " thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dat-phong/admin";
    }

    @PostMapping("/ap-dung-khuyen-mai")
    @ResponseBody
    public ResponseEntity<?> apDungKhuyenMai(@RequestParam String code, @RequestParam Double tongTienTamTinh) {
        TaiKhoan user = authService.getCurrentUser();

        return khuyenMaiService.findByMaCode(code.trim())
                .filter(km -> km.getTrangThai() != null && "Đang diễn ra".equalsIgnoreCase(km.getTrangThai().trim()))
                .map(km -> {
                    if (user != null && user.getKhachHang() != null) {
                        boolean daDung = khuyenMaiService.kiemTraUserDaDungMa(user.getKhachHang().getMaKH(), km.getMaKhuyenMai());
                        if (daDung) {
                            return ResponseEntity.ok(Map.of("success", false, "message", "Mã giảm giá này bạn đã sử dụng rồi!"));
                        }
                    }

                    double soTienGiam = khuyenMaiService.tinhSoTienGiam(km, tongTienTamTinh);
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "soTienGiam", soTienGiam,
                            "tongTienSauGiam", tongTienTamTinh - soTienGiam,
                            "message", "Áp dụng mã giảm giá thành công!"
                    ));
                })
                .orElse(ResponseEntity.ok(Map.of("success", false, "message", "Mã giảm giá không tồn tại hoặc đã hết hạn!")));
    }
}