/**
 * 
 *
 *
 * @author Gozde Gursel
 *
 * Created on 5:56:41 PM
 */

package com.mepsan.marwiz.inventory.automationdevice.dao;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;


public interface IAutomationDeviceCardDao extends ICrud<AutomationDeviceCard>{
 public List<AutomationDeviceCard> listOfCard(AutomationDevice obj);

    public int delete(AutomationDeviceCard automationDeviceCard);
    
    
}