package KMA.SmartEdu.training.major.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MajorResponse {
    private Long id;
    private String majorCode;
    private String majorName;
    private String description;
    private Boolean active;
    private LocalDateTime createdAt;

    private Long facultyId;
    private String facultyName;
}
