package com.khachsanvui.khachsanvui.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DichVuFormDTO {
    private List<CartItem> items;

    @Setter
    @Getter
    public static class CartItem {
        private Integer maDichVu;
        private Integer soLuong;
    }
}