
package com.example.website.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("profile")
public class Profile {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String avatar;

    private String title;

    private String bio;

    private String email;

    private String phone;

    private String location;

    private String github;

    private String linkedin;

    private String website;

    private String skills;

    private String education;

    private String experience;

    private LocalDateTime updatedAt;
}
