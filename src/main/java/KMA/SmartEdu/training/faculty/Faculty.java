package KMA.SmartEdu.training.faculty;

import KMA.SmartEdu.core.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faculty")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Faculty extends BaseEntity {

    @Column(name = "faculty_code", nullable = false, unique = true, length = 20)
    private String facultyCode; // Mỗi Khoa phải có một facultyCode duy nhất trong toàn hệ thống

    @Column(name = "faculty_name", nullable = false, length = 200)
    private String facultyName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
