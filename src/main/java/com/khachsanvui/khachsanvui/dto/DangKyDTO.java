package com.khachsanvui.khachsanvui.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DangKyDTO {

    @NotBlank(message = "Vui lòng nhập họ tên")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String hoTen;

    private String gioiTinh;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String soDienThoai;

    @Email(message = "Email không hợp lệ")
    @Size(max = 100, message = "Email không được quá 100 ký tự")
    private String email;

    @Size(max = 20, message = "CCCD không được quá 20 ký tự")
    private String cccd;

    @Size(max = 200, message = "Địa chỉ không được quá 200 ký tự")
    private String diaChi;

    @NotBlank(message = "Vui lòng nhập tên đăng nhập")
    @Size(max = 50, message = "Tên đăng nhập không được quá 50 ký tự")
    private String tenDangNhap;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 ký tự")
    private String matKhau;

    @NotBlank(message = "Vui lòng xác nhận mật khẩu")
    private String xacNhanMatKhau;
}