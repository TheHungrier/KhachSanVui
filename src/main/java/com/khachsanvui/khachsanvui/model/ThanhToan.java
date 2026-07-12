package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maThanhToan")
    private Integer maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maHD", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "soTien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @Column(name = "phuongThuc", nullable = false, length = 50)
    private String phuongThuc;

    @Column(name = "ngayThanhToan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Chờ thanh toán";

    @Column(name = "maGiaoDich", length = 100)
    private String maGiaoDich;

    @Column(name = "ghiChu", length = 300)
    private String ghiChu;

    @Column(name = "vnpTxnRef", length = 100)
    private String vnpTxnRef;

    @Column(name = "vnpTransactionNo", length = 100)
    private String vnpTransactionNo;

    @Column(name = "vnpResponseCode", length = 20)
    private String vnpResponseCode;

    @Column(name = "vnpBankCode", length = 50)
    private String vnpBankCode;

    @Column(name = "vnpPayDate", length = 50)
    private String vnpPayDate;

    @PrePersist
    public void prePersist() {
        if (ngayThanhToan == null) {
            ngayThanhToan = LocalDateTime.now();
        }

        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Chờ thanh toán";
        }
    }
}