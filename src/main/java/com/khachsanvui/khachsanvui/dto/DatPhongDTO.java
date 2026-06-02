package com.khachsanvui.khachsanvui.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public class DatPhongDTO {
    
    // Thông tin phòng đặt
    private Integer maPhong;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayNhan;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayTra;
    
    private Integer soLuongKhach;
    private Integer soLuongPhong = 1; // Mặc định là 1 nếu đặt trực tiếp từ trang chi tiết 1 phòng

    // Thông tin khách hàng
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String cccd;

    public DatPhongDTO() {
    }

    // Getters and Setters
    public Integer getMaPhong() {
        return maPhong;
    }

    public void setMaPhong(Integer maPhong) {
        this.maPhong = maPhong;
    }

    public LocalDate getNgayNhan() {
        return ngayNhan;
    }

    public void setNgayNhan(LocalDate ngayNhan) {
        this.ngayNhan = ngayNhan;
    }

    public LocalDate getNgayTra() {
        return ngayTra;
    }

    public void setNgayTra(LocalDate ngayTra) {
        this.ngayTra = ngayTra;
    }

    public Integer getSoLuongKhach() {
        return soLuongKhach;
    }

    public void setSoLuongKhach(Integer soLuongKhach) {
        this.soLuongKhach = soLuongKhach;
    }

    public Integer getSoLuongPhong() {
        return soLuongPhong;
    }

    public void setSoLuongPhong(Integer soLuongPhong) {
        this.soLuongPhong = soLuongPhong;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }
}
