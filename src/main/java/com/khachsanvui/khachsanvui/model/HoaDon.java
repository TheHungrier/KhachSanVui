package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maHD")
    private Integer maHD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maDatPhong")
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maLuuTru")
    private HoSoLuuTru hoSoLuuTru;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;

    @Column(name = "tongTienPhong", precision = 12, scale = 2)
    private BigDecimal tongTienPhong = BigDecimal.ZERO;

    @Column(name = "tongTienDV", precision = 12, scale = 2)
    private BigDecimal tongTienDV = BigDecimal.ZERO;

    @Column(name = "phuPhi", precision = 12, scale = 2)
    private BigDecimal phuPhi = BigDecimal.ZERO;

    @Column(name = "giamGia", precision = 12, scale = 2)
    private BigDecimal giamGia = BigDecimal.ZERO;

    @Column(name = "tongTien", precision = 12, scale = 2)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "ngayLap")
    private LocalDateTime ngayLap;

    @Column(name = "hinhThucThanhToan", length = 50)
    private String hinhThucThanhToan;

    @Column(name = "trangThaiThanhToan", length = 50)
    private String trangThaiThanhToan = "Chưa thanh toán";

    @Column(name = "ghiChu", length = 300)
    private String ghiChu;

    @OneToMany(mappedBy = "hoaDon")
    private List<ThanhToan> thanhToans = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void tinhTongTien() {
        if (ngayLap == null) {
            ngayLap = LocalDateTime.now();
        }

        if (tongTienPhong == null) {
            tongTienPhong = BigDecimal.ZERO;
        }

        if (tongTienDV == null) {
            tongTienDV = BigDecimal.ZERO;
        }

        if (phuPhi == null) {
            phuPhi = BigDecimal.ZERO;
        }

        if (giamGia == null) {
            giamGia = BigDecimal.ZERO;
        }

        tongTien = tongTienPhong
                .add(tongTienDV)
                .add(phuPhi)
                .subtract(giamGia);

        if (trangThaiThanhToan == null || trangThaiThanhToan.isBlank()) {
            trangThaiThanhToan = "Chưa thanh toán";
        }
    }
}