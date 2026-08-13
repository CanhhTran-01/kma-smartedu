package KMA.SmartEdu.internship.period.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import KMA.SmartEdu.core.exception.BaseException;
import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodResponse;
import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.mapper.InternshipPeriodMapper;
import KMA.SmartEdu.internship.period.repository.InternshipPeriodRepository;
import KMA.SmartEdu.internship.period.service.InternshipPeriodService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternshipPeriodServiceTest {

    @Mock
    private InternshipPeriodRepository internshipPeriodRepository;

    @Mock
    private InternshipPeriodMapper internshipPeriodMapper;

    @InjectMocks
    private InternshipPeriodService internshipPeriodService;

    // dữ liệu giả
    private InternshipPeriodRequest request;
    private InternshipPeriodResponse expectedResponse;
    private InternshipPeriod period;

    // Set up -> AAAV
    @BeforeEach
    void setUp() {
        request = new InternshipPeriodRequest();
        request.setInternshipType(InternshipTypeEnum.BASIC);
        request.setName("Thực tập cơ sở HK1 2026-2027");
        request.setStartDate(LocalDate.of(2026, 10, 1));
        request.setEndDate(LocalDate.of(2026, 12, 31));
        request.setRegistrationDeadline(LocalDate.of(2026, 9, 25));

        expectedResponse = InternshipPeriodResponse.builder()
                .internshipType(InternshipTypeEnum.BASIC)
                .name("Thực tập cơ sở HK1 2026-2027")
                .build();

        period = new InternshipPeriod();
        period.setId(1L);
        period.setInternshipType(InternshipTypeEnum.BASIC);
        period.setName("Thực tập cơ sở HK1 2026-2027");
        period.setStartDate(LocalDate.of(2026, 10, 1));
        period.setEndDate(LocalDate.of(2026, 12, 31));
        period.setRegistrationDeadline(LocalDate.of(2026, 9, 25));
        period.setDeleted(false);
    }

    // ==========================================
    // TEST CREATE : trùng tên + ngày không hợp lệ + thành công
    // ==========================================
    @Test
    void create_ShouldReturnResponse_WhenValidRequest() {
        // Arrange
        when(internshipPeriodRepository.existsByNameAndDeletedFalse(request.getName()))
                .thenReturn(false); // cho qua cửa check trùng tên
        when(internshipPeriodMapper.toEntity(request)).thenReturn(period);
        when(internshipPeriodMapper.toResponse(period)).thenReturn(expectedResponse);

        // Act
        InternshipPeriodResponse actualResponse = internshipPeriodService.create(request);

        // Assert & Verify
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getName()).isEqualTo("Thực tập cơ sở HK1 2026-2027");
        assertThat(actualResponse.getInternshipType()).isEqualTo(InternshipTypeEnum.BASIC);
        verify(internshipPeriodRepository).save(period); // save() phải được gọi đúng 1 lần
    }

    @Test
    void create_ShouldThrowException_WhenNameDuplicated() {
        when(internshipPeriodRepository.existsByNameAndDeletedFalse(request.getName()))
                .thenReturn(true); // không cho qua

        assertThatThrownBy(() -> internshipPeriodService.create(request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNSHIP_PERIOD_NAME_DUPLICATED);

        verify(internshipPeriodRepository, never()).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenStartDateAfterEndDate() {
        // Arrange: cố tình đặt ngày bắt đầu sau ngày kết thúc
        request.setStartDate(LocalDate.of(2026, 12, 31));
        request.setEndDate(LocalDate.of(2026, 10, 1));

        when(internshipPeriodRepository.existsByNameAndDeletedFalse(request.getName()))
                .thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> internshipPeriodService.create(request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNSHIP_PERIOD_DATE_INVALID);

        verify(internshipPeriodRepository, never()).save(any());
    }

    // ==========================================
    // TEST UPDATE : period đã xóa + trùng tên + thành công
    // ==========================================
    @Test
    void update_ShouldThrowException_WhenPeriodDeleted() {
        period.setDeleted(true);

        when(internshipPeriodRepository.findById(1L)).thenReturn(Optional.of(period));

        assertThatThrownBy(() -> internshipPeriodService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNSHIP_PERIOD_NOT_FOUND);

        verify(internshipPeriodMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldThrowException_WhenNameDuplicated() {
        // Arrange
        when(internshipPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(internshipPeriodRepository.existsByNameAndIdNotAndDeletedFalse(request.getName(), 1L))
                .thenReturn(true);

        // Act & Assert & Verify
        assertThatThrownBy(() -> internshipPeriodService.update(1L, request))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNSHIP_PERIOD_NAME_DUPLICATED);

        verify(internshipPeriodMapper, never()).updateEntityFromRequest(any(), any());
    }

    @Test
    void update_ShouldReturnResponse_WhenValidRequest() {
        // Arrange
        when(internshipPeriodRepository.findById(1L)).thenReturn(Optional.of(period));
        when(internshipPeriodRepository.existsByNameAndIdNotAndDeletedFalse(request.getName(), 1L))
                .thenReturn(false);
        when(internshipPeriodMapper.toResponse(period)).thenReturn(expectedResponse);

        // Act
        InternshipPeriodResponse actualResponse = internshipPeriodService.update(1L, request);

        // Assert & Verify
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getName()).isEqualTo("Thực tập cơ sở HK1 2026-2027");
        verify(internshipPeriodMapper).updateEntityFromRequest(request, period);
    }

    // ==========================================
    // TEST DELETE : thành công + nhập sai tên xác nhận
    // ==========================================
    @Test
    void delete_ShouldSetDeletedTrue_WhenValidConditions() {
        when(internshipPeriodRepository.findById(1L)).thenReturn(Optional.of(period));

        internshipPeriodService.delete(1L, "Thực tập cơ sở HK1 2026-2027");

        assertThat(period.isDeleted()).isTrue();
    }

    @Test
    void delete_ShouldThrowException_WhenNameNotMatch() {
        when(internshipPeriodRepository.findById(1L)).thenReturn(Optional.of(period));

        assertThatThrownBy(() -> internshipPeriodService.delete(1L, "SAI TÊN"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INTERNSHIP_PERIOD_ID_NOT_MATCHES_NAME);
    }
}
