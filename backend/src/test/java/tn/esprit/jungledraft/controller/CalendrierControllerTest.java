package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.CalendrierController;
import tn.esprit.jungledraft.Entities.BuddyPair;
import tn.esprit.jungledraft.Entities.Disponibilite;
import tn.esprit.jungledraft.Entities.EvenementCalendrier;
import tn.esprit.jungledraft.Repositories.BuddyPairRep;
import tn.esprit.jungledraft.Repositories.EvenementCalendrierRepository;
import tn.esprit.jungledraft.Services.BuddyService;
import tn.esprit.jungledraft.Services.CalendrierService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalendrierController.class)
class CalendrierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalendrierService calendrierService;

    @MockBean
    private BuddyService buddyService;

    @MockBean
    private BuddyPairRep buddyPairRep;

    @MockBean
    private EvenementCalendrierRepository evenementCalendrierRepository;

    @Test
    void testSuggestionsCreneaux() throws Exception {
        when(calendrierService.suggererCreneaux(1L, 60))
                .thenReturn(List.of(LocalDateTime.parse("2026-04-20T10:00:00")));

        mockMvc.perform(get("/api/calendrier/suggestions/1").param("dureeMinutes", "60"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testAjouterDisponibilite() throws Exception {
        Disponibilite dispo = new Disponibilite();
        dispo.setId(1L);
        when(calendrierService.ajouterDisponibilite(eq(1L), eq(100L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(dispo);

        mockMvc.perform(post("/api/calendrier/disponibilites/1")
                        .param("userId", "100")
                        .param("debut", "2026-04-20T10:00:00Z")
                        .param("fin", "2026-04-20T11:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreerRappel() throws Exception {
        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        when(buddyPairRep.findById(1L)).thenReturn(Optional.of(pair));

        EvenementCalendrier event = new EvenementCalendrier();
        event.setId(10L);
        when(evenementCalendrierRepository.save(any(EvenementCalendrier.class))).thenReturn(event);

        String payload = """
                {
                  "buddyPairId": 1,
                  "titre": "Rappel",
                  "description": "desc",
                  "dateDebut": "2026-04-20T10:00:00",
                  "type": "SESSION"
                }
                """;

        mockMvc.perform(post("/api/calendrier/rappels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void testSupprimerDisponibilite() throws Exception {
        doNothing().when(calendrierService).supprimerDisponibilite(1L);

        mockMvc.perform(delete("/api/calendrier/disponibilites/1"))
                .andExpect(status().isOk());
    }
}
