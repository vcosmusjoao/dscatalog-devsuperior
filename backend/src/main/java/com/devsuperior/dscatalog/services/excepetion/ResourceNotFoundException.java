package com.devsuperior.dscatalog.services.excepetion;

public class ResourceNotFoundException extends RuntimeException{


    public ResourceNotFoundException(String msg){
        super(msg);
    }

}
