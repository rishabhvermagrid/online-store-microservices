package com.rishabh.store.product.exception;

//the dependent service catalog/invenroty is unavailable
public class ServiceUnavailableException extends RuntimeException{
    public ServiceUnavailableException(String message){
        super(message);
    }
}
