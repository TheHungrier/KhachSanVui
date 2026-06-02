package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TaiKhoan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private String trangThai;
    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;
}