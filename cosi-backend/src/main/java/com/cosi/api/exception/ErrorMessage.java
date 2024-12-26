package com.cosi.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 오류 응답의 본문으로 언마샬링됩니다.
 */
@Getter
@AllArgsConstructor
public class ErrorMessage {
    private int statusCode;
    private String statusText;
    private String message;
}
