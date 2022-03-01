/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.01.2018 05:00:03
 */
package com.mepsan.marwiz.general.cashregister.presentation;

import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.cashregister.business.ICashRegisterService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.CashRegister;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class CashRegisterBean extends GeneralDefinitionBean<CashRegister> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{brandService}")
    public IBrandService brandService;

    @ManagedProperty(value = "#{cashRegisterService}")
    private ICashRegisterService cashRegisterService;

    private int processType;
    private List<Brand> brandList;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public void setCashRegisterService(ICashRegisterService cashRegisterService) {
        this.cashRegisterService = cashRegisterService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------CashRegisterBean--------");

        brandList = new ArrayList<>();

        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{204, 205, 206}, 0));

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new CashRegister();
        brandList = brandService.findAll(new Item(24));

        RequestContext.getCurrentInstance().execute("PF('dlg_CashProcess').show();");

    }

    public void update() {
        processType = 2;
        brandList = brandService.findAll(new Item(24));

        RequestContext.getCurrentInstance().execute("PF('dlg_CashProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = cashRegisterService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }

        } else if (processType == 2) {
            result = cashRegisterService.update(selectedObject);

        }

        if (result > 0) {
            bringAll();
            RequestContext.getCurrentInstance().execute("PF('dlg_CashProcess').hide();");
            RequestContext.getCurrentInstance().update("frmCashRegister:dtbCashRegister");
            RequestContext.getCurrentInstance().execute("PF('cashRegisterPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<CashRegister> findall() {
        return cashRegisterService.listOfCashRegister();
    }

    public void bringAll() {

        for (Brand brand : brandList) {
            if (brand.getId() == selectedObject.getBrand().getId()) {
                selectedObject.getBrand().setName(brand.getName());
            }
        }
    }

    public void delete() {
        int result = 0;
        result = cashRegisterService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext.getCurrentInstance().execute("PF('dlg_CashProcess').hide();");
            RequestContext.getCurrentInstance().update("frmCashRegister:dtbCashRegister");
            RequestContext.getCurrentInstance().execute("PF('cashRegisterPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
