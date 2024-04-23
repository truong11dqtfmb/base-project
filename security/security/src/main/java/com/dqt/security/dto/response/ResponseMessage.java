package com.dqt.security.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseMessage {

    private boolean status;
    private String message;

    private Object data;

    public ResponseMessage(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseMessage(boolean status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static ResponseMessage ok(String message) {
        return new ResponseMessage(true, message);
    }

    public static ResponseMessage error(String message) {
        return new ResponseMessage(false, message);
    }

    public static ResponseMessage ok(String message, Object data) {
        return new ResponseMessage(true, message, data);
    }

    public static ResponseMessage error(String message, Object data) {
        return new ResponseMessage(false, message, data);
    }
}

