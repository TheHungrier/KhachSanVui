package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.KhuyenMaiDTO;
import com.khachsanvui.khachsanvui.model.KhuyenMai;
import com.khachsanvui.khachsanvui.repository.KhuyenMaiRepository;
import com.khachsanvui.khachsanvui.repository.PhieuDatPhongRepository; // BỔ SUNG IMPORT
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KhuyenMaiService {
    private final KhuyenMaiRepository khuyenMaiRepository;
    private final PhieuDatPhongRepository phieuDatPhongRepository;

    public KhuyenMaiService(KhuyenMaiRepository khuyenMaiRepository,
                            PhieuDatPhongRepository phieuDatPhongRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
    }

    public List<KhuyenMai> getActiveFlashSales() {
        return khuyenMaiRepository.findActiveFlashSales();
    }

    public KhuyenMaiDTO toDTO(KhuyenMai entity) {
        if (entity == null) return null;
        KhuyenMaiDTO dto = new KhuyenMaiDTO();
        dto.setMaKhuyenMai(entity.getMaKhuyenMai());
        dto.setTenKhuyenMai(entity.getTenKhuyenMai());
        dto.setMoTa(entity.getMoTa());
        dto.setLoaiGiamGia(entity.getLoaiGiamGia());
        dto.setGiaTriGiam(entity.getGiaTriGiam());
        dto.setApDungChoPhong(entity.getApDungChoPhong());
        dto.setApDungChoDichVu(entity.getApDungChoDichVu());
        dto.setThoiGianBatDau(entity.getThoiGianBatDau());
        dto.setThoiGianKetThuc(entity.getThoiGianKetThuc());
        dto.setSoLuongPhongGioiHan(entity.getSoLuongPhongGioiHan());
        dto.setSoLuongDaDat(entity.getSoLuongDaDat());
        dto.setTrangThai(entity.getTrangThai());
        dto.setHinhAnh(entity.getHinhAnh());
        dto.setMaCode(entity.getMaCode());
        return dto;
    }

    public List<KhuyenMaiDTO> toDTOList(List<KhuyenMai> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<KhuyenMai> findByMaCode(String maCode) {
        return khuyenMaiRepository.findByMaCode(maCode);
    }

    public boolean isValid(KhuyenMai km) {
        LocalDateTime now = LocalDateTime.now();
        return km != null
                && "Đang diễn ra".equalsIgnoreCase(km.getTrangThai())
                && now.isAfter(km.getThoiGianBatDau())
                && now.isBefore(km.getThoiGianKetThuc())
                && km.getSoLuongDaDat() < km.getSoLuongPhongGioiHan();
    }

    public BigDecimal tinhSoTienGiam(KhuyenMai km, BigDecimal tongTienGoc) {
        if (km == null || tongTienGoc == null) return BigDecimal.ZERO;
        if ("PHAN_TRAM".equalsIgnoreCase(km.getLoaiGiamGia())) {
            BigDecimal phanTram = km.getGiaTriGiam().divide(new BigDecimal("100.0"));
            return tongTienGoc.multiply(phanTram);
        } else {
            return km.getGiaTriGiam();
        }
    }

    public double tinhSoTienGiam(KhuyenMai km, double tongTienGoc) {
        if ("PHAN_TRAM".equalsIgnoreCase(km.getLoaiGiamGia())) {
            return tongTienGoc * (km.getGiaTriGiam().doubleValue() / 100.0);
        } else {
            return km.getGiaTriGiam().doubleValue();
        }
    }

    public boolean kiemTraUserDaDungMa(Integer maKH, Integer maKM) {
        if (maKH == null || maKM == null) return false;

        Optional<KhuyenMai> kmOpt = khuyenMaiRepository.findById(maKM);
        if (kmOpt.isEmpty()) return false;
        String maCode = kmOpt.get().getMaCode();

        return phieuDatPhongRepository.findByKhachHang_MaKHOrderByNgayDatDesc(maKH)
                .stream()
                .anyMatch(phieu -> phieu.getMaGiamGia() != null
                        && maCode.equalsIgnoreCase(phieu.getMaGiamGia().trim())
                        && !"Đã hủy".equalsIgnoreCase(phieu.getTrangThai()));
    }

    public void ghiNhanSuDungMa(Integer maKH, Integer maKM) {
        khuyenMaiRepository.findById(maKM).ifPresent(km -> {
            int soLuongMoi = (km.getSoLuongDaDat() != null ? km.getSoLuongDaDat() : 0) + 1;
            km.setSoLuongDaDat(soLuongMoi);

            if (km.getSoLuongPhongGioiHan() != null && soLuongMoi >= km.getSoLuongPhongGioiHan()) {
                km.setTrangThai("Đã kết thúc");
            }
            khuyenMaiRepository.save(km);
        });
    }
}