
package com.example.website.controller;

import com.example.website.common.PageResult;
import com.example.website.common.ResponseResult;
import com.example.website.entity.Project;
import com.example.website.service.OperationLogService;
import com.example.website.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final OperationLogService operationLogService;

    @GetMapping("/public/projects")
    public ResponseResult<List<Project>> listPublic() {
        return ResponseResult.success(projectService.listAll());
    }

    @GetMapping("/admin/projects")
    public ResponseResult<PageResult<Project>> listAdmin(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return ResponseResult.success(projectService.list(pageNum, pageSize, keyword));
    }

    @GetMapping("/admin/project/{id}")
    public ResponseResult<Project> getById(@PathVariable Long id) {
        return ResponseResult.success(projectService.getById(id));
    }

    @PostMapping("/admin/project")
    public ResponseResult<Project> create(@RequestBody Project project, HttpServletRequest httpRequest) {
        Project result = projectService.create(project);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "创建项目",
                "项目管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"name\": \"" + project.getName() + "\"}"
        );
        
        return ResponseResult.success("创建成功", result);
    }

    @PutMapping("/admin/project/{id}")
    public ResponseResult<Project> update(@PathVariable Long id, @RequestBody Project project, HttpServletRequest httpRequest) {
        Project result = projectService.update(id, project);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "更新项目",
                "项目管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"id\": " + id + ", \"name\": \"" + project.getName() + "\"}"
        );
        
        return ResponseResult.success("更新成功", result);
    }

    @DeleteMapping("/admin/project/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        projectService.delete(id);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "删除项目",
                "项目管理",
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
