package com.khachsanvui.khachsanvui.repository.specification;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class SearchSpecification {

    public static <T> Specification<T> buildSpec(String keyword, List<String> searchFields) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            try {
                if (root.getModel().getAttribute("trangThai") != null) {
                    predicates.add(cb.notEqual(root.get("trangThai"), "Ngừng hoạt động"));
                }
            } catch (IllegalArgumentException e) {
                // Nếu Entity không có trường "trangThai", bỏ qua không lọc điều kiện này
            }

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                List<Predicate> searchPredicates = new ArrayList<>();
                for (String field : searchFields) {
                    searchPredicates.add(cb.like(cb.lower(root.get(field).as(String.class)), pattern));
                }
                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Pageable buildPageable(int page, int size, String sortBy, String idFieldName) {
        Sort sort = Sort.by(idFieldName).ascending();

        if (sortBy != null && !sortBy.isBlank()) {
            if (sortBy.contains(",")) {
                String[] parts = sortBy.split(",");
                sortBy = (parts.length > 0) ? parts[parts.length - 1].trim() : "";
            }

            String nameField = "hoTen";

            switch (idFieldName) {
                case "maPhong" -> nameField = "soPhong";
                case "maChiNhanh" -> nameField = "tenChiNhanh";
                case "maDichVu" -> nameField = "tenDichVu";
                case "maLoaiPhong" -> nameField = "tenLoaiPhong";
                case "maKhuyenMai" -> nameField = "tenKhuyenMai";
                case "maHD" -> nameField = "maHD";
                case "maLuuTru" -> nameField = "maLuuTru";
                case "maDatPhong" -> nameField = "maDatPhong";
            }

            switch (sortBy) {
                case "az" -> sort = Sort.by(nameField).ascending();
                case "za" -> sort = Sort.by(nameField).descending();
                case "id-asc" -> sort = Sort.by(idFieldName).ascending();
                case "id-desc" -> sort = Sort.by(idFieldName).descending();
                case "room-asc" -> sort = Sort.by("soPhong").ascending();
                case "room-desc" -> sort = Sort.by("soPhong").descending();
                case "price-asc", "gia-asc" -> sort = Sort.by("giaPhong").ascending();
                case "price-desc", "gia-desc" -> sort = Sort.by("giaPhong").descending();
                case "Đang lưu trú", "Trống", "Đang làm việc" -> sort = Sort.by("trangThai").ascending();
                case "Đã trả phòng", "Ngừng hoạt động" -> sort = Sort.by("trangThai").descending();
            }
        }

        return PageRequest.of(page - 1, size, sort);
    }
}