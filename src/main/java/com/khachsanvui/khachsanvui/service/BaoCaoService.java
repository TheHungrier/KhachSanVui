package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BaoCaoService {
    private final PhongRepository phongRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhieuDatPhongRepository phieuDatPhongRepository;
    private final HoaDonRepository hoaDonRepository;

    public BaoCaoService(PhongRepository phongRepository, KhachHangRepository khachHangRepository,
                         PhieuDatPhongRepository phieuDatPhongRepository, HoaDonRepository hoaDonRepository) {
        this.phongRepository = phongRepository;
        this.khachHangRepository = khachHangRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();

        long tongPhong = phongRepository.count();
        long tongKhachHang = khachHangRepository.count();
        long tongDatPhong = phieuDatPhongRepository.count();

        long phongTrong = phongRepository.findByTrangThai("Trống").size();
        long phongDangO = phongRepository.findByTrangThai("Đang sử dụng").size();
        long phongDaDat = phongRepository.findByTrangThai("Đã đặt").size();
        long phongBaoTri = phongRepository.findByTrangThai("Bảo trì").size();

        BigDecimal doanhThu = hoaDonRepository.tongDoanhThuDaThanhToan();

        data.put("tongPhong", tongPhong);
        data.put("tongKhachHang", tongKhachHang);
        data.put("tongDatPhong", tongDatPhong);
        data.put("tongLuuTru", phongDangO);

        data.put("phongTrong", phongTrong);
        data.put("phongDangO", phongDangO);
        data.put("phongDaDat", phongDaDat);
        data.put("phongBaoTri", phongBaoTri);
        data.put("doanhThu", doanhThu != null ? doanhThu : BigDecimal.ZERO);

        List<String> revenueLabels = new ArrayList<>();
        List<Double> revenueValues = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate targetDate = now.minusMonths(i);
            String label = "T" + targetDate.getMonthValue();
            revenueLabels.add(label);

            BigDecimal monthRevenue = BigDecimal.ZERO;
            List<HoaDon> hoaDons = hoaDonRepository.findByTrangThaiThanhToan("Đã thanh toán");
            if (hoaDons != null) {
                for (HoaDon hd : hoaDons) {
                    if (hd.getNgayLap() != null &&
                            hd.getNgayLap().getYear() == targetDate.getYear() &&
                            hd.getNgayLap().getMonthValue() == targetDate.getMonthValue()) {
                        monthRevenue = monthRevenue.add(hd.getTongTien() != null ? hd.getTongTien() : BigDecimal.ZERO);
                    }
                }
            }
            double revenueInMillions = monthRevenue.divide(BigDecimal.valueOf(1000000), 2, java.math.RoundingMode.HALF_UP).doubleValue();
            revenueValues.add(revenueInMillions);
        }

        data.put("revenueLabels", revenueLabels);
        data.put("revenueValues", revenueValues);

        return data;
    }
}