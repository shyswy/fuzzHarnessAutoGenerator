package com.example.fuzzharnessautogenerator.util;


import com.example.fuzzharnessautogenerator.dto.PageInfo;
import com.example.fuzzharnessautogenerator.dto.response.RootResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Log4j2
public class ResponseUtil {
    private static ResponseUtil instance;

    private ResponseUtil() {
    }

    public ResponseEntity<RootResponse> getResponseEntity(HttpStatus status, Object responseData, PageInfo pageInfo) {
        RootResponse rootResponse = getRootResponse(status, responseData, pageInfo);
        return new ResponseEntity<>(rootResponse, HttpStatus.valueOf(status.value()));
    }

    public RootResponse getRootResponse(HttpStatus status, Object responseData, PageInfo pageInfo) {
        return new RootResponse(status.value(), status.getReasonPhrase(), responseData, pageInfo);
    }

    public ResponseEntity<RootResponse> getResponseEntity(Integer status, String message, Object responseData, PageInfo pageInfo) {
        RootResponse rootResponse = getRootResponse(status, message, responseData, pageInfo);
        return new ResponseEntity<>(rootResponse, HttpStatus.valueOf(HttpStatus.NOT_ACCEPTABLE.value()));
    }

    public RootResponse getRootResponse(Integer status, String message, Object responseData, PageInfo pageInfo) {
        return new RootResponse(status, message, responseData, pageInfo);
    }

    public static ResponseUtil getInstance() {
        if (instance == null) {
            synchronized (ResponseUtil.class) {
                instance = new ResponseUtil();
            }
        }
        return instance;
    }
}
