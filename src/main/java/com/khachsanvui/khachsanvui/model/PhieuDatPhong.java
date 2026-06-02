package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuDatPhong")
public class PhieuDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDatPhong")
    private Integer maDatPhong;

    @ManyToOne
    @JoinColumn(name = "maKH", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ngayDat")
    private LocalDateTime ngayDat = LocalDateTime.now();

    @Column(name = "ngayNhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "ngayTra", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "soLuongPhong", nullable = false)
    private Integer soLuongPhong;

    @Column(name = "soLuongKhach", nullable = false)
    private Integer soLuongKhach;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Đã đặt";

    public PhieuDatPhong() {}

    public Integer getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(Integer maDatPhong) { this.maDatPhong = maDatPhong; }

    public KhachHang getKhachHang() { return khachHang; }
    public void setKhachHang(KhachHang khachHang) { this.khachHang = khachHang; }

    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }

    public LocalDate getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDate ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDate getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDate ngayTra) { this.ngayTra = ngayTra; }

    public Integer getSoLuongPhong() { return soLuongPhong; }
    public void setSoLuongPhong(Integer soLuongPhong) { this.soLuongPhong = soLuongPhong; }

    public Integer getSoLuongKhach() { return soLuongKhach; }
    public void setSoLuongKhach(Integer soLuongKhach) { this.soLuongKhach = soLuongKhach; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}