package com.saga.example.exception;

public class ChargeException extends RuntimeException {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ChargeException(String message) {
    	
        super(message);    
    }
}