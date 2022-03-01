/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:29:59 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.dao;

import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingTank;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IWashingMachicneTankDao extends ICrud<WashingTank> {

    public List<WashingTank> findAll(WashingMachicne obj);

    public int delete(WashingTank obj);

    public int testBeforeDelete(WashingTank obj);
}
