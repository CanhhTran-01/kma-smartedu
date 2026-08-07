package KMA.SmartEdu.training.major;

import KMA.SmartEdu.core.common.ApiResponse;
import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import KMA.SmartEdu.training.major.dto.MajorResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/majors")
public class MajorController {

    private final MajorService majorService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<MajorResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long facultyId,
            @RequestParam(required = false) String facultyName) {

        return ResponseEntity.ok(
                ApiResponse.success(majorService.getAll(page, size, searchName, code, active, facultyId, facultyName)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MajorResponse>> create(@Valid @RequestBody MajorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(majorService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MajorResponse>> update(
            @PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        return ResponseEntity.ok(ApiResponse.success(majorService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @RequestParam String code) {
        majorService.delete(id, code);
        return ResponseEntity.ok(ApiResponse.successWithMessage("Đã xóa!"));
    }
}
