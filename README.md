# KhachSanVui

## Mục lục

- [Giới thiệu](#giới-thiệu)
- [Chức năng chính](#chức-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Kiến trúc dự án](#kiến-trúc-dự-án)
- [Hướng dẫn cài đặt](#hướng-dẫn-cài-đặt)

---

## Giới thiệu

**KhachSanVui** là hệ thống quản lý và đặt phòng khách sạn được phát triển bằng **Java Spring Boot**. Hệ thống hỗ trợ khách hàng đặt phòng trực tuyến, đồng thời cung cấp các chức năng quản lý dành cho bộ phận vận hành khách sạn như quản lý phòng, khách hàng, dịch vụ, hóa đơn và nhân sự.

---

## Chức năng chính

### 👤 Dành cho khách hàng
- Đăng ký, đăng nhập và quản lý tài khoản.
- Đăng nhập bằng Google và Facebook (OAuth2).
- Xem, tìm kiếm và đặt phòng trực tuyến.
- Quản lý danh sách phòng yêu thích.
- Đánh giá phòng và dịch vụ sau khi lưu trú.
- Quản lý thông tin cá nhân.

### 🏨 Quản lý đặt phòng
- Tạo và quản lý đơn đặt phòng.
- Quản lý thông tin lưu trú của khách.
- Quản lý dịch vụ phát sinh trong thời gian lưu trú.

### 💳 Thanh toán
- Thanh toán trực tuyến qua VNPay Sandbox.
- Quản lý hóa đơn và trạng thái thanh toán.
- Gửi thông báo qua email.

### ⚙️ Quản trị hệ thống
- Quản lý phòng, loại phòng, dịch vụ và chi nhánh.
- Quản lý khách hàng, nhân viên và tài khoản.
- Quản lý chương trình khuyến mãi.
- Phân quyền người dùng theo vai trò.
- Thống kê và xuất báo cáo PDF/Excel.

---

## Công nghệ sử dụng

### Backend
- Java
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)

### Frontend
- Thymeleaf
- HTML5
- CSS3
- JavaScript

### Database
- SQL Server

### Khác
- OAuth2 (Google, Facebook)
- VNPay Sandbox
- Spring Mail
- Maven
- Git

---

## Kiến trúc dự án

Dự án được xây dựng theo mô hình **Layered Architecture**, giúp tách biệt xử lý giao diện, nghiệp vụ và truy cập dữ liệu, thuận tiện cho việc bảo trì và mở rộng hệ thống.

```text
src/main/java/com/khachsanvui/khachsanvui
│
├── config/                    # Cấu hình hệ thống
├── controller/                # Xử lý HTTP Request
├── dto/                       # Data Transfer Object
├── model/                     # Entity
├── repository/                # Truy cập dữ liệu
├── service/                   # Xử lý nghiệp vụ
│
└── KhachSanVuiApplication.java
```

### Luồng xử lý

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
SQL Server
```

---

## Hướng dẫn cài đặt

### 1. Clone dự án

```bash
git clone https://github.com/TheHungrier/KhachSanVui.git
```

### 2. Tạo cơ sở dữ liệu

- Tạo database **KhachSanVuiDB** trên SQL Server.
- Chạy file script SQL để tạo bảng và dữ liệu ban đầu.

### 3. Cấu hình `application.properties`

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=KhachSanVuiDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

Ngoài ra cần cấu hình:

- Google OAuth2 Client ID/Secret
- Facebook OAuth2 Client ID/Secret
- Mail Server
- VNPay Sandbox

### 4. Chạy dự án

Có thể chạy bằng IntelliJ IDEA hoặc Maven.

```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp lớp:

```text
KhachSanVuiApplication.java
```

### 5. Truy cập hệ thống

```text
http://localhost:8080
```