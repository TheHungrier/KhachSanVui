package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer>, JpaSpecificationExecutor<KhuyenMai> {

    @Query(value = "SELECT * FROM KhuyenMai WHERE trangThai = N'Đang diễn ra' " +
            "AND thoiGianBatDau <= GETDATE() AND thoiGianKetThuc >= GETDATE() " +
            "AND (soLuongPhongGioiHan IS NULL OR soLuongDaDat < soLuongPhongGioiHan)",
            nativeQuery = true)
    List<KhuyenMai> findActiveFlashSales();

    Optional<KhuyenMai> findByMaCode(String maCode);

}