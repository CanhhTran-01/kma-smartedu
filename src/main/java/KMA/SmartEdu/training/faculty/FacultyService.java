package KMA.SmartEdu.training.faculty;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.core.mapper.PageMapper;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
import KMA.SmartEdu.training.faculty.dto.FacultyResponse;
import KMA.SmartEdu.training.faculty.mapper.FacultyMapper;
import KMA.SmartEdu.training.major.MajorRepository;
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
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final FacultyMapper facultyMapper;
    private final MajorRepository majorRepository;

    @Transactional(readOnly = true)
    public PageResponse<FacultyResponse> getAll(int page, int size, String searchName, String code, Boolean active) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Bộ lọc: mã - tên - trạng thái - chưa xóa ...
        Specification<Faculty> spec = Specification.where(FacultySpecification.notDeleted())
                .and(FacultySpecification.hasName(searchName))
                .and(FacultySpecification.hasCode(code))
                .and(FacultySpecification.isActive(active)); // null: lấy tất, true: chỉ faculty đang hoạt động

        Page<FacultyResponse> responsePage =
                facultyRepository.findAll(spec, pageable).map(facultyMapper::toResponse);

        return PageMapper.toPageResponse(responsePage);
    }

    @Transactional
    public FacultyResponse create(FacultyRequest request) {

        // TODO: có cần try-catch cho "DataIntegrityException" ?

        // Chỉ check trùng code khi tạo mới
        // TODO: soft delete,làm rõ: cho phép tạo mới một khoa với facultyCode trùng với record đã bị xóa mềm không?
        if (facultyRepository.existsByFacultyCode(request.getFacultyCode())) {
            throw new BaseException(ErrorCode.FACULTY_CODE_DUPLICATED);
        }

        Faculty savedFaculty = facultyMapper.toEntity(request);
        facultyRepository.save(savedFaculty);

        return facultyMapper.toResponse(savedFaculty);
    }

    @Transactional
    public FacultyResponse update(Long id, FacultyRequest request) {

        // TODO: có cần xử lý case "lost update" ?

        // check tồn tại
        Faculty currentFaculty =
                facultyRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.FACULTY_NOT_FOUND));

        // đã được xóa rồi -> not found
        if (currentFaculty.isDeleted()) {
            throw new BaseException(ErrorCode.FACULTY_NOT_FOUND);
        }

        // Không cho phép trùng facultyCode với bản ghi khác
        // TODO: soft delete,làm rõ: cho phép đổi facultyCode thành mã đang thuộc về một khoa đã bị xóa mềm không?
        if (facultyRepository.existsByFacultyCodeAndIdNot(request.getFacultyCode(), id)) {
            throw new BaseException(ErrorCode.FACULTY_CODE_DUPLICATED);
        }

        facultyMapper.updateEntityFromRequest(request, currentFaculty);
        return facultyMapper.toResponse(currentFaculty);
    }

    @Transactional
    public void delete(Long id, String code) {

        // check tồn tại
        Faculty currentFaculty =
                facultyRepository.findById(id).orElseThrow(() -> new BaseException(ErrorCode.FACULTY_NOT_FOUND));

        // đã được xóa rồi -> not found
        if (currentFaculty.isDeleted()) {
            throw new BaseException(ErrorCode.FACULTY_NOT_FOUND);
        }

        // check id + code
        if (!currentFaculty.getFacultyCode().equals(code)) {
            throw new BaseException(ErrorCode.FACULTY_ID_NOT_MATCHES_CODE);
        }

        // check khoa faculty có đang có majors không
        if (majorRepository.existsByFacultyId(id)) {
            throw new BaseException(ErrorCode.FACULTY_HAS_MAJORS);
        }
        currentFaculty.setDeleted(true);
    }
}
