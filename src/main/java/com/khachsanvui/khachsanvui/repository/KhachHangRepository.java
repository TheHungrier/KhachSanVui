package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    // Hàm tìm kiếm khách hàng theo SĐT (Spring Data JPA sẽ tự động tạo câu truy vấn)
    Optional<KhachHang> findBySoDienThoai(String soDienThoai);
}