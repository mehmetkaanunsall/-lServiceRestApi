/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:23:04 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingNozzle;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneNozzleDao extends ICrud<WashingNozzle> {

    public List<WashingNozzle> findAll(WashingPlatform obj);

    public List<WashingNozzle> findNozzleForPlatform(String where);

    public int delete(WashingNozzle obj);

}
