package edu.famu.cop3060.yard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.famu.cop3060.yard.dto.CreateOpportunityDTO;
import edu.famu.cop3060.yard.dto.OpportunityDTO;
import edu.famu.cop3060.yard.dto.UpdateOpportunityDTO;
import edu.famu.cop3060.yard.service.OpportunitiesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc tests for OpportunitiesController.
 * Uses @WebMvcTest to load only the web layer.
 * Uses @MockBean to substitute OpportunitiesService with a Mockito mock.
 */
@WebMvcTest(controllers = {OpportunitiesController.class, ValidationExceptionHandler.class})
class OpportunitiesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpportunitiesService service;

    @Autowired
    private ObjectMapper objectMapper;

    // -------------------------------------------------------------------------
    // Part 1 — Required Test 1
    // GET /api/opportunities returns 200 and a JSON array
    // -------------------------------------------------------------------------
    @Test
    void listOpportunities_returns200AndJsonArray() throws Exception {
        List<OpportunityDTO> fakeList = List.of(
            new OpportunityDTO("opp-001", "UNCF STEM Scholars Program", "Scholarship",
                "UNCF", "2025-04-15", "Merit scholarship for HBCU students.",
                List.of("STEM", "paid"), "https://uncf.org"),
            new OpportunityDTO("opp-002", "Google HBCU Career Residency", "Fellowship",
                "Google", "2025-02-28", "Paid summer fellowship at Google.",
                List.of("tech", "paid"), "https://google.com")
        );

        when(service.getOpportunities(null, null)).thenReturn(fakeList);

        mockMvc.perform(get("/api/opportunities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("UNCF STEM Scholars Program"));
    }

    // -------------------------------------------------------------------------
    // Part 1 — Required Test 2
    // GET /api/opportunities/{id} returns 200 and the correct title for a known ID
    // -------------------------------------------------------------------------
    @Test
    void getOpportunity_knownId_returns200AndCorrectTitle() throws Exception {
        OpportunityDTO fake = new OpportunityDTO(
            "opp-001", "UNCF STEM Scholars Program", "Scholarship",
            "UNCF", "2025-04-15", "Merit scholarship.",
            List.of("STEM", "paid"), "https://uncf.org"
        );

        when(service.getOpportunityById("opp-001")).thenReturn(Optional.of(fake));

        mockMvc.perform(get("/api/opportunities/opp-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("UNCF STEM Scholars Program"));
    }

    // -------------------------------------------------------------------------
    // Part 1 — Bonus Test
    // GET /api/opportunities/{id} returns 404 for an unknown ID
    // -------------------------------------------------------------------------
    @Test
    void getOpportunity_unknownId_returns404() throws Exception {
        when(service.getOpportunityById("opp-999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/opportunities/opp-999"))
            .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // Part 2 — Required Test 1
    // POST /api/opportunities returns 201 with the new record including generated ID
    // -------------------------------------------------------------------------
    @Test
    void createOpportunity_validBody_returns201WithGeneratedId() throws Exception {
        CreateOpportunityDTO requestBody = new CreateOpportunityDTO(
            "New Test Scholarship", "Scholarship", "Test Sponsor",
            "2025-12-01", "A great scholarship for test students.",
            List.of("STEM", "undergrad"), "https://example.com/scholarship"
        );

        OpportunityDTO savedDto = new OpportunityDTO(
            "opp-011", "New Test Scholarship", "Scholarship",
            "Test Sponsor", "2025-12-01", "A great scholarship for test students.",
            List.of("STEM", "undergrad"), "https://example.com/scholarship"
        );

        when(service.createOpportunity(any(CreateOpportunityDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/opportunities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("opp-011"))
            .andExpect(jsonPath("$.title").value("New Test Scholarship"));
    }

    // -------------------------------------------------------------------------
    // Part 2 — Required Test 2
    // PUT /api/opportunities/{id} returns 200 with updated record for a known ID
    // -------------------------------------------------------------------------
    @Test
    void updateOpportunity_knownId_returns200WithUpdatedRecord() throws Exception {
        UpdateOpportunityDTO requestBody = new UpdateOpportunityDTO(
            "Updated Scholarship Title", "Scholarship", "Updated Sponsor",
            "2025-12-31", "Updated description of the scholarship opportunity.",
            List.of("updated", "STEM"), "https://example.com/updated"
        );

        OpportunityDTO updatedDto = new OpportunityDTO(
            "opp-001", "Updated Scholarship Title", "Scholarship",
            "Updated Sponsor", "2025-12-31", "Updated description of the scholarship opportunity.",
            List.of("updated", "STEM"), "https://example.com/updated"
        );

        when(service.updateOpportunity(eq("opp-001"), any(UpdateOpportunityDTO.class)))
            .thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/opportunities/opp-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated Scholarship Title"));
    }

    // -------------------------------------------------------------------------
    // Part 2 — Required Test 3
    // DELETE /api/opportunities/{id} returns 204 No Content for a known ID
    // -------------------------------------------------------------------------
    @Test
    void deleteOpportunity_knownId_returns204() throws Exception {
        when(service.deleteOpportunity("opp-001")).thenReturn(true);

        mockMvc.perform(delete("/api/opportunities/opp-001"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));
    }

    // -------------------------------------------------------------------------
    // Part 2 — Bonus Test
    // POST /api/opportunities returns 400 Bad Request when required field is missing
    // -------------------------------------------------------------------------
    @Test
    void createOpportunity_missingTitle_returns400WithFieldError() throws Exception {
        // title is intentionally omitted — should trigger @NotBlank validation
        String invalidBody = """
            {
              "type": "Scholarship",
              "sponsor": "Test Sponsor",
              "deadline": "2025-12-01",
              "description": "A test scholarship.",
              "tags": ["STEM"],
              "url": "https://example.com"
            }
            """;

        mockMvc.perform(post("/api/opportunities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").exists());
    }
}
