package com.example.demo.pojo;

import java.util.HashMap;
import java.util.Map;

public class ResponseBo extends HashMap<String, Object>
/**
 * 统一 API 响应类
 * 封装 code、msg、data，所有接口统一返回格式
 */
 {
    private static final long serialVersionUID = 1L;

    private static final String CODE = "code";
    private static final String MSG = "msg";
    private static final String DATA = "data";

    public ResponseBo() {
        put(CODE, 200);
        put(MSG, "操作成功");
    }

    public static ResponseBo ok() {
        return new ResponseBo();
    }

    public static ResponseBo ok(String msg) {
        ResponseBo rb = new ResponseBo();
        rb.put(MSG, msg);
        return rb;
    }

    public static ResponseBo ok(Map<String, Object> map) {
        ResponseBo rb = new ResponseBo();
        rb.putAll(map);
        return rb;
    }

    public static ResponseBo error() {
        return error(500, "操作失败");
    }

    public static ResponseBo error(String msg) {
        return error(500, msg);
    }

    public static ResponseBo error(int code, String msg) {
        ResponseBo rb = new ResponseBo();
        rb.put(CODE, code);
        rb.put(MSG, msg);
        return rb;
    }

    public ResponseBo putData(Object data) {
        put(DATA, data);
        return this;
    }

    @Override
    public ResponseBo put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
