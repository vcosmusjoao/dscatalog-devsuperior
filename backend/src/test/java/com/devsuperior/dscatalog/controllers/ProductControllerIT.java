package com.devsuperior.dscatalog.controllers;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existindId;
    private Long nonExistindId;
    private Long countTotalProducts;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp()throws Exception{
        existindId=1L;
        nonExistindId= 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc",nonExistindId)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content").exists());
        resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));

    }

    @Test
    public void updateShouldReturnProductDTOWhenIDExist() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/products?page=0&size=12&sort=name,asc",nonExistindId)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.content").exists());
        resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        resultActions.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        resultActions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        resultActions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        resultActions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));

    }

    @Test
    public void updateShouldReturnProdutoDTOWhenIdExist() throws Exception{
        ProductDTO productDTO = Factory.createProducDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();


        ResultActions resultActions = mockMvc.perform(put("/products/{id}",existindId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.id").value(existindId));
        resultActions.andExpect(jsonPath("$.name").value(expectedName));
        resultActions.andExpect(jsonPath("$.description").value(expectedDescription));

    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ProductDTO productDTO = Factory.createProducDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions resultActions = mockMvc.perform(put("/products/{id}", nonExistindId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isNotFound());
    }

}
