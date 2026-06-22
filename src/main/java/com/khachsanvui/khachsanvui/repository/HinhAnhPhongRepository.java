package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.HinhAnhPhong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HinhAnhPhongRepository extends JpaRepository<HinhAnhPhong, Integer> {
    List<HinhAnhPhong> findByPhong_MaPhongOrderByLaAnhChinhDesc(Integer maPhong);
}