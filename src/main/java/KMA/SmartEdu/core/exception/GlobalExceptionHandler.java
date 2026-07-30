package KMA.SmartEdu.core.exception;

import KMA.SmartEdu.core.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception) {

        String detail = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", detail);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(detail));
    }

    // 2. Business exception
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handlingBaseException(BaseException exception) {
        log.error(
                "Business Exception: code={}, message={}",
                exception.getErrorCode().getCode(),
                exception.getMessage());

        return ResponseEntity.status(exception.getErrorCode().getHttpStatus())
                .body(ApiResponse.error(exception.getErrorCode()));
    }

    // 3. Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handlingRuntimeException(Exception exception) {

        log.error("Unexpected Exception: ", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.UNEXPECTED_ERROR));
    }
}
