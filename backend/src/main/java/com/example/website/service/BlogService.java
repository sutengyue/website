
package com.example.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.website.common.PageResult;
import com.example.website.entity.Blog;

import java.util.List;

public interface BlogService extends IService<Blog> {

    PageResult<Blog> list(Integer pageNum, Integer pageSize, String keyword, String category);

    Blog findBySlug(String slug);

    Blog create(Blog blog);

    Blog update(Long id, Blog blog);

    void delete(Long id);

    List<Blog> findByCategory(String category);

    List<Blog> listAll();
}
