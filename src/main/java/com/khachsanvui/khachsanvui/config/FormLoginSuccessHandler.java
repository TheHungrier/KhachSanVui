package com.khachsanvui.khachsanvui.config;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FormLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TaiKhoanRepository taiKhoanRepository;

    public FormLoginSuccessHandler(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String username = authentication.getName();

        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username).orElse(null);

        // create lightweight detached copy for session
        TaiKhoan sessionUser = null;
        if (tk != null) {
            sessionUser = new TaiKhoan();
            sessionUser.setTenDangNhap(tk.getTenDangNhap());
            sessionUser.setVaiTro(tk.getVaiTro());
            if (tk.getKhachHang() != null) {
                com.khachsanvui.khachsanvui.model.KhachHang kh = new com.khachsanvui.khachsanvui.model.KhachHang();
                kh.setHoTen(tk.getKhachHang().getHoTen());
                kh.setHinhAnh(tk.getKhachHang().getHinhAnh());
                sessionUser.setKhachHang(kh);
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("USER_LOGIN", sessionUser);

        response.sendRedirect("/home");
    }
}
