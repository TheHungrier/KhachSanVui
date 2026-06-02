package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PhieuSuDungDV")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuSuDungDV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSuDung;
    @ManyToOne
    @JoinColumn(name = "maLuuTru")
    private HoSoLuuTru hoSoLuuTru;
    @ManyToOne
    @JoinColumn(name = "maDichVu")
    private DichVu dichVu;
    private Integer soLuong;
    private LocalDateTime thoiGianSuDung;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
}