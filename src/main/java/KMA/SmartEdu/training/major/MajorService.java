package KMA.SmartEdu.training.major;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.core.mapper.PageMapper;
import KMA.SmartEdu.training.faculty.Faculty;
import KMA.SmartEdu.training.faculty.FacultyRepository;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import KMA.SmartEdu.training.major.dto.MajorResponse;
import KMA.SmartEdu.training.major.mapper.MajorMapper;
import java.util.Objects;
import java.util.Optional;
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
public class MajorService {

    private final MajorRepository majorRepository;
    private final MajorMapper majorMapper;
    private final FacultyRepository facultyRepository;

    @Transactional(readOnly = true)
    public PageResponse<MajorResponse> getAll(
            int page, int size, String searchName, String code, Boolean active, Long facultyId, String facultyName) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Specification<Major> spec = Specification.where(MajorSpecification.notDeleted())
                .and(MajorSpecification.hasName(searchName))
                .and(MajorSpecification.hasCode(code))
                .and(MajorSpecification.isActive(active))
                .and(MajorSpecification.hasFacultyId(facultyId))
                .and(MajorSpecification.hasFacultyName(facultyName));

        Page<MajorResponse> responsePage =
                majorRepository.findAll(spec, pageable).map(majorMapper::toResponse);

        return PageMapper.toPageResponse(responsePage);
    }

    @Transactional
    public MajorResponse create(MajorRequest request) {

        // TODO: tạm thời skip concurrent handling tại đây

        Faculty faculty = facultyRepository
                .findById(request.getFacultyId()) // faculty có tồn tại không
                .filter(f -> !f.isDeleted()) // faculty bị xóa chưa
                .orElseThrow(() -> new BaseException(ErrorCode.FACULTY_NOT_FOUND));

        // check trùng code
        if (majorRepository.existsByMajorCode(request.getMajorCode())) {
            throw new BaseException(ErrorCode.MAJOR_CODE_DUPLICATED);
        }

        Major savedMajor = majorMapper.toEntity(request);
        savedMajor.setFaculty(faculty); // major mới thuộc về faculty này
        majorRepository.save(savedMajor);

        return majorMapper.toResponse(savedMajor);
    }

    @Transactional
    public MajorResponse update(Long id, MajorRequest request) {

        // TODO: tạm thời skip concurrent handling tại đây

        Major currentMajor = majorRepository
                .findById(id) // major tồn tại không ?
                .filter(m -> !m.isDeleted()) // major bị xóa chưa ?
                .orElseThrow(() -> new BaseException(ErrorCode.MAJOR_NOT_FOUND));

        // không cho trùng majorCode với record khác (nếu id thực sự bị thay đổi)
        if (majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), id)) {
            throw new BaseException(ErrorCode.MAJOR_CODE_DUPLICATED);
        }

        // cập nhật faculty cho major (nếu facultyId thực sự thay đổi)
        // không tin create(), handle edge-case: Major có thể chưa được gán Faculty nào, nhả NullPointerException
        Long currentFacultyId = Optional.ofNullable(currentMajor.getFaculty())
                .map(Faculty::getId)
                .orElse(null);

        if (!Objects.equals(currentFacultyId, request.getFacultyId())) {
            Faculty newFaculty = facultyRepository
                    .findById(request.getFacultyId())
                    .filter(f -> !f.isDeleted())
                    .orElseThrow(() -> new BaseException(ErrorCode.FACULTY_NOT_FOUND));

            currentMajor.setFaculty(newFaculty);
        }

        majorMapper.updateEntityFromRequest(request, currentMajor);

        return majorMapper.toResponse(currentMajor);
    }

    @Transactional
    public void delete(Long id, String code) {

        // TODO: tạm thời skip concurrent handling tại đây

        Major currentMajor = majorRepository
                .findById(id) // major tồn tại không ?
                .filter(m -> !m.isDeleted()) // major bị xóa chưa ?
                .orElseThrow(() -> new BaseException(ErrorCode.MAJOR_NOT_FOUND));

        /*
        TODO: kiểm tra có lớp hành chính nào thuộc major này ?
        if (administrativeClassRepository.existsByMajorId(id)) {
            throw new BaseException(ErrorCode.MAJOR_HAS_ADMINISTRATIVE_CLASSES);
        }

        TODO: kiểm tra có chương trình đào tạo nào thuộc major này ?
        if (curriculumRepository.existsByMajorId(id)) {
            throw new BaseException(ErrorCode.MAJOR_HAS_CURRICULUMS);
        }
        */

        // check code match
        if (!currentMajor.getMajorCode().equals(code)) {
            throw new BaseException(ErrorCode.MAJOR_ID_NOT_MATCHES_CODE);
        }

        currentMajor.setDeleted(true);
    }
}
