package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThanhToan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThanhToan;
    @ManyToOne
    @JoinColumn(name = "maHD")
    private HoaDon hoaDon;
    private BigDecimal soTien;
    private String phuongThuc;
    private LocalDateTime ngayThanhToan;
    private String trangThai;
    private String maGiaoDich;
    private String ghiChu;
}