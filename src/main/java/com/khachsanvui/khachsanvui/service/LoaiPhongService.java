package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.LoaiPhong;
import com.khachsanvui.khachsanvui.repository.LoaiPhongRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoaiPhongService {

    private final LoaiPhongRepository loaiPhongRepository;

    public LoaiPhongService(LoaiPhongRepository loaiPhongRepository) {
        this.loaiPhongRepository = loaiPhongRepository;
    }

    public List<LoaiPhong> getAllLoaiPhong() {
        return loaiPhongRepository.findAllByOrderByMaLoaiPhongDesc();
    }

    public LoaiPhong getById(Integer id) {
        return loaiPhongRepository.findById(id).orElse(null);
    }

    public void save(LoaiPhong loaiPhong) {
        loaiPhongRepository.save(loaiPhong);
    }

    public void delete(Integer id) {
        loaiPhongRepository.deleteById(id);
    }
}