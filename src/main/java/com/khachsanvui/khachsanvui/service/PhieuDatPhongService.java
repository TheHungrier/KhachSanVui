package com.khachsanvui.khachsanvui.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import com.khachsanvui.khachsanvui.repository.PhieuDatPhongRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhieuDatPhongService {

    private final PhieuDatPhongRepository phieuDatPhongRepository;

    public List<PhieuDatPhong> getAll() {
        return phieuDatPhongRepository.findAllByOrderByNgayDatDesc();
    }

    public List<PhieuDatPhong> getLichSuByKhachHang(Integer maKH) {
        return phieuDatPhongRepository.findByKhachHang_MaKHOrderByNgayDatDesc(maKH);
    }

    public boolean huyDon(Integer maDatPhong) {
        return phieuDatPhongRepository.findById(maDatPhong).map(phieu -> {
            if ("Chờ xác nhận".equals(phieu.getTrangThai())) {
                phieu.setTrangThai("Đã hủy");
                phieuDatPhongRepository.save(phieu);
                return true;
            }
            return false;
        }).orElse(false);
    }

    public PhieuDatPhong getById(Integer id) {
        return phieuDatPhongRepository.findById(id).orElse(null);
    }
}