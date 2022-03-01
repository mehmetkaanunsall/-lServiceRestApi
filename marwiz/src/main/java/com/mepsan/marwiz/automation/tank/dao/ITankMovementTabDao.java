/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 05.02.2019 18:20:07
 */
package com.mepsan.marwiz.automation.tank.dao;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public interface ITankMovementTabDao {

    public List<TankMovement> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, int opType, Date begin, Date end, Warehouse warehouse);

    public int count(String where, int opType, Date begin, Date end, Warehouse warehouse);

    public DataSource getDatasource();

    public String exportData(String where, int opType, Date begin, Date end, Warehouse warehouse);
}
