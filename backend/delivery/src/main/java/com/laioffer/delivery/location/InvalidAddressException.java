package com.laioffer.delivery.location;


public class InvalidAddressException extends RuntimeException{


    public InvalidAddressException() {
        super("Invalid address");
    }
}
