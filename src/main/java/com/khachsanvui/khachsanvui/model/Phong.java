package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maPhong")
    private Integer maPhong;

    @Column(name = "soPhong", nullable = false, unique = true, length = 20)
    private String soPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maChiNhanh", nullable = false)
    private ChiNhanh chiNhanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLoaiPhong", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "giaPhong", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaPhong;

    @Column(name = "sucChua")
    private Integer sucChua;

    @Column(name = "dienTich")
    private Integer dienTich;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Trống";

    @Column(name = "tienNghi", length = 500)
    private String tienNghi;

    @Column(name = "moTa", length = 1000)
    private String moTa;

    @Column(name = "hinhAnh")
    private String hinhAnh;

    @Column(name = "diemTrungBinh", precision = 3, scale = 2)
    private BigDecimal diemTrungBinh;

    @Column(name = "soLuongDanhGia")
    private Integer soLuongDanhGia = 0;

    @PrePersist
    public void prePersist() {
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Trống";
        }
        if (diemTrungBinh == null) {
            diemTrungBinh = BigDecimal.ZERO;
        }
        if (soLuongDanhGia == null) {
            soLuongDanhGia = 0;
        }
    }

    public String getTenLoaiPhong() {
        if (loaiPhong == null || loaiPhong.getTenLoaiPhong() == null || loaiPhong.getTenLoaiPhong().isBlank()) {
            return "Phòng";
        }
        return loaiPhong.getTenLoaiPhong();
    }

    public String getHinhAnhHienThi() {
        if (hinhAnh == null || hinhAnh.isBlank()) {
            return "room-standard.jpg";
        }
        String[] b = hinhAnh.split(",");
        return b[0].trim();
    }

    public String getTenChiNhanh() {
        if (chiNhanh != null && chiNhanh.getTenChiNhanh() != null) {
            return chiNhanh.getTenChiNhanh();
        }
        return "Không xác định";
    }
}