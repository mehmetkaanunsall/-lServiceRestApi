/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 11:33:23
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

public interface IAutomationDeviceService extends ICrudService<AutomationDevice> {

    public List<AutomationDevice> findAll(String where);

    public int delete(AutomationDevice automationDevice);

    public boolean configureDetail(AutomationDevice automationDevice);

    public boolean sendCommand(String command, AutomationDevice obj);

    public int controlAutomationDevice();
}
