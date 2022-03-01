/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:54:57 AM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneService extends ICrud<WashingMachicne> {

    public List<WashingMachicne> findAll(String where);

    public int delete(WashingMachicne obj);

    public int testBeforeDelete(WashingMachicne obj);
    
    public List<WashingMachicne> selectWashinMachine(String where);
    
    public boolean sendConfiguration(String command,WashingMachicne obj);
    
    public boolean configureDetail(WashingMachicne obj);
        
}
