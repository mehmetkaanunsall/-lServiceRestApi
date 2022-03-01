/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.general.model.finance.OrderItem;
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
public class GFOrderItemService extends GeneralFilterService<OrderItem> {

    @Autowired
    private IOrderItemService orderItemService;

    public void setOrderItemService(IOrderItemService orderItemService) {
        this.orderItemService = orderItemService;
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
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.boxquantity,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.shelfquantity,0)" + ",'99999999999999D9999')"+ " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "(CASE WHEN COALESCE(((odi.twomonthsale/8)/1)-odi.shelfquantity,0) < 0 THEN 0 ELSE COALESCE(((odi.twomonthsale/8)/1)-odi.shelfquantity,0) END)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "(CASE WHEN COALESCE(((odi.twomonthsale/8)/1)-odi.shelfquantity,0) < 0 THEN ceil(0 + odi.shelfquantity) ELSE COALESCE(ceil((((odi.twomonthsale/8)/1)-odi.shelfquantity) + odi.shelfquantity),0) END)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.warehousequantity,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.twomonthsale)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.twomonthsale/8,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "CASE WHEN odi.twomonthsale = 0 THEN 0 ELSE COALESCE((odi.warehousequantity/odi.twomonthsale)*7,0) END" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "CASE WHEN odi.boxquantity = 0 THEN 0 ELSE CASE WHEN COALESCE(((odi.twomonthsale/8)/1)-odi.shelfquantity,0) < 0 THEN  Round(((0 + odi.shelfquantity) - odi.warehousequantity) / odi.boxquantity, 0) * odi.boxquantity  ELSE Round(COALESCE(((((odi.twomonthsale/8)/1)-odi.shelfquantity) + odi.shelfquantity) - odi.warehousequantity,0) / odi.boxquantity, 0) * odi.boxquantity  END END" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.minimumquantity,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.maximumquantity,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        where = where + "or " + "to_char(" + "COALESCE(odi.quantity,0)" + ",'99999999999999D9999')" + " ilike '%" + value + "%'  ";
        
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void makeSearch(String value,String where) {
        searchResult = new CentrowizLazyDataModel<OrderItem>() {
            @Override
            public List<OrderItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<OrderItem> result;
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
    public List<OrderItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return orderItemService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return orderItemService.count(where);
    }

}
