package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.DangKyDTO;
import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.KhachHangRepository;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(TaiKhoanRepository taiKhoanRepository,
                       KhachHangRepository khachHangRepository,
                       PasswordEncoder passwordEncoder) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void dangKyKhachHang(DangKyDTO dto) {
        if (taiKhoanRepository.existsByTenDangNhap(dto.getTenDangNhap())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }
        if (khachHangRepository.existsBySoDienThoai(dto.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && khachHangRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        if (dto.getCccd() != null && !dto.getCccd().isBlank()
                && khachHangRepository.existsByCccd(dto.getCccd())) {
            throw new IllegalArgumentException("CCCD đã tồn tại");
        }
        if (!dto.getMatKhau().equals(dto.getXacNhanMatKhau())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }

        var kh = new KhachHang();
        kh.setHoTen(dto.getHoTen());
        kh.setGioiTinh(dto.getGioiTinh());
        kh.setSoDienThoai(dto.getSoDienThoai());
        kh.setEmail(dto.getEmail());
        kh.setCccd(dto.getCccd());
        kh.setDiaChi(dto.getDiaChi());
        kh = khachHangRepository.save(kh);

        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(dto.getTenDangNhap());
        tk.setMatKhau(passwordEncoder.encode(dto.getMatKhau()));
        tk.setVaiTro("KHACHHANG");
        tk.setTrangThai("Hoạt động");
        tk.setKhachHang(kh);
        taiKhoanRepository.save(tk);
    }

    public TaiKhoan getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return taiKhoanRepository.findByTenDangNhap(auth.getName()).orElse(null);
    }

    public boolean isAdminOrLeTan(TaiKhoan tk) {
        return tk != null && (tk.isAdmin() || tk.isLeTan());
    }
}
