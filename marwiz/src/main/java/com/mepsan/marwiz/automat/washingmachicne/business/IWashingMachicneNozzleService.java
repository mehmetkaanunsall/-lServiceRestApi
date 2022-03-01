/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:21:35 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.business;

import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneNozzleService extends ICrud<WashingNozzle> {

    public List<WashingNozzle> findAll(WashingPlatform obj);

    public int delete(WashingNozzle obj);
    
    public List<WashingNozzle> findNozzleForPlatform(List<WashingPlatform> listOfPlatform);
}
