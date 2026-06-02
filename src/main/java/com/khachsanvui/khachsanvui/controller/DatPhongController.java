package com.khachsanvui.khachsanvui.controller;

import com.khachsanvui.khachsanvui.dto.DatPhongDTO;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.repository.PhongRepository;
import com.khachsanvui.khachsanvui.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DatPhongController {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private DatPhongService datPhongService;

    // Hiển thị trang chi tiết phòng
    @GetMapping("/phong/{id}")
    public String xemChiTietPhong(@PathVariable("id") Integer id, Model model) {
        Phong phong = phongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mã phòng không hợp lệ:" + id));
        
        model.addAttribute("phong", phong);
        
        // Khởi tạo DTO rỗng truyền xuống form
        DatPhongDTO datPhongDTO = new DatPhongDTO();
        datPhongDTO.setMaPhong(phong.getMaPhong());
        model.addAttribute("datPhongDTO", datPhongDTO);
        
        return "chi-tiet-phong"; 
    }

    // Xử lý submit form đặt phòng
    @PostMapping("/dat-phong")
    public String xuLyDatPhong(@ModelAttribute("datPhongDTO") DatPhongDTO datPhongDTO, 
                               RedirectAttributes redirectAttributes) {
        try {
            datPhongService.taoPhieuDatPhong(datPhongDTO);
            redirectAttributes.addFlashAttribute("message", "Chúc mừng! Đặt phòng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/phong/" + datPhongDTO.getMaPhong();
        }
        
        return "redirect:/phong/" + datPhongDTO.getMaPhong();
    }
}