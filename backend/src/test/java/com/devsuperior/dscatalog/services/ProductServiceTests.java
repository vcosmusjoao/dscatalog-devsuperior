package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.excepetion.DatabaseException;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existindId;
    private long nonExistindId;
    private long dependentId;
    private Product product;
    private PageImpl<Product>page;
    private Category category;
    @BeforeEach
    void setUp()throws Exception{
        existindId = 1L;
        nonExistindId =2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));

        //findAll  retornando um objeto do tipo Page
        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        //save  retornando um objeto do tipo Product
        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
        //FindById  retornando um objeto do tipo Optional<Product> quando o id existe
        Mockito.when(productRepository.findById(existindId)).thenReturn(Optional.of(product));
        //FindById  retornando um objeto do tipo Optional empty quando o id não existe
        Mockito.when(productRepository.findById(nonExistindId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getOne(existindId)).thenReturn(product);
        Mockito.when(productRepository.getOne(nonExistindId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existindId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistindId)).thenThrow(EntityNotFoundException.class);

        //deleteById-metodos void
        Mockito.doNothing().when(productRepository).deleteById(existindId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistindId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

    }

    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() {
        ProductDTO productDTO = Factory.createProducDTO();
        Assertions.assertThrows(ResourceNotFoundException.class,()->{
           productService.update(nonExistindId,productDTO);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO productDTO = Factory.createProducDTO();
        ProductDTO result = productService.update(existindId,productDTO);
        Assertions.assertNotNull(result);
    }

    @Test
    public void findByIdShouldReturnResourceNotFoundExceptionWhenIdDosNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class,()->{
            productService.findById(nonExistindId);
        });
        Mockito.verify(productRepository).findById(nonExistindId);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = productService.findById(existindId);
        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).findById(existindId);
    }
    @Test
    public void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = productService.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists(){

        Assertions.assertThrows(ResourceNotFoundException.class, ()->{
            productService.delete(nonExistindId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(nonExistindId);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists(){

        Assertions.assertDoesNotThrow(()->{
            productService.delete(existindId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existindId);
    }
  
}
