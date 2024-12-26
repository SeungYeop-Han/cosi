package com.cosi.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * 클라이언트 에러(400 번대 에러)를 나타내는 예외 클래스입니다.
 */
@Getter
@AllArgsConstructor
public class BadRequestException extends RuntimeException {

    private HttpStatus httpStatus;
    protected String message;
}
