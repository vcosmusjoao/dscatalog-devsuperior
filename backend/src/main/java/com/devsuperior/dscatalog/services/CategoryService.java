package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.excepetion.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    //LISTAR TODOS OS PRODUTOS
    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
        List<Category> list =  categoryRepository.findAll();
        List<CategoryDTO> listDTO = list
                .stream()
                .map(x -> new CategoryDTO(x))
                .collect(Collectors.toList());
        return listDTO;
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
}
