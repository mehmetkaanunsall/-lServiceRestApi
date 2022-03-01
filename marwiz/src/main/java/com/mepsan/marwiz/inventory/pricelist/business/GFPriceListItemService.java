/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2020 04:49:44
 */
package com.mepsan.marwiz.inventory.pricelist.business;

import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.PriceListItem;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import com.mepsan.marwiz.inventory.pricelist.presentation.PriceListBatchOpreationsBean;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFPriceListItemService extends GeneralFilterService<PriceListItem> {

    @Autowired
    private IPriceListItemService priceListItemService;

    public void setPriceListItemService(IPriceListItemService priceListItemService) {
        this.priceListItemService = priceListItemService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(si.recommendedprice AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(pli.price AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    public String createWhereForfilter(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    public void makeSearchForFilter(String value, String type, PriceList obj) {
        searchResult = new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int count = priceListItemService.count(createWhereForfilter(value), obj);
                searchDataList = priceListItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForfilter(value), obj);
                searchResult.setRowCount(count);
                if (type.equals("pricelistbatchoperation")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    PriceListBatchOpreationsBean priceListBatchOpreationsBean = (PriceListBatchOpreationsBean) viewMap.get("priceListBatchOpreationsBean");
                    for (PriceListItem pi : priceListBatchOpreationsBean.getTempSelectedDataList()) {
                        if (!priceListBatchOpreationsBean.getSelectedStcokList().contains(pi)) {
                            priceListBatchOpreationsBean.getSelectedStcokList().add(pi);
                        }

                    }
                    searchDataList = priceListBatchOpreationsBean.changeQuantity(searchDataList);
                }
                return searchDataList;
            }
        };
    }

    public void makeSearchForPriceList(String value, PriceList obj) {
        searchResult = new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                int count = priceListItemService.count(createWhere(value), obj);
                searchDataList = priceListItemService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhere(value), obj);
                searchResult.setRowCount(count);
                return searchDataList;
            }
        };
    }

    public void makeSearchForRecordedStockFilter(String value, String type, PriceList obj) {
        searchResult = new CentrowizLazyDataModel<PriceListItem>() {
            @Override
            public List<PriceListItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int count = priceListItemService.countRecordedStock(createWhereForfilter(value), obj);
                searchDataList = priceListItemService.findAllRecordedStock(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForfilter(value), obj, 1);
                searchResult.setRowCount(count);
                if (type.equals("pricelistbatchoperationrecordstock")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    PriceListBatchOpreationsBean priceListBatchOpreationsBean = (PriceListBatchOpreationsBean) viewMap.get("priceListBatchOpreationsBean");
                    for (PriceListItem pi : priceListBatchOpreationsBean.getTempSelectedDataListRecordedStock()) {
                        if (!priceListBatchOpreationsBean.getSelectedRecordedStock().contains(pi)) {
                            priceListBatchOpreationsBean.getSelectedRecordedStock().add(pi);
                        }

                    }
                }
                return searchDataList;
            }
        };
    }

    @Override
    public String createWhereForBook(String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<PriceListItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, PriceList obj) {
        return priceListItemService.findAll(first, pageSize, sortField, sortOrder, filters, where, obj);
    }

    public int callDaoCount(String where, PriceList obj) {
        return priceListItemService.count(where, obj);
    }

    @Override
    public List<PriceListItem> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
