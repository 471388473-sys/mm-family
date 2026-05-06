package com.pos.platform.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结构
 */
@Data
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 业务状态码，0表示成功 */
    private int code;

    /** 响应描述 */
    private String message;

    /** 业务数据 */
    private T data;

    /** 服务器响应时间戳(毫秒) */
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());
        return response;
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(message);
        return response;
    }
}