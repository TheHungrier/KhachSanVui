package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.model.YeuThich;
import com.khachsanvui.khachsanvui.repository.PhongRepository;
import com.khachsanvui.khachsanvui.repository.YeuThichRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class YeuThichService {

    private final YeuThichRepository yeuThichRepository;
    private final PhongRepository phongRepository;

    public YeuThichService(YeuThichRepository yeuThichRepository, PhongRepository phongRepository) {
        this.yeuThichRepository = yeuThichRepository;
        this.phongRepository = phongRepository;
    }

    public boolean toggle(KhachHang khachHang, Integer maPhong) {
        Optional<YeuThich> old = yeuThichRepository.findByKhachHang_MaKHAndPhong_MaPhong(
                khachHang.getMaKH(),
                maPhong
        );

        if (old.isPresent()) {
            yeuThichRepository.delete(old.get());
            return false;
        }

        Phong phong = phongRepository.findById(maPhong).orElseThrow();

        YeuThich yt = new YeuThich();
        yt.setKhachHang(khachHang);
        yt.setPhong(phong);

        yeuThichRepository.save(yt);
        return true;
    }
}