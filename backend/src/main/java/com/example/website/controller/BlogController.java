
package com.example.website.controller;

import com.example.website.common.PageResult;
import com.example.website.common.ResponseResult;
import com.example.website.entity.Blog;
import com.example.website.service.BlogService;
import com.example.website.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final OperationLogService operationLogService;

    @GetMapping("/public/blogs")
    public ResponseResult<PageResult<Blog>> listPublic(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return ResponseResult.success(blogService.list(pageNum, pageSize, keyword, category));
    }

    @GetMapping("/public/blogs/all")
    public ResponseResult<List<Blog>> listAllPublic() {
        return ResponseResult.success(blogService.listAll());
    }

    @GetMapping("/public/blog/{slug}")
    public ResponseResult<Blog> getBySlug(@PathVariable String slug) {
        return ResponseResult.success(blogService.findBySlug(slug));
    }

    @GetMapping("/admin/blogs")
    public ResponseResult<PageResult<Blog>> listAdmin(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return ResponseResult.success(blogService.list(pageNum, pageSize, keyword, category));
    }

    @GetMapping("/admin/blog/{id}")
    public ResponseResult<Blog> getById(@PathVariable Long id) {
        return ResponseResult.success(blogService.getById(id));
    }

    @PostMapping("/admin/blog")
    public ResponseResult<Blog> create(@RequestBody Blog blog, HttpServletRequest httpRequest) {
        Blog result = blogService.create(blog);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "创建博客",
                "博客管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"title\": \"" + blog.getTitle() + "\"}"
        );
        
        return ResponseResult.success("创建成功", result);
    }

    @PutMapping("/admin/blog/{id}")
    public ResponseResult<Blog> update(@PathVariable Long id, @RequestBody Blog blog, HttpServletRequest httpRequest) {
        Blog result = blogService.update(id, blog);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "更新博客",
                "博客管理",
                httpRequest.getRemoteAddr(),
                httpRequest.getHeader("User-Agent"),
                "{\"id\": " + id + ", \"title\": \"" + blog.getTitle() + "\"}"
        );
        
        return ResponseResult.success("更新成功", result);
    }

    @DeleteMapping("/admin/blog/{id}")
    public ResponseResult<Void> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        blogService.delete(id);
        
        operationLogService.saveLog(
                getCurrentUsername(),
                "删除博客",
                "博客管理",
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
