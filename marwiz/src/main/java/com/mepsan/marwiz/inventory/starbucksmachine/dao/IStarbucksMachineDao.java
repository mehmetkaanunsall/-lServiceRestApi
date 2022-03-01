/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.starbucksmachine.dao;

import com.mepsan.marwiz.general.model.inventory.StarbucksMachine;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author ebubekir.buker
 */
public interface IStarbucksMachineDao extends ICrud<StarbucksMachine>{
    
    public List<StarbucksMachine>listOfStarbucksMachine();
    
    public int delete(StarbucksMachine obj);
}
