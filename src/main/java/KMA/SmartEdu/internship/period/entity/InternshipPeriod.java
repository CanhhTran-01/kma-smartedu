package KMA.SmartEdu.internship.period.entity;

import KMA.SmartEdu.core.common.BaseEntity;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "internship_periods")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternshipPeriod extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "internship_type", nullable = false, length = 50)
    private InternshipTypeEnum internshipType;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;
}
