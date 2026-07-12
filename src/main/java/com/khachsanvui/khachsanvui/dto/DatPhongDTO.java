package com.khachsanvui.khachsanvui.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class DatPhongDTO {

    @NotNull(message = "Vui lòng chọn phòng")
    private Integer maPhong;

    @NotNull(message = "Vui lòng chọn ngày nhận phòng")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayNhan;

    @NotNull(message = "Vui lòng chọn ngày trả phòng")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayTra;

    @Min(value = 1, message = "Số lượng khách phải lớn hơn 0")
    private Integer soLuongKhach = 1;

    @Min(value = 1, message = "Số lượng phòng phải lớn hơn 0")
    private Integer soLuongPhong = 1;

    @Min(value = 1, message = "Số đêm lưu trú phải lớn hơn 0")
    private Integer soDem = 1;

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String hoTen;

    private String gioiTinh;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải là 10 chữ số") // Ép chuẩn 10 số theo JS
    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String soDienThoai;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @Pattern(regexp = "^(^$|\\d{12})$", message = "CCCD nếu nhập phải đúng 12 chữ số")
    @Size(max = 12, message = "CCCD không được quá 12 ký tự")
    private String cccd;

    @Size(max = 200, message = "Địa chỉ không được quá 200 ký tự")
    private String diaChi;

    @Size(max = 300, message = "Ghi chú không được quá 300 ký tự")
    private String ghiChu;

    @Size(max = 20, message = "Mã giảm giá không được quá 20 ký tự")
    private String maGiamGia;
}