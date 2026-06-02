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
    private Integer maNV;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String chucVu;
    private LocalDate ngaySinh;
    private String diaChi;
    private String trangThai;
}