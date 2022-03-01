/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 02:09:16
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAutomationDeviceItemService extends ICrudService<AutomationDeviceItem> {

    public List<AutomationDeviceItem> listOfShelf(AutomationDevice obj);

    public int delete(AutomationDeviceItem automationDeviceItem);

    public int update(AutomationDeviceItem obj, boolean isStockChange);

    public int createMovement(AutomationDeviceItemMovement automationDeviceItemMovement);

    public List<AutomationDeviceItem> listOfShelfOnlyWithProduct(AutomationDevice obj);
}
