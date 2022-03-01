/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automat.washingmachicneshift.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Gozde Gursel
 */
public class GFWashingMachicneShiftService extends GeneralFilterService<AutomatShift> {

    @Autowired
    private IWashingMachicneShiftService washingMachicneShiftService;

    @Autowired
    private ApplicationBean applicationBean;

    public void setWashingMachicneShiftService(IWashingMachicneShiftService washingMachicneShiftService) {
        this.washingMachicneShiftService = washingMachicneShiftService;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "shf.shiftno" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.begindate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "to_char(" + "shf.enddate" + ",'dd.MM.yyyy HH:mm:ss')" + " ilike '%" + value + "%' ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%'  ";
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

    public void makeSearch(String where, String value) {
        searchResult = new CentrowizLazyDataModel<AutomatShift>() {
            @Override
            public List<AutomatShift> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<AutomatShift> result;
                String where1 = where + " " + createWhere(value);
                int count = callDaoCount(where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<AutomatShift> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return washingMachicneShiftService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return washingMachicneShiftService.count(where);
    }

}
