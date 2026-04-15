package tn.esprit.jungledraft.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.jungledraft.Controller.CommentController;
import tn.esprit.jungledraft.Entities.Comment;
import tn.esprit.jungledraft.Services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    void testCreate() throws Exception {
        Comment created = new Comment();
        created.setCommentId(1L);
        created.setComment("Hello");

        when(commentService.createFromDTO(any())).thenReturn(created);

        String payload = """
                {
                  "comment": "Hello",
                  "userId": 1,
                  "messageId": 10
                }
                """;

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1));
    }

    @Test
    void testGetById() throws Exception {
        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setComment("Hello");
        when(commentService.getById(1L)).thenReturn(Optional.of(comment));

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1));
    }

    @Test
    void testGetAll() throws Exception {
        Comment c = new Comment();
        c.setCommentId(1L);
        c.setComment("Hello");
        when(commentService.getAll()).thenReturn(List.of(c));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].commentId").value(1));
    }

    @Test
    void testUpdate() throws Exception {
        Comment updated = new Comment();
        updated.setCommentId(1L);
        updated.setComment("Updated");
        when(commentService.update(any(Comment.class))).thenReturn(updated);

        Comment payload = new Comment();
        payload.setCommentId(1L);
        payload.setComment("Updated");

        mockMvc.perform(put("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Updated"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(commentService).delete(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testNotFound() throws Exception {
        when(commentService.getById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/comments/999"))
                .andExpect(status().isNotFound());
    }
}
