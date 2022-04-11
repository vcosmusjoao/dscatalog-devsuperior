package com.devsuperior.dscatalog.entities;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;


}
