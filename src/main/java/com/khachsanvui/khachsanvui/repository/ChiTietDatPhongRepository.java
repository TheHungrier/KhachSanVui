package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
}