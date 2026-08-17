package KMA.SmartEdu.training.major.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.training.faculty.Faculty;
import KMA.SmartEdu.training.faculty.FacultyRepository;
import KMA.SmartEdu.training.major.Major;
import KMA.SmartEdu.training.major.MajorRepository;
import KMA.SmartEdu.training.major.MajorService;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import KMA.SmartEdu.training.major.dto.MajorResponse;
import KMA.SmartEdu.training.major.mapper.MajorMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MajorServiceTest {
    @Mock
    private MajorRepository majorRepository;

    @Mock
    private MajorMapper majorMapper;

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private MajorService majorService;

    private MajorRequest request;
    private MajorResponse expectedResponse;
    private Major major;
    private Faculty faculty;

    @BeforeEach
    void setUp() {
        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setFacultyCode("CNTT");
        faculty.setFacultyName("Công nghệ thông tin");
        faculty.setDeleted(false);

        request = new MajorRequest();
        request.setFacultyId(1L);
        request.setMajorCode("IT01");
        request.setMajorName("Kỹ thuật phần mềm");

        expectedResponse = MajorResponse.builder()
                .majorCode("IT01")
                .majorName("Kỹ thuật phần mềm")
                .facultyId(1L)
                .facultyName("Công nghệ thông tin")
                .build();

        major = new Major();
        major.setId(1L);
        major.setMajorCode("IT01");
        major.setFaculty(faculty);
        major.setDeleted(false);
    }

    // ==========================================
    // TEST CREATE : faculty không tồn tại + faculty đã xóa + code trùng + thành công
    // ==========================================
    @Test
    void create_ShouldThrowException_WhenFacultyNotFound() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> majorService.create(request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_NOT_FOUND);

        verify(majorRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenFacultyDeleted() {
        faculty.setDeleted(true);
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        assertThatThrownBy(() -> majorService.create(request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_NOT_FOUND);

        verify(majorRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenMajorCodeDuplicated() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(majorRepository.existsByMajorCode(request.getMajorCode())).thenReturn(true);

        assertThatThrownBy(() -> majorService.create(request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MAJOR_CODE_DUPLICATED);

        verify(majorRepository, never()).save(any());
    }

    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(majorRepository.existsByMajorCode(request.getMajorCode())).thenReturn(false);
        when(majorMapper.toEntity(request)).thenReturn(major); // mapper bỏ qua faculty
        when(majorMapper.toResponse(major)).thenReturn(expectedResponse);

        MajorResponse actualResponse = majorService.create(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getMajorCode()).isEqualTo("IT01");
        assertThat(actualResponse.getFacultyId()).isEqualTo(1L);
        verify(majorRepository).save(major);
        assertThat(major.getFaculty()).isEqualTo(faculty); // service phải tự set faculty
    }

    // ==========================================
    // TEST UPDATE : major đã xóa + code trùng + facultyId không đổi + facultyId đổi + faculty mới không tồn tại
    // ==========================================
    @Test
    void update_ShouldThrowException_WhenMajorDeleted() {
        major.setDeleted(true);
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));

        assertThatThrownBy(() -> majorService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MAJOR_NOT_FOUND);

        verify(majorMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldThrowException_WhenCodeDuplicated() {
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> majorService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MAJOR_CODE_DUPLICATED);

        verify(majorMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldNotRefetchFaculty_WhenFacultyIdUnchanged() {
        // request.facultyId = 1L, currentMajor.faculty.id = 1L
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), 1L))
                .thenReturn(false);
        when(majorMapper.toResponse(major)).thenReturn(expectedResponse);

        majorService.update(1L, request);

        verify(facultyRepository, never()).findById(any()); // facultyId không đổi, ko gọi facultyRepository.findById()
        verify(majorMapper).updateEntityFromRequest(request, major);
    }

    @Test
    void update_ShouldRefetchFaculty_WhenFacultyIdChanged() {
        // đổi sang khoa khác (id = 2L)
        request.setFacultyId(2L);
        Faculty anotherFaculty = new Faculty();
        anotherFaculty.setId(2L);
        anotherFaculty.setDeleted(false);

        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), 1L))
                .thenReturn(false);
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(anotherFaculty));
        when(majorMapper.toResponse(major)).thenReturn(expectedResponse);

        // Act
        majorService.update(1L, request);

        // Assert & Verify
        verify(facultyRepository).findById(2L);
        verify(majorMapper).updateEntityFromRequest(request, major);
        assertThat(major.getFaculty()).isEqualTo(anotherFaculty);
    }

    @Test
    void update_ShouldThrowException_WhenNewFacultyNotFound() {
        request.setFacultyId(2L);
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), 1L))
                .thenReturn(false);
        when(facultyRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> majorService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_NOT_FOUND);

        verify(majorMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldHandleNullFaculty_WithoutThrowingNPE() {
        // major hiện tại chưa có faculty nào (edge-case)
        major.setFaculty(null);

        // tạo 1 faculty khác
        Faculty anotherFaculty = new Faculty();
        anotherFaculty.setId(2L);
        anotherFaculty.setDeleted(false);

        // major.faculty = null, anotherFaculty = 2L, test major.setFaculty(anotherFaculty)
        request.setFacultyId(2L);

        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));
        when(majorRepository.existsByMajorCodeAndIdNot(request.getMajorCode(), 1L))
                .thenReturn(false);
        when(facultyRepository.findById(2L)).thenReturn(Optional.of(anotherFaculty));
        when(majorMapper.toResponse(major)).thenReturn(expectedResponse);

        MajorResponse result = majorService.update(1L, request);

        assertThat(major.getFaculty()).isEqualTo(anotherFaculty);
        assertThat(result).isEqualTo(expectedResponse);

        verify(facultyRepository).findById(2L);
        verify(majorMapper).updateEntityFromRequest(request, major);
        verify(majorMapper).toResponse(major);
    }

    // ==========================================
    // TEST DELETE : thành công + sai mã + không tồn tại
    // ==========================================
    @Test
    void delete_ShouldSetDeletedTrue_WhenValidConditions() {
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));

        majorService.delete(1L, "IT01");

        assertThat(major.isDeleted()).isTrue();
    }

    @Test
    void delete_ShouldThrowException_WhenCodeNotMatch() {
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));

        assertThatThrownBy(() -> majorService.delete(1L, "WRONG_CODE"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MAJOR_ID_NOT_MATCHES_CODE);

        assertThat(major.isDeleted()).isFalse(); // không được xóa khi code sai
    }

    @Test
    void delete_ShouldThrowException_WhenMajorAlreadyDeleted() {
        major.setDeleted(true);
        when(majorRepository.findById(1L)).thenReturn(Optional.of(major));

        assertThatThrownBy(() -> majorService.delete(1L, "IT01"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MAJOR_NOT_FOUND);
    }
}
