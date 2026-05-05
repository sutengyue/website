
package com.example.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.website.common.PageResult;
import com.example.website.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    PageResult<OperationLog> list(Integer pageNum, Integer pageSize, String keyword);

    void saveLog(String username, String operation, String module, String ip, String userAgent, String params);
}
