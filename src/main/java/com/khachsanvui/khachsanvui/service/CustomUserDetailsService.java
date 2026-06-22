package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    public CustomUserDetailsService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + tenDangNhap));

        if (!"Hoạt động".equalsIgnoreCase(tk.getTrangThai())) {
            throw new UsernameNotFoundException("Tài khoản đã bị khóa");
        }

        String role = "ROLE_" + tk.getVaiTro().toUpperCase();

        return User.builder()
                .username(tk.getTenDangNhap())
                .password(tk.getMatKhau())
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }
}
