package KMA.SmartEdu.internship.period.service;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.core.mapper.PageMapper;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodResponse;
import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.mapper.InternshipPeriodMapper;
import KMA.SmartEdu.internship.period.repository.InternshipPeriodRepository;
import KMA.SmartEdu.internship.period.specification.InternshipPeriodSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipPeriodService {

    private final InternshipPeriodRepository internshipPeriodRepository;
    private final InternshipPeriodMapper internshipPeriodMapper;

    @Transactional(readOnly = true)
    public PageResponse<InternshipPeriodResponse> getAll(
            int page, int size, String searchName, InternshipTypeEnum internshipType) {

        if (page < 0) {
            throw new BaseException(ErrorCode.VALIDATION_ERROR);
        }

        if (size < 1 || size > 100) {
            throw new BaseException(ErrorCode.VALIDATION_ERROR);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<InternshipPeriod> spec = Specification.where(InternshipPeriodSpecification.notDeleted())
                .and(InternshipPeriodSpecification.hasName(searchName))
                .and(InternshipPeriodSpecification.hasInternshipType(internshipType));

        Page<InternshipPeriodResponse> responsePage =
                internshipPeriodRepository.findAll(spec, pageable).map(internshipPeriodMapper::toResponse);

        return PageMapper.toPageResponse(responsePage);
    }

    @Transactional(readOnly = true)
    public InternshipPeriodResponse getById(Long id) {

        InternshipPeriod internshipPeriod = internshipPeriodRepository
                .findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new BaseException(ErrorCode.INTERNSHIP_PERIOD_NOT_FOUND));

        return internshipPeriodMapper.toResponse(internshipPeriod);
    }

    @Transactional
    public InternshipPeriodResponse create(InternshipPeriodRequest request) {

        request.setName(request.getName().trim());

        // check trùng tên
        if (internshipPeriodRepository.existsByNameAndDeletedFalse(request.getName())) {

            throw new BaseException(ErrorCode.INTERNSHIP_PERIOD_NAME_DUPLICATED);
        }

        // validate ngày, kiểm tra:
        // registrationDeadline <= startDate <= endDate
        validateDateRange(request);

        InternshipPeriod savedInternshipPeriod = internshipPeriodMapper.toEntity(request);

        internshipPeriodRepository.save(savedInternshipPeriod);

        return internshipPeriodMapper.toResponse(savedInternshipPeriod);
    }

    @Transactional
    public InternshipPeriodResponse update(Long id, InternshipPeriodRequest request) {

        request.setName(request.getName().trim());

        // 1. Kiểm tra InternshipPeriod có tồn tại và chưa bị xóa
        InternshipPeriod currentInternshipPeriod = internshipPeriodRepository
                .findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new BaseException(ErrorCode.INTERNSHIP_PERIOD_NOT_FOUND));

        // 2. Không cho trùng tên với InternshipPeriod khác
        // ID hiện tại được bỏ qua vì record đang update có thể giữ nguyên tên cũ
        if (internshipPeriodRepository.existsByNameAndIdNotAndDeletedFalse(request.getName(), id)) {

            throw new BaseException(ErrorCode.INTERNSHIP_PERIOD_NAME_DUPLICATED);
        }

        // 3. Kiểm tra ngày bắt đầu / ngày kết thúc hợp lệ
        validateDateRange(request);

        // 4. Cập nhật các field còn lại từ request vào entity
        internshipPeriodMapper.updateEntityFromRequest(request, currentInternshipPeriod);

        // 5. Trả về dữ liệu sau khi update
        return internshipPeriodMapper.toResponse(currentInternshipPeriod);
    }

    @Transactional
    public void delete(Long id, String confirmedName) {

        InternshipPeriod currentInternshipPeriod = internshipPeriodRepository
                .findById(id)
                .filter(b -> !b.isDeleted())
                .orElseThrow(() -> new BaseException(ErrorCode.INTERNSHIP_PERIOD_NOT_FOUND));

        // check id + tên xác nhận (giống pattern confirm-before-delete của Faculty/Major)
        if (!currentInternshipPeriod.getName().equals(confirmedName)) {
            throw new BaseException(ErrorCode.INTERNSHIP_PERIOD_ID_NOT_MATCHES_NAME);
        }

        // Soft delete: chỉ đánh dấu đã xóa, không xóa record khỏi database
        currentInternshipPeriod.setDeleted(true);
    }

    private void validateDateRange(InternshipPeriodRequest request) {

        // startDate phải <= endDate
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BaseException(ErrorCode.INTERNSHIP_PERIOD_DATE_INVALID);
        }

        // registrationDeadline phải <= startDate
        if (request.getRegistrationDeadline() != null
                && request.getRegistrationDeadline().isAfter(request.getStartDate())) {

            throw new BaseException(ErrorCode.INTERNSHIP_PERIOD_REGISTRATION_DEADLINE_INVALID);
        }
    }
}
