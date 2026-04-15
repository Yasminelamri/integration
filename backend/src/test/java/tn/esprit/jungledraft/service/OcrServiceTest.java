package tn.esprit.jungledraft.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import tn.esprit.jungledraft.Services.OcrService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = OcrService.class)
class OcrServiceTest {

    @Autowired
    private OcrService ocrService;

    @Test
    void testExtractionTexte() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "photo", "test.png", "image/png", "fake-image-content".getBytes());

        String resultat = ocrService.extraireTexte(file);

        assertNotNull(resultat);
    }
}
