package ckollmeier.de.javangertodorecap.exceptionhandler;

import ckollmeier.de.javangertodorecap.controller.TodoController;
import ckollmeier.de.javangertodorecap.exception.NotFoundException;
import ckollmeier.de.javangertodorecap.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoController controller;

    @MockitoBean
    private TodoService service;

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    void globalExceptionHandlerReturnsStatus500WithCorrectErrorDTOWhenExceptionIsThrown() throws Exception {
        when(controller.getAllTodos()).thenThrow(new RuntimeException("Test Exception"));

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isInternalServerError())
                .andExpect(content ().json("""
                   {
                     "error": "RuntimeException",
                      "message": "Test Exception",
                      "status": "INTERNAL_SERVER_ERROR"
                   }
                 """));
    }

    @Test
    void globalExceptionHandlerReturnsBadRequestWithCorrectErrorDTOWhenNullPointerExceptionIsThrown() throws Exception {
        when(controller.getAllTodos()).thenThrow(new NullPointerException("Test Exception"));

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isBadRequest())
                .andExpect(content ().json("""
                   {
                     "error": "NullPointerException",
                      "message": "Test Exception",
                      "status": "BAD_REQUEST"
                   }
                 """));
    }

    @Test
    void globalExceptionHandlerReturnsNotFoundWithCorrectErrorDTOWhenNotFoundExceptionIsThrown() throws Exception {
        when(controller.getAllTodos()).thenThrow(new NotFoundException("Test Exception"));

        mockMvc.perform(get("/api/todo"))
                .andExpect(status().isNotFound())
                .andExpect(content ().json("""
                   {
                     "error": "NotFoundException",
                      "message": "Test Exception",
                      "status": "NOT_FOUND"
                   }
                 """));
    }


}