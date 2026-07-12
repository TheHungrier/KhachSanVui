package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TaiKhoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maTaiKhoan")
    private Integer maTaiKhoan;

    @Column(name = "tenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "matKhau", nullable = false)
    private String matKhau;

    @Column(name = "vaiTro", nullable = false, length = 50)
    private String vaiTro;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Hoạt động";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "providerId")
    private String providerId;


    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }

        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Hoạt động";
        }
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(vaiTro);
    }

    public boolean isLeTan() {
        return "LETAN".equalsIgnoreCase(vaiTro);
    }

    public boolean isKhachHang() {
        return "KHACHHANG".equalsIgnoreCase(vaiTro);
    }
}