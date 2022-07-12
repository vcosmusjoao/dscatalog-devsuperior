package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.excepetion.DatabaseException;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper; //transforma json em texto

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    private Long existingId;
    private Long nonExistindId;
    private Long dependentId;

    @BeforeEach
    void setUp()throws Exception {
        existingId =1L;
        nonExistindId =2L;
        dependentId =3L;


        productDTO = Factory.createProducDTO();
        page = new PageImpl<>(List.of(productDTO));
        //find all controller
        when(service.findAllPaged(any())).thenReturn(page);
        //find by id controller com id existente
        when(service.findById(existingId)).thenReturn(productDTO);
        //find by id controller com id inexistente
        when(service.findById(nonExistindId)).thenThrow(ResourceNotFoundException.class);
        //update controller com id existente
        when(service.update(eq(existingId),any())).thenReturn(productDTO);
        //updatecontroller com id inexistente
        when(service.update(eq(nonExistindId),any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);

        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistindId);

        doThrow(DatabaseException.class).when(service).delete(dependentId);

        when(service.add(any())).thenReturn(productDTO);

    }


    @Test
    public void insertShouldReturnCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions resultActions = mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isCreated());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/products/{id}",nonExistindId));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(delete("/products/{id}",existingId));
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void updateShouldReturnProdutoDTOWhenIDExist() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions resultActions = mockMvc.perform(put("/products/{id}",existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());

    }
    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotxist() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}",nonExistindId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void findAllShouldReturnPage() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/products/{id}",existingId).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").exists());
        resultActions.andExpect(jsonPath("$.name").exists());
        resultActions.andExpect(jsonPath("$.description").exists());

    }
    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/products/{id}",nonExistindId).accept(MediaType.APPLICATION_JSON));
        resultActions.andExpect(status().isNotFound());
    }



}
