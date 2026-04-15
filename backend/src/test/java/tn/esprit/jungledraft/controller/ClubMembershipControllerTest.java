package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.ClubMembershipController;
import tn.esprit.jungledraft.DTO.ClubMembershipDTO;
import tn.esprit.jungledraft.Entities.Club;
import tn.esprit.jungledraft.Entities.ClubMembership;
import tn.esprit.jungledraft.Entities.InscriptionStatus;
import tn.esprit.jungledraft.Services.ClubMembershipService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubMembershipController.class)
class ClubMembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClubMembershipService clubMembershipService;

    private ClubMembership membership(Long id) {
        Club club = new Club();
        club.setIdClub(10L);
        club.setNom("Club A");

        ClubMembership m = new ClubMembership();
        m.setIdInscription(id);
        m.setUserId(1L);
        m.setStatus(InscriptionStatus.EN_ATTENTE);
        m.setDateInscription(new Date());
        m.setClub(club);
        return m;
    }

    @Test
    void testCreate() throws Exception {
        when(clubMembershipService.createFromDTO(any(ClubMembershipDTO.class))).thenReturn(membership(1L));

        String payload = """
                {
                  "userId": 1,
                  "status": "EN_ATTENTE",
                  "club": {"idClub": 10}
                }
                """;

        mockMvc.perform(post("/api/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscription").value(1));
    }

    @Test
    void testGetById() throws Exception {
        when(clubMembershipService.getById(1L)).thenReturn(Optional.of(membership(1L)));

        mockMvc.perform(get("/api/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscription").value(1));
    }

    @Test
    void testGetAll() throws Exception {
        when(clubMembershipService.getAll()).thenReturn(List.of(membership(1L)));

        mockMvc.perform(get("/api/memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idInscription").value(1));
    }

    @Test
    void testUpdate() throws Exception {
        ClubMembership found = membership(1L);
        ClubMembership updated = membership(1L);
        updated.setStatus(InscriptionStatus.VALIDEE);

        when(clubMembershipService.getById(1L)).thenReturn(Optional.of(found));
        when(clubMembershipService.update(any(ClubMembership.class))).thenReturn(updated);

        String payload = """
                {
                  "status": "VALIDEE"
                }
                """;

        mockMvc.perform(put("/api/memberships/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDEE"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(clubMembershipService).delete(1L);

        mockMvc.perform(delete("/api/memberships/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNotFound() throws Exception {
        when(clubMembershipService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/memberships/999"))
                .andExpect(status().isNotFound());
    }
}
