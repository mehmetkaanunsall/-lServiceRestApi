/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 04:02:28
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.inventory.stock.business.GFStockService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class StockBookCheckboxFilterBean extends BookFilterBean<Stock> {

    List<Stock> tempSelectedDataList, tempUnselectedDataList;
    String selectedCount;
    private boolean isFilter;
    private boolean isWithoutSalePrice;

    @ManagedProperty(value = "#{stockService}")
    public IStockService stockService;

    @ManagedProperty(value = "#{gfStockService}")
    public GFStockService gfStockService;

    public List<Stock> getTempSelectedDataList() {
        return tempSelectedDataList;
    }

    public void setTempSelectedDataList(List<Stock> tempSelectedDataList) {
        this.tempSelectedDataList = tempSelectedDataList;
    }

    public List<Stock> getTempUnselectedDataList() {
        return tempUnselectedDataList;
    }

    public void setTempUnselectedDataList(List<Stock> tempUnselectedDataList) {
        this.tempUnselectedDataList = tempUnselectedDataList;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    public IStockService getStockService() {
        return stockService;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setGfStockService(GFStockService gfStockService) {
        this.gfStockService = gfStockService;
    }

    public boolean isIsFilter() {
        return isFilter;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }

    public boolean isIsWithoutSalePrice() {
        return isWithoutSalePrice;
    }

    public void setIsWithoutSalePrice(boolean isWithoutSalePrice) {
        this.isWithoutSalePrice = isWithoutSalePrice;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------StockBookCheckboxFilterBean--------------------");
        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
        selectedDataList = new ArrayList<>();
        dataList = new ArrayList<>();
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
                int count = 0;
                dataList = stockService.stockBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, type, changeParam(param));
                count = stockService.stockBookCount(where, type, param);
                dataListLazyLoading.setRowCount(count);
                selectedDataList.clear();
                selectedDataList.addAll(tempSelectedDataList);
                return dataList;

            }
        };
    }

    public List<Object> changeParam(List<Object> param) {

        if ("stockbatchupdate".equals(type)) {
            param.remove(param.size() - 1);
            param.add(isFilter);
        }
        return param;
    }

    public void changeSelected() {
        List<Stock> temp = new ArrayList();
        if (autoCompleteValue == null || "".equals(autoCompleteValue)) {

            for (Stock stock : dataList) {
                for (Iterator<Stock> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    Stock next = iterator.next();
                    if (next.getId() == stock.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {
            for (Stock stock : gfStockService.searchDataList) {
                for (Iterator<Stock> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    Stock next = iterator.next();
                    if (next.getId() == stock.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataList.addAll(selectedDataList);
        for (Stock stock : temp) {
            if (!tempSelectedDataList.contains(stock)) {
                tempUnselectedDataList.add(stock);
            }
        }
        for (Stock u : tempSelectedDataList) {
            if (tempUnselectedDataList.contains(u)) {
                tempUnselectedDataList.remove(u);
            }
        }
    }

    public void reset() {

        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
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

    public void clearSelected() {
        tempSelectedDataList.clear();
        selectedDataList.clear();
        RequestContext.getCurrentInstance().update(getUpdate() + ":dtbFilter");
    }

}
