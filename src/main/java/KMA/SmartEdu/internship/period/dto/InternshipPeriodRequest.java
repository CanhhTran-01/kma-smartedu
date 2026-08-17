package KMA.SmartEdu.internship.period.dto;

import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class InternshipPeriodRequest {

    @NotNull(message = "Chưa chọn loại thực tập")
    private InternshipTypeEnum internshipType;

    @NotBlank(message = "Tên đợt thực tập không được để trống")
    @Size(min = 3, max = 150, message = "Tên đợt thực tập phải từ 3 đến 150 ký tự")
    private String name;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    private LocalDate registrationDeadline;
}
