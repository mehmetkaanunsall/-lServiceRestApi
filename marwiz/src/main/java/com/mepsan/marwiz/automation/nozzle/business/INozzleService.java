/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:26:44
 */
package com.mepsan.marwiz.automation.nozzle.business;

import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;

public interface INozzleService extends ILazyGrid<Nozzle>, ICrud<Nozzle> {

    public int checkNozzle(Nozzle nozzle);

    public int delete(Nozzle nozzle);

    public int testBeforeDelete(Nozzle nozzle);

}
