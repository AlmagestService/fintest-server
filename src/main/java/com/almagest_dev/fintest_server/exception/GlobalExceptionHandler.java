package com.almagest_dev.fintest_server.exception;


import com.almagest_dev.fintest_server.dto.CommonResponseDto;
import com.almagest_dev.fintest_server.exception.base_exceptions.AccessDeniedException;
import com.almagest_dev.fintest_server.exception.base_exceptions.BadRequestException;
import com.almagest_dev.fintest_server.exception.base_exceptions.DataAccessFailException;
import com.almagest_dev.fintest_server.exception.base_exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.ServiceUnavailableException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //추후 고려 -> 더 자세한 예외처리
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
//        Map<String, String> errorDetails = new HashMap<>();
//        errorDetails.put("errorCode", "INTERNAL_SERVER_ERROR");
//        errorDetails.put("errorMessage", "서버 오류가 발생했습니다.");
//        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    /*전역 500*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception e) {
        log.error("서버 오류 발생: " + e);
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
    }

    /*400*/
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleBadRequestExceptions(BadRequestException e) {
        CommonResponseDto<Object> responseDto = new CommonResponseDto<>();
        responseDto.setRspCode("B0000");
        responseDto.setRspMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    /*403 & 404*/
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleNotAuthorizedExceptions(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("잘못된 요청 또는 경로");
    }

    /*409 중복, 검증 실패*/
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleValidationExceptions(ValidationException e) {
        CommonResponseDto<Object> responseDto = new CommonResponseDto<>();
        responseDto.setRspCode("V0000");
        responseDto.setRspMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
    }

    /*503 서비스 사용 불가*/
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailableExceptions(ServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("서비스 사용 불가");
    }

    /*500 데이터 접근, 처리 실패*/
    @ExceptionHandler(DataAccessFailException.class)
    public ResponseEntity<CommonResponseDto<Object>> handleDataAccessExceptions(DataAccessFailException e) {
        CommonResponseDto<Object> responseDto = new CommonResponseDto<>();
        responseDto.setRspCode("S0000");
        responseDto.setRspMessage("서버 오류 발생");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }




}
