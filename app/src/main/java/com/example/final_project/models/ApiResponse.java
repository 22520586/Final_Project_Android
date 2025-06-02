package com.example.final_project.models;

public class ApiResponse<T> {
    private T metadata;  // hoặc đổi thành data
    private String message;
    private boolean success;  // thêm field này
    private int code;         // thêm field này nếu cần

    // Constructor
    public ApiResponse() {}

    public ApiResponse(T metadata, String message, boolean success) {
        this.metadata = metadata;
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public T getMetadata() {
        return metadata;
    }

    public void setMetadata(T metadata) {
        this.metadata = metadata;
    }

    // Alias method để phù hợp với code hiện tại
    public T getData() {
        return metadata;
    }

    public void setData(T data) {
        this.metadata = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}