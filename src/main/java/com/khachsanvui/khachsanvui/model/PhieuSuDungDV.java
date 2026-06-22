package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuSuDungDV")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuSuDungDV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maSuDung")
    private Integer maSuDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLuuTru", nullable = false)
    private HoSoLuuTru hoSoLuuTru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDichVu", nullable = false)
    private DichVu dichVu;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "thoiGianSuDung")
    private LocalDateTime thoiGianSuDung;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanhTien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien = BigDecimal.ZERO;

    @Column(name = "trangThai", nullable = false, length = 50)
    private String trangThai = "Chờ duyệt";

    @PrePersist
    @PreUpdate
    public void tinhThanhTien() {
        if (thoiGianSuDung == null) {
            thoiGianSuDung = LocalDateTime.now();
        }

        if (soLuong == null || soLuong <= 0) {
            soLuong = 1;
        }

        if (donGia != null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soLuong));
        }

        if (thanhTien == null) {
            thanhTien = BigDecimal.ZERO;
        }
    }
}