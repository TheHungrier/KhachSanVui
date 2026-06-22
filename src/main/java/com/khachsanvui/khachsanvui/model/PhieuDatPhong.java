package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PhieuDatPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDatPhong")
    private Integer maDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "ngayDat")
    private LocalDateTime ngayDat;

    @Column(name = "ngayNhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "ngayTra", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "soLuongPhong", nullable = false)
    private Integer soLuongPhong = 1;

    @Column(name = "soLuongKhach", nullable = false)
    private Integer soLuongKhach = 1;

    @Column(name = "ghiChu", length = 300)
    private String ghiChu;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Chờ xác nhận";

    @Column(name = "lyDoHuy", length = 300)
    private String lyDoHuy;

    @Column(name = "ngayHuy")
    private LocalDateTime ngayHuy;

    @Column(name = "maGiamGia", length = 50)
    private String maGiamGia;

    @OneToMany(mappedBy = "phieuDatPhong", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDatPhong> chiTietDatPhongs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (ngayDat == null) {
            ngayDat = LocalDateTime.now();
        }

        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Chờ xác nhận";
        }

        if (soLuongPhong == null || soLuongPhong <= 0) {
            soLuongPhong = 1;
        }

        if (soLuongKhach == null || soLuongKhach <= 0) {
            soLuongKhach = 1;
        }
    }

    public boolean coTheHuy() {
        return "Chờ xác nhận".equalsIgnoreCase(trangThai)
                || "Đã xác nhận".equalsIgnoreCase(trangThai);
    }
}