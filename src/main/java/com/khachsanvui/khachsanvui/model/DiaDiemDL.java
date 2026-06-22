package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DiaDiemDuLich")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaDiemDL {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maDiaDiemDL")
    private Integer maDiaDiemDL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maChiNhanh", nullable = false)
    private ChiNhanh chiNhanh;

    @Column(name = "tenDiaDiem", nullable = false, length = 200)
    private String tenDiaDiem;

    @Column(name = "moTa", length = 1000)
    private String moTa;

    @Column(name = "hinhAnh", length = 255)
    private String hinhAnh;

    @Column(name = "viDo", precision = 10, scale = 7)
    private BigDecimal viDo;

    @Column(name = "kinhDo", precision = 10, scale = 7)
    private BigDecimal kinhDo;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Hiển thị";
}