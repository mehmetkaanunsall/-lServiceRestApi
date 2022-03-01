/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:57:10 PM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.dao;

import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;

public interface IWashingMachicneShiftDao extends ICrud<AutomatShift>, ILazyGrid<AutomatShift> {

    public AutomatShift controlOpenShift();
}
