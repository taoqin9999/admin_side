package com.example.demo.util;

import javax.servlet.http.HttpServletRequest;

/**
 * IP 地址工具类
 * 兼容 Nginx 等反向代理场景，从 X-Forwarded-For 等请求头中获取真实客户端 IP
 */
public class IPUtils {

    /**
     * 获取客户端真实 IP 地址
     * 使用 Nginx 等反向代理软件时，不能直接通过 request.getRemoteAddr() 获取 IP。
     * 若经过多级反向代理，X-Forwarded-For 为一段 IP 列表，取第一个非 unknown 的有效 IP 作为真实 IP。
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 本机 IPv6 环回地址统一转为 127.0.0.1
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
