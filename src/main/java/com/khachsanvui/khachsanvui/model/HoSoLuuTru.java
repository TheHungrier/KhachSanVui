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
    private Integer maLuuTru;
    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "maPhong")
    private Phong phong;
    @ManyToOne
    @JoinColumn(name = "maDatPhong")
    private PhieuDatPhong phieuDatPhong;
    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
    private LocalDateTime gioNhanPhong;
    private LocalDateTime gioTraPhong;
    private String trangThai;
}