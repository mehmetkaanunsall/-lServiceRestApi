/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.03.2020 11:14:13
 */
package com.mepsan.marwiz.general.centralsupplier.business;

import com.mepsan.marwiz.general.common.CentralSupplierBookCheckboxFilterBean;
import com.mepsan.marwiz.general.model.general.CentralSupplier;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralFilterService;
import com.mepsan.marwiz.inventory.pricelist.presentation.PriceListBatchOpreationsBean;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class GFCentralSupplierService extends GeneralFilterService<CentralSupplier> {

    @Autowired
    private ICentralSupplierService centralSupplierService;

    public void setCentralSupplierService(ICentralSupplierService centralSupplierService) {
        this.centralSupplierService = centralSupplierService;
    }

    @Override
    public String createWhere(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "cspp.name" + " ilike '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    @Override
    public String createWhereForBook(String value) {
        value = value.replace("'", "");
        String where = "and (";

        where = " " + where + "cspp.name" + " ilike '%" + value + "%' ";
        where = where + ")";
        return where;
    }

    @Override
    public void makeSearchForbook(String value, String type, List<Object> param) {
        searchResult = new CentrowizLazyDataModel<CentralSupplier>() {
            @Override
            public List<CentralSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int supplierType = 0;
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                CentralSupplierBookCheckboxFilterBean centralSupplierBookCheckboxFilterBean = (CentralSupplierBookCheckboxFilterBean) viewMap.get("centralSupplierBookCheckboxFilterBean");
                supplierType = centralSupplierBookCheckboxFilterBean.getSupplierType();
                int count = centralSupplierService.centralSupplierBookCount(createWhereForBook(value), type, param, supplierType);
                searchDataList = centralSupplierService.centralSupplierBook(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhereForBook(value), type, param, supplierType);
                searchResult.setRowCount(count);
                if (type.equals("reportcheckbox")) {
                    for (CentralSupplier centralSupplier : centralSupplierBookCheckboxFilterBean.getTempSelectedDataList()) {
                        if (!centralSupplierBookCheckboxFilterBean.getSelectedDataList().contains(centralSupplier)) {
                            centralSupplierBookCheckboxFilterBean.getSelectedDataList().add(centralSupplier);
                        }

                    }
                }
                return searchDataList;
            }
        };
    }

    public void makeSearchForFilter(String value, String type) {
        searchResult = new CentrowizLazyDataModel<CentralSupplier>() {
            @Override
            public List<CentralSupplier> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

                int count = centralSupplierService.count(createWhere(value));
                searchDataList = centralSupplierService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, createWhere(value));
                searchResult.setRowCount(count);
                if (type.equals("pricelistbatchoperation")) {
                    Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                    PriceListBatchOpreationsBean priceListBatchOpreationsBean = (PriceListBatchOpreationsBean) viewMap.get("priceListBatchOpreationsBean");
                    for (CentralSupplier pi : priceListBatchOpreationsBean.getTempSelectedDataListCentralSupplier()) {
                        if (!priceListBatchOpreationsBean.getSelectedCentralSupplierList().contains(pi)) {
                            priceListBatchOpreationsBean.getSelectedCentralSupplierList().add(pi);
                        }

                    }
                    searchDataList = priceListBatchOpreationsBean.changeQuantityCentralSupplier(searchDataList);
                }            
                return searchDataList;
            }
        };
    }

    @Override
    public List<CentralSupplier> callDaoList(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int callDaoCount(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
