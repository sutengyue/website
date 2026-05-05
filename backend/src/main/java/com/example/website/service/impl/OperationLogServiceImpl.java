
package com.example.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.website.common.PageResult;
import com.example.website.entity.OperationLog;
import com.example.website.mapper.OperationLogMapper;
import com.example.website.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PageResult<OperationLog> list(Integer pageNum, Integer pageSize, String keyword) {
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(OperationLog::getUsername, keyword)
                   .or()
                   .like(OperationLog::getOperation, keyword)
                   .or()
                   .like(OperationLog::getModule, keyword);
        }
        wrapper.orderByDesc(OperationLog::getCreatedAt);
        IPage<OperationLog> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public void saveLog(String username, String operation, String module, String ip, String userAgent, String params) {
        OperationLog log = OperationLog.builder()
                .username(username)
                .operation(operation)
                .module(module)
                .ip(ip)
                .userAgent(userAgent)
                .params(params)
                .createdAt(LocalDateTime.now())
                .build();
        save(log);
    }
}
