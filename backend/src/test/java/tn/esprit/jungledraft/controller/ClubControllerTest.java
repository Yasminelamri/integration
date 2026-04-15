package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.ClubController;
import tn.esprit.jungledraft.Entities.Club;
import tn.esprit.jungledraft.Services.ClubService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
class ClubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClubService clubService;

    @Test
    void testCreate() throws Exception {
        Club saved = new Club();
        saved.setIdClub(1L);
        saved.setNom("Club A");

        when(clubService.create(any(Club.class))).thenReturn(saved);

        Club payload = new Club();
        payload.setNom("Club A");

        mockMvc.perform(post("/api/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idClub").value(1))
                .andExpect(jsonPath("$.nom").value("Club A"));
    }

    @Test
    void testGetById() throws Exception {
        Club club = new Club();
        club.setIdClub(1L);
        club.setNom("Club A");
        when(clubService.getById(1L)).thenReturn(Optional.of(club));

        mockMvc.perform(get("/api/clubs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idClub").value(1));
    }

    @Test
    void testGetAll() throws Exception {
        Club c1 = new Club();
        c1.setIdClub(1L);
        c1.setNom("Club A");
        when(clubService.getAll()).thenReturn(List.of(c1));

        mockMvc.perform(get("/api/clubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idClub").value(1));
    }

    @Test
    void testUpdate() throws Exception {
        Club updated = new Club();
        updated.setIdClub(1L);
        updated.setNom("Club B");
        when(clubService.update(any(Club.class))).thenReturn(updated);

        Club payload = new Club();
        payload.setIdClub(1L);
        payload.setNom("Club B");

        mockMvc.perform(put("/api/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Club B"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(clubService).delete(1L);

        mockMvc.perform(delete("/api/clubs/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testNotFound() throws Exception {
        when(clubService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clubs/999"))
                .andExpect(status().isNotFound());
    }
}
