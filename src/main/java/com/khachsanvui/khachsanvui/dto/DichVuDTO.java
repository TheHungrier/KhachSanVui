package com.khachsanvui.khachsanvui.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DichVuDTO {

    private Integer maDichVu;

    @NotBlank(message = "Vui lòng nhập tên dịch vụ")
    @Size(max = 100, message = "Tên dịch vụ không được quá 100 ký tự")
    private String tenDichVu;

    @Size(max = 100, message = "Loại dịch vụ không được quá 100 ký tự")
    private String loaiDichVu;

    @NotNull(message = "Vui lòng nhập đơn giá")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải lớn hơn 0")
    private BigDecimal donGia;

    @Size(max = 500, message = "Mô tả không được quá 500 ký tự")
    private String moTa;

    @Size(max = 255, message = "Tên hình ảnh không được quá 255 ký tự")
    private String hinhAnh;

    @Size(max = 50, message = "Trạng thái không được quá 50 ký tự")
    private String trangThai = "Đang cung cấp";
}