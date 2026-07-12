package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.PhongDTO;
import com.khachsanvui.khachsanvui.model.ChiNhanh;
import com.khachsanvui.khachsanvui.model.HinhAnhPhong;
import com.khachsanvui.khachsanvui.model.LoaiPhong;
import com.khachsanvui.khachsanvui.model.Phong;
import com.khachsanvui.khachsanvui.repository.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhongService {

    private static final String TRANG_THAI_TRONG = "Trống";
    private final PhongRepository phongRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final ChiNhanhRepository chiNhanhRepository;
    private final HinhAnhPhongRepository hinhAnhPhongRepository;
    private final String UPLOAD_DIR = "uploads/avatars/";

    public List<Phong> getAll() {
        return phongRepository.findAll();
    }

    public List<Phong> getPhongTrong() {
        return phongRepository.findByTrangThai(TRANG_THAI_TRONG);
    }

    public List<Phong> getPhongNoiBat() {
        List<Phong> phongTrong = phongRepository.findByTrangThai(TRANG_THAI_TRONG);
        if (phongTrong == null || phongTrong.isEmpty()) {
            phongTrong = phongRepository.findTop6ByTrangThaiOrderByMaPhongDesc(TRANG_THAI_TRONG);
        }
        return phongTrong.isEmpty() ? phongRepository.findTop6ByOrderByMaPhongDesc() : phongTrong;
    }

    public List<LoaiPhong> getLoaiPhong() {
        return loaiPhongRepository.findAll();
    }

    public Phong getById(Integer id) {
        return phongRepository.findById(id).orElse(null);
    }

    public List<Phong> findPhongAvanced(String keyword, Integer maChiNhanh, Integer maLoaiPhong, Integer soKhach,
                                        LocalDate ngayNhan, LocalDate ngayTra,
                                        String gia, String danhGiaCao, List<String> tienNghi,
                                        String sortBy) {

        String normalizedKeyword = keyword == null ? null : keyword.trim().toLowerCase(Locale.ROOT);

        List<Phong> allPhongs;
        if (keyword != null && !keyword.isBlank()) {
            allPhongs = phongRepository.findPhong(keyword.trim());
        } else {
            allPhongs = phongRepository.findAll();
        }
        for (Phong phong : allPhongs) {
            Double diemSaoTrungBinhHe5 = danhGiaRepository.getAverageRating(phong.getMaPhong(), "Hiển thị");
            double diemHe10 = (diemSaoTrungBinhHe5 != null) ? diemSaoTrungBinhHe5 * 2 : 0.0;

            Long soLuongHienThi = danhGiaRepository.countReviews(phong.getMaPhong(), "Hiển thị");

            phong.setDiemTrungBinh(java.math.BigDecimal.valueOf(diemHe10));
            phong.setSoLuongDanhGia(soLuongHienThi != null ? soLuongHienThi.intValue() : 0);
        }

        Comparator<Phong> comparator = getPhongComparator(sortBy);

        return allPhongs.stream()
                .filter(phong -> TRANG_THAI_TRONG.equalsIgnoreCase(phong.getTrangThai()))
                .filter(phong -> maChiNhanh == null || (phong.getChiNhanh() != null && maChiNhanh.equals(phong.getChiNhanh().getMaChiNhanh())))
                .filter(phong -> maLoaiPhong == null || (phong.getLoaiPhong() != null && maLoaiPhong.equals(phong.getLoaiPhong().getMaLoaiPhong())))
                .filter(phong -> soKhach == null || phong.getSucChua() == null || phong.getSucChua() >= soKhach)
                .filter(phong -> normalizedKeyword == null || normalizedKeyword.isBlank() || matchesKeyword(phong, normalizedKeyword))
                .filter(phong -> {
                    if (gia == null || "all".equalsIgnoreCase(gia) || gia.isBlank()) return true;
                    double giaPhong = phong.getGiaPhong() != null ? phong.getGiaPhong().doubleValue() : 0.0;

                    return switch (gia) {
                        case "duoi-500" -> giaPhong < 500000;
                        case "500-1000" -> giaPhong >= 500000 && giaPhong <= 1000000;
                        case "tren-1000" -> giaPhong > 1000000;
                        default -> true;
                    };
                })
                .filter(phong -> {
                    if (dActive(danhGiaCao)) return true;
                    double diem = phong.getDiemTrungBinh() != null ? phong.getDiemTrungBinh().doubleValue() : 0.0;
                    return diem >= 9.0;
                })
                .filter(phong -> {
                    if (tienNghi == null || tienNghi.isEmpty()) return true;
                    return tienNghi.stream().allMatch(tn -> phong.getTienNghi() != null && phong.getTienNghi().toLowerCase(Locale.ROOT).contains(tn.toLowerCase(Locale.ROOT)));
                })
                .filter(phong -> {
                    if (ngayNhan == null || ngayTra == null) return true;
                    if (!ngayNhan.isBefore(ngayTra)) return false;
                    return !chiTietDatPhongRepository.isRoomBooked(phong.getMaPhong(), ngayNhan, ngayTra);
                })
                .sorted(comparator)
                .toList();
    }

    private boolean dActive(String val) {
        return val == null || val.isBlank();
    }

    private static @NonNull Comparator<Phong> getPhongComparator(String sortBy) {
        Comparator<Phong> comparator = (p1, p2) -> 0;

        if ("giaTang".equals(sortBy)) {
            comparator = Comparator.comparing(Phong::getGiaPhong, Comparator.nullsLast(Comparator.naturalOrder()));
        } else if ("giaGiam".equals(sortBy)) {
            comparator = Comparator.comparing(Phong::getGiaPhong, Comparator.nullsLast(Comparator.reverseOrder()));
        } else if ("diemGiam".equals(sortBy)) {
            comparator = Comparator.comparing(Phong::getDiemTrungBinh, Comparator.nullsLast(Comparator.reverseOrder()));
        }
        return comparator;
    }

    public PhongDTO toDTO(Phong phong) {
        PhongDTO dto = new PhongDTO();
        if (phong == null) return dto;
        dto.setMaPhong(phong.getMaPhong());
        dto.setSoPhong(phong.getSoPhong());
        dto.setMaLoaiPhong(phong.getLoaiPhong() == null ? null : phong.getLoaiPhong().getMaLoaiPhong());
        dto.setMaChiNhanh(phong.getChiNhanh() == null ? null : phong.getChiNhanh().getMaChiNhanh());
        dto.setGiaPhong(phong.getGiaPhong());
        dto.setSucChua(phong.getSucChua());
        dto.setDienTich(phong.getDienTich());
        dto.setTrangThai(phong.getTrangThai());
        dto.setTienNghi(phong.getTienNghi());
        dto.setMoTa(phong.getMoTa());
        dto.setHinhAnh(phong.getHinhAnh());
        if (phong.getLoaiPhong() != null) {
            dto.setTenLoaiPhong(phong.getLoaiPhong().getTenLoaiPhong());
        }
        return dto;
    }

    @Transactional
    public void saveRoomWithImages(PhongDTO dto, MultipartFile[] filesAnh) throws IOException {
        Phong phong = dto.getMaPhong() == null ? new Phong() : phongRepository.findById(dto.getMaPhong()).orElse(new Phong());
        phong.setSoPhong(dto.getSoPhong());
        phong.setLoaiPhong(dto.getMaLoaiPhong() == null ? null : loaiPhongRepository.findById(dto.getMaLoaiPhong()).orElse(null));
        phong.setChiNhanh(dto.getMaChiNhanh() == null ? null : chiNhanhRepository.findById(dto.getMaChiNhanh()).orElse(null));
        phong.setGiaPhong(dto.getGiaPhong());
        phong.setSucChua(dto.getSucChua());
        phong.setDienTich(dto.getDienTich());
        phong.setTrangThai(dto.getTrangThai());
        phong.setTienNghi(dto.getTienNghi());
        phong.setMoTa(dto.getMoTa());

        if (dto.getMaPhong() == null) {
            phong = phongRepository.save(phong);
        }

        boolean uniqueMainImage = true;
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : filesAnh) {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                HinhAnhPhong hinhAnhPhong = new HinhAnhPhong();
                hinhAnhPhong.setPhong(phong);
                hinhAnhPhong.setDuongDanAnh(fileName);

                if (uniqueMainImage && (phong.getHinhAnh() == null || phong.getHinhAnh().isBlank())) {
                    hinhAnhPhong.setLaAnhChinh(true);
                    phong.setHinhAnh(fileName);
                    uniqueMainImage = false;
                } else {
                    hinhAnhPhong.setLaAnhChinh(false);
                }
                hinhAnhPhongRepository.save(hinhAnhPhong);
            }
        }

        if (dto.getMaPhong() != null) {
            if (phong.getHinhAnh() == null || phong.getHinhAnh().isBlank()) {
                phong.setHinhAnh(dto.getHinhAnh());
            }
        }
        phongRepository.save(phong);
    }

    public void delete(Integer id) {
        phongRepository.deleteById(id);
    }

    private boolean matchesKeyword(Phong phong, String keyword) {
        return contains(phong.getSoPhong(), keyword) || contains(phong.getTienNghi(), keyword) || contains(phong.getMoTa(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    public String getGoogleMapEmbedUrl(Phong phong) {
        if (phong == null || phong.getChiNhanh() == null) return null;
        ChiNhanh cn = phong.getChiNhanh();
        if (cn.getViDo() == null || cn.getKinhDo() == null) return null;
        return "https://www.google.com/maps/embed?pb=!1m14!1m12!1m3!1d10000!2d" + cn.getKinhDo() + "!3d" + cn.getViDo() + "!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!5e0!3m2!1svi!2s!4v" + System.currentTimeMillis();
    }
}