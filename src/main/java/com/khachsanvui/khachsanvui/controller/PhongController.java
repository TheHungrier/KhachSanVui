package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.dto.PhongDTO;
import com.khachsanvui.khachsanvui.model.DanhGia;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.DanhGiaRepository;
import com.khachsanvui.khachsanvui.repository.PhongRepository;
import com.khachsanvui.khachsanvui.repository.YeuThichRepository;
import com.khachsanvui.khachsanvui.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/phong")
public class PhongController {
    private final PhongService phongService;
    private final AuthService authService;
    private final DanhGiaService danhGiaService;
    private final HinhAnhPhongService hinhAnhPhongService;
    private final NoiBatService noiBatService;
    private final DanhGiaRepository danhGiaRepository;
    private final ChiNhanhService chiNhanhService;
    private final PhongRepository phongRepository;
    private final GenericSearchService genericSearchService;
    private final YeuThichRepository yeuThichRepository;

    public PhongController(PhongService phongService, AuthService authService,
                           DanhGiaService danhGiaService, HinhAnhPhongService hinhAnhPhongService,
                           NoiBatService noiBatService, DanhGiaRepository danhGiaRepository,
                           ChiNhanhService chiNhanhService, PhongRepository phongRepository,
                           GenericSearchService genericSearchService, YeuThichRepository yeuThichRepository) {
        this.phongService = phongService;
        this.authService = authService;
        this.danhGiaService = danhGiaService;
        this.hinhAnhPhongService = hinhAnhPhongService;
        this.noiBatService = noiBatService;
        this.danhGiaRepository = danhGiaRepository;
        this.chiNhanhService = chiNhanhService;
        this.phongRepository = phongRepository;
        this.genericSearchService = genericSearchService;
        this.yeuThichRepository = yeuThichRepository;
    }

    @GetMapping({"", "/"})
    public String danhSachPhong(@RequestParam(required = false) String keyword,
                                @RequestParam(required = false) Integer maChiNhanh,
                                @RequestParam(required = false) Integer maLoaiPhong,
                                @RequestParam(required = false) Integer soKhach,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
                                @RequestParam(required = false) String gia,
                                @RequestParam(required = false) String danhGiaCao,
                                @RequestParam(required = false) List<String> tienNghi,
                                @RequestParam(required = false) String sortBy,
                                Model model) {

        model.addAttribute("dsPhong", phongService.findPhongAvanced(keyword, maChiNhanh, maLoaiPhong, soKhach, ngayNhan, ngayTra, gia, danhGiaCao, tienNghi, sortBy));
        model.addAttribute("dsLoaiPhong", phongService.getLoaiPhong());
        model.addAttribute("dsChiNhanh", chiNhanhService.findAll());

        model.addAttribute("keyword", keyword);
        model.addAttribute("maChiNhanh", maChiNhanh);
        model.addAttribute("maLoaiPhong", maLoaiPhong);
        model.addAttribute("soKhach", soKhach);
        model.addAttribute("ngayNhan", ngayNhan);
        model.addAttribute("ngayTra", ngayTra);
        model.addAttribute("giaDaChon", gia);
        model.addAttribute("tienNghiDaChon", tienNghi);
        model.addAttribute("sortBy", sortBy);

        return "phong/danh-sach";
    }

    @GetMapping("/{id}")
    public String chiTietPhong(@PathVariable Integer id, Model model) {
        Phong phong = phongService.getById(id);

        List<DanhGia> danhSachDanhGia = danhGiaRepository.findByMaPhong(id, "Hiển thị");

        long soBaiTot = danhSachDanhGia.stream()
                .filter(dg -> dg.getSoSao() != null && dg.getSoSao() >= 4)
                .count();
        double tyLeHaiLong = danhSachDanhGia.isEmpty() ? 100.0 : ((double) soBaiTot / danhSachDanhGia.size()) * 100;

        Double diemSaoTrungBinhHe5 = danhGiaRepository.getAverageRating(id, "Hiển thị");
        double diemHe10ThucTe = (diemSaoTrungBinhHe5 != null) ? diemSaoTrungBinhHe5 * 2 : 0.0;

        phong.setDiemTrungBinh(java.math.BigDecimal.valueOf(diemHe10ThucTe));
        phong.setSoLuongDanhGia(danhSachDanhGia.size());

        boolean isFavorite = false;
        TaiKhoan user = authService.getCurrentUser();
        if (user != null && user.getKhachHang() != null) {
            isFavorite = yeuThichRepository.existsByKhachHang_MaKHAndPhong_MaPhong(user.getKhachHang().getMaKH(), id);
        }

        model.addAttribute("phong", phong);
        model.addAttribute("tyLeHaiLong", tyLeHaiLong);
        model.addAttribute("diemTraiNghiemThuc", diemHe10ThucTe);
        model.addAttribute("danhSachDanhGia", danhSachDanhGia);
        model.addAttribute("isFavorite", isFavorite);

        model.addAttribute("danhSachAnh", hinhAnhPhongService.getAnhTheoPhong(id));
        model.addAttribute("mapEmbedUrl", phongService.getGoogleMapEmbedUrl(phong));
        model.addAttribute("highlights", noiBatService.getHighlights(phong));

        return "phong/chi-tiet";
    }

    @PostMapping("/{id}/danh-gia")
    public String guiDanhGia(@PathVariable Integer id,
                             @RequestParam Integer soSao,
                             @RequestParam String binhLuan,
                             RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (user == null) return "redirect:/login";
        if (user.getKhachHang() == null) {
            redirect.addFlashAttribute("error", "Chỉ khách hàng mới được đánh giá.");
            return "redirect:/phong/" + id;
        }
        try {
            danhGiaService.taoDanhGia(id, user.getKhachHang().getMaKH(), null, soSao, binhLuan);
            redirect.addFlashAttribute("success", "Cảm ơn bạn đã đánh giá!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/phong/" + id;
    }

    @GetMapping("/admin")
    public String adminDanhSach(@RequestParam(required = false, defaultValue = "") String keyword,
                                @RequestParam(required = false, defaultValue = "") String sortBy,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";

        List<String> searchFields = List.of("tenPhong", "trangThai", "moTa");

        PageResponseDTO<Phong> response = genericSearchService.search(
                phongRepository, keyword, searchFields, sortBy, page, 10, "maPhong"
        );

        model.addAttribute("dsPhong", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/phong/danh-sach";
    }

    @GetMapping("/admin/them")
    public String adminThem(Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        model.addAttribute("dto", new PhongDTO());
        model.addAttribute("dsLoaiPhong", phongService.getLoaiPhong());
        model.addAttribute("dsChiNhan", chiNhanhService.findAll());
        model.addAttribute("formTitle", "Thêm phòng");
        return "admin/phong/form";
    }

    @GetMapping("/admin/sua/{id}")
    public String adminSua(@PathVariable Integer id, Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        model.addAttribute("dto", phongService.toDTO(phongService.getById(id)));
        model.addAttribute("dsLoaiPhong", phongService.getLoaiPhong());
        model.addAttribute("dsChiNhan", chiNhanhService.findAll());
        model.addAttribute("formTitle", "Sửa phòng");
        return "admin/phong/form";
    }

    @PostMapping("/admin/luu")
    public String adminLuu(@ModelAttribute("dto") PhongDTO dto,
                           @RequestParam("filesAnh") MultipartFile[] filesAnh,
                           RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            phongService.saveRoomWithImages(dto, filesAnh);
            redirect.addFlashAttribute("success", "Lưu phòng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/phong/admin";
    }

    @PostMapping("/admin/xoa/{id}")
    public String adminXoa(@PathVariable Integer id, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            Phong phong = phongService.getById(id);
            phong.setTrangThai("Ngừng hoạt động");
            phongService.saveRoomWithImages(phongService.toDTO(phong), new MultipartFile[0]);
            redirect.addFlashAttribute("success", "Cập nhật trạng thái ngừng hoạt động của phòng thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Xảy ra lỗi trong tiến trình xử lý nghiệp vụ.");
        }
        return "redirect:/phong/admin";
    }
}