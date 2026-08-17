package KMA.SmartEdu.training.faculty.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FacultyRequest {

    @NotBlank(message = "Mã khoa không được để trống")
    @Pattern(regexp = "^[A-Z]{2,10}$", message = "Mã khoa chỉ gồm chữ cái in hoa từ 2 đến 10 ký tự")
    private String facultyCode;

    @NotBlank(message = "Tên khoa không được để trống")
    @Size(min = 3, max = 100, message = "Tên khoa phải từ 3 đến 100 ký tự")
    private String facultyName;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String description;
}
