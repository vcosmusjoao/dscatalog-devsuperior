package com.devsuperior.dscatalog.services.excepetion;

public class EntityNotFoundException extends RuntimeException{


    public EntityNotFoundException (String msg){
        super(msg);
    }

}
