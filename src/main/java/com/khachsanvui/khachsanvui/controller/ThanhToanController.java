package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.model.PhieuSuDungDV;
import com.khachsanvui.khachsanvui.repository.PhieuSuDungDVRepository;
import com.khachsanvui.khachsanvui.service.HoaDonService;
import com.khachsanvui.khachsanvui.service.ThanhToanService;
import com.khachsanvui.khachsanvui.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/thanh-toan")
public class ThanhToanController {
    private final HoaDonService hoaDonService;
    private final ThanhToanService thanhToanService;
    private final VNPayService vnPayService;
    private final PhieuSuDungDVRepository phieuSuDungDVRepository;

    public ThanhToanController(HoaDonService hoaDonService,
                               ThanhToanService thanhToanService,
                               VNPayService vnPayService,
                               PhieuSuDungDVRepository phieuSuDungDVRepository) {
        this.hoaDonService = hoaDonService;
        this.thanhToanService = thanhToanService;
        this.vnPayService = vnPayService;
        this.phieuSuDungDVRepository = phieuSuDungDVRepository;
    }

    @GetMapping("/{maHD}")
    public String formThanhToan(@PathVariable Integer maHD, Model model, HttpServletRequest request) {
        HoaDon hoaDon = hoaDonService.getById(maHD);
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("thanhToans", thanhToanService.getByHoaDon(maHD));

        if (!"Đã thanh toán".equals(hoaDon.getTrangThaiThanhToan())) {
            String paymentUrl = vnPayService.taoUrlThanhToan(maHD, hoaDon.getTongTien(), request);
            String txnRef = vnPayService.layTxnRefTuUrl(paymentUrl);

            thanhToanService.taoThanhToanVNPayChoHoaDon(maHD, txnRef);
            model.addAttribute("paymentUrl", paymentUrl);
            model.addAttribute("vnpTxnRef", txnRef);
        }

        return "thanhtoan/thanh-toan";
    }

    @PostMapping("/{maHD}/tien-mat")
    public String thanhToanTienMat(@PathVariable Integer maHD, RedirectAttributes redirect) {
        try {
            thanhToanService.thanhToanTienMat(maHD, "Thanh toán tiền mặt tại khách sạn");
            redirect.addFlashAttribute("success", "Thanh toán thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/thanh-toan/" + maHD;
    }

    @PostMapping("/{maHD}/vnpay")
    public String thanhToanVNPay(@PathVariable Integer maHD, HttpServletRequest request) {
        HoaDon hoaDon = hoaDonService.getById(maHD);
        String paymentUrl = vnPayService.taoUrlThanhToan(maHD, hoaDon.getTongTien(), request);
        String txnRef = vnPayService.layTxnRefTuUrl(paymentUrl);

        thanhToanService.taoThanhToanVNPayChoHoaDon(maHD, txnRef);
        return "redirect:" + paymentUrl;
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(HttpServletRequest request, RedirectAttributes redirect) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) params.put(key, values[0]);
        });

        try {
            if (!vnPayService.kiemTraChuKy(params)) {
                redirect.addFlashAttribute("error", "Chữ ký VNPay không hợp lệ.");
                return "redirect:/home";
            }

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");

            if (vnp_TxnRef != null) {
                if (vnp_TxnRef.startsWith("KSVDV_") || (vnp_TxnRef.startsWith("KSV") && !vnp_TxnRef.startsWith("KSVHD_"))) {

                    if ("00".equals(vnp_ResponseCode)) {
                        if (vnp_TxnRef.startsWith("KSVDV_")) {
                            String[] parts = vnp_TxnRef.split("_");
                            String thoiGianNhom = parts[1];

                            List<PhieuSuDungDV> tatCaPhieu = phieuSuDungDVRepository.findAll();
                            for (PhieuSuDungDV p : tatCaPhieu) {
                                if (p.getTrangThai() != null && p.getTrangThai().equals("Chờ thanh toán_" + thoiGianNhom)) {
                                    p.setTrangThai("Đã duyệt");
                                    phieuSuDungDVRepository.save(p);
                                }
                            }
                            redirect.addFlashAttribute("success", "Thanh toán các dịch vụ thành công!");
                            return "redirect:/dich-vu";
                        } else {
                            String[] parts = vnp_TxnRef.split("_");
                            String maSuDungStr = parts[0].substring(3);
                            Integer maSuDung = Integer.parseInt(maSuDungStr);

                            PhieuSuDungDV phieu = phieuSuDungDVRepository.findById(maSuDung).orElse(null);
                            if (phieu != null) {
                                phieu.setTrangThai("Đã duyệt");
                                phieuSuDungDVRepository.save(phieu);
                                redirect.addFlashAttribute("success", "Thanh toán dịch vụ thành công!");
                                return "redirect:/dich-vu";
                            }
                        }
                    }

                    if (vnp_TxnRef.startsWith("KSVDV_")) {
                        String[] parts = vnp_TxnRef.split("_");
                        String thoiGianNhom = parts[1];
                        List<PhieuSuDungDV> tatCaPhieu = phieuSuDungDVRepository.findAll();
                        for (PhieuSuDungDV p : tatCaPhieu) {
                            if (p.getTrangThai() != null && p.getTrangThai().equals("Chờ thanh toán_" + thoiGianNhom)) {
                                phieuSuDungDVRepository.delete(p);
                            }
                        }
                    }

                    redirect.addFlashAttribute("error", "Thanh toán dịch vụ thất bại hoặc bị hủy.");
                    return "redirect:/dich-vu";

                } else if (vnp_TxnRef.startsWith("KSVHD_")) {
                    boolean ok = thanhToanService.xuLyKetQuaVNPay(params);
                    redirect.addFlashAttribute(ok ? "success" : "error",
                            ok ? "Thanh toán hóa đơn phòng thành công." : "Thanh toán hóa đơn phòng thất bại.");
                    return "redirect:/home";
                }
            }

            redirect.addFlashAttribute("error", "Mã tham chiếu giao dịch không xác định.");
            return "redirect:/home";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Lỗi xử lý phản hồi: " + e.getMessage());
            return "redirect:/home";
        }
    }
}