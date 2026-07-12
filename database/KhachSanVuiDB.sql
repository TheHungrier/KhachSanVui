CREATE DATABASE KhachSanVuiDB;
GO
USE KhachSanVuiDB;
GO

-- =============================================
-- PHẦN 1: TẠO BẢNG
-- =============================================

-- 1. KhachHang
CREATE TABLE KhachHang (
    maKH INT IDENTITY(1,1) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai NVARCHAR(15) NOT NULL UNIQUE,
    cccd NVARCHAR(20) NULL UNIQUE,
    email NVARCHAR(100) NULL UNIQUE,
    ngaySinh DATE NULL,
    diaChi NVARCHAR(200) NULL,
    hinhAnh NVARCHAR(500) NULL,
    ngayTao DATETIME DEFAULT GETDATE()
);
GO

-- 2. NhanVien
CREATE TABLE NhanVien (
    maNV INT IDENTITY(1,1) PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    gioiTinh NVARCHAR(10) NOT NULL,
    soDienThoai NVARCHAR(15) NOT NULL UNIQUE,
    email NVARCHAR(100) NULL UNIQUE,
    chucVu NVARCHAR(50) NOT NULL,
    ngaySinh DATE NULL,
    diaChi NVARCHAR(200) NULL,
    hinhAnh NVARCHAR(500) NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang làm'
);
GO

-- 3. TaiKhoan
CREATE TABLE TaiKhoan (
    maTaiKhoan INT IDENTITY(1,1) PRIMARY KEY,
    tenDangNhap NVARCHAR(50) NOT NULL UNIQUE,
    matKhau NVARCHAR(255) NOT NULL,
    vaiTro NVARCHAR(50) NOT NULL CHECK (vaiTro IN ('ADMIN','LETAN','KHACHHANG')),
    trangThai NVARCHAR(50) DEFAULT N'Hoạt động',
    maNV INT NULL,
    maKH INT NULL,
    ngayTao DATETIME DEFAULT GETDATE(),
    provider NVARCHAR(50) NULL,
    providerId NVARCHAR(255) NULL,
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT FK_TaiKhoan_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH)
);
CREATE INDEX IX_TaiKhoan_ProviderId ON TaiKhoan(providerId);
GO

-- 4. ChiNhanh
CREATE TABLE ChiNhanh (
    maChiNhanh INT IDENTITY(1,1) PRIMARY KEY,
    tenChiNhanh NVARCHAR(100) NOT NULL,
    diaChi NVARCHAR(200) NOT NULL,
    moTa NVARCHAR(1000),
    hinhAnh NVARCHAR(255),
    viDo DECIMAL(10,7) NULL,
    kinhDo DECIMAL(10,7) NULL,
    trangThai NVARCHAR(50) DEFAULT N'Hiển thị'
);
GO

-- 5. DiaDiemDuLich
CREATE TABLE DiaDiemDuLich (
    maDiaDiemDL INT IDENTITY(1, 1) PRIMARY KEY,
    maChiNhanh INT NOT NULL,
    tenDiaDiem NVARCHAR(200) NOT NULL,
    moTa NVARCHAR(1000),
    hinhAnh NVARCHAR(255),
    viDo DECIMAL(10,7),
    kinhDo DECIMAL(10,7),
    trangThai NVARCHAR(50) DEFAULT N'Hiển thị',
    CONSTRAINT FK_DiaDiemDuLich_ChiNhanh FOREIGN KEY(maChiNhanh) REFERENCES ChiNhanh(maChiNhanh)
);

-- 6. LoaiPhong
CREATE TABLE LoaiPhong (
    maLoaiPhong INT IDENTITY(1,1) PRIMARY KEY,
    tenLoaiPhong NVARCHAR(50) NOT NULL UNIQUE,
    moTa NVARCHAR(200) NULL,
    sucChua INT NOT NULL DEFAULT 2 CHECK (sucChua > 0),
    hinhAnh NVARCHAR(255) NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang sử dụng'
);
GO

-- 7. Phong
CREATE TABLE Phong (
    maPhong INT IDENTITY(1,1) PRIMARY KEY,
    soPhong NVARCHAR(20) NOT NULL UNIQUE,
    maChiNhanh INT NOT NULL,
    maLoaiPhong INT NOT NULL,
    giaPhong DECIMAL(12,2) NOT NULL CHECK (giaPhong >= 0),
    sucChua INT NOT NULL DEFAULT 2,
    dienTich INT NULL,
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Trống',
    tienNghi NVARCHAR(500) NULL,
    moTa NVARCHAR(1000) NULL,
    hinhAnh NVARCHAR(255) NULL,
    diemTrungBinh DECIMAL(3,2) NULL,
    soLuongDanhGia INT DEFAULT 0,
    CONSTRAINT FK_Phong_LoaiPhong FOREIGN KEY (maLoaiPhong) REFERENCES LoaiPhong(maLoaiPhong),
    CONSTRAINT FK_Phong_ChiNhanh FOREIGN KEY (maChiNhanh) REFERENCES ChiNhanh(maChiNhanh)
);
GO

-- 8. PhieuDatPhong
CREATE TABLE PhieuDatPhong (
    maDatPhong INT IDENTITY(1,1) PRIMARY KEY,
    maKH INT NOT NULL,
    maNV INT NULL,
    ngayDat DATETIME DEFAULT GETDATE(),
    ngayNhan DATE NOT NULL,
    ngayTra DATE NOT NULL,
    soLuongPhong INT NOT NULL DEFAULT 1 CHECK (soLuongPhong > 0),
    soLuongKhach INT NOT NULL DEFAULT 1 CHECK (soLuongKhach > 0),
    ghiChu NVARCHAR(300) NULL,
    trangThai NVARCHAR(50) DEFAULT N'Chờ xác nhận',
    lyDoHuy NVARCHAR(300) NULL,
    ngayHuy DATETIME NULL,
    maGiamGia NVARCHAR(50) NULL,
    CONSTRAINT FK_PhieuDatPhong_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_PhieuDatPhong_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT CK_PhieuDatPhong_Ngay CHECK (ngayTra >= ngayNhan)
);
GO

-- 9. ChiTietDatPhong
CREATE TABLE ChiTietDatPhong (
    maChiTiet INT IDENTITY(1,1) PRIMARY KEY,
    maDatPhong INT NOT NULL,
    maPhong INT NOT NULL,
    ngayNhan DATE NOT NULL,
    ngayTra DATE NOT NULL,
    donGia DECIMAL(12,2) NOT NULL CHECK (donGia >= 0),
    soDem INT NOT NULL DEFAULT 1 CHECK (soDem > 0),
    thanhTien DECIMAL(12,2) NOT NULL DEFAULT 0 CHECK (thanhTien >= 0),
    CONSTRAINT FK_ChiTietDatPhong_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    CONSTRAINT FK_ChiTietDatPhong_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT CK_ChiTietDatPhong_Ngay CHECK (ngayTra >= ngayNhan)
);
GO

-- 10. HoSoLuuTru
CREATE TABLE HoSoLuuTru (
    maLuuTru INT IDENTITY(1,1) PRIMARY KEY,
    maKH INT NOT NULL,
    maPhong INT NOT NULL,
    maDatPhong INT NULL,
    maNV INT NULL,
    gioNhanPhong DATETIME NOT NULL DEFAULT GETDATE(),
    gioTraPhong DATETIME NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang lưu trú',
    CONSTRAINT FK_HoSoLuuTru_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_HoSoLuuTru_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT FK_HoSoLuuTru_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    CONSTRAINT FK_HoSoLuuTru_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO

-- 11. DichVu
CREATE TABLE DichVu (
    maDichVu INT IDENTITY(1,1) PRIMARY KEY,
    tenDichVu NVARCHAR(100) NOT NULL,
    loaiDichVu NVARCHAR(100) NULL,
    donGia DECIMAL(12,2) NOT NULL CHECK (donGia >= 0),
    moTa NVARCHAR(500) NULL,
    hinhAnh NVARCHAR(255) NULL,
    trangThai NVARCHAR(50) DEFAULT N'Đang cung cấp'
);
GO

-- 12. PhieuSuDungDV
CREATE TABLE PhieuSuDungDV (
    maSuDung INT IDENTITY(1,1) PRIMARY KEY,
    maLuuTru INT NOT NULL,
    maDichVu INT NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    thoiGianSuDung DATETIME DEFAULT GETDATE(),
    donGia DECIMAL(12,2) NOT NULL CHECK (donGia >= 0),
    thanhTien DECIMAL(12,2) NOT NULL CHECK (thanhTien >= 0),
    trangThai NVARCHAR(50) NOT NULL DEFAULT N'Chờ duyệt',
    CONSTRAINT FK_PhieuSuDungDV_HoSoLuuTru FOREIGN KEY (maLuuTru) REFERENCES HoSoLuuTru(maLuuTru),
    CONSTRAINT FK_PhieuSuDungDV_DichVu FOREIGN KEY (maDichVu) REFERENCES DichVu(maDichVu)
);
GO

-- 13. KhuyenMai
CREATE TABLE KhuyenMai (
    maKhuyenMai INT IDENTITY(1,1) PRIMARY KEY,
    tenKhuyenMai NVARCHAR(200) NOT NULL,
    moTa NVARCHAR(500) NULL,
    loaiGiamGia NVARCHAR(20) NOT NULL CHECK (loaiGiamGia IN ('PHAN_TRAM', 'SO_TIEN')),
    giaTriGiam DECIMAL(12,2) NOT NULL CHECK (giaTriGiam >= 0),
    apDungChoPhong BIT DEFAULT 1,
    apDungChoDichVu BIT DEFAULT 0,
    thoiGianBatDau DATETIME NOT NULL,
    thoiGianKetThuc DATETIME NOT NULL,
    soLuongPhongGioiHan INT NULL,
    soLuongDaDat INT DEFAULT 0,
    trangThai NVARCHAR(50) DEFAULT N'Đang diễn ra',
    hinhAnh NVARCHAR(255) NULL,
    ngayTao DATETIME DEFAULT GETDATE(),
    maCode NVARCHAR(255) NOT NULL
);
GO

-- 14. HoaDon
CREATE TABLE HoaDon (
    maHD INT IDENTITY(1,1) PRIMARY KEY,
    maDatPhong INT NULL,
    maLuuTru INT NULL,
    maNV INT NULL,
    maKhuyenMai INT NULL,
    tongTienPhong DECIMAL(12,2) DEFAULT 0,
    tongTienDV DECIMAL(12,2) DEFAULT 0,
    phuPhi DECIMAL(12,2) DEFAULT 0,
    giamGia DECIMAL(12,2) DEFAULT 0,
    tongTien DECIMAL(12,2) DEFAULT 0,
    ngayLap DATETIME DEFAULT GETDATE(),
    hinhThucThanhToan NVARCHAR(50) NULL,
    trangThaiThanhToan NVARCHAR(50) DEFAULT N'Chưa thanh toán',
    ghiChu NVARCHAR(300) NULL,
    CONSTRAINT FK_HoaDon_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong),
    CONSTRAINT FK_HoaDon_HoSoLuuTru FOREIGN KEY (maLuuTru) REFERENCES HoSoLuuTru(maLuuTru),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai)
);
GO

-- 15. ThanhToan
CREATE TABLE ThanhToan (
    maThanhToan INT IDENTITY(1,1) PRIMARY KEY,
    maHD INT NOT NULL,
    soTien DECIMAL(12,2) NOT NULL CHECK (soTien >= 0),
    phuongThuc NVARCHAR(50) NOT NULL,
    ngayThanhToan DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Chờ thanh toán',
    maGiaoDich NVARCHAR(100) NULL,
    ghiChu NVARCHAR(300) NULL,
    vnpTxnRef NVARCHAR(100) NULL,
    vnpTransactionNo NVARCHAR(100) NULL,
    vnpResponseCode NVARCHAR(20) NULL,
    vnpBankCode NVARCHAR(50) NULL,
    vnpPayDate NVARCHAR(50) NULL,
    CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD)
);
GO

-- 16. YeuThich
CREATE TABLE YeuThich (
    maYeuThich INT IDENTITY(1,1) PRIMARY KEY,
    maKH INT NOT NULL,
    maPhong INT NOT NULL,
    ngayTao DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_YeuThich_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_YeuThich_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
);
GO

-- 17. HinhAnhPhong
CREATE TABLE HinhAnhPhong (
    maAnh INT IDENTITY(1,1) PRIMARY KEY,
    maPhong INT NOT NULL,
    duongDanAnh NVARCHAR(255) NOT NULL,
    laAnhChinh BIT DEFAULT 0,
    CONSTRAINT FK_HinhAnhPhong_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong)
);
GO

-- 18. DanhGia
CREATE TABLE DanhGia (
    maDanhGia INT IDENTITY(1,1) PRIMARY KEY,
    maPhong INT NOT NULL,
    maKhachHang INT NOT NULL,
    maDatPhong INT NULL,
    soSao INT NOT NULL CHECK (soSao BETWEEN 1 AND 5),
    binhLuan NVARCHAR(500) NULL,
    ngayDanhGia DATETIME DEFAULT GETDATE(),
    trangThai NVARCHAR(50) DEFAULT N'Hiển thị',
    CONSTRAINT FK_DanhGia_Phong FOREIGN KEY (maPhong) REFERENCES Phong(maPhong),
    CONSTRAINT FK_DanhGia_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_DanhGia_PhieuDatPhong FOREIGN KEY (maDatPhong) REFERENCES PhieuDatPhong(maDatPhong)
);
GO

-- =============================================
-- INDEXES
-- =============================================
CREATE INDEX IX_Phong_MaLoaiPhong ON Phong(maLoaiPhong);
CREATE INDEX IX_Phong_TrangThai ON Phong(trangThai);
CREATE INDEX IX_PhieuDatPhong_MaKH ON PhieuDatPhong(maKH);
CREATE INDEX IX_PhieuDatPhong_TrangThai ON PhieuDatPhong(trangThai);
CREATE INDEX IX_ChiTietDatPhong_MaPhong ON ChiTietDatPhong(maPhong);
CREATE INDEX IX_HoaDon_MaDatPhong ON HoaDon(maDatPhong);
CREATE INDEX IX_ThanhToan_MaHD ON ThanhToan(maHD);
CREATE INDEX IX_KhuyenMai_ThoiGian ON KhuyenMai(thoiGianBatDau, thoiGianKetThuc);
CREATE INDEX IX_DanhGia_MaPhong ON DanhGia(maPhong);
CREATE INDEX IX_YeuThich_MaKH ON YeuThich(maKH);
GO

-- =============================================
-- PHẦN 2: DỮ LIỆU MẪU
-- =============================================

-- 1. KhachHang
INSERT INTO KhachHang (hoTen, gioiTinh, soDienThoai, cccd, email, diaChi) VALUES
(N'Lê Văn Khách', N'Nam', '0911111111', '079200000001', 'khach1@gmail.com', N'Quận 1, TP.HCM'),
(N'Phạm Thị Mai', N'Nữ', '0922222222', '079200000002', 'khach2@gmail.com', N'Quận 3, TP.HCM'),
(N'Nguyễn Hoài An', N'Nữ', '0933333333', '079200000003', 'khach3@gmail.com', N'Thủ Đức, TP.HCM'),
(N'Trần Văn Bình', N'Nam', '0944444444', '079200000004', 'binh@gmail.com', N'Quận 7, TP.HCM'),
(N'Lê Thị Cúc', N'Nữ', '0955555555', '079200000005', 'cuc@gmail.com', N'Quận 10, TP.HCM'),
(N'Hoàng Long Vũ', N'Nam', '0966666666', '079200000006', 'vuhoang@gmail.com', N'Đống Đa, Hà Nội'),
(N'Đỗ Diễm My', N'Nữ', '0977777777', '079200000007', 'mydiem@gmail.com', N'Hải Châu, Đà Nẵng'),
(N'Phan Văn Đức', N'Nam', '0988888888', '079200000008', 'ducphan@gmail.com', N'Ninh Kiều, Cần Thơ'),
(N'Bùi Minh Tuấn', N'Nam', '0999999999', '079200000009', 'tuanbui@gmail.com', N'Nha Trang, Khánh Hòa'),
(N'Vũ Thu Thảo', N'Nữ', '0901122334', '079200000010', 'thaovu@gmail.com', N'Vũng Tàu, Bà Rịa');
GO

-- 2. NhanVien
INSERT INTO NhanVien (hoTen, gioiTinh, soDienThoai, email, chucVu, ngaySinh, diaChi, trangThai) VALUES
(N'Nguyễn Hữu Tân', N'Nam', '0900000001', 'huutan1@gmail.com', N'Lễ tân', '2000-01-01', N'Gò Vấp, TP.HCM', N'Đang làm'),
(N'Trần Ngọc Lê', N'Nữ', '0900000002', 'ngocle1@gmail.com', N'Quản lý', '1995-05-10', N'Quận 12, TP.HCM', N'Đang làm'),
(N'Lê Hồng Thủy', N'Nữ', '0900000003', 'hongthuy2@gmail.com', N'Lễ tân', '1999-03-15', N'Hoàn Kiếm, Hà Nội', N'Đang làm'),
(N'Phạm Thị Hòa', N'Nữ', '0900000004', 'thihoa2@ksv.com', N'Lễ tân', '1990-07-20', N'Sơn Trà, Đà Nẵng', N'Đang làm'),
(N'Hoàng Long', N'Nam', '0900000005', 'hoanglong1@ksv.com', N'Lễ tân', '1985-11-30', N'Bình Thạnh, TP.HCM', N'Đang làm'),
(N'Nguyễn Trần Đức', N'Nam', '0900000006', 'ductran123@ksv.com', N'Lễ tân', '1993-02-14', N'Phú Nhuận, TP.HCM', N'Đang làm'),
(N'Đặng Long An', N'Nam', '0900000007', 'anlong123@ksv.com', N'Lễ tân', '1988-08-08', N'Quận Tân Bình, TP.HCM', N'Đang làm'),
(N'Vũ Thị Xuân', N'Nữ', '0900000008', 'thixuan123@ksv.com', N'Lễ tân', '1996-12-25', N'Thanh Xuân, Hà Nội', N'Đang làm'),
(N'Lý Vân Nam', N'Nam', '0900000009', 'vannam2@gmail.com', N'Lễ tân', '2001-10-10', N'Quận 5, TP.HCM', N'Đang làm'),
(N'Trần Bích Thủy', N'Nữ', '0900000010', 'bichthuy222@ksv.com', N'Quản lý', '1992-04-30', N'Quận 4, TP.HCM', N'Đang làm');
GO


-- 3. TaiKhoan
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, trangThai, maNV, maKH, provider, providerId) VALUES
('admin',  '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'ADMIN', N'Hoạt động', 2, NULL, NULL, NULL),
('letan',  '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'LETAN', N'Hoạt động', 1, NULL, NULL, NULL),
('letan2', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'LETAN', N'Hoạt động', 3, NULL, NULL, NULL),
('khach1', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 1, NULL, NULL),
('khach2', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 2, NULL, NULL),
('khach3', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 3, NULL, NULL),
('binh',   '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 4, NULL, NULL),
('cuc',    '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 5, NULL, NULL),
('google_user1', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 6, 'GOOGLE', '1029384756'),
('facebook_user1', '$2a$12$1PqWu/j.mYLdQmurDr4T8e6QiL9hRnUsXTID9lxgHw1DAyY0gf2JS', 'KHACHHANG', N'Hoạt động', NULL, 7, 'FACEBOOK', '5647382910')
GO

-- 4. ChiNhanh
INSERT INTO ChiNhanh (tenChiNhanh, diaChi, moTa, hinhAnh, viDo, kinhDo, trangThai) VALUES
(N'KhachSanVui Hà Nội', N'123 Phố Hàng Bông, Quận Hoàn Kiếm, Hà Nội', N'Chi nhánh KhachSanVui tại trung tâm Hà Nội, thuận tiện tham quan phố cổ.', 'branch-hanoi.jpg', 21.028511, 105.804817, N'Hiển thị'),
(N'KhachSanVui Hạ Long', N'88 Đường Hạ Long, Bãi Cháy, Quảng Ninh', N'Chi nhánh gần Vịnh Hạ Long, phù hợp nghỉ dưỡng.', 'branch-halong.jpg', 20.959902, 107.042542, N'Hiển thị'),
(N'KhachSanVui Đà Nẵng', N'25 Võ Nguyên Giáp, Sơn Trà, Đà Nẵng', N'Chi nhánh gần biển Mỹ Khê và Cầu Rồng.', 'branch-danang.jpg', 16.054456, 108.202308, N'Hiển thị'),
(N'KhachSanVui Đà Lạt', N'12 Trần Phú, TP. Đà Lạt', N'Chi nhánh nằm gần Hồ Xuân Hương.', 'branch-dalat.jpg', 11.940414, 108.458313, N'Hiển thị'),
(N'KhachSanVui Phú Quốc', N'45 Trần Hưng Đạo, Dương Đông, Phú Quốc', N'Chi nhánh gần biển và khu vui chơi.', 'branch-phuquoc.jpg', 10.289879, 103.984020, N'Hiển thị'),
(N'KhachSanVui Nha Trang', N'100 Trần Phú, TP. Nha Trang', N'Chi nhánh hướng biển với nhiều tiện ích.', 'branch-nhatrang.jpg', 12.238791, 109.196749, N'Hiển thị'),
(N'KhachSanVui Vũng Tàu', N'15 Thùy Vân, TP. Vũng Tàu', N'Chi nhánh gần Bãi Sau.', 'branch-vungtau.jpg',  10.345990, 107.084242, N'Hiển thị'),
(N'KhachSanVui Hội An', N'08 Nguyễn Thái Học, TP. Hội An', N'Chi nhánh ngay trung tâm phố cổ.', 'branch-hoian.jpg', 15.880058, 108.338047, N'Hiển thị'),
(N'KhachSanVui Sa Pa', N'50 Fansipan, Thị trấn Sa Pa, Lào Cai', N'Chi nhánh với tầm nhìn núi Fansipan.', 'branch-sapa.jpg', 22.336423, 103.843799, N'Hiển thị'),
(N'KhachSanVui Huế', N'20 Lê Lợi, TP. Huế', N'Chi nhánh gần Đại Nội và sông Hương.', 'branch-hue.jpg', 16.463713, 107.590866, N'Hiển thị');
GO

-- 5. DiaDiemDuLich
INSERT INTO DiaDiemDuLich (maChiNhanh, tenDiaDiem, moTa, hinhAnh, viDo, kinhDo, trangThai) VALUES
(1, N'Hà Nội', N'Thủ đô ngàn năm văn hiến, cổ kính và yên bình.', 'destination-hanoi.jpg', 21.028511, 105.804817, N'Hiển thị'),
(2, N'Hạ Long', N'Kỳ quan thiên nhiên thế giới với hàng ngàn đảo đá.', 'destination-halong.jpg', 20.959902, 107.042542, N'Hiển thị'),
(3, N'Đà Nẵng', N'Thành phố đáng sống với những bãi biển xanh cát trắng.', 'destination-danang.jpg', 16.054456, 108.202308, N'Hiển thị'),
(4, N'Đà Lạt', N'Thành phố sương mù mộng mơ, ngàn hoa khoe sắc rực rỡ.', 'destination-dalat.jpg', 11.940414, 108.458313, N'Hiển thị'),
(5, N'Phú Quốc', N'Đảo ngọc hoang sơ với những khu nghỉ dưỡng đẳng cấp.', 'destination-phuquoc.jpg', 10.289879, 103.984020, N'Hiển thị'),
(6, N'Nha Trang', N'Vịnh biển thiên đường thơ mộng, ngập tràn nắng gió.', 'destination-nhatrang.jpg', 12.238791, 109.196749, N'Hiển thị'),
(7, N'Vũng Tàu', N'Thành phố biển nhộn nhịp lý tưởng cho ngày cuối tuần.', 'destination-vungtau.jpg', 10.345990, 107.084242, N'Hiển thị'),
(8, N'Hội An', N'Phố cổ đèn lồng lãng mạn bên dòng sông Hoài phẳng lặng.', 'destination-hoian.jpg', 15.880058, 108.338047, N'Hiển thị'),
(9, N'Sa Pa', N'Thị trấn trong mây với ruộng bậc thang và đỉnh Fansipan.', 'destination-sapa.jpg', 22.336423, 103.843799, N'Hiển thị'),
(10, N'Huế', N'Cố đô thiêng liêng cổ kính với dòng sông Hương thơ mộng.', 'destination-hue.jpg', 16.463713, 107.590866, N'Hiển thị');
GO

-- 6. LoaiPhong
INSERT INTO LoaiPhong (tenLoaiPhong, moTa, sucChua, trangThai) VALUES
(N'Phòng Đơn Tiêu Chuẩn', N'Phòng ấm cúng dành cho khách đi một mình hoặc cặp đôi.', 2, N'Đang sử dụng'),
(N'Phòng Đôi Sang Trọng', N'Phòng rộng rãi view đẹp, đầy đủ trang thiết bị hiện đại.', 2, N'Đang sử dụng'),
(N'Phòng Gia Đình Ấm Cúng', N'Phòng diện tích lớn, thiết kế hoàn hảo cho gia đình nhỏ.', 4, N'Đang sử dụng'),
(N'Phòng VIP Tổng Thống', N'Không gian đẳng cấp hoàng gia, nội thất thượng hạng bậc nhất.', 2, N'Đang sử dụng'),
(N'Phòng Studio Tiện Nghi', N'Tích hợp quầy bếp mini cao cấp, thích hợp lưu trú dài ngày.', 2, N'Đang sử dụng'),
(N'Phòng Deluxe Hướng Biển', N'View trực diện đại dương xanh, đón bình minh rực rỡ.', 2, N'Đang sử dụng'),
(N'Phòng Suite Thượng Hạng', N'Phòng tiếp khách biệt lập sang trọng, không gian riêng tư.', 3, N'Đang sử dụng'),
(N'Phòng Tập Thể Dorm', N'Hệ thống giường tầng trẻ trung, tiết kiệm cho nhóm phượt.', 6, N'Đang sử dụng'),
(N'Phòng Bungalow Sân Vườn', N'Căn hộ nhà gỗ biệt lập, hòa mình cùng thiên nhiên cỏ cây.', 2, N'Đang sử dụng'),
(N'Phòng Penthouse Cao Cấp', N'Nằm tại tầng cao nhất của tòa nhà, sở hữu bể bơi vô cực riêng.', 4, N'Đang sử dụng');
GO

-- 7. Phong
INSERT INTO Phong (soPhong, maChiNhanh, maLoaiPhong, giaPhong, sucChua, dienTich, trangThai, tienNghi, moTa, hinhAnh, diemTrungBinh, soLuongDanhGia) VALUES
('101', 3, 1, 300000, 2, 22, N'Trống', N'Wifi, TV, Máy lạnh', N'Phòng đơn giá tốt tại Đà Nẵng.', 'room-standard.jpg', 4.50, 2),
('102', 3, 2, 500000, 2, 30, N'Đang sử dụng', N'Wifi, TV, Máy lạnh, Tủ lạnh', N'Phòng đôi rộng rãi view phố biển.', 'room-deluxe.jpg', 4.80, 3),
('103', 1, 1, 320000, 2, 24, N'Trống', N'Wifi, TV, Máy lạnh', N'Phòng đơn yên tĩnh giữa lòng Hà Nội.', 'room-standard.jpg', 4.20, 1),
('104', 4, 3, 750000, 4, 38, N'Trống', N'Wifi, TV, Máy lạnh, 2 Giường lớn', N'Phòng gia đình view đồi thông Đà Lạt.', 'room-family.jpg', 4.60, 1),
('105', 5, 4, 2500000, 2, 40, N'Trống', N'Wifi, TV, Máy lạnh, Bồn tắm, Minibar', N'Phòng VIP thượng hạng tại Phú Quốc.', 'room-premium.jpg', 4.90, 5),
('201', 3, 6, 900000, 2, 35, N'Trống', N'Ban công, View biển, Bồn tắm', N'Phòng Deluxe hướng biển đón gió mát.', 'room-sea.jpg', 4.70, 2),
('202', 2, 1, 280000, 2, 20, N'Trống', N'Wifi, TV, Quạt máy', N'Phòng đơn giá rẻ tham quan vịnh Hạ Long.', 'room-standard.jpg', 4.00, 1),
('301', 6, 3, 850000, 4, 42, N'Trống', N'Wifi, TV, Tủ lạnh, Bếp nấu ăn', N'Phòng gia đình tiện nghi tại Nha Trang.', 'room-family.jpg', 4.50, 0),
('302', 5, 9, 1200000, 2, 45, N'Bảo trì', N'Nhà gỗ, Sân vườn, Võng nằm', N'Bungalow sinh thái đang bảo dưỡng.', 'room-bungalow.jpg', NULL, 0),
('401', 1, 2, 480000, 2, 28, N'Trống', N'Wifi, TV, Máy sưởi', N'Phòng đôi ấm áp cho mùa đông Hà Nội.', 'room-deluxe.jpg', 4.30, 2);
GO

-- 8. PhieuDatPhong
INSERT INTO PhieuDatPhong (maKH, maNV, ngayDat, ngayNhan, ngayTra, soLuongPhong, soLuongKhach, ghiChu, trangThai, lyDoHuy, ngayHuy) VALUES
(1, 1, DATEADD(DAY, -10, GETDATE()), CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), 1, 2, N'Không hút thuốc', N'Đã hoàn thành', NULL, NULL),
(2, 1, DATEADD(DAY, -3, GETDATE()), CAST(GETDATE() AS DATE), CAST(DATEADD(DAY, 2, GETDATE()) AS DATE), 1, 2, N'Yêu cầu phòng tầng cao', N'Đã nhận phòng', NULL, NULL),
(3, NULL, DATEADD(DAY, -1, GETDATE()), CAST(DATEADD(DAY, 2, GETDATE()) AS DATE), CAST(DATEADD(DAY, 5, GETDATE()) AS DATE), 1, 1, N'Check in muộn', N'Chờ xác nhận', NULL, NULL),
(4, 2, DATEADD(DAY, -2, GETDATE()), CAST(DATEADD(DAY, 3, GETDATE()) AS DATE), CAST(DATEADD(DAY, 6, GETDATE()) AS DATE), 1, 4, N'Cần nệm phụ', N'Đã xác nhận', NULL, NULL),
(5, 3, DATEADD(DAY, -15, GETDATE()), CAST(DATEADD(DAY, -12, GETDATE()) AS DATE), CAST(DATEADD(DAY, -10, GETDATE()) AS DATE), 1, 2, N'', N'Đã hoàn thành', NULL, NULL),
(6, NULL, DATEADD(DAY, -5, GETDATE()), CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), CAST(DATEADD(DAY, -2, GETDATE()) AS DATE), 1, 2, N'', N'Đã hủy', N'Thay đổi lộ trình chuyến đi', DATEADD(DAY, -4, GETDATE())),
(7, 1, DATEADD(DAY, -1, GETDATE()), CAST(DATEADD(DAY, 5, GETDATE()) AS DATE), CAST(DATEADD(DAY, 7, GETDATE()) AS DATE), 1, 2, N'Kỷ niệm ngày cưới', N'Đã xác nhận', NULL, NULL),
(8, NULL, GETDATE(), CAST(DATEADD(DAY, 10, GETDATE()) AS DATE), CAST(DATEADD(DAY, 12, GETDATE()) AS DATE), 1, 2, N'', N'Chờ xác nhận', NULL, NULL),
(9, 2, DATEADD(DAY, -4, GETDATE()), CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), 1, 2, N'', N'Đã hoàn thành', NULL, NULL),
(10, 3, DATEADD(DAY, -6, GETDATE()), CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), 1, 2, N'', N'Đã hoàn thành', NULL, NULL);
GO

-- 9. ChiTietDatPhong
INSERT INTO ChiTietDatPhong (maDatPhong, maPhong, ngayNhan, ngayTra, donGia, soDem, thanhTien) VALUES
(1, 1, CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), 300000, 2, 600000),
(2, 2, CAST(GETDATE() AS DATE), CAST(DATEADD(DAY, 2, GETDATE()) AS DATE), 500000, 2, 1000000),
(3, 3, CAST(DATEADD(DAY, 2, GETDATE()) AS DATE), CAST(DATEADD(DAY, 5, GETDATE()) AS DATE), 320000, 3, 960000),
(4, 4, CAST(DATEADD(DAY, 3, GETDATE()) AS DATE), CAST(DATEADD(DAY, 6, GETDATE()) AS DATE), 750000, 3, 2250000),
(5, 7, CAST(DATEADD(DAY, -12, GETDATE()) AS DATE), CAST(DATEADD(DAY, -10, GETDATE()) AS DATE), 280000, 2, 560000),
(6, 6, CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), CAST(DATEADD(DAY, -2, GETDATE()) AS DATE), 900000, 2, 1800000),
(7, 6, CAST(DATEADD(DAY, 5, GETDATE()) AS DATE), CAST(DATEADD(DAY, 7, GETDATE()) AS DATE), 900000, 2, 1800000),
(8, 5, CAST(DATEADD(DAY, 10, GETDATE()) AS DATE), CAST(DATEADD(DAY, 12, GETDATE()) AS DATE), 2500000, 2, 5000000),
(9, 10, CAST(DATEADD(DAY, -3, GETDATE()) AS DATE), CAST(DATEADD(DAY, -1, GETDATE()) AS DATE), 480000, 2, 960000),
(10, 1, CAST(DATEADD(DAY, -5, GETDATE()) AS DATE), CAST(DATEADD(DAY, -4, GETDATE()) AS DATE), 300000, 1, 300000);
GO

-- 10. HoSoLuuTru
INSERT INTO HoSoLuuTru (maKH, maPhong, maDatPhong, maNV, gioNhanPhong, gioTraPhong, trangThai) VALUES
(1, 1, 1, 1, DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -3, GETDATE()), N'Đã trả phòng'),
(2, 2, 2, 1, GETDATE(), NULL, N'Đang lưu trú'),
(5, 7, 5, 3, DATEADD(DAY, -12, GETDATE()), DATEADD(DAY, -10, GETDATE()), N'Đã trả phòng'),
(9, 10, 9, 2, DATEADD(DAY, -3, GETDATE()), DATEADD(DAY, -1, GETDATE()), N'Đã trả phòng'),
(10, 1, 10, 3, DATEADD(DAY, -5, GETDATE()), DATEADD(DAY, -4, GETDATE()), N'Đã trả phòng'),
(3, 3, NULL, 1, DATEADD(DAY, -8, GETDATE()), DATEADD(DAY, -6, GETDATE()), N'Đã trả phòng'),
(4, 4, NULL, 2, DATEADD(DAY, -4, GETDATE()), DATEADD(DAY, -2, GETDATE()), N'Đã trả phòng'),
(6, 6, NULL, 1, DATEADD(DAY, -2, GETDATE()), DATEADD(DAY, -1, GETDATE()), N'Đã trả phòng'),
(7, 1, NULL, 3, DATEADD(DAY, -7, GETDATE()), DATEADD(DAY, -5, GETDATE()), N'Đã trả phòng'),
(8, 8, NULL, 2, DATEADD(DAY, -6, GETDATE()), DATEADD(DAY, -3, GETDATE()), N'Đã trả phòng');
GO

-- 11. DichVu
INSERT INTO DichVu (tenDichVu, loaiDichVu, donGia, moTa, hinhAnh, trangThai) VALUES
(N'Giặt ủi siêu tốc', N'Dịch vụ giặt là', 50000, N'Giặt, sấy khô và ủi phẳng quần áo lấy ngay trong 4 tiếng.', 'service-laundry.jpg', N'Đang cung cấp'),
(N'Buffet sáng cao cấp', N'Ẩm thực', 120000, N'Hơn 40 món ăn Á - Âu đa dạng, phục vụ từ 6:00 đến 9:30 hàng ngày.', 'service-breakfast.jpg', N'Đang cung cấp'),
(N'Thuê xe máy tay ga', N'Di chuyển', 150000, N'Xe tay ga đời mới, bao gồm 2 mũ bảo hiểm và bản đồ du lịch.', 'service-transport.jpg', N'Đang cung cấp'),
(N'Đưa đón sân bay 4 chỗ', N'Di chuyển', 250000, N'Xe ô tô 4 chỗ đời mới, đưa đón tận sảnh, tài xế lịch sự chu đáo.', 'service-transport.jpg', N'Đang cung cấp'),
(N'Massage Toàn Thân & Spa', N'Thư giãn', 400000, N'Liệu trình 60 phút massage tinh dầu đá nóng, thư giãn tuyệt đối.', 'service-spa.jpg', N'Đang cung cấp'),
(N'Nước ngọt lon Minibar', N'Ẩm thực', 250000, N'Các loại nước ngọt setup sẵn trong tủ lạnh phòng (Tính theo lon).', 'service-drink.jpg', N'Đang cung cấp'),
(N'Thuê xe ô tô tự lái', N'Di chuyển', 800000, N'Thuê ô tô 7 chỗ tự lái theo ngày, thủ tục nhanh gọn.', 'service-car.jpg', N'Đang cung cấp'),
(N'Họp báo & Hội nghị', N'Sự kiện', 2000000, N'Thuê phòng hội trường có máy chiếu, âm thanh, nước suối.', 'service-event.jpg', N'Đang cung cấp'),
(N'Trang trí phòng tuần trăng mật', N'Dịch vụ phòng', 300000, N'Trang trí giường thiên nga hoa hồng, bóng bay và nến lãng mạn.', 'service-decor.jpg', N'Đang cung cấp'),
(N'Sử dụng phòng Gym/Pool', N'Giải trí', 0, N'Miễn phí hoàn toàn cho mọi khách lưu trú tại hệ thống khách sạn.', 'service-gym.jpg', N'Đang cung cấp');
GO

-- 12. PhieuSuDungDV
INSERT INTO PhieuSuDungDV (maLuuTru, maDichVu, soLuong, thoiGianSuDung, donGia, thanhTien, trangThai) VALUES
(1, 2, 2, DATEADD(DAY, -4, GETDATE()), 120000, 240000, N'Đã duyệt'),
(1, 1, 1, DATEADD(DAY, -4, GETDATE()), 50000, 50000, N'Đã duyệt'),
(2, 6, 3, GETDATE(), 25000, 75000, N'Đã duyệt'),
(2, 3, 1, DATEADD(HOUR, 2, GETDATE()), 150000, 150000, N'Đã duyệt'),
(3, 2, 2, DATEADD(DAY, -11, GETDATE()), 120000, 240000, N'Đã duyệt'),
(4, 4, 1, DATEADD(DAY, -2, GETDATE()), 250000, 250000, N'Đã duyệt'),
(6, 2, 1, DATEADD(DAY, -7, GETDATE()), 120000, 120000, N'Đã duyệt'),
(7, 5, 1, DATEADD(DAY, -3, GETDATE()), 400000, 400000, N'Đã duyệt'),
(10, 2, 2, DATEADD(DAY, -5, GETDATE()), 120000, 240000, N'Đã duyệt'),
(10, 9, 1, DATEADD(DAY, -5, GETDATE()), 300000, 300000, N'Đã duyệt');
GO

-- 13. KhuyenMai
INSERT INTO KhuyenMai (tenKhuyenMai, moTa, loaiGiamGia, giaTriGiam, apDungChoPhong, apDungChoDichVu, thoiGianBatDau, thoiGianKetThuc, soLuongPhongGioiHan, soLuongDaDat, trangThai, hinhAnh, maCode) VALUES
(N'Flash Sale cuối tuần', N'Giảm ngay 20% tiền phòng cho khách săn phòng Deluxe vào ngày thứ 7 và chủ nhật.', 'PHAN_TRAM', 20.00, 1, 0, DATEADD(HOUR, -2, GETDATE()), DATEADD(HOUR, 22, GETDATE()), 5, 1, N'Đang diễn ra', 'flash-deluxe.jpg', 'FLASHSALE'),
(N'Chào hè rực rỡ', N'Giảm thẳng 100.000 VND cho đơn đặt phòng từ 2 đêm trở lên.', 'SO_TIEN', 100000.00, 1, 0, DATEADD(DAY, -30, GETDATE()), DATEADD(DAY, 30, GETDATE()), 100, 25, N'Đang diễn ra', 'promo-summer.jpg', 'CHAOHE2026'),
(N'Tri ân khách hàng thân thiết', N'Giảm 10% tổng hóa đơn phòng cho khách hàng cũ.', 'PHAN_TRAM', 10.00, 1, 0, '2026-01-01', '2026-12-31', 500, 142, N'Đang diễn ra', 'promo-vip.jpg', 'KSVVIP'),
(N'Ưu đãi đặt sớm 15 ngày', N'Giảm 15% cho quý khách lên kế hoạch đi chơi đặt phòng trước nửa tháng.', 'PHAN_TRAM', 15.00, 1, 0, '2026-01-01', '2026-06-30', 50, 12, N'Đang diễn ra', 'promo-early.jpg', 'EARLY15'),
(N'Giảm giá dịch vụ Spa', N'Giảm 50.000 VND trực tiếp khi đăng ký combo chăm sóc sức khỏe massage đá nóng.', 'SO_TIEN', 50000.00, 0, 1, '2026-05-01', '2026-08-31', 200, 50, N'Đang diễn ra', 'promo-spa.jpg', 'SPARELAX'),
(N'Mừng ngày lễ lớn 30/4', N'Giảm 25% giá tất cả các hạng phòng đặt trong dịp lễ.', 'PHAN_TRAM', 25.00, 1, 0, '2026-04-25', '2026-05-05', 20, 20, N'Đã kết thúc', 'promo-holiday.jpg', 'LE3004'),
(N'Khai xuân đón lộc', N'Lì xì ngay 50.000 VND tiền phòng cho khách đặt đầu năm.', 'SO_TIEN', 50000.00, 1, 0, '2026-02-01', '2026-02-28', 100, 100, N'Đã kết thúc', 'promo-tet.jpg', 'XUAN2026'),
(N'Thứ 4 vui vẻ cùng KSV', N'Flash sale cố định giữa tuần giảm giá 12% mọi loại phòng.', 'PHAN_TRAM', 12.00, 1, 0, '2026-01-01', '2026-12-31', 10, 4, N'Đang diễn ra', 'promo-wed.jpg', 'HAPPYWED'),
(N'Ưu đãi mùa thu lãng mạn', N'Giảm 15% cho phòng đôi tại khu vực Đà Lạt thơ mộng.', 'PHAN_TRAM', 15.00, 1, 0, '2026-09-01', '2026-11-30', 30, 0, N'Chưa diễn ra', 'promo-autumn.jpg', 'AUTUMN2026'),
(N'Săn deal giờ chót', N'Giảm 30% cho phòng trống phát sinh trong ngày.', 'PHAN_TRAM', 30.00, 1, 0, '2026-06-01', '2026-07-31', 5, 2, N'Đang diễn ra', 'promo-lastminute.jpg', 'LASTMINUTE');
GO

-- 14. HoaDon
INSERT INTO HoaDon (maDatPhong, maLuuTru, maNV, maKhuyenMai, tongTienPhong, tongTienDV, phuPhi, giamGia, tongTien, ngayLap, trangThaiThanhToan, ghiChu) VALUES
(1, 1, 1, NULL, 600000, 290000, 0, 0, 890000, DATEADD(DAY, -3, GETDATE()), N'Đã thanh toán', N'Khách đã checkout xuôi sẻ'),
(2, 2, 1, 1, 1000000, 225000, 50000, 200000, 1075000, GETDATE(), N'Chưa thanh toán', N'Đang lưu trú, tiền DV tạm tính'),
(5, 3, 3, 2, 560000, 240000, 0, 100000, 700000, DATEADD(DAY, -10, GETDATE()), N'Đã thanh toán', N''),
(9, 4, 2, NULL, 960000, 0, 0, 0, 960000, DATEADD(DAY, -1, GETDATE()), N'Đã thanh toán', N''),
(10, 5, 3, NULL, 300000, 540000, 20000, 0, 860000, DATEADD(DAY, -4, GETDATE()), N'Đã thanh toán', N''),
(NULL, 6, 1, NULL, 640000, 0, 0, 0, 640000, DATEADD(DAY, -6, GETDATE()), N'Đã thanh toán', N'Khách vãng lai'),
(NULL, 7, 2, NULL, 1500000, 250000, 0, 0, 1750000, DATEADD(DAY, -2, GETDATE()), N'Đã thanh toán', N'Khách vãng lai'),
(NULL, 8, 1, NULL, 900000, 120000, 0, 0, 1020000, DATEADD(DAY, -1, GETDATE()), N'Đã thanh toán', N'Khách vãng lai'),
(NULL, 9, 3, NULL, 560000, 400000, 0, 0, 960000, DATEADD(DAY, -5, GETDATE()), N'Đã thanh toán', N'Khách vãng lai'),
(NULL, 10, 2, NULL, 2400000, 0, 100000, 0, 2500000, DATEADD(DAY, -3, GETDATE()), N'Đã thanh toán', N'Khách vãng lai');
GO

-- 15. ThanhToan
INSERT INTO ThanhToan (maHD, soTien, phuongThuc, ngayThanhToan, trangThai, maGiaoDich, ghiChu, vnpTxnRef, vnpTransactionNo, vnpResponseCode, vnpBankCode, vnpPayDate) VALUES
(1, 890000, N'Tiền mặt', DATEADD(DAY, -3, GETDATE()), N'Thành công', 'TM-001', N'Khách thanh toán mặt', NULL, NULL, NULL, NULL, NULL),
(2, 1075000, N'Chuyển khoản VNPay', NULL, N'Chờ thanh toán', NULL, N'Chờ khách quét mã QR', 'KSV2026061601', NULL, NULL, NULL, NULL),
(3, 700000, N'Thẻ tín dụng', DATEADD(DAY, -10, GETDATE()), N'Thành công', 'POS-8938', N'Quẹt thẻ tại quầy', NULL, NULL, NULL, NULL, NULL),
(4, 960000, N'Chuyển khoản VNPay', DATEADD(DAY, -1, GETDATE()), N'Thành công', 'VNP-998822', N'Thanh toán qua app', 'KSV2026061502', '14567839', '00', 'NCB', '20260615143022'),
(5, 860000, N'Tiền mặt', DATEADD(DAY, -4, GETDATE()), N'Thành công', 'TM-002', N'', NULL, NULL, NULL, NULL, NULL),
(6, 640000, N'Tiền mặt', DATEADD(DAY, -6, GETDATE()), N'Thành công', 'TM-003', N'', NULL, NULL, NULL, NULL, NULL),
(7, 1750000, N'Chuyển khoản VNPay', DATEADD(DAY, -2, GETDATE()), N'Thành công', 'VNP-998825', N'', 'KSV2026061405', '14567990', '00', 'VCB', '20260614091511'),
(8, 1020000, N'Tiền mặt', DATEADD(DAY, -1, GETDATE()), N'Thành công', 'TM-004', N'', NULL, NULL, NULL, NULL, NULL),
(9, 960000, N'Tiền mặt', DATEADD(DAY, -5, GETDATE()), N'Thành công', 'TM-005', N'', NULL, NULL, NULL, NULL, NULL),
(10, 2500000, N'Chuyển khoản VNPay', DATEADD(DAY, -3, GETDATE()), N'Thành công', 'VNP-998829', N'', 'KSV2026061309', '14568112', '00', 'AGRIBANK', '20260613182240');
GO

-- 16. YeuThich
INSERT INTO YeuThich (maKH, maPhong) VALUES
(1, 5), (2, 2), (3, 5), (4, 4), (5, 6),
(6, 2), (7, 4), (8, 5), (9, 6), (10, 10);
GO

-- 17. HinhAnhPhong
INSERT INTO HinhAnhPhong (maPhong, duongDanAnh, laAnhChinh) VALUES
(1, 'room101_1.jpg', 1),
(1, 'room101_2.jpg', 0),
(2, 'room102_1.jpg', 1),
(2, 'room102_2.jpg', 0),
(3, 'room103_1.jpg', 1),
(4, 'room104_1.jpg', 1),
(5, 'room105_1.jpg', 1),
(6, 'room201_1.jpg', 1),
(7, 'room202_1.jpg', 1),
(10, 'room401_1.jpg', 1);
GO

-- 18. DanhGia
INSERT INTO DanhGia (maPhong, maKhachHang, maDatPhong, soSao, binhLuan, ngayDanhGia, trangThai) VALUES
(1, 1, 1,    5, N'Phòng sạch sẽ, gọn gàng, lễ tân hỗ trợ nhiệt tình!',        DATEADD(DAY, -4, GETDATE()), N'Hiển thị'),
(2, 2, 2,    4, N'Phòng đôi siêu rộng, đầy đủ tiện nghi, view phố biển rất thoáng.', DATEADD(DAY, -1, GETDATE()), N'Hiển thị'),
(7, 5, 5,    4, N'Giá cả phải chăng, phòng ốc bài trí hợp lý sạch đẹp.',     DATEADD(DAY, -9, GETDATE()), N'Hiển thị'),
(10, 9, 9,   4, N'Giường nằm êm ái, cách âm khá tốt, sẽ quay lại lần sau.',   DATEADD(DAY, -1, GETDATE()), N'Hiển thị'),
(1, 10, 10,  4, N'Khách sạn gần trung tâm đi lại rất tiện lợi.',             DATEADD(DAY, -3, GETDATE()), N'Hiển thị'),
(3, 3, NULL, 4, N'Phòng đơn yên tĩnh phù hợp đi công tác.',                  DATEADD(DAY, -5, GETDATE()), N'Hiển thị'),
(4, 4, NULL, 5, N'Phòng rộng, gia đình mình ở rất thoải mái.',              DATEADD(DAY, -1, GETDATE()), N'Hiển thị'),
(6, 6, NULL, 5, N'View trực diện biển siêu xịn sò luôn nha mng.',           DATEADD(DAY, -1, GETDATE()), N'Hiển thị'),
(1, 7, NULL, 4, N'Dịch vụ tốt, thủ tục nhận phòng nhanh chóng.',            DATEADD(DAY, -4, GETDATE()), N'Hiển thị'),
(5, 8, NULL, 5, N'Phòng VIP đỉnh cao, bồn tắm nằm cực kỳ thư giãn.',        DATEADD(DAY, -2, GETDATE()), N'Hiển thị');
GO

SELECT TOP 10 * FROM TaiKhoan