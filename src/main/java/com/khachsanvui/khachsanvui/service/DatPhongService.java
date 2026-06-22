package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.DatPhongDTO;
import com.khachsanvui.khachsanvui.dto.HuyDonDTO;
import com.khachsanvui.khachsanvui.model.*;
import com.khachsanvui.khachsanvui.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DatPhongService {
    private final KhachHangRepository khachHangRepository;
    private final PhongRepository phongRepository;
    private final PhieuDatPhongRepository phieuDatPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final HoaDonService hoaDonService;
    private final KhuyenMaiService khuyenMaiService;

    public DatPhongService(KhachHangRepository khachHangRepository,
                           PhongRepository phongRepository,
                           PhieuDatPhongRepository phieuDatPhongRepository,
                           ChiTietDatPhongRepository chiTietDatPhongRepository,
                           HoaDonService hoaDonService,
                           KhuyenMaiService khuyenMaiService) {
        this.khachHangRepository = khachHangRepository;
        this.phongRepository = phongRepository;
        this.phieuDatPhongRepository = phieuDatPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.hoaDonService = hoaDonService;
        this.khuyenMaiService = khuyenMaiService;
    }

    @Transactional
    public HoaDon taoDatPhong(DatPhongDTO dto) {
        validate(dto);

        Phong phong = phongRepository.findById(dto.getMaPhong())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));

        KhachHang kh = khachHangRepository.findBySoDienThoai(dto.getSoDienThoai())
                .orElseGet(() -> {
                    KhachHang k = new KhachHang();
                    k.setHoTen(dto.getHoTen());
                    k.setGioiTinh(dto.getGioiTinh());
                    k.setSoDienThoai(dto.getSoDienThoai());
                    k.setEmail(dto.getEmail());
                    k.setCccd(dto.getCccd());
                    k.setDiaChi(dto.getDiaChi());
                    return khachHangRepository.save(k);
                });

        long soDem = ChronoUnit.DAYS.between(dto.getNgayNhan(), dto.getNgayTra());
        if (soDem <= 0) soDem = 1;

        BigDecimal tienPhongGoc = phong.getGiaPhong().multiply(BigDecimal.valueOf(soDem));

        BigDecimal thuePhi = tienPhongGoc.multiply(new BigDecimal("0.1"));
        BigDecimal tongTienTruocGiam = tienPhongGoc.add(thuePhi);

        BigDecimal soTienGiam = BigDecimal.ZERO;
        KhuyenMai kmDung = null;

        if (dto.getMaGiamGia() != null && !dto.getMaGiamGia().isBlank()) {
            var kmOpt = khuyenMaiService.findByMaCode(dto.getMaGiamGia().trim());
            if (kmOpt.isPresent()) {
                KhuyenMai km = kmOpt.get();

                if (km.getTrangThai() == null || !"Đang diễn ra".equalsIgnoreCase(km.getTrangThai().trim())) {
                    throw new IllegalArgumentException("Mã giảm giá đã hết hạn hoặc chưa được kích hoạt!");
                }

                boolean daSuDung = khuyenMaiService.kiemTraUserDaDungMa(kh.getMaKH(), km.getMaKhuyenMai());
                if (daSuDung) {
                    throw new IllegalArgumentException("Bạn đã sử dụng mã giảm giá này cho đơn đặt trước rồi!");
                }

                soTienGiam = km.tinhSoTienGiam(tongTienTruocGiam);
                kmDung = km;
            } else {
                throw new IllegalArgumentException("Mã giảm giá không tồn tại trên hệ thống!");
            }
        }

        PhieuDatPhong phieu = new PhieuDatPhong();
        phieu.setKhachHang(kh);
        phieu.setNgayNhan(dto.getNgayNhan());
        phieu.setNgayTra(dto.getNgayTra());
        phieu.setSoLuongPhong(1);
        phieu.setSoLuongKhach(dto.getSoLuongKhach());
        phieu.setGhiChu(dto.getGhiChu());
        phieu.setTrangThai("Chờ xác nhận");

        if (dto.getMaGiamGia() != null && !dto.getMaGiamGia().isBlank()) {
            phieu.setMaGiamGia(dto.getMaGiamGia().trim());
        }

        phieu = phieuDatPhongRepository.save(phieu);

        ChiTietDatPhong ct = new ChiTietDatPhong();
        ct.setPhieuDatPhong(phieu);
        ct.setPhong(phong);
        ct.setNgayNhan(dto.getNgayNhan());
        ct.setNgayTra(dto.getNgayTra());
        ct.setDonGia(phong.getGiaPhong());
        ct.setSoDem((int) soDem);
        ct.setThanhTien(tienPhongGoc);
        chiTietDatPhongRepository.save(ct);

        if (kmDung != null) {
            khuyenMaiService.ghiNhanSuDungMa(kh.getMaKH(), kmDung.getMaKhuyenMai());
        }

        return hoaDonService.taoHoaDonDatPhong(phieu, tongTienTruocGiam, soTienGiam);
    }

    private void validate(DatPhongDTO dto) {
        if (dto.getMaPhong() == null) throw new IllegalArgumentException("Vui lòng chọn phòng");
        if (dto.getNgayNhan() == null || dto.getNgayTra() == null)
            throw new IllegalArgumentException("Vui lòng chọn ngày nhận/trả phòng");
        if (dto.getNgayTra().isBefore(dto.getNgayNhan()))
            throw new IllegalArgumentException("Ngày trả phải sau ngày nhận");
        if (dto.getHoTen() == null || dto.getHoTen().isBlank())
            throw new IllegalArgumentException("Vui lòng nhập họ tên");
        if (dto.getSoDienThoai() == null || dto.getSoDienThoai().isBlank())
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại");
        if (dto.getSoLuongKhach() == null || dto.getSoLuongKhach() <= 0)
            throw new IllegalArgumentException("Số khách không hợp lệ");
    }

    public List<PhieuDatPhong> lichSuTheoKhachHang(Integer maKH) {
        return phieuDatPhongRepository.findWithChiTietAndPhongByKhachHang_MaKHOrderByNgayDatDesc(maKH);
    }

    public List<PhieuDatPhong> getAll() {
        return phieuDatPhongRepository.findAllByOrderByNgayDatDesc();
    }

    public PhieuDatPhong getById(Integer id) {
        return phieuDatPhongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng"));
    }

    public List<ChiTietDatPhong> getChiTiet(Integer maDatPhong) {
        return chiTietDatPhongRepository.findByPhieuDatPhong_MaDatPhong(maDatPhong);
    }

    @Transactional
    public void huyDon(Integer maDatPhong, HuyDonDTO dto) {
        PhieuDatPhong phieu = getById(maDatPhong);
        String lyDo = dto == null ? null : dto.getLyDoHuy();
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập lý do hủy đơn");
        }

        HoaDon hienTaiHD = hoaDonService.getByDatPhong(maDatPhong);
        if (hienTaiHD != null && "Đã thanh toán".equalsIgnoreCase(hienTaiHD.getTrangThaiThanhToan())) {
            phieu.setTrangThai("Yêu cầu hủy đơn");
            phieu.setLyDoHuy(lyDo.trim());
            phieuDatPhongRepository.save(phieu);
            return;
        }

        phieu.setTrangThai("Đã hủy");
        phieu.setLyDoHuy(lyDo.trim());
        phieu.setNgayHuy(LocalDateTime.now());
        phieuDatPhongRepository.save(phieu);

        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByPhieuDatPhong_MaDatPhong(maDatPhong);
        if (chiTiets != null) {
            for (ChiTietDatPhong ct : chiTiets) {
                Phong phong = ct.getPhong();
                if (phong != null) {
                    phong.setTrangThai("Trống");
                    phongRepository.save(phong);
                }
            }
        }
    }

    @Transactional
    public void capNhatTrangThaiVoiPhong(Integer maDatPhong, String trangThai) {
        PhieuDatPhong phieu = getById(maDatPhong);
        phieu.setTrangThai(trangThai);
        phieuDatPhongRepository.save(phieu);

        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByPhieuDatPhong_MaDatPhong(maDatPhong);
        if (chiTiets != null) {
            for (ChiTietDatPhong ct : chiTiets) {
                Phong phong = ct.getPhong();
                if (phong != null) {
                    if ("Đã hủy".equalsIgnoreCase(trangThai) || "Đã trả phòng".equalsIgnoreCase(trangThai)) {
                        phong.setTrangThai("Trống");
                    } else if ("Đã nhận phòng".equalsIgnoreCase(trangThai) || "Đang sử dụng".equalsIgnoreCase(trangThai)) {
                        phong.setTrangThai("Đang sử dụng");
                    } else {
                        phong.setTrangThai("Đã đặt");
                    }
                    phongRepository.save(phong);
                }
            }
        }
    }

    @Transactional
    public void capNhatTrangThai(Integer maDatPhong, String trangThai) {
        PhieuDatPhong phieu = getById(maDatPhong);
        phieu.setTrangThai(trangThai);
        phieuDatPhongRepository.save(phieu);
    }

    public byte[] convertHtmlToPdf(String htmlContent) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(htmlContent, "/");
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage());
        }
    }
}