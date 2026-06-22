package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PhieuDatPhongRepository extends JpaRepository<PhieuDatPhong, Integer>, JpaSpecificationExecutor<PhieuDatPhong> {
    List<PhieuDatPhong> findByKhachHang_MaKHOrderByNgayDatDesc(Integer maKH);

    @org.springframework.data.jpa.repository.Query("select distinct p from PhieuDatPhong p left join fetch p.chiTietDatPhongs ct left join fetch ct.phong where p.khachHang.maKH = :maKH order by p.ngayDat desc")
    List<PhieuDatPhong> findWithChiTietAndPhongByKhachHang_MaKHOrderByNgayDatDesc(@org.springframework.data.repository.query.Param("maKH") Integer maKH);

    List<PhieuDatPhong> findAllByOrderByNgayDatDesc();
}