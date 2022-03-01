/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.kaanunsal.ilservice.service;

import de.kaanunsal.ilservice.exception.IlAlreadyExistsException;
import de.kaanunsal.ilservice.exception.IlNotFounException;
import java.util.List;
import org.springframework.stereotype.Service;
import de.kaanunsal.ilservice.model.IL;
import de.kaanunsal.ilservice.repository.IlRepository;
import java.util.Date;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
public class ILService {

    private final IlRepository ilRepository;

    public List<IL> getIller(String name) {
        if (name == null) {
            return ilRepository.findAll();
        } else {
            return ilRepository.findAllByName(name);
        }
    }

    public IL createIl(IL newIl) {
        Optional<IL> ilByName = ilRepository.findByName(newIl.getName());

        if (ilByName.isPresent()) {
            throw new IlAlreadyExistsException("IlAlreadyExistsException");
        } else {
            return ilRepository.save(newIl);
        }

    }

    public void deleteIl(String id) {
        ilRepository.deleteById(id);
    }

    public IL getIlById(String id) {
        return ilRepository.findById(id)
                .orElseThrow(() -> new IlNotFounException("Il not found"));
    }

    public void updateIl(String id, IL newIl) {
        IL oldil = getIlById(id);
        oldil.setName(newIl.getName());
        ilRepository.save(oldil);
    }

}
