
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
@TableName("blog")
public class Blog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String slug;

    private String content;

    private String summary;

    private String cover;

    private String category;

    private String tags;

    private Integer views;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
