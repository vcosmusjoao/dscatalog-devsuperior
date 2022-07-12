package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.excepetion.DatabaseException;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;


@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    //LISTAR TODOS OS PRODUTOS
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable){
        Page<Category> page =  categoryRepository.findAll(pageable);
        Page<CategoryDTO> pageDTO = page
                .map(x -> new CategoryDTO(x));
        return pageDTO;
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
           Category category = categoryRepository
                   .findById(id)
                   .orElseThrow(() -> new ResourceNotFoundException("ID Não Encontrado"));

           CategoryDTO categoryDTO = new CategoryDTO(category);
           return categoryDTO;

    }

    @Transactional
    public CategoryDTO add (CategoryDTO categoryDTO){
        Category c = new Category();
        c.setName(categoryDTO.getName());
        c = categoryRepository.save(c);
        return new CategoryDTO(c);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO categoryDTO) {
        try{
            Category entity = categoryRepository.getOne(id);
            entity.setName(categoryDTO.getName());
            entity = categoryRepository.save(entity);
            return new CategoryDTO(entity);
        }catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id Não Encontrado "+id);
        }

    }

    public void delete(Long id) {
        try{
            categoryRepository.deleteById(id);
        }catch(EmptyResultDataAccessException e){
            throw new ResourceNotFoundException(("Id não encontrado ")+ id);
        }catch(DataIntegrityViolationException e){//caso eu apague uma categoria que iria comprometer a integridade do banco, por exemplo, eu apagar uma categoria que tem varios produtos dependendo dessa categoria
            throw new DatabaseException("Violação de Integridade");
        }
    }


}
