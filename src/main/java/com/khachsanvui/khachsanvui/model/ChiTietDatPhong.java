package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "ChiTietDatPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maChiTiet")
    private Integer maChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong", nullable = false)
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @Column(name = "ngayNhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "ngayTra", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "soDem", nullable = false)
    private Integer soDem = 1;

    @Column(name = "thanhTien", nullable = false, precision = 12, scale = 2)
    private BigDecimal thanhTien = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void tinhTien() {
        if (ngayNhan != null && ngayTra != null) {
            long days = ChronoUnit.DAYS.between(ngayNhan, ngayTra);
            soDem = (int) Math.max(days, 1);
        }

        if (donGia != null && soDem != null) {
            thanhTien = donGia.multiply(BigDecimal.valueOf(soDem));
        }

        if (thanhTien == null) {
            thanhTien = BigDecimal.ZERO;
        }
    }
}