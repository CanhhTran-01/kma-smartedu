package KMA.SmartEdu.training.major.slice;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.training.major.MajorController;
import KMA.SmartEdu.training.major.MajorService;
import KMA.SmartEdu.training.major.dto.MajorRequest;
import KMA.SmartEdu.training.major.dto.MajorResponse;
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

@WebMvcTest(MajorController.class)
public class MajorControllerTest {
    @MockitoBean(name = "jpaAuditingHandler")
    private AuditingHandler auditingHandler;

    @MockitoBean(name = "jpaMappingContext")
    private Object jpaMappingContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MajorService majorService;

    private MajorRequest validRequest;
    private MajorResponse validResponse;
    private PageResponse<MajorResponse> pageResponse;

    @BeforeEach
    void setUp() {
        validRequest = new MajorRequest();
        validRequest.setFacultyId(1L);
        validRequest.setMajorCode("IT01");
        validRequest.setMajorName("Kỹ thuật phần mềm");

        validResponse = MajorResponse.builder()
                .id(1L)
                .majorCode("IT01")
                .majorName("Kỹ thuật phần mềm")
                .facultyId(1L)
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
        when(majorService.getAll(anyInt(), anyInt(), any(), any(), any(), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/majors")
                        .param("page", "0")
                        .param("size", "10")
                        .param("facultyName", "Công nghệ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].majorCode").value("IT01"))
                .andExpect(jsonPath("$.data.content[0].facultyName").value("Công nghệ thông tin"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    // ==========================================
    // TEST CREATE
    // ==========================================
    @Test
    void create_ShouldReturn201_WhenRequestIsValid() throws Exception {
        when(majorService.create(any(MajorRequest.class))).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.majorCode").value("IT01"))
                .andExpect(jsonPath("$.data.facultyId").value(1L));
    }

    @Test
    void create_ShouldReturn400_WhenFacultyIdIsMissing() throws Exception {
        validRequest.setFacultyId(null); // vi phạm @NotNull

        mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturn400_WhenMajorCodePatternIsInvalid() throws Exception {
        validRequest.setMajorCode("it01"); // cố tình viết thường, vi phạm @Pattern

        mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturn400_WhenMajorNameTooShort() throws Exception {
        validRequest.setMajorName("AB"); // vi phạm @Size(min = 3)

        mockMvc.perform(post("/api/v1/majors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // TEST UPDATE
    // ==========================================
    @Test
    void update_ShouldReturn200_WhenRequestIsValid() throws Exception {
        when(majorService.update(eq(1L), any(MajorRequest.class))).thenReturn(validResponse);

        mockMvc.perform(put("/api/v1/majors/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.majorCode").value("IT01"));
    }

    // ==========================================
    // TEST DELETE
    // ==========================================
    @Test
    void delete_ShouldReturn200_WhenRequestIsValid() throws Exception {
        doNothing().when(majorService).delete(1L, "IT01");

        mockMvc.perform(delete("/api/v1/majors/{id}", 1L).param("code", "IT01").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã xóa!"));
    }

    @Test
    void delete_ShouldReturn400_WhenCodeIsMissing() throws Exception {
        mockMvc.perform(delete("/api/v1/majors/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
