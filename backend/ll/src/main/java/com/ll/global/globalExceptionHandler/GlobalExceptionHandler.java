package com.ll.global.globalExceptionHandler;

import com.ll.global.exception.ServiceException;
import com.ll.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class) // 조회 결과가 없을 때
    public ResponseEntity<RsData<Void>> handle(NoSuchElementException exception){
        return new ResponseEntity<>(
                new RsData<>(
                        "404-1",
                        "해당 데이터가 존재하지 않습니다."
                ),
                NOT_FOUND
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class) // DTO 유효성 검사 실패
    public ResponseEntity<RsData<Void>> handle(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .filter(error -> error instanceof FieldError)
                .map(error -> (FieldError) error)
                .map(error -> error.getField() + "-" + error.getCode() + "-" + error.getDefaultMessage())
                .sorted(Comparator.comparing(String::toString))
                .collect(Collectors.joining("\n"));

        return new ResponseEntity<>(
                new RsData<>(
                        "400-1",
                        message
                ),
                BAD_REQUEST
        );
    }
    @ExceptionHandler(HttpMessageNotReadableException.class) //JSON 문법 오류
    public ResponseEntity<RsData<Void>> handle(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                new RsData<>(
                        "400-1",
                        "요청 본문이 올바르지 않습니다."
                ),
                BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RsData<Void>> handleDuplicateEmail(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                new RsData<>(
                        "400-2",
                        "이미 사용 중인 이메일입니다."
                ),
                CONFLICT
        );
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<Void>> handleServiceException(ServiceException ex) {
        String[] parts = ex.getMessage().split(" : ", 2);
        String code = parts.length > 0 ? parts[0] : "500-1";
        String message = parts.length > 1 ? parts[1] : "알 수 없는 오류가 발생했습니다.";

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        if (code.startsWith("401")) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (code.startsWith("403")) {
            status = HttpStatus.FORBIDDEN;
        } else if (code.startsWith("404")) {
            status = HttpStatus.NOT_FOUND;
        } else if (code.startsWith("400")) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(
                new RsData<>(
                        code,
                        message
                ),
                status
        );

    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Void>> handle(IllegalArgumentException ex) {
        // 찜 목록 관련 예외인지 확인
        if (ex.getMessage().contains("존재하지 않는 찜 목록 항목이다")) {
            return new ResponseEntity<>(
                    new RsData<>(
                            "404-1",
                            ex.getMessage()
                    ),
                    NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                new RsData<>(
                        "400-1",
                        ex.getMessage()
                ),
                BAD_REQUEST
        );
    }
}
