package tn.esprit.jungledraft.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.jungledraft.Services.TranslationService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = TranslationService.class)
class TranslationServiceTest {

    @Autowired
    private TranslationService translationService;

    @Test
    void testTraductionAnglaisVersFrancais() throws Exception {
        String traduction = translationService.traduireEnFrancais("hello world");
        assertNotNull(traduction);
    }

    @Test
    void testTraductionTexteVide() throws Exception {
        String traduction = translationService.traduireEnFrancais("");
        assertEquals("", traduction);
    }
}
