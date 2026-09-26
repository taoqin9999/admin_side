package com.example.demo.handler;

import com.example.demo.pojo.ResponseBo;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 全局异常处理器
 * 统一处理权限不足、未登录、系统异常等情况
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthorizationException.class)
    public ResponseBo handleAuthorizationException() {
        return ResponseBo.error(403, "没有权限访问");
    }

    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseBo handleUnauthenticatedException() {
        return ResponseBo.error(401, "未登录或登录已过期");
    }

    @ExceptionHandler(Exception.class)
    public ResponseBo handleException(Exception e) {
        e.printStackTrace();
        return ResponseBo.error(500, "服务器内部错误: " + e.getMessage());
    }
}