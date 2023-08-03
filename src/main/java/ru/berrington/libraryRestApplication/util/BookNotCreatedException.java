package ru.berrington.libraryRestApplication.util;

public class BookNotCreatedException extends RuntimeException{
    public BookNotCreatedException(String msg){
        super(msg);
    }
}
