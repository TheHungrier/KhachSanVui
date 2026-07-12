package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {

    @Query("SELECT d FROM DanhGia d WHERE d.phong.maPhong = :maPhong AND d.trangThai = :trangThai ORDER BY d.ngayDanhGia DESC")
    List<DanhGia> findByMaPhong(@Param("maPhong") Integer maPhong, @Param("trangThai") String trangThai);

    List<DanhGia> findTop6ByTrangThaiOrderByNgayDanhGiaDesc(String trangThai);

    @Query("SELECT COALESCE(AVG(d.soSao), 0) FROM DanhGia d WHERE d.phong.maPhong = :maPhong AND d.trangThai = :trangThai")
    Double getAverageRating(@Param("maPhong") Integer maPhong, @Param("trangThai") String trangThai);

    @Query("SELECT COUNT(d) FROM DanhGia d WHERE d.phong.maPhong = :maPhong AND d.trangThai = :trangThai")
    Long countReviews(@Param("maPhong") Integer maPhong, @Param("trangThai") String trangThai);
}