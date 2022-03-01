/**
 * This interface ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 11:32:55
 */
package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IAutomationDeviceDao extends ICrud<AutomationDevice> {

    public List<AutomationDevice> findAll(String where);

    public int delete(AutomationDevice automationDevice);

    public String configureDetail(AutomationDevice automationDevice);

    public int controlAutomationDevice();
}
