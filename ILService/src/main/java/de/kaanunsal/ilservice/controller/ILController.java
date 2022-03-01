package de.kaanunsal.ilservice.controller;

import de.kaanunsal.ilservice.exception.IlAlreadyExistsException;
import de.kaanunsal.ilservice.exception.IlNotFounException;
import de.kaanunsal.ilservice.model.IL;
import de.kaanunsal.ilservice.service.ILService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author kaan.unsal
 */
@RestController
@RequestMapping("/iller")
@AllArgsConstructor
public class ILController {

    private final ILService iLService;

    @GetMapping
    public ResponseEntity<List<IL>> getIller(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(iLService.getIller(name), OK);

    }

    /*
    
        @PathVariable ile gelen id parametresini değişkene atadık
    
     */
    @GetMapping("/{id}")
    public ResponseEntity<IL> getIl(@PathVariable String id) {
      
            return new ResponseEntity<>(getIlById(id), OK);
       
    }

    /*
    
        @RequestBody ile parametre olarak gelecek il yapısında JSON beklediğimizi belirtiyoruz.
    
     */
    @PostMapping
    public ResponseEntity<IL> createIl(@RequestBody IL newIl) {
        return new ResponseEntity<>(iLService.createIl(newIl), HttpStatus.CREATED);
    }

    /*
        @PutMapping ile ilgili id ye sahip ilin bilgilerini güncellemeyi sağlar
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> getIl(@PathVariable String id, @RequestBody IL newIl) {

        iLService.updateIl(id, newIl);
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIl(@PathVariable String id) {
        iLService.deleteIl(id);
        return new ResponseEntity<>(OK);
    }

    private IL getIlById(String id) {
        return iLService.getIlById(id);

    }
    
    
    @ExceptionHandler(IlNotFounException.class)
    public  ResponseEntity<String> handleIlNotFoundException(IlNotFounException ilNotFounException)
    {
        return  new ResponseEntity<>(ilNotFounException.getMessage(),HttpStatus.NOT_FOUND);
    }

    
      @ExceptionHandler(IlAlreadyExistsException.class)
    public  ResponseEntity<String> handleIlAlreadyExistsException(IlAlreadyExistsException iaee)
    {
        return  new ResponseEntity<>(iaee.getMessage(),HttpStatus.CONFLICT);
    }

    
}
