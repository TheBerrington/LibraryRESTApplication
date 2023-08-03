package ru.berrington.libraryRestApplication.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.berrington.libraryRestApplication.dto.BookDTO;
import ru.berrington.libraryRestApplication.dto.PersonDTO;
import ru.berrington.libraryRestApplication.models.Book;
import ru.berrington.libraryRestApplication.models.Person;
import ru.berrington.libraryRestApplication.services.BooksService;
import ru.berrington.libraryRestApplication.services.PeopleService;
import ru.berrington.libraryRestApplication.util.BookErrorResponse;
import ru.berrington.libraryRestApplication.util.BookNotCreatedException;
import ru.berrington.libraryRestApplication.util.BookNotFoundException;
import ru.berrington.libraryRestApplication.util.BookNotUpdatedException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BooksService booksService;
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;

    @Autowired
    public BookController(BooksService booksService, PeopleService peopleService, ModelMapper modelMapper) {
        this.booksService = booksService;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<BookDTO> getBooks(@RequestParam(value = "page",required = false) String pages,
                               @RequestParam(value = "books_per_page",required = false) String booksPerPage,
                               @RequestParam(value = "sort_by_year",required = false) boolean sortByYear) {
        return booksService.findAll(pages,booksPerPage,sortByYear).stream().map(b -> booksService.convertToBookDTO(b)).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public BookDTO google(@RequestParam(value = "sample") StringBuilder sample){
        BookDTO bookDTO = new BookDTO();
        if(sample != null && sample.length() > 0) {
            sample.setCharAt(0,Character.toUpperCase(sample.charAt(0)));
            Book book = booksService.findBookLike(sample.toString());
            if (book == null) return bookDTO;
            int book_id = book.getBook_id();
           bookDTO = booksService.convertToBookDTO(book);
        }
        return bookDTO;
    }


    @GetMapping("/{book_id}")
    public BookDTO getBook(@PathVariable("book_id") int book_id) {
        BookDTO bookDTO = booksService.convertToBookDTO(booksService.findById(book_id));
        if(booksService.findOwnerById(book_id) != null){
            bookDTO.setOwner(peopleService.convertToPersonDTO(booksService.findOwnerById(book_id)));
        }
        return bookDTO;
    }

    @ExceptionHandler
    private ResponseEntity<BookErrorResponse> handlerException(BookNotFoundException e){
        BookErrorResponse response = new BookErrorResponse(
                "Book with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/new")
    public BookDTO newBook(){
        return new BookDTO();
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid BookDTO bookDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            StringBuilder errorSb = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error: errors){
                errorSb.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new BookNotCreatedException(errorSb.toString());
        }

        booksService.save(booksService.convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }



    @ExceptionHandler
    private ResponseEntity<BookErrorResponse> handlerException(BookNotCreatedException e){
        BookErrorResponse response = new BookErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{book_id}/edit")
    public BookDTO edit(@PathVariable("book_id") int book_id){
        return booksService.convertToBookDTO(booksService.findById(book_id));
    }

    @PatchMapping("{book_id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid BookDTO bookDTO, BindingResult bindingResult, @PathVariable("book_id") int book_id){
        if(bindingResult.hasErrors()){
            StringBuilder errorSb = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error: errors){
                errorSb.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new BookNotUpdatedException(errorSb.toString());
        }
        booksService.update(book_id,booksService.convertToBook(bookDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{book_id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("book_id") int book_id){
        booksService.delete(book_id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{book_id}/assign")
    public BookDTO assign(@PathVariable("book_id") int book_id, @RequestBody @Valid PersonDTO selectedPersonDTO){
        Person selectedPerson = peopleService.findPersonByName(selectedPersonDTO.getName()).orElse(null);
        booksService.assign(book_id,selectedPerson);
        return booksService.convertToBookDTO(booksService.findById(book_id));
    }

    @PatchMapping("/{book_id}/release")
    public BookDTO release(@PathVariable("book_id") int book_id){
        booksService.release(book_id);
        return booksService.convertToBookDTO(booksService.findById(book_id));
    }

}
