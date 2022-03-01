/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   19.01.2018 03:21:39
 */
package com.mepsan.marwiz.inventory.warehouse.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped

public class WarehouseBean extends GeneralDefinitionBean<Warehouse> {

    private Object object;

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{warehouseService}")
    private IWarehouseService warehouseService;

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("-----WarehouseBean----------");
        object = new Object();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{145}, 0));
    }

    @Override
    public void create() {
        marwiz.goToPage("/pages/inventory/warehouse/warehouseprocess.xhtml", object, 0, 28);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Warehouse> findall() {
        return warehouseService.findAll();
    }

}
