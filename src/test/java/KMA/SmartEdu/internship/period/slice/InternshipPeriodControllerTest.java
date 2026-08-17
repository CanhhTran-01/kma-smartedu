package KMA.SmartEdu.internship.period.slice;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KMA.SmartEdu.core.common.PageResponse;
import KMA.SmartEdu.internship.period.controller.InternshipPeriodController;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodRequest;
import KMA.SmartEdu.internship.period.dto.InternshipPeriodResponse;
import KMA.SmartEdu.internship.period.enums.InternshipTypeEnum;
import KMA.SmartEdu.internship.period.service.InternshipPeriodService;
import java.time.LocalDate;
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

@WebMvcTest(InternshipPeriodController.class)
class InternshipPeriodControllerTest {
    // =========================================================================
    // MOCK CÁC THÀNH PHẦN JPA AUDITING CHO SLICE TEST (@WebMvcTest)
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
    private InternshipPeriodService internshipPeriodService;

    private InternshipPeriodRequest validRequest;
    private InternshipPeriodResponse validResponse;
    private PageResponse<InternshipPeriodResponse> pageResponse;

    @BeforeEach // set up -> AAA
    void setUp() {
        validRequest = new InternshipPeriodRequest();
        validRequest.setInternshipType(InternshipTypeEnum.BASIC);
        validRequest.setName("Thực tập cơ sở HK1 2026-2027");
        validRequest.setStartDate(LocalDate.of(2026, 10, 1));
        validRequest.setEndDate(LocalDate.of(2026, 12, 31));
        validRequest.setRegistrationDeadline(LocalDate.of(2026, 9, 25));

        validResponse = InternshipPeriodResponse.builder()
                .id(1L)
                .internshipType(InternshipTypeEnum.BASIC)
                .name("Thực tập cơ sở HK1 2026-2027")
                .startDate(LocalDate.of(2026, 10, 1))
                .endDate(LocalDate.of(2026, 12, 31))
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
        when(internshipPeriodService.getAll(anyInt(), anyInt(), any(), any())).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/internship-periods")
                        .param("page", "0")
                        .param("size", "7")
                        .param("searchName", "Thực tập")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].name").value("Thực tập cơ sở HK1 2026-2027"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    // ==========================================
    // TEST CREATE
    // ==========================================
    @Test
    void create_ShouldReturn201_WhenRequestIsValid() throws Exception {
        // Arrange
        when(internshipPeriodService.create(any(InternshipPeriodRequest.class))).thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/internship-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Thực tập cơ sở HK1 2026-2027"))
                .andExpect(jsonPath("$.data.internshipType").value("BASIC"));
    }

    @Test
    void create_ShouldReturn400_WhenNameIsBlank() throws Exception {
        // Arrange
        validRequest.setName(""); // vi phạm @NotBlank và @Size

        // Act & Assert
        mockMvc.perform(post("/api/v1/internship-periods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_ShouldReturn400_WhenInternshipTypeIsMissing() throws Exception {
        // Arrange
        validRequest.setInternshipType(null); // vi phạm @NotNull

        // Act & Assert
        mockMvc.perform(post("/api/v1/internship-periods")
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
        when(internshipPeriodService.update(eq(1L), any(InternshipPeriodRequest.class)))
                .thenReturn(validResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/internship-periods/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Thực tập cơ sở HK1 2026-2027"));
    }

    // ==========================================
    // TEST DELETE
    // ==========================================
    @Test
    void delete_ShouldReturn200_WhenRequestIsValid() throws Exception {
        // Arrange
        doNothing().when(internshipPeriodService).delete(1L, "Thực tập cơ sở HK1 2026-2027");

        // Act & Assert
        mockMvc.perform(delete("/api/v1/internship-periods/{id}", 1L)
                        .param("confirmedName", "Thực tập cơ sở HK1 2026-2027")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Đã xóa!"));
    }

    @Test
    void delete_ShouldReturn400_WhenConfirmedNameIsMissing() throws Exception {
        // Act & Assert: cố tình không truyền param "confirmedName"
        mockMvc.perform(delete("/api/v1/internship-periods/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
