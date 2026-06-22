package com.khachsanvui.khachsanvui.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DanhGiaDTO {
    private Integer maDanhGia;
    private Integer soSao;
    private String binhLuan;
    private LocalDateTime ngayDanhGia;
    private String tenKhachHang;
    private String hinhAnhKhach;
}