package com.khachsanvui.khachsanvui.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String toEmail, String promoCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("🎉 Chào mừng bạn đến với KhachSanVui - Nhận ngay mã giảm giá!");

            String htmlContent = "<h3>Cảm ơn bạn đã đăng ký nhận bản tin từ KhachSanVui!</h3>"
                    + "<p>Chúng tôi rất vui được đồng hành cùng bạn trong những chuyến đi sắp tới.</p>"
                    + "<p>Gửi tặng bạn mã giảm giá <b>10%</b> cho lần đặt phòng đầu tiên:</p>"
                    + "<div style='background:#f4f4f4; padding:10px; display:inline-block; font-size:18px; font-weight:bold; color:#ff5a5f; border:1px dashed #ff5a5f;'>"
                    + promoCode + "</div>"
                    + "<p>Hãy áp dụng mã này tại bước Đặt phòng nhé!</p>"
                    + "<br><hr><p style='font-size:12px; color:#888;'>Đây là email tự động, vui lòng không phản hồi email này.</p>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("Đã gửi email thành công tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
        }
    }
}