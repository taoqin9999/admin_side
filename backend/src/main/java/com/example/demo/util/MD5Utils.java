package com.example.demo.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
/**
 * MD5 加密工具类
 * 使用 Shiro SimpleHash，带盐值加密
 */
public class MD5Utils
/**
 * MD5 加密工具类
 * 使用 Shiro SimpleHash，带盐值加密
 */
 {
    private static final String SALT = "mrbird";
    private static final String ALGORITH_NAME = "md5";
    private static final int HASH_ITERATIONS = 2;

    public static String encrypt(String username, String pswd) {
        return new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(username + SALT), HASH_ITERATIONS).toHex();
    }

    public static String encrypt(String pswd) {
        return new SimpleHash(ALGORITH_NAME, pswd, ByteSource.Util.bytes(SALT), HASH_ITERATIONS).toHex();
    }
}
