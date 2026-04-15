package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.BuddySessionController;
import tn.esprit.jungledraft.Entities.BuddyPair;
import tn.esprit.jungledraft.Entities.BuddySession;
import tn.esprit.jungledraft.Repositories.BuddyPairRep;
import tn.esprit.jungledraft.Services.BuddySessionService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuddySessionController.class)
class BuddySessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuddySessionService buddySessionService;

    @MockBean
    private BuddyPairRep buddyPairRep;

    @Test
    void testCreate() throws Exception {
        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        when(buddyPairRep.findById(1L)).thenReturn(Optional.of(pair));

        BuddySession created = new BuddySession();
        created.setIdSession(5L);
        created.setBuddyPair(pair);
        created.setDate(LocalDateTime.parse("2026-04-20T10:00:00"));
        when(buddySessionService.create(any(BuddySession.class))).thenReturn(created);

        String payload = """
                {
                  "buddyPair": {"idPair": 1},
                  "date": "2026-04-20T10:00:00",
                  "duree": 60,
                  "sujet": "Session Java",
                  "lieu": "Salle 1",
                  "notes": "notes"
                }
                """;

        mockMvc.perform(post("/api/buddySessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSession").value(5));
    }

    @Test
    void testGetById() throws Exception {
        BuddySession s = new BuddySession();
        s.setIdSession(2L);
        when(buddySessionService.getById(2L)).thenReturn(Optional.of(s));

        mockMvc.perform(get("/api/buddySessions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSession").value(2));
    }

    @Test
    void testGetAll() throws Exception {
        BuddySession s = new BuddySession();
        s.setIdSession(2L);
        when(buddySessionService.getAll()).thenReturn(List.of(s));

        mockMvc.perform(get("/api/buddySessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idSession").value(2));
    }

    @Test
    void testUpdate() throws Exception {
        BuddySession updated = new BuddySession();
        updated.setIdSession(2L);
        updated.setSujet("Nouveau sujet");
        when(buddySessionService.update(any(BuddySession.class))).thenReturn(updated);

        BuddySession payload = new BuddySession();
        payload.setSujet("Nouveau sujet");

        mockMvc.perform(put("/api/buddySessions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sujet").value("Nouveau sujet"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(buddySessionService).delete(2L);

        mockMvc.perform(delete("/api/buddySessions/2"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNotFound() throws Exception {
        when(buddySessionService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/buddySessions/999"))
                .andExpect(status().isNotFound());
    }
}
