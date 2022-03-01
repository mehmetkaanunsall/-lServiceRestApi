/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:55:45 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneDao extends ICrud<WashingMachicne> {

    public List<WashingMachicne> findAll(String where);
    
    public List<WashingMachicne> selectWashinMachine(String where);

    public int delete(WashingMachicne obj);

    public int testBeforeDelete(WashingMachicne obj);
    
    public String sendConfiguration(WashingMachicne obj);

}
