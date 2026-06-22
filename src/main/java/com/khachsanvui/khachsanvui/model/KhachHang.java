package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KhachHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maKH")
    private Integer maKH;

    @Column(name = "hoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "gioiTinh", nullable = false, length = 10)
    private String gioiTinh;

    @Column(name = "soDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "cccd", unique = true, length = 20)
    private String cccd;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Column(name = "diaChi", length = 200)
    private String diaChi;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "hinhAnh", length = 500)
    private String hinhAnh;

    @OneToMany(mappedBy = "khachHang")
    private List<PhieuDatPhong> danhSachDatPhong = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}