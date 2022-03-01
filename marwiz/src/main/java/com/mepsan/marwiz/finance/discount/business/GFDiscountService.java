/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.04.2019 15:19:35
 */
package com.mepsan.marwiz.finance.discount.business;

import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

public class GFDiscountService extends GeneralFilterService<Discount> {

    @Autowired
    private IDiscountService discountService;

    public void setDiscountService(IDiscountService discountService) {
        this.discountService = discountService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "to_char(" + "dsc.begindate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + " to_char(" + "dsc.enddate" + ",'dd.MM.yyyy')" + " ilike '%" + value + "%' ";
        where = where + "or " + "dsc.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "dsc.description" + " ilike '%" + value + "%'  ";
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

    @Override
    public List<Discount> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return discountService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return discountService.count(where);
    }

}
