package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "NhanVien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maNV")
    private Integer maNV;

    @Column(name = "gioiTinh", nullable = false, length = 10)
    private String gioiTinh;

    @Column(name = "hoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "soDienThoai", nullable = false, unique = true, length = 15)
    private String soDienThoai;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "chucVu", nullable = false, length = 50)
    private String chucVu;

    @Column(name = "ngaySinh")
    private LocalDate ngaySinh;

    @Column(name = "diaChi", length = 200)
    private String diaChi;

    @Column(name = "hinhAnh", length = 500)
    private String hinhAnh;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Đang làm";

    @PrePersist
    public void prePersist() {
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Đang làm";
        }
    }
}