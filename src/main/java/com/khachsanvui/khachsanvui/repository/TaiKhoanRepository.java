package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {
    @EntityGraph(attributePaths = {"khachHang", "nhanVien"})
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    boolean existsByTenDangNhap(String tenDangNhap);

    Optional<TaiKhoan> findByKhachHang_MaKH(Integer maKH);

    Optional<TaiKhoan> findByProviderAndProviderId(String provider, String providerId);
}