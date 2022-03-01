/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 10:49:23
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.centralsupplier.business.GFCentralSupplierService;
import com.mepsan.marwiz.general.centralsupplier.business.ICentralSupplierService;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
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
public class CentralSupplierBookCheckboxFilterBean extends BookFilterBean<CentralSupplier> {

    @ManagedProperty(value = "#{centralSupplierService}")
    public ICentralSupplierService centralSupplierService;

    @ManagedProperty(value = "#{gfCentralSupplierService}")
    public GFCentralSupplierService gfCentralSupplierService;

    List<CentralSupplier> tempSelectedDataList, tempUnselectedDataList;
    String selectedCount;
    int supplierType;

    public List<CentralSupplier> getTempSelectedDataList() {
        return tempSelectedDataList;
    }

    public void setTempSelectedDataList(List<CentralSupplier> tempSelectedDataList) {
        this.tempSelectedDataList = tempSelectedDataList;
    }

    public List<CentralSupplier> getTempUnselectedDataList() {
        return tempUnselectedDataList;
    }

    public void setTempUnselectedDataList(List<CentralSupplier> tempUnselectedDataList) {
        this.tempUnselectedDataList = tempUnselectedDataList;
    }

    public String getSelectedCount() {
        return selectedCount;
    }

    public void setSelectedCount(String selectedCount) {
        this.selectedCount = selectedCount;
    }

    public void setCentralSupplierService(ICentralSupplierService centralSupplierService) {
        this.centralSupplierService = centralSupplierService;
    }

    public void setGfCentralSupplierService(GFCentralSupplierService gfCentralSupplierService) {
        this.gfCentralSupplierService = gfCentralSupplierService;
    }

    public int getSupplierType() {
        return supplierType;
    }

    public void setSupplierType(int supplierType) {
        this.supplierType = supplierType;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------CentralSupplierBookCheckboxFilterBean--------------------");
        tempSelectedDataList = new ArrayList<>();
        tempUnselectedDataList = new ArrayList<>();
        selectedDataList = new ArrayList<>();
        dataList = new ArrayList<>();
        supplierType = 0;
    }

    @Override
    public List<CentralSupplier> callService(List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<CentralSupplier> callServiceLazyLoading(String where, List<Object> param, String type) {
        return new CentrowizLazyDataModel<CentralSupplier>() {

            @Override
            public List<CentralSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                int count = 0;
                dataList = centralSupplierService.centralSupplierBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, type, param, supplierType);
                count = centralSupplierService.centralSupplierBookCount(where, type, param, supplierType);
                dataListLazyLoading.setRowCount(count);
                selectedDataList.clear();
                selectedDataList.addAll(tempSelectedDataList);
                return dataList;

            }
        };
    }

    public void changeSelected() {
        List<CentralSupplier> temp = new ArrayList();
        if (autoCompleteValue == null || "".equals(autoCompleteValue)) {

            for (CentralSupplier centralSupplier : dataList) {
                for (Iterator<CentralSupplier> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    CentralSupplier next = iterator.next();
                    if (next.getId() == centralSupplier.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        } else {
            for (CentralSupplier centralSupplier : gfCentralSupplierService.searchDataList) {
                for (Iterator<CentralSupplier> iterator = tempSelectedDataList.iterator(); iterator.hasNext();) {
                    CentralSupplier next = iterator.next();
                    if (next.getId() == centralSupplier.getId()) {
                        iterator.remove();
                        temp.add(next);
                        break;
                    }
                }
            }
        }

        tempSelectedDataList.addAll(selectedDataList);
        for (CentralSupplier centralSupplier : temp) {
            if (!tempSelectedDataList.contains(centralSupplier)) {
                tempUnselectedDataList.add(centralSupplier);
            }
        }
        for (CentralSupplier u : tempSelectedDataList) {
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
            gfCentralSupplierService.makeSearchForbook(autoCompleteValue, type, param);
            dataListLazyLoading = gfCentralSupplierService.searchResult;
        }
    }

    public void clearSelected() {
        tempSelectedDataList.clear();
        selectedDataList.clear();
        RequestContext.getCurrentInstance().update(getUpdate() + ":dtbFilter");
    }

}
