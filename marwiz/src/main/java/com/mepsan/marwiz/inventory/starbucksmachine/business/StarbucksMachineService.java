/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.starbucksmachine.business;

import com.mepsan.marwiz.general.model.inventory.StarbucksMachine;
import com.mepsan.marwiz.inventory.starbucksmachine.dao.IStarbucksMachineDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ebubekir.buker
 */
public class StarbucksMachineService implements IStarbucksMachineService{

    @Autowired
    private IStarbucksMachineDao starbucksMachineDao;

    public void setStarbucksMachineDao(IStarbucksMachineDao starbucksMachineDao) {
        this.starbucksMachineDao = starbucksMachineDao;
    }

    @Override
    public List<StarbucksMachine> listOfStarbucksMachine() {
        return starbucksMachineDao.listOfStarbucksMachine();
    }

    @Override
    public int delete(StarbucksMachine obj) {
        return starbucksMachineDao.delete(obj);
    }

    @Override
    public int create(StarbucksMachine obj) {
        return starbucksMachineDao.create(obj);
    }

    @Override
    public int update(StarbucksMachine obj) {
        return starbucksMachineDao.update(obj);
    }
    
  
    
}
