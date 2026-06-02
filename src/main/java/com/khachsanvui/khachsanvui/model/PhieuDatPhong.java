package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuDatPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDatPhong;
    @ManyToOne
    @JoinColumn(name = "maKH")
    private KhachHang khachHang;
    @ManyToOne
    @JoinColumn(name = "maNV")
    private NhanVien nhanVien;
    private LocalDateTime ngayDat;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private Integer soLuongPhong;
    private Integer soLuongKhach;
    private String trangThai;
}