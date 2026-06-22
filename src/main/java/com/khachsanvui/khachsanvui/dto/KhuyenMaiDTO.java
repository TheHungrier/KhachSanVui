package com.khachsanvui.khachsanvui.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class KhuyenMaiDTO {
    private Integer maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private Boolean apDungChoPhong;
    private Boolean apDungChoDichVu;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private Integer soLuongPhongGioiHan;
    private Integer soLuongDaDat;
    private String trangThai;
    private String hinhAnh;
    private String maCode;
}