package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ChiTietDatPhong")
public class ChiTietDatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maChiTiet")
    private Integer maChiTiet;

    @ManyToOne
    @JoinColumn(name = "maDatPhong", nullable = false)
    private PhieuDatPhong phieuDatPhong;

    @ManyToOne
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @Column(name = "ngayNhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "ngayTra", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "donGia")
    private BigDecimal donGia;

    public ChiTietDatPhong() {}

    public Integer getMaChiTiet() { return maChiTiet; }
    public void setMaChiTiet(Integer maChiTiet) { this.maChiTiet = maChiTiet; }

    public PhieuDatPhong getPhieuDatPhong() { return phieuDatPhong; }
    public void setPhieuDatPhong(PhieuDatPhong phieuDatPhong) { this.phieuDatPhong = phieuDatPhong; }

    public Phong getPhong() { return phong; }
    public void setPhong(Phong phong) { this.phong = phong; }

    public LocalDate getNgayNhan() { return ngayNhan; }
    public void setNgayNhan(LocalDate ngayNhan) { this.ngayNhan = ngayNhan; }

    public LocalDate getNgayTra() { return ngayTra; }
    public void setNgayTra(LocalDate ngayTra) { this.ngayTra = ngayTra; }

    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
}