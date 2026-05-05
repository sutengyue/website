
package com.example.website.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {

    private Integer code;

    private String message;

    private T data;

    public static <T> ResponseResult<T> success(T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ResponseResult<T> success(String message, T data) {
        return ResponseResult.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseResult<T> error(Integer code, String message) {
        return ResponseResult.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ResponseResult<T> error(String message) {
        return error(500, message);
    }
}
