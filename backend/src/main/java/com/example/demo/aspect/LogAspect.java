package com.example.demo.aspect;

import com.example.demo.annotation.Log;
import com.example.demo.dao.SysLogMapper;
import com.example.demo.pojo.SysLog;
import com.example.demo.pojo.User;
import com.example.demo.util.HttpContextUtils;
import com.example.demo.util.IPUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 用户操作日志切面
 * 通过 AOP 拦截所有标注了 {@link Log} 注解的 Controller 方法，
 * 在方法执行前后记录操作描述、请求方法、参数、IP、当前用户、耗时等信息并入库。
 * 参考 SpringAll/07.Spring-Boot-AOP-Log，适配本项目（MySQL + Shiro 真实用户名）。
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    /** 用于将复杂参数对象序列化为 JSON，避免记录对象地址 */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 敏感字段 key 关键词（不区分大小写，命中即脱敏） */
    private static final String[] SENSITIVE_KEYS = {"password", "pwd", "passwd", "token", "secret", "apikey", "accesskey", "privatekey"};

    @Autowired
    private SysLogMapper sysLogMapper;

    @Pointcut("@annotation(com.example.demo.annotation.Log)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        try {
            Object result = point.proceed();
            saveLog(point, System.currentTimeMillis() - beginTime);
            return result;
        } catch (Throwable e) {
            // 方法执行失败也记录该次操作，随后重新抛出，交由全局异常处理器统一处理
            saveLog(point, System.currentTimeMillis() - beginTime);
            throw e;
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            SysLog sysLog = new SysLog();
            // 注解上的操作描述
            Log logAnnotation = method.getAnnotation(Log.class);
            if (logAnnotation != null) {
                sysLog.setOperation(logAnnotation.value());
            }
            // 请求的方法名（类名.方法名()）
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            sysLog.setMethod(className + "." + methodName + "()");

            // 请求的方法参数名 + 参数值
            Object[] args = joinPoint.getArgs();
            LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] paramNames = discoverer.getParameterNames(method);
            if (args != null && paramNames != null) {
                StringBuilder params = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        params.append("  ");
                    }
                    params.append(paramNames[i]).append(": ").append(paramToString(args[i]));
                }
                sysLog.setParams(params.toString());
            }

            // 获取请求与客户端 IP
            HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
            sysLog.setIp(IPUtils.getIpAddr(request));

            // 当前登录用户（Shiro 实时获取；登录等未认证场景可能为空）
            sysLog.setUsername(getCurrentUsername());

            sysLog.setTime(time);
            sysLog.setCreateTime(new Date());
            sysLogMapper.save(sysLog);
        } catch (Exception e) {
            // 日志记录失败不影响业务主流程
            log.error("保存系统操作日志失败", e);
        }
    }

    /**
     * 将方法参数转为可读字符串
     * - 基础类型、字符串、日期、枚举等直接 toString
     * - 复杂对象（POJO）、Map、List、数组等序列化为 JSON（并对敏感字段脱敏），避免输出对象地址（如 User@xxx）
     */
    private String paramToString(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (arg instanceof CharSequence || arg instanceof Number
                || arg instanceof Boolean || arg instanceof Character
                || arg instanceof Date || arg instanceof Enum) {
            return arg.toString();
        }
        try {
            JsonNode node = OBJECT_MAPPER.valueToTree(arg);
            maskSensitive(node);
            return node.toString();
        } catch (Exception e) {
            // 序列化失败时降级为 toString，保证日志始终可记录
            return arg.toString();
        }
    }

    /**
     * 递归遍历 JSON 树，对敏感字段（如 password、token 等）的值替换为 ***
     */
    private void maskSensitive(JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isObject()) {
            java.util.Iterator<java.util.Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                java.util.Map.Entry<String, JsonNode> field = fields.next();
                String key = field.getKey();
                if (isSensitive(key)) {
                    ((ObjectNode) node).put(key, "***");
                } else {
                    maskSensitive(field.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                maskSensitive(child);
            }
        }
    }

    /**
     * 判断字段名是否敏感（忽略大小写，按关键词模糊匹配）
     */
    private boolean isSensitive(String key) {
        if (key == null) {
            return false;
        }
        String lower = key.toLowerCase();
        for (String s : SENSITIVE_KEYS) {
            if (lower.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 Shiro 获取当前登录用户名；未登录时返回空字符串
     */
    private String getCurrentUsername() {
        try {
            Subject subject = SecurityUtils.getSubject();
            Object principal = subject.getPrincipal();
            if (principal instanceof User) {
                return ((User) principal).getUsername();
            }
            if (principal != null) {
                return principal.toString();
            }
        } catch (Exception e) {
            // 忽略，未登录场景
        }
        return "";
    }
}
