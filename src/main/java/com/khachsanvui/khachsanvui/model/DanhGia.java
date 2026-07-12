package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhGia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDanhGia")
    private Integer maDanhGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKhachHang", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong")
    private PhieuDatPhong phieuDatPhong;

    @Column(name = "soSao", nullable = false)
    private Integer soSao;

    @Column(name = "binhLuan", length = 500)
    private String binhLuan;

    @Column(name = "ngayDanhGia")
    private LocalDateTime ngayDanhGia;

    @Column(name = "trangThai", length = 50)
    private String trangThai;

    @PrePersist
    protected void onCreate() {
        ngayDanhGia = LocalDateTime.now();
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Hiển thị";
        }
    }
}