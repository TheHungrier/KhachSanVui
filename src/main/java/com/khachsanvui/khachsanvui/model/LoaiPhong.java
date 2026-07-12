package com.khachsanvui.khachsanvui.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LoaiPhong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "maLoaiPhong")
    private Integer maLoaiPhong;

    @Column(name = "tenLoaiPhong", nullable = false, length = 100)
    private String tenLoaiPhong;

    @Column(name = "moTa", length = 500)
    private String moTa;

    @Column(name = "sucChua")
    private Integer sucChua;

    @Column(name = "trangThai", length = 50)
    private String trangThai = "Đang sử dụng";

    @OneToMany(mappedBy = "loaiPhong")
    private List<Phong> danhSachPhong = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (trangThai == null || trangThai.isBlank()) {
            trangThai = "Đang sử dụng";
        }
    }
}