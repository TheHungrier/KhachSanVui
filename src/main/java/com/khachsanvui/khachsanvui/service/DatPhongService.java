package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.DatPhongDTO;
import com.khachsanvui.khachsanvui.model.*; // Chú ý: Cần import các Entity tương ứng của bạn
import com.khachsanvui.khachsanvui.repository.*; // Chú ý: Cần import các Repository tương ứng của bạn
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DatPhongService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PhieuDatPhongRepository phieuDatPhongRepository;

    @Autowired
    private ChiTietDatPhongRepository chiTietDatPhongRepository;

    @Autowired
    private PhongRepository phongRepository;

    @Transactional
    public void taoPhieuDatPhong(DatPhongDTO dto) {
        // 1. Tìm hoặc tạo mới Khách Hàng dựa trên Số Điện Thoại
        KhachHang khachHang = khachHangRepository.findBySoDienThoai(dto.getSoDienThoai()).orElse(null);
        
        if (khachHang == null) {
            khachHang = new KhachHang();
            khachHang.setHoTen(dto.getHoTen());
            khachHang.setSoDienThoai(dto.getSoDienThoai());
            khachHang.setCccd(dto.getCccd());
            khachHang.setEmail(dto.getEmail());
            khachHang = khachHangRepository.save(khachHang);
        }

        // 2. Tạo Phiếu Đặt Phòng
        PhieuDatPhong phieu = new PhieuDatPhong();
        phieu.setKhachHang(khachHang);
        phieu.setNgayDat(LocalDateTime.now());
        phieu.setNgayNhan(dto.getNgayNhan());
        phieu.setNgayTra(dto.getNgayTra());
        phieu.setSoLuongPhong(dto.getSoLuongPhong());
        phieu.setSoLuongKhach(dto.getSoLuongKhach());
        phieu.setTrangThai("Đã đặt");
        
        phieu = phieuDatPhongRepository.save(phieu);

        // 3. Tạo Chi Tiết Đặt Phòng (Liên kết với bảng Phong)
        Phong phong = phongRepository.findById(dto.getMaPhong())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng!"));
            
        ChiTietDatPhong chiTiet = new ChiTietDatPhong();
        chiTiet.setPhieuDatPhong(phieu);
        chiTiet.setPhong(phong);
        chiTiet.setNgayNhan(dto.getNgayNhan());
        chiTiet.setNgayTra(dto.getNgayTra());
        chiTiet.setDonGia(phong.getGiaPhong()); // Lấy giá phòng hiện tại lưu vào chi tiết
        
        chiTietDatPhongRepository.save(chiTiet);
    }
}