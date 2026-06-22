package com.khachsanvui.khachsanvui.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HuyDonDTO {

    @NotBlank(message = "Vui lòng nhập lý do hủy đơn")
    @Size(max = 300, message = "Lý do hủy không được quá 300 ký tự")
    private String lyDoHuy;
}