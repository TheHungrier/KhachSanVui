package com.khachsanvui.khachsanvui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KhamPhaController {

    @GetMapping("/kham-pha")
    public String khamPhaPage() {
        return "khampha/gioi-thieu";
    }

    @GetMapping({"/lien-he", "/lien-he/", "/kham-pha/lien-he", "/kham-pha/lien-he/"})
    public String lienHePage() {
        return "khampha/lien-he";
    }

    @GetMapping({"/gioi-thieu", "/gioi-thieu/", "/kham-pha/gioi-thieu", "/kham-pha/gioi-thieu/"})
    public String gioiThieuPage() {
        return "khampha/gioi-thieu";
    }
}
