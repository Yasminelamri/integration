package tn.esprit.jungledraft.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.VisionController;
import tn.esprit.jungledraft.Entities.VocabulairePersonnel;
import tn.esprit.jungledraft.Repositories.VocabulaireRepository;
import tn.esprit.jungledraft.Services.OcrService;
import tn.esprit.jungledraft.Services.TranslationService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisionController.class)
class VocabulaireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @MockBean
    private TranslationService translationService;

    @MockBean
    private VocabulaireRepository vocabulaireRepository;

    @Test
    void testAjouterMotVocabulaire() throws Exception {
        when(vocabulaireRepository.findByUserIdAndClubIdAndMot(1L, 10L, "hello"))
                .thenReturn(Optional.empty());
        when(vocabulaireRepository.save(any(VocabulairePersonnel.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(post("/api/vision/vocabulaire/ajouter")
                        .param("userId", "1")
                        .param("clubId", "10")
                        .param("mot", "hello")
                        .param("traduction", "bonjour"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mot ajouté au vocabulaire"));
    }

    @Test
    void testGetVocabulaire() throws Exception {
        VocabulairePersonnel v = new VocabulairePersonnel();
        v.setId(1L);
        v.setUserId(1L);
        v.setClubId(10L);
        v.setMot("hello");
        v.setTraduction("bonjour");

        when(vocabulaireRepository.findByUserIdAndClubIdOrderByFoisVuDesc(1L, 10L))
                .thenReturn(List.of(v));

        mockMvc.perform(get("/api/vision/vocabulaire/1/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mot").value("hello"));
    }
}
