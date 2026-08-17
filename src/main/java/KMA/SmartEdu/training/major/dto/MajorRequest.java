package KMA.SmartEdu.training.major.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MajorRequest {
    @NotNull(message = "Chưa chọn khoa cho ngành học muốn thêm")
    private Long facultyId;

    @NotBlank(message = "Mã ngành không được để trống")
    @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Mã ngành chỉ gồm chữ in hoa và số, từ 2 đến 10 ký tự")
    private String majorCode;

    @NotBlank(message = "Tên ngành không được để trống")
    @Size(min = 3, max = 200, message = "Tên ngành phải từ 3 đến 200 ký tự")
    private String majorName;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;
}
