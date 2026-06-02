package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHD;
    @ManyToOne
    @JoinColumn(name = "maLuuTru")
    private HoSoLuuTru hoSoLuuTru;
    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
    private BigDecimal tongTienPhong;
    private BigDecimal tongTienDV;
    private BigDecimal phuPhi;
    private BigDecimal giamGia;
    private BigDecimal tongTien;
    private LocalDateTime ngayLap;
    private String hinhThucThanhToan;
    private String trangThaiThanhToan;
    private String ghiChu;
}