package KMA.SmartEdu.training.faculty.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.training.faculty.Faculty;
import KMA.SmartEdu.training.faculty.FacultyRepository;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
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
public class FacultyIntegrationTest {

    @Container
    @ServiceConnection // Spring Boot tự động nhận diện và tiêm URL, User, Pass vào Datasource
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FacultyRepository facultyRepository;

    private FacultyRequest request;

    @BeforeEach
    void setUp() {
        request = new FacultyRequest();
        request.setFacultyCode("TEST");
        request.setFacultyName("Test tạo mới một khoa");
        request.setDescription("Khoa đào tạo ...............");
    }

    // ==========================================
    // TEST GET : chạy trước (tránh bị nhảy lên 21 do test dưới chạy trước)
    // ==========================================
    @Test
    void shouldReturnAllFacultiesSuccessfully() throws Exception {
        mockMvc.perform(get("/api/v1/faculties").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(7))) // lấy 7 elements cho 1 page như mặc định
                .andExpect(jsonPath("$.data.totalElements").value(20)) // tổng có 20 record trong csdl
                .andExpect(jsonPath("$.data.pageNumber").value(0)) // page số 0 như mặc định
                .andExpect(jsonPath("$.data.last").value(false)); // không thể là trang cuối
    }

    // ==========================================
    // TEST UPDATE : chạy sau
    // ==========================================
    @Test
    void createFaculty_success_shouldPersistToRealDatabase() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.facultyCode").value("TEST"))
                .andExpect(jsonPath("$.data.facultyName").value("Test tạo mới một khoa"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andReturn();

        // check xem entity thật sự được lưu chưa
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        Faculty saved = facultyRepository.findById(id.longValue()).orElseThrow();

        assertThat(saved.getFacultyCode()).isEqualTo("TEST");
        assertThat(saved.getFacultyName()).isEqualTo("Test tạo mới một khoa");
        assertThat(saved.getActive()).isTrue();
        assertThat(saved.isDeleted()).isFalse();
        assertThat(saved.getCreatedAt()).isNotNull(); // check auditing

        // check số lượng bản ghi: V1_init_schema.sql đang có 20 record -> phải là 21 record
        assertThat(facultyRepository.count()).isEqualTo(21);
    }
}
