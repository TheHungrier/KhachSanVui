package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.HoSoLuuTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HoSoLuuTruRepository extends JpaRepository<HoSoLuuTru, Integer>, JpaSpecificationExecutor<HoSoLuuTru> {
    List<HoSoLuuTru> findByTrangThai(String trangThai);
}