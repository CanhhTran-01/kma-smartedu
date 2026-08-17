package KMA.SmartEdu.internship.period.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.entity.InternshipPeriod;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.repository.InternshipPeriodRepository;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class InternshipPeriodIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InternshipPeriodRepository internshipPeriodRepository;

    private InternshipPeriodRequest request;

    @BeforeEach
    void setUp() {
        request = new InternshipPeriodRequest();
        request.setInternshipType(InternshipTypeEnum.BASIC);
        request.setName("TEST - Đợt thực tập kiểm thử");
        // dùng khoảng ngày trong quá khứ để status luôn deterministic = ENDED,
        // không phụ thuộc vào ngày chạy test thực tế
        request.setStartDate(LocalDate.of(2020, 1, 1));
        request.setEndDate(LocalDate.of(2020, 1, 31));
        request.setRegistrationDeadline(LocalDate.of(2019, 12, 25));
    }

    // ==========================================
    // TEST GET : chạy trước (tránh bị nhảy số do test dưới chạy trước)
    // ==========================================
    @Test
    void shouldReturnAllInternshipPeriodsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/internship-periods").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(4))) // 4 record mẫu từ migration
                .andExpect(jsonPath("$.data.totalElements").value(4))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.last").value(true)); // 4 record < size mặc định (7) -> đã là trang cuối
    }

    // ==========================================
    // TEST CREATE : chạy sau
    // ==========================================
    @Test
    void createInternshipPeriod_success_shouldPersistToRealDatabase() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/internship-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.name").value("TEST - Đợt thực tập kiểm thử"))
                .andExpect(jsonPath("$.data.internshipType").value("BASIC"))
                .andExpect(jsonPath("$.data.status").value("ENDED")) // do dùng ngày quá khứ
                .andReturn();

        // check xem entity thật sự được lưu chưa
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        InternshipPeriod saved =
                internshipPeriodRepository.findById(id.longValue()).orElseThrow();

        assertThat(saved.getName()).isEqualTo("TEST - Đợt thực tập kiểm thử");
        assertThat(saved.getInternshipType()).isEqualTo(InternshipTypeEnum.BASIC);
        assertThat(saved.isDeleted()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull(); // check auditing

        // migration đang có 4 record -> tạo thêm 1 phải thành 5
        assertThat(internshipPeriodRepository.count()).isEqualTo(5);
    }

    // ==========================================
    // TEST VALIDATION NGÀY : đặc thù riêng của InternshipPeriod, không có bên Faculty/Major
    // ==========================================
    @Test
    void createInternshipPeriod_shouldReturn400_WhenStartDateAfterEndDate() throws Exception {
        request.setStartDate(LocalDate.of(2026, 12, 31));
        request.setEndDate(LocalDate.of(2026, 10, 1));

        mockMvc.perform(post("/api/v1/internship-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
