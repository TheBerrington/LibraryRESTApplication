package ru.berrington.libraryRestApplication.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.berrington.libraryRestApplication.dto.BookDTO;
import ru.berrington.libraryRestApplication.models.Book;
import ru.berrington.libraryRestApplication.models.Person;
import ru.berrington.libraryRestApplication.repositories.BooksRepositories;
import ru.berrington.libraryRestApplication.repositories.PeopleRepositories;
import ru.berrington.libraryRestApplication.util.BookNotFoundException;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BooksService {
    public final BooksRepositories booksRepositories;
    public final PeopleRepositories peopleRepositories;
    public final ModelMapper modelMapper;

    @Autowired
    public BooksService(BooksRepositories booksRepositories, PeopleRepositories peopleRepositories, ModelMapper modelMapper) {
        this.booksRepositories = booksRepositories;
        this.peopleRepositories = peopleRepositories;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void save(Book book){
        booksRepositories.save(book);
    }
    @Transactional
    public void update(int id, Book updatingBook){
        updatingBook.setBook_id(id);
        booksRepositories.save(updatingBook);
    }
    @Transactional
    public void delete(int id){
        booksRepositories.deleteById(id);
    }

    public List<Book> findAll(String pages, String booksPerPage, boolean sortByYear){
        if(pages==null || booksPerPage == null) {
            if(sortByYear){
                return booksRepositories.findAll(Sort.by("year"));
            }
            return booksRepositories.findAll();
        }
        if(sortByYear){
            return booksRepositories.findAll(PageRequest.of((Integer.parseInt(pages)),(Integer.parseInt(booksPerPage)), Sort.by("year"))).getContent();
        }
        return booksRepositories.findAll(PageRequest.of((Integer.parseInt(pages)),(Integer.parseInt(booksPerPage)))).getContent();

    }
    public Book findById(int book_id){
        return booksRepositories.findById(book_id).orElseThrow(BookNotFoundException::new);
    }
    public Person findOwnerById(int book_id){
        return booksRepositories.findById(book_id).orElse(null).getOwner();
    }

    @Transactional
    public void assign(int book_id, Person person){
        Book book = booksRepositories.findById(book_id).orElse(null);
        book.setAssignAt(new Date());
        book.setOwner(person);
        booksRepositories.save(book);
    }
    @Transactional
    public void release(int book_id){

        Book book = booksRepositories.findById(book_id).orElse(null);
        book.setOwner(null);
        book.setAssignAt(null);
        booksRepositories.save(book);
    }
    public Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO,Book.class);
    }

    public BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book,BookDTO.class);
    }

    public List<Book> findBooksByPersonId(int id){
        Person owner = peopleRepositories.findById(id).orElse(null);
        return booksRepositories.findByOwner(owner);
    }

    public Book findBookLike(String sample){
        return booksRepositories.findBookByTitleStartingWith(sample);
    }

}
