package ru.berrington.libraryRestApplication.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class BookDTO {
    @NotEmpty(message = "Название не должно быть пустым!")
    @Size(min = 1,max = 100,message = "Размер от 1 до 100")
    private String title;

    @NotEmpty(message = "Author shouldn't be empty!")
    @Size(min = 1,max = 50,message = "Size should be in range 1 to 50")
    private String author;

    @Min(value = 1500, message = "Год написания не должен быть старше 1500")
    private int year;

    private PersonDTO owner;


    public PersonDTO getOwner() {
        return owner;
    }

    public void setOwner(PersonDTO owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
