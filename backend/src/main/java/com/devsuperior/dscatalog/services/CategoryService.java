package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.excepetion.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                   .orElseThrow(() -> new EntityNotFoundException("ID Não Encontrado"));

           CategoryDTO categoryDTO = new CategoryDTO(category);
           return categoryDTO;

    }




}
