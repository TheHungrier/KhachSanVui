package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.KhachHang;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.KhachHangRepository;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final String UPLOAD_DIR = "uploads/avatars/";

    public KhachHangService(KhachHangRepository khachHangRepository, TaiKhoanRepository taiKhoanRepository) {
        this.khachHangRepository = khachHangRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public List<KhachHang> getAll() {
        return khachHangRepository.findAllActiveCustomers("Ngừng hoạt động");
    }

    public KhachHang getById(Integer id) {
        return khachHangRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng"));
    }

    public KhachHang findByTenDangNhap(String tenDangNhap) {
        return khachHangRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin khách hàng liên kết với tài khoản này"));
    }

    @Transactional
    public void save(KhachHang khachHang) {
        validate(khachHang);
        if (khachHang.getMaKH() == null) {
            khachHang.setNgayTao(LocalDateTime.now());
            khachHangRepository.save(khachHang);
            return;
        }

        KhachHang old = getById(khachHang.getMaKH());
        old.setHoTen(khachHang.getHoTen());
        old.setGioiTinh(khachHang.getGioiTinh());
        old.setSoDienThoai(khachHang.getSoDienThoai());
        old.setEmail(khachHang.getEmail());
        old.setCccd(khachHang.getCccd());
        old.setNgaySinh(khachHang.getNgaySinh());
        old.setDiaChi(khachHang.getDiaChi());
        if (khachHang.getHinhAnh() != null && !khachHang.getHinhAnh().isBlank()) {
            old.setHinhAnh(khachHang.getHinhAnh());
        }
        khachHangRepository.save(old);
    }

    @Transactional
    public void saveWithFile(KhachHang khachHang, MultipartFile fileAnh) throws IOException {
        if (fileAnh != null && !fileAnh.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + fileAnh.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(fileAnh.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            khachHang.setHinhAnh(fileName);
        }
        save(khachHang);
    }

    @Transactional
    public void delete(Integer id) {
        Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByKhachHang_MaKH(id);
        if (tkOpt.isPresent()) {
            TaiKhoan tk = tkOpt.get();
            tk.setTrangThai("Ngừng hoạt động");
            taiKhoanRepository.save(tk);
        } else {
            khachHangRepository.deleteById(id);
        }
    }

    private void validate(KhachHang khachHang) {
        if (khachHang.getHoTen() == null || khachHang.getHoTen().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập họ tên khách hàng");
        }
        if (khachHang.getHoTen().length() > 100) {
            throw new IllegalArgumentException("Họ tên không được vượt quá 100 ký tự");
        }
        if (khachHang.getGioiTinh() == null || khachHang.getGioiTinh().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn giới tính");
        }
        if (khachHang.getSoDienThoai() == null || khachHang.getSoDienThoai().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập số điện thoại");
        }
        if (khachHang.getSoDienThoai().length() > 15) {
            throw new IllegalArgumentException("Số điện thoại không được vượt quá 15 ký tự");
        }
        if (khachHang.getEmail() != null && khachHang.getEmail().length() > 100) {
            throw new IllegalArgumentException("Email không được vượt quá 100 ký tự");
        }
        if (khachHang.getCccd() != null && khachHang.getCccd().length() > 20) {
            throw new IllegalArgumentException("CCCD không được vượt quá 20 ký tự");
        }
        if (khachHang.getDiaChi() != null && khachHang.getDiaChi().length() > 200) {
            throw new IllegalArgumentException("Địa chỉ không được vượt quá 200 ký tự");
        }

        if (khachHang.getMaKH() == null) {
            if (khachHangRepository.existsBySoDienThoai(khachHang.getSoDienThoai())) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại trên hệ thống");
            }
            if (khachHang.getEmail() != null && !khachHang.getEmail().isBlank() && khachHangRepository.existsByEmail(khachHang.getEmail())) {
                throw new IllegalArgumentException("Email đã tồn tại trên hệ thống");
            }
            if (khachHang.getCccd() != null && !khachHang.getCccd().isBlank() && khachHangRepository.existsByCccd(khachHang.getCccd())) {
                throw new IllegalArgumentException("CCCD đã tồn tại trên hệ thống");
            }
        } else {
            KhachHang current = khachHangRepository.findById(khachHang.getMaKH()).orElse(null);
            if (current != null) {
                if (!current.getSoDienThoai().equals(khachHang.getSoDienThoai()) && khachHangRepository.existsBySoDienThoai(khachHang.getSoDienThoai())) {
                    throw new IllegalArgumentException("Số điện thoại đã bị tài khoản khác sử dụng");
                }
                if (khachHang.getEmail() != null && !khachHang.getEmail().isBlank() && (current.getEmail() == null || !current.getEmail().equals(khachHang.getEmail())) && khachHangRepository.existsByEmail(khachHang.getEmail())) {
                    throw new IllegalArgumentException("Email đã bị tài khoản khác sử dụng");
                }
                if (khachHang.getCccd() != null && !khachHang.getCccd().isBlank() && (current.getCccd() == null || !current.getCccd().equals(khachHang.getCccd())) && khachHangRepository.existsByCccd(khachHang.getCccd())) {
                    throw new IllegalArgumentException("CCCD đã bị tài khoản khác sử dụng");
                }
            }
        }
    }
}