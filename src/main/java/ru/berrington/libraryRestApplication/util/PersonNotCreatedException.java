package ru.berrington.libraryRestApplication.util;

public class PersonNotCreatedException extends RuntimeException {
    public PersonNotCreatedException(String msg){
        super(msg);
    }
}
