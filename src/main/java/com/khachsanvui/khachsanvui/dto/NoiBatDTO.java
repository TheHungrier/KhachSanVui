package com.khachsanvui.khachsanvui.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoiBatDTO {
    private String icon;
    private String text;
    private String badge;
    private String iconColor;

    public NoiBatDTO(String icon, String text) {
        this.icon = icon;
        this.text = text;
    }

    public NoiBatDTO(String icon, String text, String badge) {
        this.icon = icon;
        this.text = text;
        this.badge = badge;
    }

}