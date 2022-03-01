/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.05.2020 09:26:21
 */
package com.mepsan.marwiz.inventory.transferbetweenwarehouses.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.WarehouseTransfer;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.GFWarehouseTransferService;
import com.mepsan.marwiz.inventory.transferbetweenwarehouses.business.ITransferBetweenWarehouseService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class TransferBetweenWarehouseBean extends GeneralBean<WarehouseTransfer> {

    @ManagedProperty(value = "#{transferBetweenWarehouseService}")
    private ITransferBetweenWarehouseService transferBetweenWarehouseService;

    @ManagedProperty(value = "#{gfWarehouseTransferService}")
    private GFWarehouseTransferService gfWarehouseTransferService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    private Object object;

    public void setTransferBetweenWarehouseService(ITransferBetweenWarehouseService transferBetweenWarehouseService) {
        this.transferBetweenWarehouseService = transferBetweenWarehouseService;
    }

    public void setGfWarehouseTransferService(GFWarehouseTransferService gfWarehouseTransferService) {
        this.gfWarehouseTransferService = gfWarehouseTransferService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("----TransferBetweenWarehouseBean-----------");
        object = new Object();
        listOfObjects = findall(" ");
        toogleList = Arrays.asList(true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{317}, 0));
    }

    @Override
    public void create() {
        marwiz.goToPage("/pages/inventory/transferbetweenwarehouses/transferbetweenwarehousesprocess.xhtml", object, 0, 215);
    }

    public void update() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/inventory/transferbetweenwarehouses/transferbetweenwarehousesprocess.xhtml", list, 0, 215);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<WarehouseTransfer> findall(String where) {
        return new CentrowizLazyDataModel<WarehouseTransfer>() {
            @Override
            public List<WarehouseTransfer> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<WarehouseTransfer> result = transferBetweenWarehouseService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = transferBetweenWarehouseService.count(where);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {

            listOfObjects = findall("");
        } else {
            gfWarehouseTransferService.makeSearch(autoCompleteValue, "");
            listOfObjects = gfWarehouseTransferService.searchResult;
        }
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
