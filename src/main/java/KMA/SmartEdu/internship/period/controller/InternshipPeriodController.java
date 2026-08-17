package KMA.SmartEdu.internship.period.controller;

import KMA.SmartEdu.core.common.ApiResponse;
import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodResponse;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.service.InternshipPeriodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/internship-periods")
public class InternshipPeriodController {

    private final InternshipPeriodService internshipPeriodService;

    @GetMapping // lấy danh sách đợt thực tập
    public ResponseEntity<ApiResponse<PageResponse<InternshipPeriodResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) InternshipTypeEnum internshipType) {

        return ResponseEntity.ok(
                ApiResponse.success(internshipPeriodService.getAll(page, size, searchName, internshipType)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipPeriodResponse>> getById(@PathVariable Long id) {

        return ResponseEntity.ok(ApiResponse.success(internshipPeriodService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InternshipPeriodResponse>> create(
            @Valid @RequestBody InternshipPeriodRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(internshipPeriodService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InternshipPeriodResponse>> update(
            @PathVariable Long id, @Valid @RequestBody InternshipPeriodRequest request) {

        return ResponseEntity.ok(ApiResponse.success(internshipPeriodService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, @RequestParam String confirmedName) {

        internshipPeriodService.delete(id, confirmedName);

        return ResponseEntity.ok(ApiResponse.successWithMessage("Đã xóa!"));
    }
}
