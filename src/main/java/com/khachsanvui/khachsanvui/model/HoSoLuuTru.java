package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "HoSoLuuTru")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoSoLuuTru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maLuuTru")
    private Integer maLuuTru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong")
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "gioNhanPhong")
    private LocalDateTime gioNhanPhong;

    @Column(name = "gioTraPhong")
    private LocalDateTime gioTraPhong;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Đang lưu trú";

    @PrePersist
    public void prePersist() {
        if (gioNhanPhong == null) {
            gioNhanPhong = LocalDateTime.now();
        }

        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Đang lưu trú";
        }
    }

    public boolean dangLuuTru() {
        return "Đang lưu trú".equalsIgnoreCase(trangThai);
    }

    public boolean daTraPhong() {
        return "Đã trả phòng".equalsIgnoreCase(trangThai);
    }
}