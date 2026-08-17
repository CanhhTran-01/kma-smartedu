package KMA.SmartEdu.internship.period.dto;

import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.enums.PeriodStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InternshipPeriodResponse {
    private Long id;
    private InternshipTypeEnum internshipType;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationDeadline;
    private PeriodStatus status;
    private LocalDateTime createdAt;
}
