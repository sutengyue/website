
package com.example.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.website.common.PageResult;
import com.example.website.entity.Admin;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

public interface AdminService extends IService<Admin>, UserDetailsService {

    Admin findByUsername(String username);

    PageResult<Admin> list(Integer pageNum, Integer pageSize, String keyword);

    Admin create(Admin admin);

    Admin update(Long id, Admin admin);

    void delete(Long id);

    Map<String, Object> login(String username, String password);
}
