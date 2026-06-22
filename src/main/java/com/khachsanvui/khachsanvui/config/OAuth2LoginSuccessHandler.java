package com.khachsanvui.khachsanvui.config;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TaiKhoanRepository taiKhoanRepository;
    private final com.khachsanvui.khachsanvui.repository.KhachHangRepository khachHangRepository;

    public OAuth2LoginSuccessHandler(TaiKhoanRepository taiKhoanRepository, com.khachsanvui.khachsanvui.repository.KhachHangRepository khachHangRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        OAuth2User user = (OAuth2User) authentication.getPrincipal();

        assert user != null;
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        String picture = null;

        // Try common attribute keys for profile images across providers
        Map<String, Object> attributes = user.getAttributes();

        if (attributes != null) {
            // 1) Google often returns a String under "picture"
            Object picObj = attributes.get("picture");
            if (picObj instanceof String) {
                picture = (String) picObj;
            } else if (picObj instanceof Map) {
                // Facebook may put { data: { url: "..." } } in picture
                Object dataObj = ((Map<?, ?>) picObj).get("data");
                if (dataObj instanceof Map) {
                    Object url = ((Map<?, ?>) dataObj).get("url");
                    if (url instanceof String) picture = (String) url;
                }
            }

            // 2) GitHub uses "avatar_url"
            if (picture == null) {
                Object githubAvatar = attributes.get("avatar_url");
                if (githubAvatar instanceof String) picture = (String) githubAvatar;
            }

            // 3) Some providers use "avatar" or "profile_image"
            if (picture == null) {
                Object avatar = attributes.get("avatar");
                if (avatar instanceof String) picture = (String) avatar;
            }
            if (picture == null) {
                Object profileImage = attributes.get("profile_image");
                if (profileImage instanceof String) picture = (String) profileImage;
            }
        }

        final String avatarUrl = picture;

        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(email)
                .orElseGet(() -> {
                    TaiKhoan newTk = new TaiKhoan();
                    newTk.setTenDangNhap(email);
                    newTk.setVaiTro("KHACHHANG");
                    newTk.setTrangThai("Hoạt động");
                    com.khachsanvui.khachsanvui.model.KhachHang kh = new com.khachsanvui.khachsanvui.model.KhachHang();
                    kh.setHoTen(name);
                    kh.setEmail(email);
                    kh.setSoDienThoai("0000000000");
                    kh.setHinhAnh(avatarUrl);
                    // save KhachHang and link to account
                    // use repository injected into this handler
                    this.khachHangRepository.save(kh);
                    newTk.setKhachHang(kh);
                    return taiKhoanRepository.save(newTk);
                });

        if (tk.getKhachHang() != null && avatarUrl != null) {
            com.khachsanvui.khachsanvui.model.KhachHang kh = tk.getKhachHang();
            kh.setHinhAnh(avatarUrl);
            this.khachHangRepository.save(kh);
        }
        taiKhoanRepository.save(tk);

        com.khachsanvui.khachsanvui.model.KhachHang sessionKh = null;
        if (tk.getKhachHang() != null) {
            sessionKh = new com.khachsanvui.khachsanvui.model.KhachHang();
            sessionKh.setHoTen(tk.getKhachHang().getHoTen());
            sessionKh.setHinhAnh(tk.getKhachHang().getHinhAnh());
        } else if (name != null && !name.isBlank()) {
            sessionKh = new com.khachsanvui.khachsanvui.model.KhachHang();
            sessionKh.setHoTen(name);
            sessionKh.setHinhAnh(avatarUrl);
        }
        TaiKhoan sessionUser = new TaiKhoan();
        sessionUser.setTenDangNhap(tk.getTenDangNhap());
        sessionUser.setVaiTro(tk.getVaiTro());
        sessionUser.setKhachHang(sessionKh);

        HttpSession session = request.getSession();
        session.setAttribute("USER_LOGIN", sessionUser);

        response.sendRedirect("/home");
    }
}