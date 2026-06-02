package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maKH")
    private Integer maKH;

    @Column(name = "hoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "gioiTinh", length = 10)
    private String gioiTinh;

    @Column(name = "soDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "cccd", unique = true, length = 20)
    private String cccd;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "diaChi", length = 200)
    private String diaChi;

    @Column(name = "ngayTao", updatable = false)
    private LocalDateTime ngayTao = LocalDateTime.now();

    public KhachHang() {}

    public Integer getMaKH() { return maKH; }
    public void setMaKH(Integer maKH) { this.maKH = maKH; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }
}