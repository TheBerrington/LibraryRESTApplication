package ru.berrington.libraryRestApplication.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.berrington.libraryRestApplication.dto.PersonDTO;
import ru.berrington.libraryRestApplication.services.BooksService;
import ru.berrington.libraryRestApplication.services.PeopleService;
import ru.berrington.libraryRestApplication.util.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final BooksService booksService;
    private final PersonValidator personValidator;
    private final ModelMapper modelMapper;


    @Autowired
    public PeopleController(PeopleService peopleService, BooksService booksService, PersonValidator personValidator, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.booksService = booksService;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PersonDTO> getPeople() {
        return peopleService.findAll().stream().map(p -> peopleService.convertToPersonDTO(p)).collect(Collectors.toList());
}
    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) {
        return peopleService.convertToPersonDTO(peopleService.findById(id));
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handlerException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/new")
    public PersonDTO newPerson() {
        return new PersonDTO();
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
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
            throw new PersonNotCreatedException(errorSb.toString());
        }
        peopleService.save(peopleService.convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }



    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handlerException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



    @GetMapping("/{id}/edit")
    public PersonDTO edit(@PathVariable("id") int id) {
        return peopleService.convertToPersonDTO(peopleService.findById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if(bindingResult.hasErrors()){
            StringBuilder errorSb = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error: errors){
                errorSb.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotUpdatedException(errorSb.toString());
        }
        peopleService.update(id,peopleService.convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        peopleService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
