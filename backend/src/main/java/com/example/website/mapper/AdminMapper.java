
package com.example.website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.website.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    Optional<Admin> findByUsername(String username);
}
