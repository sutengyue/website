
package com.example.website.controller;

import com.example.website.common.ResponseResult;
import com.example.website.entity.Profile;
import com.example.website.service.OperationLogService;
import com.example.website.service.ProfileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final OperationLogService operationLogService;

    @GetMapping("/public/profile")
    public ResponseResult<Profile> getPublicProfile() {
        return ResponseResult.success(profileService.getProfile());
    }

    @GetMapping("/admin/profile")
    public ResponseResult<Profile> getAdminProfile() {
        return ResponseResult.success(profileService.getProfile());
    }

    @PutMapping("/admin/profile")
    public ResponseResult<Profile> update(@RequestBody Profile profile, HttpServletRequest httpRequest) {
        Profile result = profileService.update(profile);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "更新个人资料",
                "个人资料",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"name\": \"" + profile.getName() + "\"}"
        );
        
        return ResponseResult.success("更新成功", result);
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "unknown";
    }
}
