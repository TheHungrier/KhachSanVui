package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.DichVuDTO;
import com.khachsanvui.khachsanvui.model.DichVu;
import com.khachsanvui.khachsanvui.repository.DichVuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DichVuService {

    private static final String TRANG_THAI_DANG_CUNG_CAP = "Đang cung cấp";
    private final DichVuRepository dichVuRepository;
    private final String UPLOAD_DIR = "uploads/avatars/";

    public List<DichVu> getAll() {
        return dichVuRepository.findAll();
    }

    public List<DichVu> getDangCungCap() {
        return dichVuRepository.findTop6ByTrangThaiOrderByMaDichVuDesc(TRANG_THAI_DANG_CUNG_CAP);
    }

    public List<DichVu> getDichVuNoiBat() {
        List<DichVu> dangCungCap = getDangCungCap();
        return dangCungCap.isEmpty()
                ? dichVuRepository.findTop6ByOrderByMaDichVuDesc()
                : dangCungCap;
    }

    public List<DichVu> search(String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return dichVuRepository.searchDichVu(keyword.trim());
        }
        return getAll();
    }

    public DichVu getById(Integer id) {
        return dichVuRepository.findById(id).orElse(null);
    }

    public void saveWithFile(DichVuDTO dto, MultipartFile fileAnh) throws IOException {
        DichVu dichVu = dto.getMaDichVu() == null ? new DichVu() : dichVuRepository.findById(dto.getMaDichVu()).orElse(new DichVu());

        if (fileAnh != null && !fileAnh.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + fileAnh.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fileAnh.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            dichVu.setHinhAnh(fileName);
        } else if (dto.getHinhAnh() != null) {
            dichVu.setHinhAnh(dto.getHinhAnh());
        }

        dichVu.setTenDichVu(dto.getTenDichVu());
        dichVu.setLoaiDichVu(dto.getLoaiDichVu());
        dichVu.setDonGia(dto.getDonGia());
        dichVu.setMoTa(dto.getMoTa());
        dichVu.setTrangThai(dto.getTrangThai());
        dichVuRepository.save(dichVu);
    }

    public void delete(Integer id) {
        dichVuRepository.deleteById(id);
    }

    public DichVuDTO toDTO(DichVu dichVu) {
        DichVuDTO dto = new DichVuDTO();
        if (dichVu == null) return dto;
        dto.setMaDichVu(dichVu.getMaDichVu());
        dto.setTenDichVu(dichVu.getTenDichVu());
        dto.setLoaiDichVu(dichVu.getLoaiDichVu());
        dto.setDonGia(dichVu.getDonGia());
        dto.setMoTa(dichVu.getMoTa());
        dto.setHinhAnh(dichVu.getHinhAnh());
        dto.setTrangThai(dichVu.getTrangThai());
        return dto;
    }
}