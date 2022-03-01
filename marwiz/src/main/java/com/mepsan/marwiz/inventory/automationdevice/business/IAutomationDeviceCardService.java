/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:57:50 PM
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

public interface IAutomationDeviceCardService extends  ICrud<AutomationDeviceCard>{

    public List<AutomationDeviceCard> listOfCard(AutomationDevice obj);

    public int delete(AutomationDeviceCard automationDeviceCard);
    
    public AutomationDeviceCard sendCommand(String command,AutomationDevice obj);
}
