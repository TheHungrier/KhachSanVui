package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Integer>, JpaSpecificationExecutor<DichVu> {
    List<DichVu> findTop6ByTrangThaiOrderByMaDichVuDesc(String trangThai);

    List<DichVu> findTop6ByOrderByMaDichVuDesc();

    @Query("SELECT d FROM DichVu d WHERE d.tenDichVu LIKE %:keyword% AND d.trangThai = 'Đang cung cấp'")
    List<DichVu> searchDichVu(@Param("keyword") String keyword);
}