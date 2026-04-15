package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.BuddyPairController;
import tn.esprit.jungledraft.Entities.BuddyPair;
import tn.esprit.jungledraft.Services.BuddyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BuddyPairController.class)
class BuddyPairControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BuddyService buddyService;

    @Test
    void testCreate() throws Exception {
        BuddyPair created = new BuddyPair();
        created.setIdPair(1L);
        created.setUserID_1(10L);
        created.setUserID_2(20L);

        when(buddyService.create(any(BuddyPair.class))).thenReturn(created);

        BuddyPair payload = new BuddyPair();
        payload.setUserID_1(10L);
        payload.setUserID_2(20L);

        mockMvc.perform(post("/api/buddyPairs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPair").value(1));
    }

    @Test
    void testGetById() throws Exception {
        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        when(buddyService.getById(1L)).thenReturn(Optional.of(pair));

        mockMvc.perform(get("/api/buddyPairs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPair").value(1));
    }

    @Test
    void testGetAll() throws Exception {
        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        when(buddyService.getAll()).thenReturn(List.of(pair));

        mockMvc.perform(get("/api/buddyPairs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPair").value(1));
    }

    @Test
    void testUpdate() throws Exception {
        BuddyPair updated = new BuddyPair();
        updated.setIdPair(1L);
        updated.setUserID_1(11L);
        when(buddyService.update(any(BuddyPair.class))).thenReturn(updated);

        BuddyPair payload = new BuddyPair();
        payload.setUserID_1(11L);
        payload.setUserID_2(20L);

        mockMvc.perform(put("/api/buddyPairs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID_1").value(11));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(buddyService).delete(1L);

        mockMvc.perform(delete("/api/buddyPairs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNotFound() throws Exception {
        when(buddyService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/buddyPairs/999"))
                .andExpect(status().isNotFound());
    }
}
