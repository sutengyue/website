
package com.example.website.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.website.common.PageResult;
import com.example.website.entity.Admin;
import com.example.website.mapper.AdminMapper;
import com.example.website.security.JwtService;
import com.example.website.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Admin findByUsername(String username) {
        return getBaseMapper().findByUsername(username).orElse(null);
    }

    @Override
    public PageResult<Admin> list(Integer pageNum, Integer pageSize, String keyword) {
        Page<Admin> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Admin::getUsername, keyword)
                   .or()
                   .like(Admin::getNickname, keyword);
        }
        wrapper.orderByDesc(Admin::getCreatedAt);
        IPage<Admin> result = page(page, wrapper);
        return PageResult.of(result.getRecords(), result.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public Admin create(Admin admin) {
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setRole("ROLE_ADMIN");
        admin.setStatus(1);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        save(admin);
        return admin;
    }

    @Override
    @Transactional
    public Admin update(Long id, Admin admin) {
        Admin existing = getById(id);
        if (existing == null) {
            throw new RuntimeException("管理员不存在");
        }
        if (admin.getPassword() != null && !admin.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(admin.getPassword()));
        }
        if (admin.getNickname() != null) {
            existing.setNickname(admin.getNickname());
        }
        if (admin.getEmail() != null) {
            existing.setEmail(admin.getEmail());
        }
        if (admin.getAvatar() != null) {
            existing.setAvatar(admin.getAvatar());
        }
        if (admin.getStatus() != null) {
            existing.setStatus(admin.getStatus());
        }
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
    public Map<String, Object> login(String username, String password) {
        Admin admin = findByUsername(username);
        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            throw new RuntimeException("账号已禁用");
        }
        UserDetails userDetails = User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(admin.getRole())))
                .build();
        String token = jwtService.generateToken(userDetails);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(admin.getRole())))
                .build();
    }
}
