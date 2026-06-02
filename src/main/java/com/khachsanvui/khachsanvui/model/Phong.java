package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Phong")
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maPhong")
    private Integer maPhong;

    @Column(name = "soPhong", nullable = false, unique = true, length = 20)
    private String soPhong;

    @Column(name = "loaiPhong", nullable = false, length = 50)
    private String loaiPhong;

    @Column(name = "giaPhong", nullable = false)
    private BigDecimal giaPhong;

    @Column(name = "trangThai", nullable = false, length = 50)
    private String trangThai = "Trống";

    @Column(name = "tienNghi", length = 200)
    private String tienNghi;

    @Column(name = "moTa", length = 300)
    private String moTa;

    @Column(name = "hinhAnh", length = 255)
    private String hinhAnh;

    // Constructors
    public Phong() {}

    // Getters and Setters
    public Integer getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Integer maPhong) {
        this.maPhong = maPhong;
    }

    public String getSoPhong() {
        return soPhong;
    }

    public void setSoPhong(String soPhong) {
        this.soPhong = soPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public BigDecimal getGiaPhong() {
        return giaPhong;
    }

    public void setGiaPhong(BigDecimal giaPhong) {
        this.giaPhong = giaPhong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getTienNghi() {
        return tienNghi;
    }

    public void setTienNghi(String tienNghi) {
        this.tienNghi = tienNghi;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }
}