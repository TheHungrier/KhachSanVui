package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.HoaDon;
import org.springframework.data.jpa.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, JpaSpecificationExecutor<HoaDon> {
    Optional<HoaDon> findByPhieuDatPhong_MaDatPhong(Integer maDatPhong);

    List<HoaDon> findAllByOrderByNgayLapDesc();

    List<HoaDon> findByTrangThaiThanhToan(String trangThaiThanhToan);

    @Query("SELECT COALESCE(SUM(h.tongTien), 0) FROM HoaDon h WHERE h.trangThaiThanhToan = 'Đã thanh toán'")
    BigDecimal tongDoanhThuDaThanhToan();
}
