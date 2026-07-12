package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.KhachHangRepository;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;

    public CustomOAuth2UserService(TaiKhoanRepository taiKhoanRepository,
                                   KhachHangRepository khachHangRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.khachHangRepository = khachHangRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {

        OAuth2User oAuth2User = super.loadUser(request);
        Map<String, Object> attr = oAuth2User.getAttributes();

        String provider = request.getClientRegistration().getRegistrationId();

        String providerId = String.valueOf(attr.get("id"));
        String email = (String) attr.get("email");
        String name = (String) attr.get("name");

        String avatarUrl = null;

        if ("google".equals(provider)) {
            avatarUrl = (String) attr.get("picture");
        }

        if ("facebook".equals(provider)) {
            Map<String, Object> picture = (Map<String, Object>) attr.get("picture");
            if (picture != null) {
                Map<String, Object> data = (Map<String, Object>) picture.get("data");
                if (data != null) {
                    avatarUrl = (String) data.get("url");
                }
            }
        }

        final String finalProvider = provider;
        final String finalProviderId = providerId;
        final String finalEmail = email;
        final String finalName = name;
        final String finalAvatarUrl = avatarUrl;

        TaiKhoan tk = taiKhoanRepository.findByProviderAndProviderId(finalProvider, finalProviderId)
                .orElseGet(() -> {

                    KhachHang kh = new KhachHang();
                    kh.setHoTen(finalName);
                    kh.setEmail(finalEmail);
                    kh.setSoDienThoai("0000000000");
                    kh.setHinhAnh(finalAvatarUrl);
                    khachHangRepository.save(kh);

                    TaiKhoan newTk = new TaiKhoan();
                    newTk.setTenDangNhap(finalEmail);
                    newTk.setVaiTro("KHACHHANG");
                    newTk.setTrangThai("Hoạt động");
                    newTk.setProvider(finalProvider);
                    newTk.setProviderId(finalProviderId);
                    newTk.setKhachHang(kh);

                    return taiKhoanRepository.save(newTk);
                });

        if (tk.getKhachHang() != null && avatarUrl != null) {
            KhachHang kh = tk.getKhachHang();
            kh.setHinhAnh(avatarUrl);
            khachHangRepository.save(kh);
        }

        return oAuth2User;
    }
}