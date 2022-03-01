/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2019 02:59:15
 */
package com.mepsan.marwiz.automation.fuelshift.business;

import com.mepsan.marwiz.automation.fuelshift.dao.IFuelShiftTransferDao;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFFuelShiftService extends GeneralFilterService<FuelShift> {

    @Autowired
    private IFuelShiftTransferDao fuelShiftTransferDao;

    public void setFuelShiftTransferDao(IFuelShiftTransferDao fuelShiftTransferDao) {
        this.fuelShiftTransferDao = fuelShiftTransferDao;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "shf.shiftno" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.begindate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.enddate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<FuelShift> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, boolean isCheckDeleted) {
        return fuelShiftTransferDao.findAll(first, pageSize, sortField, sortOrder, filters, where, isCheckDeleted);
    }

    public List<FuelShift> callDaoCount(String where, boolean isCheckDeleted) {
        return fuelShiftTransferDao.count(where, isCheckDeleted);
    }

    public void makeSearch(String where, boolean isCheckDeleted, String value) {
        searchResult = new CentrowizLazyDataModel<FuelShift>() {
            @Override
            public List<FuelShift> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<FuelShift> result;
                List<FuelShift> listOfTotals;
                String where1 = where + " " + createWhere(value);
                listOfTotals = callDaoCount(where1, isCheckDeleted);
                int count = 0;
                for (FuelShift fuelShift : listOfTotals) {
                    count += fuelShift.getId();
                }

                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1, isCheckDeleted);

                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<FuelShift> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
