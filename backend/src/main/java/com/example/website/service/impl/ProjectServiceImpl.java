
package com.example.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.website.common.PageResult;
import com.example.website.entity.Project;
import com.example.website.mapper.ProjectMapper;
import com.example.website.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public PageResult<Project> list(Integer pageNum, Integer pageSize, String keyword) {
        Page<Project> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Project::getName, keyword)
                   .or()
                   .like(Project::getDescription, keyword);
        }
        wrapper.orderByAsc(Project::getSortOrder);
        IPage<Project> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public Project create(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        save(project);
        return project;
    }

    @Override
    @Transactional
    public Project update(Long id, Project project) {
        Project existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("项目不存在");
        }
        existing.setName(project.getName());
        existing.setDescription(project.getDescription());
        existing.setCover(project.getCover());
        existing.setUrl(project.getUrl());
        existing.setGithubUrl(project.getGithubUrl());
        existing.setTags(project.getTags());
        existing.setSortOrder(project.getSortOrder());
        existing.setStatus(project.getStatus());
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
    public List<Project> listAll() {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getStatus, 1);
        wrapper.orderByAsc(Project::getSortOrder);
        return list(wrapper);
    }
}
