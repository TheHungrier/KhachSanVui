package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhieuDatPhongRepository extends JpaRepository<PhieuDatPhong, Integer> {
}