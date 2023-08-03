package ru.berrington.libraryRestApplication.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "Book")
public class Book {
    @Id
    @Column(name = "book_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int book_id;

    @Column(name = "title")
    @NotEmpty(message = "Название не должно быть пустым!")
    @Size(min = 1,max = 100,message = "Размер от 1 до 100")
    private String title;

    @Column(name = "author")
    @NotEmpty(message = "Author shouldn't be empty!")
    @Size(min = 1,max = 50,message = "Size should be in range 1 to 50")
    private String author;

    @Column(name = "year")
    @Min(0)
    private int year;

    @ManyToOne()
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person owner;

    @Transient
    private boolean overDue;

    public boolean isOverDue(){
        return (new Date().getTime()-assignAt.getTime()>864000000);
    }


    public Date getAssignAt() {
        return assignAt;
    }

    public void setAssignAt(Date assignAt) {
        this.assignAt = assignAt;
    }

    @Column(name = "assign_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return book_id == book.book_id &&
                year == book.year &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(book_id, title, author, year);
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Book() {
    }

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
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
