package tn.esprit.jungledraft.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.VisionController;
import tn.esprit.jungledraft.Repositories.VocabulaireRepository;
import tn.esprit.jungledraft.Services.OcrService;
import tn.esprit.jungledraft.Services.TranslationService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisionController.class)
class VisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OcrService ocrService;

    @MockBean
    private TranslationService translationService;

    @MockBean
    private VocabulaireRepository vocabulaireRepository;

    @Test
    void testOcrEndpoint() throws Exception {
        when(ocrService.extraireTexte(org.mockito.ArgumentMatchers.any())).thenReturn("hello world");

        MockMultipartFile file = new MockMultipartFile("photo", "img.png", "image/png", "test".getBytes());

        mockMvc.perform(multipart("/api/vision/ocr").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texte").value("hello world"));
    }

    @Test
    void testOcrTraduireEndpoint() throws Exception {
        when(ocrService.extraireTexte(org.mockito.ArgumentMatchers.any())).thenReturn("hello");
        when(translationService.traduireEnFrancais("hello")).thenReturn("bonjour");

        MockMultipartFile file = new MockMultipartFile("photo", "img.png", "image/png", "test".getBytes());

        mockMvc.perform(multipart("/api/vision/ocr/traduire").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.texteOriginal").value("hello"))
                .andExpect(jsonPath("$.traduction").value("bonjour"));
    }
}
