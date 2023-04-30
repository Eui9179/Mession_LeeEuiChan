package com.ll.gramgram.base.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;

    public DataNotFoundException(String entityName, Object id) {
        super("해당 " + entityName + "가 존재하지 않습니다. " + id);
    }
}
