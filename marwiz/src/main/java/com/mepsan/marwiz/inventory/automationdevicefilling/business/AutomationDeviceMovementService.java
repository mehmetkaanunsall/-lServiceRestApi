/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2020 11:44:33
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.business;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.inventory.automationdevicefilling.dao.IAutomationDeviceMovementDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class AutomationDeviceMovementService implements IAutomationDeviceMovementService {

    @Autowired
    private IAutomationDeviceMovementDao automationDeviceMovementDao;

    public void setAutomationDeviceMovementDao(IAutomationDeviceMovementDao automationDeviceMovementDao) {
        this.automationDeviceMovementDao = automationDeviceMovementDao;
    }

    @Override
    public String createWhere(AutomationDevice automationDevice, Date beginDate, Date endDate) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND vmm.vendingmachineitem_id IN(SELECT vmi.id FROM inventory.vendingmachineitem vmi WHERE vmi.deleted = FALSE AND vmi.vendingmachine_id = " + automationDevice.getId() + ") ";

        where += " AND vmm.processdate BETWEEN '" + sd.format(beginDate) + "' AND '" + sd.format(endDate) + "' ";
        return where;
    }

    @Override
    public List<AutomationDeviceItemMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return automationDeviceMovementDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return automationDeviceMovementDao.count(where);
    }

}
