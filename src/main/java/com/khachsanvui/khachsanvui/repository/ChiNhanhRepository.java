package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.ChiNhanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiNhanhRepository extends JpaRepository<ChiNhanh, Integer>, JpaSpecificationExecutor<ChiNhanh> {
    List<ChiNhanh> findByTrangThai(String trangThai);
}