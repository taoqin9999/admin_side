package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户操作日志注解
 * 标注在需要监控的 Controller 方法上，配合 {@link com.example.demo.aspect.LogAspect} 记录用户操作
 * 用法：@Log("新增用户")
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    /**
     * 操作描述
     */
    String value() default "";
}
