package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
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
        Product p = new Product();
        p.setName(productDTO.getName());
        p.setDate(productDTO.getDate());
        p.setDescription(productDTO.getDescription());
        p.setImgUrl(productDTO.getImgUrl());
        p.setPrice(productDTO.getPrice());
        p = productRepository.save(p);
        return new ProductDTO(p);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO productDTO) {
        try{
            Product entity = productRepository.getOne(id);
            entity.setName(productDTO.getName());
            entity.setName(productDTO.getName());
            entity.setDate(productDTO.getDate());
            entity.setDescription(productDTO.getDescription());
            entity.setImgUrl(productDTO.getImgUrl());
            entity.setPrice(productDTO.getPrice());
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
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
}
