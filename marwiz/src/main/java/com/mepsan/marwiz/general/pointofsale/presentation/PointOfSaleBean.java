/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   23.01.2018 04:18:22
 */
package com.mepsan.marwiz.general.pointofsale.presentation;

import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.cashregister.business.ICashRegisterService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.CashRegister;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.pointofsale.business.IPointOfSaleSafeService;
import com.mepsan.marwiz.general.pointofsale.business.IPointOfSaleService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceService;
import com.mepsan.marwiz.inventory.warehouse.business.IWarehouseService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class PointOfSaleBean extends GeneralDefinitionBean<PointOfSale> {

    @ManagedProperty(value = "#{pointOfSaleService}")
    private IPointOfSaleService pointOfSaleService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{brandService}")
    public IBrandService brandService;

    @ManagedProperty(value = "#{warehouseService}")
    public IWarehouseService warehouseService;

    @ManagedProperty(value = "#{cashRegisterService}")
    public ICashRegisterService cashRegisterService;

    @ManagedProperty(value = "#{safeService}")
    public ISafeService safeService;

    @ManagedProperty(value = "#{pointOfSaleSafeService}")
    public IPointOfSaleSafeService pointOfSaleSafeService;

    @ManagedProperty(value = "#{pointOfSaleSafeTabBean}")
    public PointOfSaleSafeTabBean pointOfSaleSafeTabBean;

    @ManagedProperty(value = "#{automationDeviceService}")
    private IAutomationDeviceService automationDeviceService;

    private int processType, activeIndex;
    private List<Brand> brandList;
    private List<CashRegister> cashRegisterList;
    private List<Warehouse> warehouseList;
    private List<Safe> defaultSafeList;
    private int availableAutomatDevice;

    public void setPointOfSaleService(IPointOfSaleService pointOfSaleService) {
        this.pointOfSaleService = pointOfSaleService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public List<CashRegister> getCashRegisterList() {
        return cashRegisterList;
    }

    public void setCashRegisterList(List<CashRegister> cashRegisterList) {
        this.cashRegisterList = cashRegisterList;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public void setWarehouseService(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void setCashRegisterService(ICashRegisterService cashRegisterService) {
        this.cashRegisterService = cashRegisterService;
    }

    public List<Safe> getDefaultSafeList() {
        return defaultSafeList;
    }

    public void setDefaultSafeList(List<Safe> defaultSafeList) {
        this.defaultSafeList = defaultSafeList;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setPointOfSaleSafeService(IPointOfSaleSafeService pointOfSaleSafeService) {
        this.pointOfSaleSafeService = pointOfSaleSafeService;
    }

    public void setPointOfSaleSafeTabBean(PointOfSaleSafeTabBean pointOfSaleSafeTabBean) {
        this.pointOfSaleSafeTabBean = pointOfSaleSafeTabBean;
    }

    public void setAutomationDeviceService(IAutomationDeviceService automationDeviceService) {
        this.automationDeviceService = automationDeviceService;
    }

    public int getAvailableAutomatDevice() {
        return availableAutomatDevice;
    }

    public void setAvailableAutomatDevice(int availableAutomatDevice) {
        this.availableAutomatDevice = availableAutomatDevice;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------PointOfSaleBean--------");
        warehouseList = new ArrayList<>();
        cashRegisterList = new ArrayList<>();
        brandList = new ArrayList<>();
        defaultSafeList = new ArrayList<>();
        availableAutomatDevice = 0;

        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, false);
        //Paro İle Entegrason Var ise
        if (sessionBean.getLastBranchSetting().getParoUrl() != null) {
            if (!sessionBean.getLastBranchSetting().getParoUrl().isEmpty()) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);
            }
        }
        setListBtn(sessionBean.checkAuthority(new int[]{198, 199, 200}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{53}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new PointOfSale();
        brandList = brandService.findAll(new Item(5));
        warehouseList = warehouseService.selectListWarehouse(" AND iw.is_fuel=FALSE ");
        cashRegisterList = cashRegisterService.selectListCashRegister();
        String createWhere = safeService.createWhere(0, new ArrayList<>());
        defaultSafeList = safeService.findSafeByCurrency(createWhere);

        RequestContext.getCurrentInstance().execute("PF('dlg_PosProcess').show();");

    }

    public void update() {
        processType = 2;
        availableAutomatDevice = automationDeviceService.controlAutomationDevice();
        // activeIndex = 1;
        brandList = brandService.findAll(new Item(5));
        warehouseList = warehouseService.selectListWarehouse(" AND iw.is_fuel=FALSE ");
        cashRegisterList = cashRegisterService.selectListCashRegister();
        String createWhere = safeService.createWhere(0, new ArrayList<>());
        defaultSafeList = safeService.findSafeByCurrency(createWhere);

        pointOfSaleSafeTabBean.setSelectedPOS(selectedObject);
        pointOfSaleSafeTabBean.setListOfSafe(pointOfSaleSafeService.listofPOSSafe(selectedObject));

        RequestContext.getCurrentInstance().execute("PF('dlg_PosProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;
        selectedObject.setMacAddress(selectedObject.getMacAddress().toUpperCase());

        if (processType == 1) {
            result = pointOfSaleService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);

                pointOfSaleSafeTabBean.setSelectedPOS(selectedObject);
                pointOfSaleSafeTabBean.setListOfSafe(new ArrayList<>());
                processType = 2;
                //activeIndex = 1;
                RequestContext.getCurrentInstance().update("pngPOSTab");
                RequestContext.getCurrentInstance().update("tbvPOSProcess:frmDiscountTab");
            }

        } else if (processType == 2) {
            result = pointOfSaleService.update(selectedObject);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_PosProcess').hide();");
            }

        }

        if (result > 0) {
            bringAll();
            RequestContext.getCurrentInstance().update("frmPointOfSale:dtbPointOfSale");
            RequestContext.getCurrentInstance().execute("PF('pointOfSalePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<PointOfSale> findall() {
        return pointOfSaleService.listOfPointOfSale();
    }


    public void bringAll() {

        for (Status status : sessionBean.getStatus(5)) {
            if (status.getId() == selectedObject.getStatus().getId()) {
                selectedObject.getStatus().setTag(status.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }

    public void delete() {
        int result = 0;
        result = pointOfSaleService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_PosProcess').hide();");
            context.update("frmPointOfSale:dtbPointOfSale");
            context.execute("PF('pointOfSalePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringDefaultTime() {
        if (selectedObject.isIsOffline()) {
            selectedObject.setStockTime(BigDecimal.valueOf(1));
            selectedObject.setUnitTaxTime(BigDecimal.valueOf(24));
            selectedObject.setUserTime(BigDecimal.valueOf(24));
            selectedObject.setCategorizationTime(BigDecimal.valueOf(6));
            selectedObject.setBankAccountTime(BigDecimal.valueOf(24));
            selectedObject.setPointOfSaleTime(BigDecimal.valueOf(2));
            availableAutomatDevice = automationDeviceService.controlAutomationDevice();
            if (availableAutomatDevice == 1) {
                selectedObject.setVendingMachineTime(BigDecimal.valueOf(12));
            } else {
                selectedObject.setVendingMachineTime(null);
            }
        } else {
            selectedObject.setStockTime(null);
            selectedObject.setUnitTaxTime(null);
            selectedObject.setUserTime(null);
            selectedObject.setCategorizationTime(null);
            selectedObject.setBankAccountTime(null);
            selectedObject.setPointOfSaleTime(null);
            selectedObject.setVendingMachineTime(null);
        }
    }

}
