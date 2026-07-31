package KMA.SmartEdu.training.faculty;

import KMA.SmartEdu.core.common.ApiResponse;
import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
import KMA.SmartEdu.training.faculty.dto.FacultyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faculties")
public class FacultyController {

    private final FacultyService facultyService;

    // TODO: API thông tin chi tiết của khoa -> mã, các ngành, số lượng học sinh, số giảng viên, ...
    // TODO: API PATH /activate
    // TODO: Validation Later

    @GetMapping // lấy danh sách khoa
    public ResponseEntity<ApiResponse<PageResponse<FacultyResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean active) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.getAll(page, size, searchName, code, active)));
    }

    @PostMapping // tạo mới khoa
    public ResponseEntity<ApiResponse<FacultyResponse>> create(@RequestBody FacultyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(facultyService.create(request)));
    }

    @PutMapping("/{id}") // update khoa
    public ResponseEntity<ApiResponse<FacultyResponse>> update(
            @PathVariable Long id, @RequestBody FacultyRequest request) {
        return ResponseEntity.ok(ApiResponse.success(facultyService.update(id, request)));
    }

    @DeleteMapping("/{id}") // soft delete khoa
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @RequestParam String confirmedCode) {
        facultyService.delete(id, confirmedCode);
        return ResponseEntity.ok(ApiResponse.successWithMessage("Đã xóa!"));
    }
}
