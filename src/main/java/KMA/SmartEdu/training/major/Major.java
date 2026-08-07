package KMA.SmartEdu.training.major;

import KMA.SmartEdu.core.common.BaseEntity;
import KMA.SmartEdu.training.faculty.Faculty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "major")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Major extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "major_code", nullable = false, unique = true, length = 20)
    private String majorCode; // mỗi major phải có 1 majorCode duy nhất toàn hệ thống

    @Column(name = "major_name", nullable = false, length = 200)
    private String majorName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;
}
