package KMA.SmartEdu.training.faculty.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FacultyResponse {
    private Long id;
    private String facultyCode;
    private String facultyName;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;
}
