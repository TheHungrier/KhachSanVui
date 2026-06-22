package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.model.ThanhToan;
import com.khachsanvui.khachsanvui.repository.HoaDonRepository;
import com.khachsanvui.khachsanvui.repository.ThanhToanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ThanhToanService {
    private final ThanhToanRepository thanhToanRepository;
    private final HoaDonRepository hoaDonRepository;

    public ThanhToanService(ThanhToanRepository thanhToanRepository, HoaDonRepository hoaDonRepository) {
        this.thanhToanRepository = thanhToanRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    public List<ThanhToan> getByHoaDon(Integer maHD) {
        return thanhToanRepository.findByHoaDon_MaHD(maHD);
    }

    @Transactional
    public void thanhToanTienMat(Integer maHD, String ghiChu) {
        MakePaymentAndInvoice(maHD, ghiChu);
    }

    private void MakePaymentAndInvoice(Integer maHD, String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(maHD)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));

        ThanhToan tt = new ThanhToan();
        tt.setHoaDon(hoaDon);
        tt.setSoTien(hoaDon.getTongTien());
        tt.setPhuongThuc("Tiền mặt");
        tt.setNgayThanhToan(LocalDateTime.now());
        tt.setTrangThai("Thành công");
        tt.setGhiChu(ghiChu);
        tt.setMaGiaoDich("TM" + System.currentTimeMillis());

        hoaDon.setTrangThaiThanhToan("Đã thanh toán");
        hoaDon.setHinhThucThanhToan("Tiền mặt");

        if (hoaDon.getPhieuDatPhong() != null) {
            hoaDon.getPhieuDatPhong().setTrangThai("Đã xác nhận");
        }

        hoaDonRepository.save(hoaDon);
        thanhToanRepository.save(tt);
    }

    @Transactional
    public void taoThanhToanVNPayChoHoaDon(Integer maHD, String vnpTxnRef) {
        HoaDon hoaDon = hoaDonRepository.findById(maHD)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
        ThanhToan tt = new ThanhToan();
        tt.setHoaDon(hoaDon);
        tt.setSoTien(hoaDon.getTongTien());
        tt.setPhuongThuc("VNPay");
        tt.setTrangThai("Chờ thanh toán");
        tt.setVnpTxnRef(vnpTxnRef);
        tt.setMaGiaoDich(vnpTxnRef);
        thanhToanRepository.save(tt);
    }

    @Transactional
    public boolean xuLyKetQuaVNPay(Map<String, String> params) {
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        ThanhToan tt = thanhToanRepository.findByVnpTxnRef(txnRef)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch VNPay"));

        tt.setVnpTransactionNo(params.get("vnp_TransactionNo"));
        tt.setVnpResponseCode(responseCode);
        tt.setVnpBankCode(params.get("vnp_BankCode"));
        tt.setVnpPayDate(params.get("vnp_PayDate"));
        tt.setNgayThanhToan(LocalDateTime.now());

        HoaDon hoaDon = tt.getHoaDon();
        if ("00".equals(responseCode)) {
            tt.setTrangThai("Thành công");
            if (!"Đã thanh toán".equals(hoaDon.getTrangThaiThanhToan())) {
                hoaDon.setTrangThaiThanhToan("Đã thanh toán");
                hoaDon.setHinhThucThanhToan("VNPay");
            }
            if (hoaDon.getPhieuDatPhong() != null) {
                hoaDon.getPhieuDatPhong().setTrangThai("Đã xác nhận");
            }
        }

        hoaDonRepository.save(hoaDon);
        thanhToanRepository.save(tt);
        return "00".equals(responseCode);
    }
}