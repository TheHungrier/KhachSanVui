package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DichVu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maDichVu;
    private String tenDichVu;
    private String loaiDichVu;
    private BigDecimal donGia;
    private String moTa;
    private String trangThai;
}