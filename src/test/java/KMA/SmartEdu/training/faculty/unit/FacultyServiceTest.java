package KMA.SmartEdu.training.faculty.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.training.faculty.Faculty;
import KMA.SmartEdu.training.faculty.FacultyRepository;
import KMA.SmartEdu.training.faculty.FacultyService;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
import KMA.SmartEdu.training.faculty.dto.FacultyResponse;
import KMA.SmartEdu.training.faculty.mapper.FacultyMapper;
import KMA.SmartEdu.training.major.MajorRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private FacultyMapper facultyMapper;

    @Mock
    private MajorRepository majorRepository;

    @InjectMocks
    private FacultyService facultyService;

    // dữ liệu giả
    private FacultyRequest request;
    private FacultyResponse expectedResponse;
    private Faculty faculty;

    // Set up -> AAAV
    @BeforeEach
    void setUp() {
        request = new FacultyRequest();
        request.setFacultyCode("CNTT");
        request.setFacultyName("Công nghệ thông tin");

        expectedResponse = FacultyResponse.builder()
                .facultyCode("CNTT")
                .facultyName("Công nghệ thông tin")
                .build();

        faculty = new Faculty();
        faculty.setId(1L);
        faculty.setFacultyCode("CNTT");
        faculty.setDeleted(false);
    }

    // ==========================================
    // TEST CREATE : duplicated facultyCode + not duplicated facultyCode
    // ==========================================
    @Test
    void create_ShouldReturnResponse_WhenCodeNotDuplicated() {
        // Arrange - chuẩn bị kịch bản
        when(facultyRepository.existsByFacultyCode(request.getFacultyCode())).thenReturn(false); // cho qua cửa check
        when(facultyMapper.toEntity(request)).thenReturn(faculty); // phải return về entity đã mock
        when(facultyMapper.toResponse(faculty)).thenReturn(expectedResponse); // phải return về response đã mock

        // Act - thực thi
        FacultyResponse actualResponse = facultyService.create(request); // gọi create với request đã mock

        // Assert & Verify - Kiểm tra
        assertThat(actualResponse).isNotNull(); // response không được null
        assertThat(actualResponse.getFacultyCode()).isEqualTo("CNTT"); // code phải đúng
        assertThat(actualResponse.getFacultyName()).isEqualTo("Công nghệ thông tin"); // name phải đúng
        verify(facultyRepository).save(faculty); // save() phải được gọi đúng 1 lần
    }

    @Test
    void create_ShouldThrowException_WhenCodeDuplicated() {
        when(facultyRepository.existsByFacultyCode(request.getFacultyCode())).thenReturn(true); // không cho qua

        assertThatThrownBy(() -> facultyService.create(request))
                .isInstanceOf(BaseException.class) // phải ném đúng BaseException
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_CODE_DUPLICATED); // phải đúng errorCode

        verify(facultyRepository, never()).save(any()); // save() không được gọi với bất kì param nào
    }

    // ==========================================
    // TEST UPDATE : faculty đã xóa + duplicate facultyCode + thành công
    // ==========================================
    @Test
    void update_ShouldThrowException_WhenFacultyDeleted() {
        faculty.setDeleted(true);

        when(facultyRepository.findById(1L))
                .thenReturn(Optional.of(faculty)); // tìm được record có deleted = true, id mong đợi

        assertThatThrownBy(() -> facultyService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_NOT_FOUND);

        verify(facultyMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldThrowException_WhenCodeDuplicated() {
        // Arrange
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.existsByFacultyCodeAndIdNot(request.getFacultyCode(), 1L))
                .thenReturn(true);

        // Act & Assert & Verify
        assertThatThrownBy(() -> facultyService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_CODE_DUPLICATED);

        verify(facultyMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Arrange
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(facultyRepository.existsByFacultyCodeAndIdNot(request.getFacultyCode(), 1L))
                .thenReturn(false);
        when(facultyMapper.toResponse(faculty)).thenReturn(expectedResponse);

        // Act
        FacultyResponse actualResponse = facultyService.update(1L, request);

        // Assert & Verify
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getFacultyCode()).isEqualTo("CNTT");
        assertThat(actualResponse.getFacultyName()).isEqualTo("Công nghệ thông tin");
        verify(facultyMapper).updateEntityFromRequest(request, faculty);
    }

    // ==========================================
    // TEST DELETE : thành công + Nhập sai mã khoa + khoa vẫn đang có majors
    // ==========================================
    @Test
    void delete_ShouldSetDeletedTrue_WhenValidConditions() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(majorRepository.existsByFacultyId(1L)).thenReturn(false);

        facultyService.delete(1L, "CNTT");

        assertThat(faculty.isDeleted()).isTrue();
    }

    @Test
    void delete_ShouldThrowException_WhenCodeNotMatch() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));

        assertThatThrownBy(() -> facultyService.delete(1L, "WRONG_CODE"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_ID_NOT_MATCHES_CODE);
    }

    @Test
    void delete_ShouldThrowException_WhenFacultyHasMajors() {
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(faculty));
        when(majorRepository.existsByFacultyId(1L)).thenReturn(true);

        assertThatThrownBy(() -> facultyService.delete(1L, "CNTT"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FACULTY_HAS_MAJORS);
    }
}
