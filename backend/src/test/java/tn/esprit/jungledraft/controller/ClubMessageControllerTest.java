package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.ClubMessageController;
import tn.esprit.jungledraft.Entities.Club;
import tn.esprit.jungledraft.Entities.ClubMessage;
import tn.esprit.jungledraft.Services.ClubMessageService;
import tn.esprit.jungledraft.Services.MessageEpingleService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubMessageController.class)
class ClubMessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClubMessageService clubMessageService;

    @MockBean
    private MessageEpingleService messageEpingleService;

    private ClubMessage message(Long id) {
        Club club = new Club();
        club.setIdClub(10L);

        ClubMessage m = new ClubMessage();
        m.setIdMessage(id);
        m.setUserId(1L);
        m.setContenu("Message test");
        m.setLikes(5);
        m.setDateEnvoi(new Date());
        m.setClub(club);
        return m;
    }

    @Test
    void testCreate() throws Exception {
        when(clubMessageService.createFromRequest(any())).thenReturn(message(1L));

        String payload = """
                {
                  "contenu": "Message test",
                  "clubId": 10,
                  "userId": 1
                }
                """;

        mockMvc.perform(post("/api/clubMessages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage").value(1));
    }

    @Test
    void testGetById() throws Exception {
        when(clubMessageService.getById(1L)).thenReturn(Optional.of(message(1L)));

        mockMvc.perform(get("/api/clubMessages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMessage").value(1));
    }

    @Test
    void testGetAll() throws Exception {
        when(clubMessageService.getAll()).thenReturn(List.of(message(1L)));

        mockMvc.perform(get("/api/clubMessages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMessage").value(1));
    }

    @Test
    void testUpdate() throws Exception {
        ClubMessage updated = message(1L);
        updated.setContenu("Updated");
        when(clubMessageService.update(any(ClubMessage.class))).thenReturn(updated);

        ClubMessage payload = message(1L);
        payload.setContenu("Updated");

        mockMvc.perform(put("/api/clubMessages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenu").value("Updated"));
    }

    @Test
    void testDelete() throws Exception {
        when(clubMessageService.getById(1L)).thenReturn(Optional.of(message(1L)));
        when(clubMessageService.update(any(ClubMessage.class))).thenReturn(message(1L));
        doNothing().when(clubMessageService).delete(1L);

        mockMvc.perform(delete("/api/clubMessages/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testNotFound() throws Exception {
        when(clubMessageService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clubMessages/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEpingleMessage() throws Exception {
        ClubMessage epingle = message(2L);
        epingle.setEpingle(true);

        when(messageEpingleService.epinglerManuellement(eq(2L), eq("important"))).thenReturn(epingle);

        mockMvc.perform(post("/api/clubMessages/2/epinger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"raison\":\"important\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.epingle").value(true));
    }

    @Test
    void testDesepingleMessage() throws Exception {
        ClubMessage m = message(2L);
        m.setEpingle(false);
        when(messageEpingleService.desepingler(2L)).thenReturn(m);

        mockMvc.perform(delete("/api/clubMessages/2/desepingler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.epingle").value(false));
    }

    @Test
    void testGetMessagesEpingle() throws Exception {
        ClubMessage m = message(3L);
        m.setEpingle(true);
        when(messageEpingleService.getMessagesEpingle(10L)).thenReturn(List.of(m));

        mockMvc.perform(get("/api/clubMessages/club/10/epingles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMessage").value(3));
    }

    @Test
    void testPeutEpinger() throws Exception {
        when(messageEpingleService.peutEpingle(10L)).thenReturn(true);
        when(messageEpingleService.getEpingleRestantes(10L)).thenReturn(2L);

        mockMvc.perform(get("/api/clubMessages/club/10/peut-epinger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.peutEpinger").value(true))
                .andExpect(jsonPath("$.restantes").value(2))
                .andExpect(jsonPath("$.max").value(3));
    }
}
