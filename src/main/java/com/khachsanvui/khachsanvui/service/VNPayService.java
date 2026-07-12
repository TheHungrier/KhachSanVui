package com.khachsanvui.khachsanvui.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {
    @Value("${vnpay.tmn-code:DEMO}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret:DEMO_SECRET}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url:http://localhost:8080/thanh-toan/vnpay-return}")
    private String vnpReturnUrl;

    public String taoUrlThanhToan(Integer maHD, BigDecimal amount, HttpServletRequest request) {
        String txnRef = "KSVHD_" + maHD + "_" + System.currentTimeMillis();
        String ipAddr = getIpAddress(request);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(cld.getTime());

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf(amount.multiply(BigDecimal.valueOf(100)).longValue()));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan hoa don KhachSanVui #" + maHD);
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", ipAddr);
        params.put("vnp_CreateDate", createDate);
        params.put("vnp_ExpireDate", expireDate);

        return buildPaymentUrl(params);
    }

    public String layTxnRefTuUrl(String paymentUrl) {
        int idx = paymentUrl.indexOf("vnp_TxnRef=");
        if (idx < 0) return null;
        String sub = paymentUrl.substring(idx + "vnp_TxnRef=".length());
        int amp = sub.indexOf("&");
        return amp >= 0 ? sub.substring(0, amp) : sub;
    }

    public boolean kiemTraChuKy(Map<String, String> fields) {
        String secureHash = fields.get("vnp_SecureHash");
        if (secureHash == null || secureHash.isBlank()) return false;

        Map<String, String> data = new HashMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key != null && key.startsWith("vnp_") && !key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                if (value != null && !value.isBlank()) {
                    data.put(key, value);
                }
            }
        }

        String hashData = buildEncodedHashData(data);
        String calculated = hmacSHA512(vnpHashSecret, hashData);

        return secureHash.equalsIgnoreCase(calculated);
    }

    public String taoUrlThanhToanDatNhieuDichVu(Integer maLuuTru, BigDecimal amount, String maNhom, HttpServletRequest request) {
        String txnRef = "KSVDV_" + maNhom + "_" + maLuuTru + "_" + System.currentTimeMillis();
        String ipAddr = getIpAddress(request);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String createDate = formatter.format(cld.getTime());

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", vnpTmnCode);
        vnpParams.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        vnpParams.put("vnp_CurrCode", "VND");
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", "Thanh toan cum dich vu nhom " + maNhom);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
        vnpParams.put("vnp_IpAddr", ipAddr);
        vnpParams.put("vnp_CreateDate", createDate);

        cld.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        return buildPaymentUrl(vnpParams);
    }

    private String buildPaymentUrl(Map<String, String> params) {
        String hashData = buildEncodedHashData(params);
        String secureHash = hmacSHA512(vnpHashSecret, hashData);
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isBlank()) {
                if (!query.isEmpty()) query.append("&");
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }
        query.append("&vnp_SecureHash=").append(secureHash);
        return vnpPayUrl + "?" + query;
    }

    private String buildEncodedHashData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String value = params.get(fieldName);
            if (value != null && !value.isBlank()) {
                if (!hashData.isEmpty()) hashData.append("&");
                hashData.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                hashData.append("=");
                hashData.append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
            }
        }
        return hashData.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo chữ ký VNPay", e);
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        return (ip == null || ip.isBlank()) ? request.getRemoteAddr() : ip;
    }
}