package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.YeuThich;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuThichRepository extends JpaRepository<YeuThich, Integer> {

    @Query("SELECT yt FROM YeuThich yt " +
            "JOIN FETCH yt.phong p " +
            "JOIN FETCH p.chiNhanh cn " +
            "WHERE yt.khachHang.maKH = :maKH")
    List<YeuThich> findByKhachHang_MaKH(@Param("maKH") Integer maKH);

    Optional<YeuThich> findByKhachHang_MaKHAndPhong_MaPhong(Integer maKH, Integer maPhong);

    boolean existsByKhachHang_MaKHAndPhong_MaPhong(Integer maKH, Integer maPhong);
}