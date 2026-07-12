package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.NoiBatDTO;
import com.khachsanvui.khachsanvui.model.Phong;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.math.RoundingMode;

@Component
public class NoiBatService {

    // Tạo bảng tra cứu xếp hạng dựa trên điểm số thực tế (Hệ điểm 10)
    private static final NavigableMap<Double, String> RATING_SCALE = new TreeMap<>();
    static {
        RATING_SCALE.put(0.1, "Tốt");
        RATING_SCALE.put(8.0, "Rất tốt");
        RATING_SCALE.put(9.0, "Xuất sắc");
        RATING_SCALE.put(9.5, "Tuyệt hảo");
    }

    public List<NoiBatDTO> getHighlights(Phong phong) {
        List<NoiBatDTO> highlights = new ArrayList<>();

        if (phong == null) {
            return highlights;
        }

        // 1. Lấy diện tích thực tế từ Database
        if (phong.getDienTich() != null) {
            highlights.add(new NoiBatDTO("fa-ruler-combined", "Không gian rộng " + phong.getDienTich() + " m²"));
        }

        // 2. Lấy sức chứa thực tế từ Database
        if (phong.getSucChua() != null) {
            highlights.add(new NoiBatDTO("fa-users", "Sức chứa tối đa " + phong.getSucChua() + " người lớn"));
        }

        // 3. Hiển thị trạng thái phòng thực tế (Trống, Đang sửa,...)
        if (phong.getTrangThai() != null && !phong.getTrangThai().isBlank()) {
            highlights.add(new NoiBatDTO("fa-door-open", "Trạng thái: " + phong.getTrangThai()));
        }

        // 4. Chính sách hủy phòng cố định, tăng độ uy tín
        highlights.add(new NoiBatDTO("fa-clock", "Hủy phòng linh hoạt trong 24h"));

        // 5. Tính toán danh hiệu điểm dựa trên điểm trung bình thật (Hệ 10) của phòng
        BigDecimal diemTrungBinh = (phong.getDiemTrungBinh() != null) ? phong.getDiemTrungBinh() : BigDecimal.ZERO;
        double diemDouble = diemTrungBinh.doubleValue();

        var matchedScale = RATING_SCALE.floorEntry(diemDouble);

        if (matchedScale != null && diemDouble > 0) {
            // Hiển thị điểm kèm danh hiệu (Ví dụ: 9.2 điểm - Xuất sắc)
            BigDecimal scaled = diemTrungBinh.setScale(1, RoundingMode.HALF_UP);
            String displayScore = scaled.toPlainString() + " điểm";
            NoiBatDTO rating = new NoiBatDTO("fa-star", displayScore, matchedScale.getValue());
            rating.setIconColor("gold"); // Gán icon ngôi sao màu vàng cho nổi bật
            highlights.add(rating);
        } else if (phong.getSoLuongDanhGia() == 0 || diemDouble == 0) {
            highlights.add(new NoiBatDTO("fa-star-half-stroke", "Phòng mới", "Chưa có đánh giá"));
        }

        return highlights;
    }
}