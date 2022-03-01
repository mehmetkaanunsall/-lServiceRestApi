/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   22.01.2018 03:45:27
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.inventory.stock.business.GFStockService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped

public class StockBookFilterBean extends BookFilterBean<Stock> {

    @ManagedProperty(value = "#{gfStockService}")
    public GFStockService gfStockService;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setGfStockService(GFStockService gfStockService) {
        this.gfStockService = gfStockService;
    }


    @Override
    public List<Stock> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<Stock> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<Stock>() {

            @Override
            public List<Stock> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                String whereSatment = " ";
                dataList = stockService.stockBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, whereSatment, type, param);
                int count = stockService.stockBookCount(whereSatment, type, param);
                dataListLazyLoading.setRowCount(count);
                return dataList;
            }
        };
    }

    @Override
    public void generalFilter(String type, List<Object> param) {
        if (autoCompleteValue == null) {
            refresh();
        } else {
            gfStockService.makeSearchForbook(autoCompleteValue, type, param);
            dataListLazyLoading = gfStockService.searchResult;
        }
    }

}
