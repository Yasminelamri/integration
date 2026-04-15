package tn.esprit.jungledraft.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import tn.esprit.jungledraft.Entities.Club;
import tn.esprit.jungledraft.Entities.ClubMessage;
import tn.esprit.jungledraft.Repositories.ClubMessageRep;
import tn.esprit.jungledraft.Repositories.ClubRep;
import tn.esprit.jungledraft.Services.MessageEpingleService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MessageEpingleService.class)
class MessageEpingleServiceTest {

    @Autowired
    private MessageEpingleService messageEpingleService;

    @MockBean
    private ClubMessageRep clubMessageRep;

    @MockBean
    private ClubRep clubRep;

    private ClubMessage buildMessage(Long id, Long clubId) {
        Club club = new Club();
        club.setIdClub(clubId);

        ClubMessage message = new ClubMessage();
        message.setIdMessage(id);
        message.setClub(club);
        message.setLikes(15);
        message.setDateEnvoi(new Date());
        return message;
    }

    @Test
    void testEpinglerManuellement_Success() {
        ClubMessage message = buildMessage(1L, 10L);
        when(clubMessageRep.findById(1L)).thenReturn(Optional.of(message));
        when(clubMessageRep.countMessagesEpingle(10L)).thenReturn(1L);
        when(clubMessageRep.save(any(ClubMessage.class))).thenAnswer(i -> i.getArgument(0));

        ClubMessage result = messageEpingleService.epinglerManuellement(1L, "Important");

        assertTrue(result.isEpingle());
        assertEquals("Important", result.getRaisonEpingle());
    }

    @Test
    void testDesepingler_Success() {
        ClubMessage message = buildMessage(1L, 10L);
        message.setEpingle(true);

        when(clubMessageRep.findById(1L)).thenReturn(Optional.of(message));
        when(clubMessageRep.save(any(ClubMessage.class))).thenAnswer(i -> i.getArgument(0));

        ClubMessage result = messageEpingleService.desepingler(1L);

        assertFalse(result.isEpingle());
        assertNull(result.getDateEpingle());
    }

    @Test
    void testAutoEpinglerMessagesViraux() {
        Club club = new Club();
        club.setIdClub(10L);
        ClubMessage viral = buildMessage(2L, 10L);

        when(clubRep.findAll()).thenReturn(List.of(club));
        when(clubMessageRep.findMessagesViraux(eq(10L), eq(10), any(Date.class))).thenReturn(List.of(viral));
        when(clubMessageRep.countMessagesEpingle(10L)).thenReturn(0L);
        when(clubMessageRep.save(any(ClubMessage.class))).thenAnswer(i -> i.getArgument(0));

        messageEpingleService.autoEpinglerMessagesViraux();

        verify(clubMessageRep, atLeastOnce()).save(any(ClubMessage.class));
    }

    @Test
    void testDesepinglerMessagesAnciens() {
        when(clubMessageRep.desepinglerMessagesAnciens(any(Date.class))).thenReturn(2);

        messageEpingleService.desepinglerMessagesAnciens();

        verify(clubMessageRep).desepinglerMessagesAnciens(any(Date.class));
    }

    @Test
    void testPeutEpingle_LimiteAtteinte() {
        when(clubMessageRep.countMessagesEpingle(10L)).thenReturn(3L);

        boolean peut = messageEpingleService.peutEpingle(10L);

        assertFalse(peut);
    }
}
