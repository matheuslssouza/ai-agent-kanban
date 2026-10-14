package com.kanban.dto;

public class AiCommandResponseDto {

    private boolean success;
    private String message;
    private String actionExecuted;
    private Object data;

    public AiCommandResponseDto() {
    }

    public AiCommandResponseDto(boolean success, String message, String actionExecuted, Object data) {
        this.success = success;
        this.message = message;
        this.actionExecuted = actionExecuted;
        this.data = data;
    }

    public static AiCommandResponseDto ok(String message, String action, Object data) {
        return new AiCommandResponseDto(true, message, action, data);
    }

    public static AiCommandResponseDto fail(String message, String action) {
        return new AiCommandResponseDto(false, message, action, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActionExecuted() {
        return actionExecuted;
    }

    public void setActionExecuted(String actionExecuted) {
        this.actionExecuted = actionExecuted;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
