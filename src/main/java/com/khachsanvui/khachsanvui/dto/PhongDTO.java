package com.khachsanvui.khachsanvui.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PhongDTO {

    private Integer maPhong;

    @NotBlank(message = "Vui lòng nhập số phòng")
    @Size(max = 20, message = "Số phòng không được quá 20 ký tự")
    private String soPhong;

    @NotNull(message = "Vui lòng chọn chi nhánh")
    private Integer maChiNhanh;

    @NotNull(message = "Vui lòng chọn loại phòng")
    private Integer maLoaiPhong;

    private String tenLoaiPhong;

    @NotNull(message = "Vui lòng nhập giá phòng")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phòng phải lớn hơn 0")
    private BigDecimal giaPhong;

    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer sucChua;

    @Min(value = 1, message = "Diện tích phải lớn hơn 0")
    private Integer dienTich;

    @Size(max = 50, message = "Trạng thái không được quá 50 ký tự")
    private String trangThai = "Trống";

    @Size(max = 500, message = "Tiện nghi không được quá 500 ký tự")
    private String tienNghi;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String moTa;

    @Size(max = 255, message = "Tên hình ảnh không được quá 255 ký tự")
    private String hinhAnh;
}