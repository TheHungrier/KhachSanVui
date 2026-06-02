package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "KhachHang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKH;
    private String hoTen;
    private String gioiTinh;
    private String soDienThoai;
    private String cccd;
    private String email;
    private String diaChi;
    private LocalDateTime ngayTao;
}
