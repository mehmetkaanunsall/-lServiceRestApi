/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 05.02.2019 18:27:19
 */
package com.mepsan.marwiz.automation.tank.business;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.automation.tank.dao.TankMovement;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITankMovementTabService {

    public List<TankMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, Warehouse warehouse);

    public int count(String where, int opType, Date begin, Date end, Warehouse warehouse);
    
    public void exportExcel(Warehouse warehouse, List<Boolean> toogleList, String where, int opType, Date begin, Date end);
    
}
