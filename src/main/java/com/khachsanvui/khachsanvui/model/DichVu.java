package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DichVu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDichVu")
    private Integer maDichVu;

    @Column(name = "tenDichVu", nullable = false, length = 100)
    private String tenDichVu;

    @Column(name = "loaiDichVu", length = 100)
    private String loaiDichVu;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "moTa", length = 500)
    private String moTa;

    @Column(name = "hinhAnh")
    private String hinhAnh;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Đang cung cấp";

    @PrePersist
    public void prePersist() {
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Đang cung cấp";
        }
    }

    public String getHinhAnhHienThi() {
        if (hinhAnh == null || hinhAnh.isBlank()) {
            return "service-default.jpg";
        }
        return hinhAnh;
    }
}