package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.HoaDon;
import com.khachsanvui.khachsanvui.model.NhanVien;
import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import com.khachsanvui.khachsanvui.repository.HoaDonRepository;
import com.khachsanvui.khachsanvui.repository.NhanVienRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HoaDonService {
    private final HoaDonRepository hoaDonRepository;
    private final NhanVienRepository nhanVienRepository;

    public HoaDonService(HoaDonRepository hoaDonRepository, NhanVienRepository nhanVienRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<HoaDon> getAll() {
        return hoaDonRepository.findAllByOrderByNgayLapDesc();
    }

    public HoaDon getById(Integer id) {
        return hoaDonRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
    }

    public HoaDon getByDatPhong(Integer maDatPhong) {
        return hoaDonRepository.findByPhieuDatPhong_MaDatPhong(maDatPhong).orElse(null);
    }

    @Transactional
    public HoaDon taoHoaDonDatPhong(PhieuDatPhong phieu, BigDecimal tongTienPhong, BigDecimal soTienGiam) {
        String.valueOf(phieu);
        HoaDon existed = getByDatPhong(phieu.getMaDatPhong());
        if (existed != null) return existed;

        HoaDon hd = new HoaDon();
        hd.setPhieuDatPhong(phieu);
        hd.setTongTienPhong(tongTienPhong);
        hd.setTongTienDV(BigDecimal.ZERO);
        hd.setPhuPhi(BigDecimal.ZERO);
        hd.setGiamGia(soTienGiam != null ? soTienGiam : BigDecimal.ZERO);
        BigDecimal tongTienThucTe = tongTienPhong.subtract(hd.getGiamGia());
        hd.setTongTien(tongTienThucTe);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTrangThaiThanhToan("Chưa thanh toán");
        return hoaDonRepository.save(hd);
    }

    @Transactional
    public void xuLyThanhToan(Integer maHD, String hinhThuc, BigDecimal phuPhi, String ghiChu, Integer maNV) {
        HoaDon hd = getById(maHD);
        if ("Đã thanh toán".equalsIgnoreCase(hd.getTrangThaiThanhToan())) {
            throw new IllegalArgumentException("Hóa đơn này đã được thanh toán trước đó.");
        }

        if (phuPhi != null) {
            hd.setPhuPhi(phuPhi);
        }

        BigDecimal tienPhong = hd.getTongTienPhong() != null ? hd.getTongTienPhong() : BigDecimal.ZERO;
        BigDecimal tienDV = hd.getTongTienDV() != null ? hd.getTongTienDV() : BigDecimal.ZERO;
        BigDecimal giamGia = hd.getGiamGia() != null ? hd.getGiamGia() : BigDecimal.ZERO;
        BigDecimal phuPhiGoc = hd.getPhuPhi() != null ? hd.getPhuPhi() : BigDecimal.ZERO;

        BigDecimal tongCuoiCung = tienPhong.add(tienDV).add(phuPhiGoc).subtract(giamGia);
        hd.setTongTien(tongCuoiCung.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : tongCuoiCung);

        hd.setHinhThucThanhToan(hinhThuc);
        hd.setGhiChu(ghiChu);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTrangThaiThanhToan("Đã thanh toán");

        if (maNV != null) {
            NhanVien nv = nhanVienRepository.findById(maNV).orElse(null);
            hd.setNhanVien(nv);
        }
        hoaDonRepository.save(hd);
    }
}