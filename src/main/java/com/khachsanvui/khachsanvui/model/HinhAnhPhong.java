package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HinhAnhPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HinhAnhPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maAnh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maPhong", nullable = false)
    private Phong phong;

    @Column(name = "duongDanAnh", nullable = false)
    private String duongDanAnh;

    @Column(name = "laAnhChinh")
    private Boolean laAnhChinh = false;
}