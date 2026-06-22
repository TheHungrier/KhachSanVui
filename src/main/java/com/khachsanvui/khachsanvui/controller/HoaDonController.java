package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.HoaDonRepository;
import com.khachsanvui.khachsanvui.service.AuthService;
import com.khachsanvui.khachsanvui.service.GenericSearchService;
import com.khachsanvui.khachsanvui.service.HoaDonService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/hoa-don")
public class HoaDonController {
    private final HoaDonService hoaDonService;
    private final AuthService authService;
    private final HoaDonRepository hoaDonRepository;
    private final GenericSearchService genericSearchService;

    public HoaDonController(HoaDonService hoaDonService, AuthService authService, HoaDonRepository hoaDonRepository, GenericSearchService genericSearchService) {
        this.hoaDonService = hoaDonService;
        this.authService = authService;
        this.hoaDonRepository = hoaDonRepository;
        this.genericSearchService = genericSearchService;
    }

    @GetMapping("/admin")
    public String adminDanhSach(@RequestParam(required = false, defaultValue = "") String keyword,
                                @RequestParam(required = false, defaultValue = "") String sortBy,
                                @RequestParam(required = false, defaultValue = "1") int page,
                                Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";

        List<String> searchFields = List.of("hinhThucThanhToan", "trangThaiThanhToan");

        PageResponseDTO<HoaDon> response = genericSearchService.search(
                hoaDonRepository, keyword, searchFields, sortBy, page, 10, "maHD"
        );

        model.addAttribute("dsHoaDon", response.getContent());
        model.addAttribute("pageData", response);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);

        return "admin/hoadon/danh-sach";
    }

    @GetMapping("/admin/thanh-toan/{id}")
    public String formThanhToan(@PathVariable Integer id, Model model, RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            HoaDon hoaDon = hoaDonService.getById(id);
            model.addAttribute("hoaDon", hoaDon);
            return "admin/hoadon/form";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/hoa-don/admin";
        }
    }

    @PostMapping("/admin/xac-nhan/{id}")
    public String xacNhanThanhToan(@PathVariable Integer id,
                                   @RequestParam String hinhThucThanhToan,
                                   @RequestParam(required = false, defaultValue = "0") BigDecimal phuPhi,
                                   @RequestParam(required = false) String ghiChu,
                                   RedirectAttributes redirect) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        try {
            Integer maNV = (user.getNhanVien() != null) ? user.getNhanVien().getMaNV() : null;
            hoaDonService.xuLyThanhToan(id, hinhThucThanhToan, phuPhi, ghiChu, maNV);
            redirect.addFlashAttribute("success", "Xác nhận thanh toán hóa đơn thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/hoa-don/admin";
    }

    @GetMapping("/admin/in-pdf/{id}")
    public String viewInAnPDF(@PathVariable Integer id, Model model) {
        TaiKhoan user = authService.getCurrentUser();
        if (!authService.isAdminOrLeTan(user)) return "redirect:/login";
        model.addAttribute("hd", hoaDonService.getById(id));
        return "admin/hoadon/in-pdf";
    }

    @GetMapping("/admin/export-excel")
    public void xuatExcelDanhSachHD(HttpServletResponse response) throws IOException {
        TaiKhoan user = authService.getCurrentUser();
        if (user == null || !authService.isAdminOrLeTan(user)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        List<HoaDon> list = hoaDonService.getAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Danh sach hoa don");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Mã HĐ", "Khách hàng", "Hồ sơ gốc", "Ngày lập", "Tổng tiền", "Phương thức", "Trạng thái"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (HoaDon hd : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("HĐ-" + hd.getMaHD());

            String khName = "Khách vãng lai";
            if (hd.getPhieuDatPhong() != null && hd.getPhieuDatPhong().getKhachHang() != null) {
                khName = hd.getPhieuDatPhong().getKhachHang().getHoTen();
            } else if (hd.getHoSoLuuTru() != null && hd.getHoSoLuuTru().getKhachHang() != null) {
                khName = hd.getHoSoLuuTru().getKhachHang().getHoTen();
            }
            row.createCell(1).setCellValue(khName);

            String origin = "───";
            if (hd.getPhieuDatPhong() != null) {
                origin = "Đặt phòng #" + hd.getPhieuDatPhong().getMaDatPhong();
            } else if (hd.getHoSoLuuTru() != null) {
                origin = "Lưu trú #" + hd.getHoSoLuuTru().getMaLuuTru();
            }
            row.createCell(2).setCellValue(origin);

            row.createCell(3).setCellValue(hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "───");
            row.createCell(4).setCellValue(hd.getTongTien() != null ? hd.getTongTien().doubleValue() : 0.0);
            row.createCell(5).setCellValue(hd.getHinhThucThanhToan() != null ? hd.getHinhThucThanhToan() : "───");
            row.createCell(6).setCellValue(hd.getTrangThaiThanhToan());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=danh-sach-hoa-don.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}