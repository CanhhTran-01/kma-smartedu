package KMA.SmartEdu.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ======= 1xxx - General Errors
    UNEXPECTED_ERROR(1000, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1001, "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),

    // ======= 2xxx - Business Logic Error
    FACULTY_NOT_FOUND(2001, "Không tìm thấy Khoa", HttpStatus.NOT_FOUND),
    MAJOR_NOT_FOUND(2002, "Không tìm thấy Ngành", HttpStatus.NOT_FOUND),
    FACULTY_CODE_DUPLICATED(2003, "Mã Khoa đã tồn tại", HttpStatus.CONFLICT),
    MAJOR_CODE_DUPLICATED(2004, "Mã Ngành đã tồn tại", HttpStatus.CONFLICT),

    // ======= 3xxx - External Service Errors

    // ======= 4xxx - Authentication & Authorization Errors
    UNAUTHENTICATED(4000, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(4001, "Không có quyền truy cập", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
