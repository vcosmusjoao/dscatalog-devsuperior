package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private Long nonExistinId;
    private Long existinId;
    private Product product;
    @BeforeEach
    void setUp()throws Exception{
        nonExistinId =100L;
        existinId=  1L;
        product = Factory.createProduct();
    }
    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){
        repository.deleteById(existinId);
        Optional<Product> result = repository.findById(existinId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(EmptyResultDataAccessException.class,()->{
            repository.deleteById(nonExistinId);
        });
    }

    @Test
    public void saveShouldPersistWhenIdIsNull(){
        product.setId(null);
        product = repository.save(product);
        Assertions.assertNotNull(product.getId());
    }

    @Test
    public void findByIdShouldReturnOptionalProductNotEmptyWhenIdExists(){
        Optional<Product> result = repository.findById(existinId);
        Assertions.assertTrue(result.isPresent());
    }
    @Test
    public void findByIdShouldReturnOptionalProductEmptyWhenIdDoesNotExists(){
        Optional<Product> result = repository.findById(nonExistinId);
        Assertions.assertTrue(result.isEmpty());
    }


}
