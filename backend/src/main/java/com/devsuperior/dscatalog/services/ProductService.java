package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.excepetion.DatabaseException;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //LISTAR TODOS OS PRODUTOS
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest){
        Page<Product> page =  productRepository.findAll(pageRequest);
        Page<ProductDTO> pageDTO = page
                .map(x -> new ProductDTO(x));
        return pageDTO;
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID Não Encontrado"));

        ProductDTO ProductDTO = new ProductDTO(product, product.getCategories());
        return ProductDTO;

    }

    @Transactional
    public ProductDTO add (ProductDTO productDTO){
        Product productEntity = new Product();
        copyDTOtoEntity(productDTO,productEntity);
        productEntity= productRepository.save(productEntity);
        return new ProductDTO(productEntity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        try{
            Product productEntity = productRepository.getOne(id);
            copyDTOtoEntity(productDTO,productEntity);
            productEntity = productRepository.save(productEntity);
            return new ProductDTO(productEntity);
        }catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id Não Encontrado "+id);
        }

    }

    public void delete(Long id) {
        try{
            productRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException(("Id não encontrado ")+ id);
        }catch(DataIntegrityViolationException e){//caso eu apague uma categoria que iria comprometer a integridade do banco, por exemplo, eu apagar uma categoria que tem varios produtos dependendo dessa categoria
            throw new DatabaseException("Violação de Integridade");
        }
    }

    private void copyDTOtoEntity(ProductDTO dto, Product entity){
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());
        entity.getCategories().clear();
        for(CategoryDTO categoryDTO :dto.getCategories()){
            Category category = categoryRepository.getOne(categoryDTO.getId());
            entity.getCategories().add(category);
        }
    }
}
