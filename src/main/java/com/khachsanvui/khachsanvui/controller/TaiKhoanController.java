package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import com.khachsanvui.khachsanvui.service.KhachHangService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/tai-khoan")
public class TaiKhoanController {

    private final KhachHangService khachHangService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;

    public TaiKhoanController(KhachHangService khachHangService,
                              TaiKhoanRepository taiKhoanRepository,
                              PasswordEncoder passwordEncoder) {
        this.khachHangService = khachHangService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/ho-so")
    public String hoSoCaNhanPage(Model model, Principal principal) {
        String tenDangNhap = principal.getName();
        KhachHang khachHang = khachHangService.findByTenDangNhap(tenDangNhap);
        model.addAttribute("khachHang", khachHang);
        return "taikhoan/ho-so";
    }

    @PostMapping("/ho-so/cap-nhat")
    public String capNhatHoSo(@ModelAttribute("khachHang") KhachHang khachHangForm,
                              @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                              Principal principal,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            String tenDangNhap = principal.getName();
            KhachHang khachHangGoc = khachHangService.findByTenDangNhap(tenDangNhap);

            khachHangGoc.setHoTen(khachHangForm.getHoTen());
            khachHangGoc.setSoDienThoai(khachHangForm.getSoDienThoai());
            khachHangGoc.setNgaySinh(khachHangForm.getNgaySinh());
            khachHangGoc.setGioiTinh(khachHangForm.getGioiTinh());
            khachHangGoc.setDiaChi(khachHangForm.getDiaChi());

            if (avatarFile != null && !avatarFile.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + avatarFile.getOriginalFilename();
                String uploadDir = "uploads/avatars/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                khachHangGoc.setHinhAnh(fileName);
            }

            khachHangService.save(khachHangGoc);

            Object userLoginObj = session.getAttribute("USER_LOGIN");
            if (userLoginObj != null) {
                com.khachsanvui.khachsanvui.model.TaiKhoan taiKhoanSession = (com.khachsanvui.khachsanvui.model.TaiKhoan) userLoginObj;
                if (taiKhoanSession.getKhachHang() != null) {
                    taiKhoanSession.getKhachHang().setHoTen(khachHangGoc.getHoTen());
                    taiKhoanSession.getKhachHang().setHinhAnh(khachHangGoc.getHinhAnh());
                    session.setAttribute("USER_LOGIN", taiKhoanSession);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống khi lưu hồ sơ: " + e.getMessage());
        }
        return "redirect:/tai-khoan/ho-so";
    }

    @GetMapping("/doi-mat-khau")
    public String doiMatKhauPage(Model model, Principal principal) {
        String tenDangNhap = principal.getName();
        KhachHang khachHang = khachHangService.findByTenDangNhap(tenDangNhap);
        model.addAttribute("khachHang", khachHang);
        return "taikhoan/doi-mat-khau";
    }

    @PostMapping("/doi-mat-khau/thuc-hien")
    public String thucHienDoiMatKhau(@RequestParam("matKhauCu") String matKhauCu,
                                     @RequestParam("matKhauMoi") String matKhauMoi,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        try {
            String tenDangNhap = principal.getName();
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(tenDangNhap);

            if (taiKhoanOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản người dùng!");
                return "redirect:/tai-khoan/doi-mat-khau";
            }

            TaiKhoan taiKhoan = taiKhoanOpt.get();

            if (!passwordEncoder.matches(matKhauCu, taiKhoan.getMatKhau())) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không chính xác!");
                return "redirect:/tai-khoan/doi-mat-khau";
            }

            taiKhoan.setMatKhau(passwordEncoder.encode(matKhauMoi));
            taiKhoanRepository.save(taiKhoan);

            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            return "redirect:/tai-khoan/ho-so";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/tai-khoan/doi-mat-khau";
        }
    }

    @PostMapping("/xoa-tai-khoan")
    public String xoaTaiKhoan(Principal principal,
                              HttpServletRequest request,
                              HttpServletResponse response,
                              RedirectAttributes redirectAttributes) {
        try {
            String tenDangNhap = principal.getName();
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(tenDangNhap);

            if (taiKhoanOpt.isPresent()) {
                KhachHang khachHang = khachHangService.findByTenDangNhap(tenDangNhap);

                if (khachHang != null) {
                    khachHang.setHoTen("Tài khoản đã xóa");
                    khachHang.setSoDienThoai("0000000000");
                    khachHang.setDiaChi("");
                    khachHang.setHinhAnh("avatar-default.png");
                    khachHangService.save(khachHang);
                }

                TaiKhoan taiKhoan = taiKhoanOpt.get();
                taiKhoan.setTrangThai("Không hoạt động");
                taiKhoanRepository.save(taiKhoan);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null) {
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                }

                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }

                redirectAttributes.addFlashAttribute("success", "Tài khoản của bạn đã được xóa thành công!");
                return "redirect:/";
            }

            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản cần xóa!");
            return "redirect:/tai-khoan/ho-so";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xử lý xóa tài khoản: " + e.getMessage());
            return "redirect:/tai-khoan/ho-so";
        }
    }
}