package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.ChiNhanh;
import com.khachsanvui.khachsanvui.repository.ChiNhanhRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChiNhanhService {
    private final ChiNhanhRepository chiNhanhRepository;

    public ChiNhanhService(ChiNhanhRepository chiNhanhRepository) {
        this.chiNhanhRepository = chiNhanhRepository;
    }

    public List<ChiNhanh> getChiNhanhHienThi() {
        return chiNhanhRepository.findByTrangThai("Hiển thị");
    }

    public List<ChiNhanh> findAll() {
        return chiNhanhRepository.findAll();
    }
}