/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 04:14:50
 */
package com.mepsan.marwiz.inventory.stock.business;

import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFStockService extends GeneralFilterService<Stock> {

    @Autowired
    private IStockService stockService;

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";
        where = " " + where + "stck.barcode" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.code" + " ilike '%" + value + "%'  ";
        where = where + "or " + "acc.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "cspp.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.centerproductcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "gunt.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "br.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sttd.name" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(si.purchasecount AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(si.salecount AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(si.currentpurchaseprice AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + "or " + "CAST(si.currentsaleprice AS TEXT)" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;

    }

    @Override
    public String createWhereForBook(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "stck.name" + " ilike '%" + value + "%' ";
        where = where + "or " + "stck.code" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.centerproductcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "stck.barcode" + " ilike '%" + value + "%'  ";
        where = where + "or " + "sab.barcode" + " ilike '%" + value + "%'  ";
        where = where + ")";
        return where;
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        searchResult = new CentrowizLazyDataModel<Stock>() {
            @Override
            public List<Stock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int count = stockService.stockBookCount(createWhereForBook(value), type, param);
                searchDataList = stockService.stockBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForBook(value), type, param);
                searchResult.setRowCount(count);
                if (type.equals("reportcheckbox") || type.equals("reportcheckboxwithbranch") || type.equals("stockbatchupdate") || type.equals("warehouseitem") || type.equals("discountitem")
                       || type.equals("invoiceCheckBox") || type.equals("orderCheckBox")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    StockBookCheckboxFilterBean stockBookCheckboxFilterBean = (StockBookCheckboxFilterBean) viewMap.get("stockBookCheckboxFilterBean");
                    for (Stock stock : stockBookCheckboxFilterBean.getTempSelectedDataList()) {
                        if (!stockBookCheckboxFilterBean.getSelectedDataList().contains(stock)) {
                            stockBookCheckboxFilterBean.getSelectedDataList().add(stock);
                        }

                    }
                }
                return searchDataList;
            }
        };
    }

    public void makeSearch(String where, String value, List<Stock> listOfTotals) {
        searchResult = new CentrowizLazyDataModel<Stock>() {
            @Override
            public List<Stock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Stock> result;
                String where1 = where + " " + createWhere(value);
                result = callDaoList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where1);
                int count = 0;
                for (Stock stock : listOfTotals) {
                    count += stock.getId();
                }
                searchResult.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public List<Stock> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return stockService.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int callDaoCount(String where) {
        return stockService.count(where);
    }

}
