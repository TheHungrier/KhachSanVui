package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.ThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ThanhToanRepository extends JpaRepository<ThanhToan, Integer> {
    List<ThanhToan> findByHoaDon_MaHD(Integer maHD);

    Optional<ThanhToan> findByVnpTxnRef(String vnpTxnRef);
}
