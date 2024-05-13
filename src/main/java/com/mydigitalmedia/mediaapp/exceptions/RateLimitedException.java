package com.mydigitalmedia.mediaapp.exceptions;

public class RateLimitedException extends Exception{
    public RateLimitedException(String message) {
        super(message);
    }
}
