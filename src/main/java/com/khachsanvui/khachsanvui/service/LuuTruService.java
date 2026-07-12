package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.HoSoLuuTru;
import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.NhanVien;
import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.repository.HoSoLuuTruRepository;
import com.khachsanvui.khachsanvui.repository.KhachHangRepository;
import com.khachsanvui.khachsanvui.repository.NhanVienRepository;
import com.khachsanvui.khachsanvui.repository.PhieuDatPhongRepository;
import com.khachsanvui.khachsanvui.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LuuTruService {

    private final HoSoLuuTruRepository hoSoLuuTruRepository;
    private final KhachHangRepository khachHangRepository;
    private final PhongRepository phongRepository;
    private final PhieuDatPhongRepository phieuDatPhongRepository;
    private final NhanVienRepository nhanVienRepository;

    public LuuTruService(HoSoLuuTruRepository hoSoLuuTruRepository,
                         KhachHangRepository khachHangRepository,
                         PhongRepository phongRepository,
                         PhieuDatPhongRepository phieuDatPhongRepository,
                         NhanVienRepository nhanVienRepository) {
        this.hoSoLuuTruRepository = hoSoLuuTruRepository;
        this.khachHangRepository = khachHangRepository;
        this.phongRepository = phongRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    public List<HoSoLuuTru> getAll() {
        return hoSoLuuTruRepository.findAll();
    }

    public List<HoSoLuuTru> getDangLuuTru() {
        return hoSoLuuTruRepository.findByTrangThai("Đang lưu trú");
    }

    public HoSoLuuTru getById(Integer id) {
        return hoSoLuuTruRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hồ sơ lưu trú"));
    }

    @Transactional
    public void nhanPhong(Integer maDatPhong, Integer maPhong, Integer maKH, Integer maNV) {
        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));

        KhachHang khachHang = khachHangRepository.findById(maKH)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng"));

        PhieuDatPhong phieuDatPhong = null;
        if (maDatPhong != null) {
            phieuDatPhong = phieuDatPhongRepository.findById(maDatPhong)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu đặt phòng"));
        }

        NhanVien nhanVien = null;
        if (maNV != null) {
            nhanVien = nhanVienRepository.findById(maNV).orElse(null);
        }

        HoSoLuuTru hoSo = new HoSoLuuTru();
        hoSo.setKhachHang(khachHang);
        hoSo.setPhong(phong);
        hoSo.setPhieuDatPhong(phieuDatPhong);
        hoSo.setNhanVien(nhanVien);
        hoSo.setGioNhanPhong(LocalDateTime.now());
        hoSo.setTrangThai("Đang lưu trú");

        phong.setTrangThai("Đang sử dụng");
        phongRepository.save(phong);

        if (phieuDatPhong != null) {
            phieuDatPhong.setTrangThai("Đã nhận phòng");
            phieuDatPhongRepository.save(phieuDatPhong);
        }

        hoSoLuuTruRepository.save(hoSo);
    }

    @Transactional
    public void traPhong(Integer maLuuTru) {
        HoSoLuuTru hoSo = getById(maLuuTru);

        if (!"Đang lưu trú".equalsIgnoreCase(hoSo.getTrangThai())) {
            throw new IllegalArgumentException("Hồ sơ này không ở trạng thái đang lưu trú");
        }

        hoSo.setGioTraPhong(LocalDateTime.now());
        hoSo.setTrangThai("Đã trả phòng");

        Phong phong = hoSo.getPhong();
        if (phong != null) {
            phong.setTrangThai("Trống");
            phongRepository.save(phong);
        }

        PhieuDatPhong phieu = hoSo.getPhieuDatPhong();
        if (phieu != null) {
            phieu.setTrangThai("Đã trả phòng");
            phieuDatPhongRepository.save(phieu);
        }

        hoSoLuuTruRepository.save(hoSo);
    }
}