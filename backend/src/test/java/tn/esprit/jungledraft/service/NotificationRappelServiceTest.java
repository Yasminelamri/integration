package tn.esprit.jungledraft.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tn.esprit.jungledraft.Entities.BuddyPair;
import tn.esprit.jungledraft.Entities.BuddySession;
import tn.esprit.jungledraft.Entities.SessionStatus;
import tn.esprit.jungledraft.Repositories.BuddySessionRep;
import tn.esprit.jungledraft.Services.NotificationRappelService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = NotificationRappelService.class)
class NotificationRappelServiceTest {

    @Autowired
    private NotificationRappelService notificationRappelService;

    @MockBean
    private BuddySessionRep buddySessionRep;

    @Test
    void testSchedulerEtDetectionSessionsProches() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Africa/Tunis"));

        BuddyPair pair = new BuddyPair();
        pair.setIdPair(1L);
        pair.setUserID_1(100L);
        pair.setUserID_2(200L);

        BuddySession proche = new BuddySession();
        proche.setIdSession(1L);
        proche.setBuddyPair(pair);
        proche.setStatus(SessionStatus.PLANIFIEE);
        proche.setDate(now.plusMinutes(3));
        proche.setSujet("Session proche");
        proche.setDuree(60);
        proche.setRappelEnvoye(false);

        when(buddySessionRep.findAll()).thenReturn(List.of(proche));
        when(buddySessionRep.save(any(BuddySession.class))).thenAnswer(i -> i.getArgument(0));

        notificationRappelService.marquerSessionsProches();
        List<Map<String, Object>> sessions = notificationRappelService.getSessionsProchesPourUtilisateur(100L);

        assertFalse(sessions.isEmpty());
        assertTrue(sessions.get(0).containsKey("sessionId"));
        verify(buddySessionRep, atLeastOnce()).save(any(BuddySession.class));
    }
}
