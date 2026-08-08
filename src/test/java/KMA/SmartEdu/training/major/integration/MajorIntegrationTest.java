package KMA.SmartEdu.training.major.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.core.exception.ErrorCode;
import KMA.SmartEdu.training.major.Major;
import KMA.SmartEdu.training.major.MajorRepository;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import com.jayway.jsonpath.JsonPath;
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
public class MajorIntegrationTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MajorRepository majorRepository;

    private MajorRequest request;

    @BeforeEach
    void setUp() {
        request = new MajorRequest();
        request.setFacultyId(1L);
        request.setMajorCode("TEST01");
        request.setMajorName("Test tạo mới một ngành");
        request.setDescription("Ngành đào tạo ...............");
    }

    // ==========================================
    // TEST GET : chạy trước
    // ==========================================
    @Test
    void shouldReturnAllMajorsSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/majors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(7))) // lấy 7 elements cho 1 page như mặc định
                .andExpect(jsonPath("$.data.totalElements").value(13)) // tổng có 13 record trong csdl
                .andExpect(jsonPath("$.data.pageNumber").value(0)) // page số 0 như mặc định
                .andExpect(jsonPath("$.data.last").value(false)); // không thể là trang cuối
    }

    @Test
    void shouldFilterMajorsByFacultyId() throws Exception {
        mockMvc.perform(get("/api/v1/majors").param("facultyId", "1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2))) // faculty có id=1 có 2 majors
                .andExpect(jsonPath("$.data.totalElements").value(2)) // sau khi filter còn 2 record
                .andExpect(jsonPath("$.data.content[0].facultyId").value(1))
                .andExpect(jsonPath("$.data.content[1].facultyId").value(1))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.last").value(true)); // Chỉ có 2 record nên là page cuối
    }

    // ==========================================
    // TEST CREATE : chạy sau
    // ==========================================
    @Test
    void createMajor_success_shouldPersistToRealDatabase() throws Exception {
        long countBefore = majorRepository.count(); // đếm số record hiện tại, không set cứng = 13

        MvcResult result = mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.majorCode").value("TEST01"))
                .andExpect(jsonPath("$.data.majorName").value("Test tạo mới một ngành"))
                .andExpect(jsonPath("$.data.facultyId").value(1L))
                .andExpect(jsonPath("$.data.active").value(true))
                .andReturn();

        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        Major saved = majorRepository.findById(id.longValue()).orElseThrow();

        // lưu major chuẩn chưa ?
        assertThat(saved.getMajorCode()).isEqualTo("TEST01");
        assertThat(saved.getMajorName()).isEqualTo("Test tạo mới một ngành");
        assertThat(saved.getActive()).isTrue();
        assertThat(saved.isDeleted()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull();

        // set faculty cho major chuẩn chưa ?
        assertThat(saved.getFaculty()).isNotNull();
        assertThat(saved.getFaculty().getId()).isEqualTo(1L);

        // tổng record có tăng lên 1 không ?
        assertThat(majorRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    void createMajor_shouldReturn404_WhenFacultyDoesNotExist() throws Exception {
        request.setFacultyId(999999L); // faculty không tồn tại

        mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ErrorCode.FACULTY_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.FACULTY_NOT_FOUND.getMessage()));
    }
}
