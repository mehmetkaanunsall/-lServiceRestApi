/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 11.02.2019 14:16:57
 */
package com.mepsan.marwiz.automation.nozzle.business;

import com.mepsan.marwiz.automation.nozzle.dao.NozzleMovement;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;

public interface INozzleMovementService extends ILazyGrid<NozzleMovement> {

    public String createWhere(Date beginDate, Date endDate, Nozzle nozzle);


}
