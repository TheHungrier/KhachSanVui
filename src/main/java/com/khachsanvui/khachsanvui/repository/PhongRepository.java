package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer>, JpaSpecificationExecutor<Phong> {

    List<Phong> findByTrangThai(String trangThai);

    List<Phong> findTop6ByTrangThaiOrderByMaPhongDesc(String trangThai);

    List<Phong> findTop6ByOrderByMaPhongDesc();

    @Query("SELECT p FROM Phong p WHERE " +
            "LOWER(p.soPhong) LIKE LOWER(CONCAT('%', :kw, '%')) " +
            "OR LOWER(p.loaiPhong.tenLoaiPhong) LIKE LOWER(CONCAT('%', :kw, '%')) " + // SỬA Ở ĐÂY: đổi p.tenLoaiPhong thành p.loaiPhong.tenLoaiPhong
            "OR LOWER(p.chiNhanh.tenChiNhanh) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Phong> findPhong(@Param("kw") String kw);
}
