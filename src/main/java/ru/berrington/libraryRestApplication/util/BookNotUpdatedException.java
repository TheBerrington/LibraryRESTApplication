package ru.berrington.libraryRestApplication.util;

public class BookNotUpdatedException extends RuntimeException {
    public BookNotUpdatedException (String msg){
        super(msg);
    }
}
