
package com.example.website.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.website.entity.Profile;
import com.example.website.mapper.ProfileMapper;
import com.example.website.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl extends ServiceImpl<ProfileMapper, Profile> implements ProfileService {

    @Override
    public Profile getProfile() {
        List<Profile> list = list();
        if (list.isEmpty()) {
            return Profile.builder()
                    .name("开发者")
                    .title("全栈工程师")
                    .bio("热爱技术，专注于创造有价值的产品")
                    .build();
        }
        return list.get(0);
    }

    @Override
    @Transactional
    public Profile update(Profile profile) {
        List<Profile> list = list();
        if (list.isEmpty()) {
            profile.setUpdatedAt(LocalDateTime.now());
            save(profile);
            return profile;
        }
        Profile existing = list.get(0);
        existing.setName(profile.getName());
        existing.setAvatar(profile.getAvatar());
        existing.setTitle(profile.getTitle());
        existing.setBio(profile.getBio());
        existing.setEmail(profile.getEmail());
        existing.setPhone(profile.getPhone());
        existing.setLocation(profile.getLocation());
        existing.setGithub(profile.getGithub());
        existing.setLinkedin(profile.getLinkedin());
        existing.setWebsite(profile.getWebsite());
        existing.setSkills(profile.getSkills());
        existing.setEducation(profile.getEducation());
        existing.setExperience(profile.getExperience());
        existing.setUpdatedAt(LocalDateTime.now());
        updateById(existing);
        return existing;
    }
}
