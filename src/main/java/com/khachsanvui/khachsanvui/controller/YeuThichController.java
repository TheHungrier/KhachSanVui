package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.model.YeuThich;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import com.khachsanvui.khachsanvui.repository.YeuThichRepository;
import com.khachsanvui.khachsanvui.service.YeuThichService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/yeu-thich")
public class YeuThichController {

    private final YeuThichService yeuThichService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final YeuThichRepository yeuThichRepository;

    public YeuThichController(YeuThichService yeuThichService,
                              TaiKhoanRepository taiKhoanRepository,
                              YeuThichRepository yeuThichRepository) {
        this.yeuThichService = yeuThichService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.yeuThichRepository = yeuThichRepository;
    }

    @GetMapping({"", "/"})
    public String danhSachYeuThich(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            username = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username).orElse(null);
        if (tk == null || tk.getKhachHang() == null) {
            return "redirect:/login";
        }

        KhachHang khachHang = tk.getKhachHang();
        List<YeuThich> listYt = yeuThichRepository.findByKhachHang_MaKH(khachHang.getMaKH());
        List<Phong> dsPhongYeuThich = new ArrayList<>();
        for (YeuThich yt : listYt) {
            if (yt.getPhong() != null) {
                dsPhongYeuThich.add(yt.getPhong());
            }
        }

        model.addAttribute("dsPhongYeuThich", dsPhongYeuThich);
        return "yeuthich/danh-sach";
    }

    @GetMapping("/toggle/{maPhong}")
    public String toggle(@PathVariable Integer maPhong, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }

        String username = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof OAuth2User) {
            username = ((OAuth2User) principal).getAttribute("email");
        } else if (principal instanceof String) {
            username = (String) principal;
        }

        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username).orElse(null);
        if (tk == null || tk.getKhachHang() == null) {
            return "redirect:/login";
        }

        yeuThichService.toggle(tk.getKhachHang(), maPhong);
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }
}