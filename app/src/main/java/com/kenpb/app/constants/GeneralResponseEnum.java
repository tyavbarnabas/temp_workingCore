package com.kenpb.app.constants;


import lombok.Getter;

@Getter
public enum GeneralResponseEnum {

    SUCCESS("00", "Success", "Process completed successfully"),
    FAILED("01", "Failed", "Process failed");

    private final String statusCode;
    private final String message;
    private final Object data;
    GeneralResponseEnum(String statusCode, String message, Object data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
