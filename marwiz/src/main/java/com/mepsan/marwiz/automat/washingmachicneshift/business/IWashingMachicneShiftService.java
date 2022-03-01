/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:57:36 PM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.business;

import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;

public interface IWashingMachicneShiftService extends ICrudService<AutomatShift>, ILazyGridService<AutomatShift> {

    public AutomatShift controlOpenShift();
}
