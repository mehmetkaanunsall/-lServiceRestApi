/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kaanunsal.ilservice.repository;

import de.kaanunsal.ilservice.model.IL;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author kaan.unsal
 */
public interface IlRepository extends MongoRepository<IL,String>{
    
    List<IL> findAllByName(String name);
    Optional<IL>  findByName(String name);
    
}
