package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Integer>, JpaSpecificationExecutor<NhanVien> {
    List<NhanVien> findByTrangThai(String trangThai);
}