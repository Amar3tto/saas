package ru.kpfu.itis.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseApiController {
    protected <T> ResponseEntity<T> createGoodResponse(T body) {
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    protected <T> ResponseEntity<T> createGoodResponse() {
        return createGoodResponse(null);
    }
}
