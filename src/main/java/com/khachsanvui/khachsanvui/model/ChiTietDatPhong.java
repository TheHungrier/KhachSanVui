package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ChiTietDatPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maChiTiet;
    @ManyToOne
    @JoinColumn(name = "maDatPhong")
    private PhieuDatPhong phieuDatPhong;
    @ManyToOne
    @JoinColumn(name = "maPhong")
    private Phong phong;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private BigDecimal donGia;
}