
package com.example.website.controller;

import com.example.website.common.PageResult;
import com.example.website.common.ResponseResult;
import com.example.website.entity.OperationLog;
import com.example.website.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public ResponseResult<PageResult<OperationLog>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return ResponseResult.success(operationLogService.list(pageNum, pageSize, keyword));
    }
}
