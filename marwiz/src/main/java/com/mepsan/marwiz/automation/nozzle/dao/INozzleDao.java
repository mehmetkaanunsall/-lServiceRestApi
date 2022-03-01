/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:27:08
 */
package com.mepsan.marwiz.automation.nozzle.dao;

import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;

public interface INozzleDao extends ICrud<Nozzle>, ILazyGrid<Nozzle> {

    public int checkNozzle(Nozzle nozzle);

    public int delete(Nozzle nozzle);

    public int testBeforeDelete(Nozzle nozzle);
}
