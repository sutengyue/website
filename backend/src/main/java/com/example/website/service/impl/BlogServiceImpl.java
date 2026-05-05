
package com.example.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.website.common.PageResult;
import com.example.website.entity.Blog;
import com.example.website.mapper.BlogMapper;
import com.example.website.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Override
    public PageResult<Blog> list(Integer pageNum, Integer pageSize, String keyword, String category) {
        Page<Blog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Blog::getTitle, keyword)
                   .or()
                   .like(Blog::getSummary, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(Blog::getCategory, category);
        }
        wrapper.eq(Blog::getStatus, 1);
        wrapper.orderByDesc(Blog::getCreatedAt);
        IPage<Blog> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    public Blog findBySlug(String slug) {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getSlug, slug);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public Blog create(Blog blog) {
        blog.setViews(0);
        blog.setCreatedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
        save(blog);
        return blog;
    }

    @Override
    @Transactional
    public Blog update(Long id, Blog blog) {
        Blog existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("博客不存在");
        }
        existing.setTitle(blog.getTitle());
        existing.setSlug(blog.getSlug());
        existing.setContent(blog.getContent());
        existing.setSummary(blog.getSummary());
        existing.setCover(blog.getCover());
        existing.setCategory(blog.getCategory());
        existing.setTags(blog.getTags());
        existing.setStatus(blog.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        updateById(existing);
        return existing;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        removeById(id);
    }

    @Override
    public List<Blog> findByCategory(String category) {
        return getBaseMapper().findByCategory(category);
    }

    @Override
    public List<Blog> listAll() {
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Blog::getStatus, 1);
        wrapper.orderByDesc(Blog::getCreatedAt);
        return list(wrapper);
    }
}
