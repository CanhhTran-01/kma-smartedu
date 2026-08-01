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
    MISSING_PARAMETER(1002, "Thiếu tham số bắt buộc", HttpStatus.BAD_REQUEST),

    // ======= 2xxx - Business Logic Error
    // == FACULTY
    FACULTY_NOT_FOUND(2001, "Không tìm thấy Khoa", HttpStatus.NOT_FOUND),
    FACULTY_ID_NOT_MATCHES_CODE(2005, "ID và mã Khoa không khớp", HttpStatus.BAD_REQUEST),
    FACULTY_HAS_MAJORS(2006, "Không thể xóa Khoa vì vẫn còn Ngành", HttpStatus.CONFLICT),
    FACULTY_CODE_DUPLICATED(2003, "Mã Khoa đã tồn tại", HttpStatus.CONFLICT),

    // == MAJOR
    MAJOR_NOT_FOUND(2002, "Không tìm thấy Ngành", HttpStatus.NOT_FOUND),
    MAJOR_CODE_DUPLICATED(2004, "Mã Ngành đã tồn tại", HttpStatus.CONFLICT),

    // ======= 3xxx - External Service Errors

    // ======= 4xxx - Authentication & Authorization Errors
    UNAUTHENTICATED(4000, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(4001, "Không có quyền truy cập", HttpStatus.FORBIDDEN);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
