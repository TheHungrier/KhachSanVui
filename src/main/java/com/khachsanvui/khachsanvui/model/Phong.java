package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Phong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maPhong;
    private String soPhong;
    private String loaiPhong;
    private BigDecimal giaPhong;
    private String trangThai;
    private String tienNghi;
    private String moTa;
    private String hinhAnh;
}