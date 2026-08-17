package KMA.SmartEdu.internship.period.enums;

import java.time.LocalDate;

public enum PeriodStatus {
    REGISTRATION_OPEN, // đang mở đăng ký
    REGISTRATION_CLOSED, // đã đóng đăng ký, chưa tới ngày bắt đầu
    IN_PROGRESS, // đang trong thời gian thực tập
    ENDED; // đã kết thúc

    public static PeriodStatus of(LocalDate startDate, LocalDate endDate, LocalDate registrationDeadline) {
        LocalDate today = LocalDate.now();

        if (today.isAfter(endDate)) {
            return ENDED;
        }
        if (!today.isBefore(startDate)) {
            return IN_PROGRESS;
        }
        if (registrationDeadline != null && today.isAfter(registrationDeadline)) {
            return REGISTRATION_CLOSED;
        }
        return REGISTRATION_OPEN;
    }
}
