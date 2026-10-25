package com.example.demo.controller;

import com.example.demo.pojo.PageResult;
import com.example.demo.pojo.ResponseBo;
import com.example.demo.pojo.SysLog;
import com.example.demo.service.SysLogService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 系统操作日志管理控制器
 * 仅提供只读的分页查询与 CSV 导出，不做新增/修改/删除
 */
@RestController
@RequestMapping("/api/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 分页查询操作日志
     * 支持用户名、操作描述模糊搜索，以及时间范围（startTime/endTime，格式 yyyy-MM-dd HH:mm:ss）
     */
    @GetMapping
    @RequiresPermissions("log:list")
    public ResponseBo list(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int pageSize,
                           @RequestParam(required = false) String username,
                           @RequestParam(required = false) String operation,
                           @RequestParam(required = false) String startTime,
                           @RequestParam(required = false) String endTime) {
        // 日志数据量大，强制走分页，避免一次性全量返回
        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0 || pageSize > 200) {
            pageSize = 10;
        }
        PageResult<SysLog> pageResult = sysLogService.findPage(page, pageSize, username, operation, startTime, endTime);
        return ResponseBo.ok().putData(pageResult);
    }

    /**
     * 导出操作日志为 CSV（按当前筛选条件导出全部匹配记录，不分页）
     * 返回的 data 为 CSV 文本内容，由前端拼接为文件下载
     */
    @GetMapping("/export")
    @RequiresPermissions("log:list")
    public ResponseBo export(@RequestParam(required = false) String username,
                             @RequestParam(required = false) String operation,
                             @RequestParam(required = false) String startTime,
                             @RequestParam(required = false) String endTime) {
        List<SysLog> list = sysLogService.findAllForExport(username, operation, startTime, endTime);
        return ResponseBo.ok().putData(buildCsv(list));
    }

    /**
     * 将日志列表生成为 CSV 文本（UTF-8，无 BOM，前端下载时再补 BOM 以便 Excel 识别）
     */
    private String buildCsv(List<SysLog> list) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StringBuilder csv = new StringBuilder();
        csv.append("ID,用户名,操作,请求方法,请求参数,IP,耗时(ms),操作时间\r\n");
        for (SysLog l : list) {
            csv.append(l.getId() == null ? "" : l.getId()).append(',')
                    .append(csvEscape(l.getUsername())).append(',')
                    .append(csvEscape(l.getOperation())).append(',')
                    .append(csvEscape(l.getMethod())).append(',')
                    .append(csvEscape(l.getParams())).append(',')
                    .append(csvEscape(l.getIp())).append(',')
                    .append(l.getTime() == null ? "" : l.getTime()).append(',')
                    .append(csvEscape(formatDate(l.getCreateTime(), sdf)))
                    .append("\r\n");
        }
        return csv.toString();
    }

    /**
     * CSV 字段转义：含逗号/引号/换行时用双引号包裹，内部双引号翻倍
     */
    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        String v = value.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }

    private String formatDate(Date date, SimpleDateFormat sdf) {
        return date == null ? "" : sdf.format(date);
    }
}
