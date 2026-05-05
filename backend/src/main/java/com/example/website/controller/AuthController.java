
package com.example.website.controller;

import com.example.website.common.ResponseResult;
import com.example.website.service.AdminService;
import com.example.website.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminService adminService;
    private final OperationLogService operationLogService;

    @PostMapping("/login")
    public ResponseResult<Map<String, Object>> login(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        String username = request.get("username");
        String password = request.get("password");
        Map<String, Object> result = adminService.login(username, password);
        
        operationLogService.saveLog(
                username,
                "登录系统",
                "认证",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"username\": \"" + username + "\"}"
        );
        
        return ResponseResult.success(result);
    }
}
