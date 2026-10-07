package com.rishabh.store.product.exception;

public class ProductUnavailableException extends RuntimeException{
    public ProductUnavailableException(String message){
        super(message);
    }
}
