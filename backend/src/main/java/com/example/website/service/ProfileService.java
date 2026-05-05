
package com.example.website.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.website.entity.Profile;

public interface ProfileService extends IService<Profile> {

    Profile getProfile();

    Profile update(Profile profile);
}
