package tn.esprit.jungledraft.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.jungledraft.Entities.BuddyPair;
import tn.esprit.jungledraft.Entities.Disponibilite;
import tn.esprit.jungledraft.Repositories.BuddyPairRep;
import tn.esprit.jungledraft.Repositories.BuddySessionRep;
import tn.esprit.jungledraft.Repositories.DisponibiliteRepository;
import tn.esprit.jungledraft.Repositories.EvenementCalendrierRepository;
import tn.esprit.jungledraft.Services.BuddySessionService;
import tn.esprit.jungledraft.Services.CalendrierService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendrierServiceTest {

    @InjectMocks
    private CalendrierService calendrierService;

    @Mock
    private DisponibiliteRepository disponibiliteRepository;

    @Mock
    private EvenementCalendrierRepository evenementCalendrierRepository;

    @Mock
    private BuddyPairRep buddyPairRep;

    @Mock
    private BuddySessionService buddySessionService;

    @Mock
    private BuddySessionRep buddySessionRep;

    @Test
    void testRechercheDisponibilitesCommunes() {
        // 1. Créer le BuddyPair
        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        pair.setUserID_1(10L);
        pair.setUserID_2(20L);

        // 2. Créer les disponibilités avec leur BuddyPair
        Disponibilite d1 = new Disponibilite();
        d1.setId(1L);
        d1.setBuddyPair(pair);  // ← AJOUTE CETTE LIGNE
        d1.setUserId(10L);
        d1.setDebut(LocalDateTime.parse("2026-04-20T10:00:00"));
        d1.setFin(LocalDateTime.parse("2026-04-20T12:00:00"));

        Disponibilite d2 = new Disponibilite();
        d2.setId(2L);
        d2.setBuddyPair(pair);  // ← AJOUTE CETTE LIGNE
        d2.setUserId(20L);
        d2.setDebut(LocalDateTime.parse("2026-04-20T11:00:00"));
        d2.setFin(LocalDateTime.parse("2026-04-20T13:00:00"));

        // 3. Configurer les mocks
        when(buddyPairRep.findById(1L)).thenReturn(Optional.of(pair));
        when(disponibiliteRepository.findAll()).thenReturn(List.of(d1, d2));
        when(disponibiliteRepository.findByBuddyPairIdAndUserId(1L, 10L)).thenReturn(List.of(d1));
        when(disponibiliteRepository.findByBuddyPairIdAndUserId(1L, 20L)).thenReturn(List.of(d2));

        // 4. Exécuter le test
        List<LocalDateTime[]> result = calendrierService.getDisponibilitesCommunes(1L, 30);

        // 5. Vérifier les résultats
        assertFalse(result.isEmpty());
        assertEquals(LocalDateTime.parse("2026-04-20T11:00:00"), result.get(0)[0]);
    }
}