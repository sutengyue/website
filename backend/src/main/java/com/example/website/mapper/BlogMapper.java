
package com.example.website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.website.entity.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    List<Blog> findByCategory(String category);
}
