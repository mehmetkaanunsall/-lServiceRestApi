/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:02:38 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicnePlatformService extends ICrud<WashingPlatform> {

    public List<WashingPlatform> findAll(WashingMachicne obj);
    
    public List<WashingPlatform> findPlatformForWashingMachine(List<WashingMachicne> listOfWashingMachine);

    public int delete(WashingPlatform obj);

    public int testBeforeDelete(WashingPlatform obj);
}
