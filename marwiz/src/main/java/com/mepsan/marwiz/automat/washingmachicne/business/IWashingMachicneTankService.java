/**
 * 
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:32:08 PM
 */

package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IWashingMachicneTankService extends ICrud<WashingTank> {

    public List<WashingTank> findAll(WashingMachicne obj);
    
    public int delete(WashingTank obj);
    
        public int testBeforeDelete(WashingTank obj);


}