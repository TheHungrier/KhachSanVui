package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/newsletter")
public class NewsletterController {

    private final MailService mailService;

    public NewsletterController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeNewsletter(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email không hợp lệ!"));
        }

        String HolidayPromoCode = "KSVWELCOME10";

        new Thread(() -> mailService.sendWelcomeEmail(email, HolidayPromoCode)).start();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "🎉 Đăng ký thành công! Vui lòng kiểm tra hộp thư đến để nhận mã giảm giá."
        ));
    }
}