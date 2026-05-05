
package com.example.website.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.website.entity.Profile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProfileMapper extends BaseMapper<Profile> {
}
