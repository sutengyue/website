
package com.example.website.controller;

import com.example.website.common.PageResult;
import com.example.website.common.ResponseResult;
import com.example.website.entity.Admin;
import com.example.website.service.AdminService;
import com.example.website.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public ResponseResult<PageResult<Admin>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return ResponseResult.success(adminService.list(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public ResponseResult<Admin> getById(@PathVariable Long id) {
        return ResponseResult.success(adminService.getById(id));
    }

    @PostMapping
    public ResponseResult<Admin> create(@RequestBody Admin admin, HttpServletRequest httpRequest) {
        Admin result = adminService.create(admin);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "创建管理员",
                "管理员管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"username\": \"" + admin.getUsername() + "\"}"
        );
        
        return ResponseResult.success("创建成功", result);
    }

    @PutMapping("/{id}")
    public ResponseResult<Admin> update(@PathVariable Long id, @RequestBody Admin admin, HttpServletRequest httpRequest) {
        Admin result = adminService.update(id, admin);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "更新管理员",
                "管理员管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"id\": " + id + "}"
        );
        
        return ResponseResult.success("更新成功", result);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        adminService.delete(id);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "删除管理员",
                "管理员管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"id\": " + id + "}"
        );
        
        return ResponseResult.success("删除成功", null);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "unknown";
    }
}
