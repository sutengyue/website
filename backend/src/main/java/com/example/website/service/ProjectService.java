
package com.example.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.website.common.PageResult;
import com.example.website.entity.Project;

import java.util.List;

public interface ProjectService extends IService<Project> {

    PageResult<Project> list(Integer pageNum, Integer pageSize, String keyword);

    Project create(Project project);

    Project update(Long id, Project project);

    void delete(Long id);

    List<Project> listAll();
}
