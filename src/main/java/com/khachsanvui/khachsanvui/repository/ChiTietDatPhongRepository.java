package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.ChiTietDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
    List<ChiTietDatPhong> findByPhieuDatPhong_MaDatPhong(Integer maDatPhong);

    @Query("SELECT COUNT(c) > 0 FROM ChiTietDatPhong c " +
            "WHERE c.phong.maPhong = :maPhong " +
            "AND c.phieuDatPhong.trangThai <> 'Đã hủy' " +
            "AND :ngayNhan < c.ngayTra " +
            "AND :ngayTra > c.ngayNhan")
    boolean isRoomBooked(
            @Param("maPhong") Integer maPhong,
            @Param("ngayNhan") LocalDate ngayNhan,
            @Param("ngayTra") LocalDate ngayTra
    );
}