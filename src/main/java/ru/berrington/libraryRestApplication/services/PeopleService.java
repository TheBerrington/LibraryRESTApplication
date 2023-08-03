package ru.berrington.libraryRestApplication.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.berrington.libraryRestApplication.dto.PersonDTO;
import ru.berrington.libraryRestApplication.models.Book;
import ru.berrington.libraryRestApplication.models.Person;
import ru.berrington.libraryRestApplication.repositories.PeopleRepositories;
import ru.berrington.libraryRestApplication.util.PersonNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    public final PeopleRepositories peopleRepositories;
    public final ModelMapper modelMapper;

    @Autowired
    public PeopleService(PeopleRepositories peopleRepositories, ModelMapper modelMapper) {
        this.peopleRepositories = peopleRepositories;
        this.modelMapper = modelMapper;
    }
    @Transactional
    public void save(Person person){
        person.setCountryId(nationality(person.getName()));
        peopleRepositories.save(person);

    }

    private String nationality(String name){
        RestTemplate restTemplate = new RestTemplate();
        String[] nameList = name.split(" ");
        if(nameList.length>1) name = nameList[1];
        StringBuilder translatedName = new StringBuilder(translater(name));
        translatedName.deleteCharAt(0);
        translatedName.deleteCharAt(0);
        translatedName.deleteCharAt(translatedName.length()-1);
        translatedName.deleteCharAt(translatedName.length()-1);
        String url = "https://api.nationalize.io?name=" + translatedName.toString();
        String ans = restTemplate.getForObject(url, String.class);
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder nationality = new StringBuilder("UNKNOWN");
        try {
            JsonNode node = mapper.readTree(ans);
            nationality.setLength(0);
            nationality.append(node.get("country").get(0).get("country_id"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return nationality.toString();
    }

    private String translater(String name) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://translate.api.cloud.yandex.net/translate/v2/translate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization","Bearer " + "YANDEX_TOKEN");
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("folderId","YOUR_FOLDER_ID");
        jsonData.put("targetLanguageCode", "en");
        jsonData.put("texts","[" + name + "]");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(jsonData, headers);
        String requestFromYandex = restTemplate.postForObject(url, request, String.class);
        ObjectMapper mapper = new ObjectMapper();
        StringBuilder translatedName = new StringBuilder();
        try {
            JsonNode obj = mapper.readTree(requestFromYandex);
            translatedName.append(obj.get("translations").get(0).get("text"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return translatedName.toString();
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO,Person.class);
    }

    public PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person,PersonDTO.class);
    }

    @Transactional
    public void update(int id, Person updatingPerson){
        updatingPerson.setId(id);
        peopleRepositories.save(updatingPerson);
    }
    @Transactional
    public void delete(int id){
        peopleRepositories.deleteById(id);
    }

    public Optional<Person> findPersonByName(String name){
        return peopleRepositories.findPersonByName(name);
    }

    public Person findById(int id){
        return peopleRepositories.findById(id).orElseThrow(PersonNotFoundException::new);
    }
    public List<Person> findAll(){
        return peopleRepositories.findAll();
    }
    public List<Book> findBooksByPersonId(int id){
        return peopleRepositories.findById(id).orElse(null).getBooks();
    }



}
