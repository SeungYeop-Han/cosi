package com.cosi.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * API 호출에 응답하는 도중 발생한 모든 예외를 가로채서 적절한 {@link ErrorMessage} 로 응답합니다.
 */
@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    /**
     * 사용자 정의 클라이언트 에러 처리
     */
    @ExceptionHandler({BadRequestException.class})
    protected ResponseEntity<ErrorMessage> handleBadRequestException(BadRequestException e) {

        HttpStatus httpStatus = e.getHttpStatus();
        String message = e.getMessage();

        return ResponseEntity
                .status(httpStatus.value())
                .body(new ErrorMessage(
                        httpStatus.value(),
                        httpStatus.getReasonPhrase(),
                        message
                ));
    }

    /**
     * 스프링 validation 검증 실패 처리
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        final String STATUS_TEXT = "유효하지 않은 파라미터";

        ObjectError objectError = e.getGlobalError();
        if (objectError != null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage(
                            HttpStatus.BAD_REQUEST.value(),
                            STATUS_TEXT,
                            objectError.getDefaultMessage()
                    ));
        }

        // else
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(
                        HttpStatus.BAD_REQUEST.value(),
                        STATUS_TEXT,
                        STATUS_TEXT
                ));
    }

    /**
     * API 요청 처리 중 발생한 예외가 본 클래스의 다른 핸들러에 의해 처리되지 않은 경우,
     * 이러한 예외는 전부 서버 에러로 간주하여 처리합니다.
     */
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<ErrorMessage> handleUncaughtException(Exception e) {

        // 서버 오류를 ERROR 수준에서 로깅
        log.error(
                "***** 서버 오류 발생 *****\nmessage: {}\ncause: {}\nstacktrace: {}\n************************",
                e.getMessage(), e.getCause(), e.getStackTrace());
        
        // 응답 메시지
        final String RESPONSE_MESSAGE = "서버 오류가 발생했습니다. 문제가 지속되는 경우 서버 운영자에게 연락해주기시 바랍니다. (contact: ammezkhan@gmail.com)";

        // 응답 반환
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        RESPONSE_MESSAGE
                ));
    }
}
