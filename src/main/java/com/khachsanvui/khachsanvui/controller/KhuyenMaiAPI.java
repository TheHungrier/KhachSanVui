package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.KhuyenMai;
import com.khachsanvui.khachsanvui.service.KhuyenMaiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/khuyen-mai")
public class KhuyenMaiAPI {

    private final KhuyenMaiService khuyenMaiService;

    public KhuyenMaiAPI(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkMa(@RequestParam("code") String code, @RequestParam("tongTien") double tongTien) {
        var kmOpt = khuyenMaiService.findByMaCode(code);
        if (kmOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Mã giảm giá không tồn tại!"));
        }

        KhuyenMai km = kmOpt.get();
        if (!khuyenMaiService.isValid(km)) {
            return ResponseEntity.ok(Map.of("success", false, "message", "Mã giảm giá đã hết hạn hoặc hết lượt dùng!"));
        }

        double soTienGiam = khuyenMaiService.tinhSoTienGiam(km, tongTien);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "soTienGiam", soTienGiam,
                "message", "Áp dụng mã giảm giá thành công!"
        ));
    }
}