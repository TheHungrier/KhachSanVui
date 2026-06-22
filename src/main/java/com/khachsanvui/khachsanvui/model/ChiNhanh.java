package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ChiNhanh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiNhanh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maChiNhanh")
    private Integer maChiNhanh;

    @Column(name = "tenChiNhanh", nullable = false, length = 100)
    private String tenChiNhanh;

    @Column(name = "diaChi", length = 200)
    private String diaChi;

    @Column(name = "moTa", length = 1000)
    private String moTa;

    @Column(name = "hinhAnh")
    private String hinhAnh;

    @Column(name = "viDo", precision = 10, scale = 7)
    private BigDecimal viDo;

    @Column(name = "kinhDo", precision = 10, scale = 7)
    private BigDecimal kinhDo;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Hiển thị";
}