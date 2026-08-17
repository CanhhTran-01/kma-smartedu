package KMA.SmartEdu.training.faculty.slice;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.training.faculty.FacultyController;
import KMA.SmartEdu.training.faculty.FacultyService;
import KMA.SmartEdu.training.faculty.dto.FacultyRequest;
import KMA.SmartEdu.training.faculty.dto.FacultyResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(FacultyController.class)
class FacultyControllerTest {
    // =========================================================================
    // MOCK CÁC THÀNH PHẦN JPA AUDITING CHO SLICE TEST (@WebMvcTest)
    //
    // Nguyên nhân: Do ứng dụng có bật tính năng JPA Auditing, nhưng @WebMvcTest
    // chỉ cô lập tầng Web (Controller) nên không khởi tạo sẵn DataSource/Metamodel.
    // Giải pháp: Khai báo mock các bean này để đánh lừa Spring Boot, giúp Context
    // tải thành công mà không bị văng lỗi khởi tạo cơ sở dữ liệu.
    // =========================================================================
    @MockitoBean(name = "jpaAuditingHandler")
    private AuditingHandler auditingHandler;

    @MockitoBean(name = "jpaMappingContext")
    private Object jpaMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FacultyService facultyService;

    private FacultyRequest validRequest;
    private FacultyResponse validResponse;
    private PageResponse<FacultyResponse> pageResponse;

    @BeforeEach // set up -> AAA
    void setUp() {
        validRequest = new FacultyRequest();
        validRequest.setFacultyCode("CNTT");
        validRequest.setFacultyName("Công nghệ thông tin");

        validResponse = FacultyResponse.builder()
                .id(1L)
                .facultyCode("CNTT")
                .facultyName("Công nghệ thông tin")
                .build();

        pageResponse = new PageResponse<>();
        pageResponse.setContent(List.of(validResponse));
        pageResponse.setTotalElements(1);
    }

    // ==========================================
    // TEST GET ALL
    // ==========================================
    @Test
    void getAll_ShouldReturn200AndPageResponse_WhenRequestIsValid() throws Exception {
        // Arrange
        when(facultyService.getAll(anyInt(), anyInt(), any(), any(), any())).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/faculties")
                        .param("page", "0")
                        .param("size", "10")
                        .param("searchName", "Công nghệ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // status phải là 200
                .andExpect(jsonPath("$.data.content[0].facultyCode").value("CNTT"))
                .andExpect(jsonPath("$.data.totalElements").value(1)); // số record phải đúng
    }

    // ==========================================
    // TEST CREATE
    // ==========================================
    @Test
    void create_ShouldReturn201_WhenRequestIsValid() throws Exception {
        // Arrange
        when(facultyService.create(any(FacultyRequest.class))).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.facultyCode").value("CNTT"))
                .andExpect(jsonPath("$.data.facultyName").value("Công nghệ thông tin"));
    }

    @Test
    void create_ShouldReturn400_WhenFacultyCodePatternIsInvalid() throws Exception {
        // Arrange
        validRequest.setFacultyCode("cntt"); // cố tình viết thường

        // Act & Assert
        mockMvc.perform(post("/api/v1/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest()); // ném lỗi 400
    }

    @Test
    void create_ShouldReturn400_WhenFacultyNameIsBlank() throws Exception {
        // Arrange
        validRequest.setFacultyName(""); // Vi phạm @NotBlank và @Size

        // Act & Assert
        mockMvc.perform(post("/api/v1/faculties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // TEST UPDATE
    // ==========================================
    @Test
    void update_ShouldReturn200_WhenRequestIsValid() throws Exception {
        // Arrange
        when(facultyService.update(eq(1L), any(FacultyRequest.class))).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/faculties/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.facultyCode").value("CNTT"))
                .andExpect(jsonPath("$.data.facultyName").value("Công nghệ thông tin"));
    }

    // ==========================================
    // TEST DELETE
    // ==========================================
    @Test
    void delete_ShouldReturn200_WhenRequestIsValid() throws Exception {
        // Arrange
        doNothing().when(facultyService).delete(1L, "CNTT");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/faculties/{id}", 1L)
                        .param("confirmedCode", "CNTT")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã xóa!"));
    }

    @Test
    void delete_ShouldReturn400_WhenConfirmedCodeIsMissing() throws Exception {
        // Act & Assert: cố tình không truyền param "confirmedCode"
        mockMvc.perform(delete("/api/v1/faculties/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
