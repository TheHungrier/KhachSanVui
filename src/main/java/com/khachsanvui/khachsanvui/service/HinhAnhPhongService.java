package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.HinhAnhPhong;
import com.khachsanvui.khachsanvui.repository.HinhAnhPhongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HinhAnhPhongService {
    private final HinhAnhPhongRepository hinhAnhPhongRepository;

    public HinhAnhPhongService(HinhAnhPhongRepository hinhAnhPhongRepository) {
        this.hinhAnhPhongRepository = hinhAnhPhongRepository;
    }

    public List<HinhAnhPhong> getAnhTheoPhong(Integer maPhong) {
        return hinhAnhPhongRepository.findByPhong_MaPhongOrderByLaAnhChinhDesc(maPhong);
    }
}