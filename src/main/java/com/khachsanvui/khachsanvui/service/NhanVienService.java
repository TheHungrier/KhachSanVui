package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.NhanVien;
import com.khachsanvui.khachsanvui.model.TaiKhoan;
import com.khachsanvui.khachsanvui.repository.NhanVienRepository;
import com.khachsanvui.khachsanvui.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public NhanVienService(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public List<NhanVien> getAll() {
        return nhanVienRepository.findByTrangThai("Đang làm");
    }

    public NhanVien getById(Integer id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin nhân viên"));
    }

    @Transactional
    public void delete(Integer id) {
        NhanVien nv = getById(id);
        nv.setTrangThai("Đã nghỉ việc");
        nhanVienRepository.save(nv);
        List<TaiKhoan> getAllTaiKhoan = taiKhoanRepository.findAll();
        for (TaiKhoan tk : getAllTaiKhoan) {
            if (tk.getNhanVien() != null && id.equals(tk.getNhanVien().getMaNV())) {
                tk.setTrangThai("Ngừng hoạt động");
                taiKhoanRepository.save(tk);
                break;
            }
        }
    }
}