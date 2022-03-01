/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:04:35 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicnePlatformDao extends ICrud<WashingPlatform> {

    public List<WashingPlatform> findAll(WashingMachicne obj);
    
    public List<WashingPlatform> findPlatformForWashingMachicne(String where);
    

    public int delete(WashingPlatform obj);

    public int testBeforeDelete(WashingPlatform obj);

}
