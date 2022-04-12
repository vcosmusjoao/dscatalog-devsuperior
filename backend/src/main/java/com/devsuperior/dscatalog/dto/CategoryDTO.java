package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;

    //CONSTRUTOR QUE RECEBE A ENTIDADE

    public CategoryDTO(Category entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }

}
