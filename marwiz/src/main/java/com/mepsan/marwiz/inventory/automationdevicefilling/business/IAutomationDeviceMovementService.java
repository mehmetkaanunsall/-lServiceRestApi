/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2020 11:44:16
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.Date;


public interface IAutomationDeviceMovementService extends ILazyGridService<AutomationDeviceItemMovement> {

    public String createWhere(AutomationDevice automationDevice, Date beginDate, Date endDate);

}
