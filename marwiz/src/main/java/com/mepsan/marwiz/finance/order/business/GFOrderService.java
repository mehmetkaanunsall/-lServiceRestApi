/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class GFOrderService extends GeneralFilterService<Order> {

    @Autowired
    private IOrderService orderService;

    public void setOrderService(IOrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "od.orderdate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "od.documentnumber" + " ilike '%" + value + "%' ";
        where = where + "or " + "od.documentserial" + " ilike '%" + value + "%' ";
        where = where + "or " + "concat(od.documentserial,'',od.documentnumber)" + " ilike '%" + value + "%' ";
        where = where + "or " + "acc.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "br.name" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void makeSearch(String value,String where) {
        searchResult = new CentrowizLazyDataModel<Order>() {
            @Override
            public List<Order> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Order> result;
                String where1 = createWhere(value);
                int count = callDaoCount(where+where1);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where+where1);
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Order> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return orderService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return orderService.count(where);
    }

}
