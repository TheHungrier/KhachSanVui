package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer>, JpaSpecificationExecutor<KhachHang> {
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);

    @Query("SELECT tk.khachHang FROM TaiKhoan tk WHERE tk.tenDangNhap = :tenDangNhap")
    Optional<KhachHang> findByTenDangNhap(@Param("tenDangNhap") String tenDangNhap);

    @Query("SELECT kh FROM KhachHang kh LEFT JOIN TaiKhoan tk ON tk.khachHang.maKH = kh.maKH " +
            "WHERE tk IS NULL OR tk.trangThai != :trangThai")
    List<KhachHang> findAllActiveCustomers(@Param("trangThai") String trangThai);

    boolean existsBySoDienThoai(String soDienThoai);

    boolean existsByEmail(String email);

    boolean existsByCccd(String cccd);
}