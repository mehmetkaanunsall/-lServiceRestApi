/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 02:10:32
 */
package com.mepsan.marwiz.inventory.automationdevice.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.inventory.automationdevice.dao.IAutomationDeviceItemDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomationDeviceItemService implements IAutomationDeviceItemService {

    @Autowired
    private IAutomationDeviceItemDao automationDeviceItemDao;

    public void setAutomationDeviceItemDao(IAutomationDeviceItemDao automationDeviceItemDao) {
        this.automationDeviceItemDao = automationDeviceItemDao;
    }

    @Override
    public List<AutomationDeviceItem> listOfShelf(AutomationDevice obj) {
        return automationDeviceItemDao.listOfShelf(obj);
    }

    @Override
    public int create(AutomationDeviceItem obj) {
        return automationDeviceItemDao.create(obj);
    }

    @Override
    public int delete(AutomationDeviceItem automationDeviceItem) {
        return automationDeviceItemDao.delete(automationDeviceItem);
    }

    @Override
    public int update(AutomationDeviceItem obj, boolean isStockChange) {
        return automationDeviceItemDao.update(obj, isStockChange);
    }

    @Override
    public int update(AutomationDeviceItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int createMovement(AutomationDeviceItemMovement automationDeviceItemMovement) {
        return automationDeviceItemDao.createMovement(automationDeviceItemMovement);
    }

    @Override
    public List<AutomationDeviceItem> listOfShelfOnlyWithProduct(AutomationDevice obj) {
        return automationDeviceItemDao.listOfShelfOnlyWithProduct(obj);
    }

}
