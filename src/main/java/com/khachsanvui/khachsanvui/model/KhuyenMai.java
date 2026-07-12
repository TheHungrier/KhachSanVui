package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhuyenMai")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maKhuyenMai")
    private Integer maKhuyenMai;

    @Column(name = "tenKhuyenMai", nullable = false, length = 200)
    private String tenKhuyenMai;

    @Column(name = "moTa", length = 500)
    private String moTa;

    @Column(name = "loaiGiamGia", nullable = false, length = 20)
    private String loaiGiamGia;

    @Column(name = "giaTriGiam", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "apDungChoPhong")
    private Boolean apDungChoPhong;

    @Column(name = "apDungChoDichVu")
    private Boolean apDungChoDichVu;

    @Column(name = "thoiGianBatDau", nullable = false)
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoiGianKetThuc", nullable = false)
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "soLuongPhongGioiHan")
    private Integer soLuongPhongGioiHan;

    @Column(name = "soLuongDaDat")
    private Integer soLuongDaDat;

    @Column(name = "trangThai", length = 50)
    private String trangThai;

    @Column(name = "hinhAnh")
    private String hinhAnh;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "maCode")
    private String maCode;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (trangThai == null) trangThai = "Đang diễn ra";
        if (soLuongDaDat == null) soLuongDaDat = 0;
        if (apDungChoPhong == null) apDungChoPhong = true;
        if (apDungChoDichVu == null) apDungChoDichVu = false;
    }

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return "Đang diễn ra".equals(trangThai) &&
                now.isAfter(thoiGianBatDau) &&
                now.isBefore(thoiGianKetThuc) &&
                (soLuongPhongGioiHan == null || soLuongDaDat < soLuongPhongGioiHan);
    }

    public BigDecimal tinhSoTienGiam(BigDecimal giaGoc) {
        if (giaGoc == null) return BigDecimal.ZERO;

        if ("PHAN_TRAM".equalsIgnoreCase(loaiGiamGia)) {
            return giaGoc.multiply(giaTriGiam).divide(BigDecimal.valueOf(100));
        } else {
            return giaTriGiam.min(giaGoc);
        }
    }
}