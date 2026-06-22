package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.DanhGiaDTO;
import com.khachsanvui.khachsanvui.model.DanhGia;
import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.PhieuDatPhong;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.repository.DanhGiaRepository;
import com.khachsanvui.khachsanvui.repository.PhongRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DanhGiaService {
    private final DanhGiaRepository danhGiaRepository;
    private final PhongRepository phongRepository;

    public DanhGiaService(DanhGiaRepository danhGiaRepository, PhongRepository phongRepository) {
        this.danhGiaRepository = danhGiaRepository;
        this.phongRepository = phongRepository;
    }

    public List<DanhGia> getDanhGiaNoiBat() {
        return danhGiaRepository.findTop6ByTrangThaiOrderByNgayDanhGiaDesc("Hiển thị");
    }

    @Transactional
    public void taoDanhGia(Integer maPhong, Integer maKhachHang, Integer maDatPhong, Integer soSao, String binhLuan) {
        if (soSao < 1 || soSao > 5) throw new IllegalArgumentException("Số sao từ 1 đến 5");

        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));

        DanhGia danhGia = new DanhGia();
        danhGia.setPhong(phong);

        KhachHang kh = new KhachHang();
        kh.setMaKH(maKhachHang);
        danhGia.setKhachHang(kh);

        if (maDatPhong != null) {
            PhieuDatPhong pdp = new PhieuDatPhong();
            pdp.setMaDatPhong(maDatPhong);
            danhGia.setPhieuDatPhong(pdp);
        }

        danhGia.setSoSao(soSao);
        danhGia.setBinhLuan(binhLuan);
        danhGia.setTrangThai("Hiển thị");

        danhGiaRepository.save(danhGia);
        Double avg = danhGiaRepository.getAverageRating(maPhong, "Hiển thị");
        phong.setDiemTrungBinh(avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO);

        Long count = danhGiaRepository.countReviews(maPhong, "Hiển thị");
        phong.setSoLuongDanhGia(count != null ? count.intValue() : 0);
        phongRepository.save(phong);
    }

    public DanhGiaDTO toDTO(DanhGia entity) {
        if (entity == null) return null;

        DanhGiaDTO dto = new DanhGiaDTO();
        dto.setMaDanhGia(entity.getMaDanhGia());
        dto.setSoSao(entity.getSoSao());
        dto.setBinhLuan(entity.getBinhLuan());
        dto.setNgayDanhGia(entity.getNgayDanhGia());

        if (entity.getKhachHang() != null) {
            dto.setTenKhachHang(entity.getKhachHang().getHoTen());
            dto.setHinhAnhKhach(entity.getKhachHang().getHinhAnh());
        }

        return dto;
    }

    public List<DanhGiaDTO> toDTOList(List<DanhGia> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}