package com.mybrary.backend.global.handler;

import com.mybrary.backend.global.exception.DuplicateEmailException;
import com.mybrary.backend.global.exception.PasswordMismatchException;
import com.mybrary.backend.global.format.ApiResponse;
import com.mybrary.backend.global.format.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Log4j2
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ApiResponse response;

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleException(Exception e) {
        log.error("Exception = {}", e.getMessage());
        return response.error(ErrorCode.GLOBAL_UNEXPECTED_ERROR.getMessage());
    }

    @ExceptionHandler(PasswordMismatchException.class)
    protected ResponseEntity<?> handlePasswordMismatchException(PasswordMismatchException e) {
        log.error("PasswordMismatchException = {}", e.getMessage());
        return response.error(ErrorCode.MEMBER_PASSWORD_MISMATCH.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    protected ResponseEntity<?> handleDuplicateEmailException(DuplicateEmailException e) {
        log.error("DuplicateEmailException = {}", e.getMessage());
        return response.error(ErrorCode.MEMBER_EMAIL_DUPLICATED.getMessage());
    }

}
