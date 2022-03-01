/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 01:38:44
 */
package com.mepsan.marwiz.inventory.automationdevice.presentation;

import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItem;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceItemService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AutomationDeviceTabBean extends AuthenticationLists {

    private List<AutomationDeviceItem> listOfShelf;
    private AutomationDeviceItem selectedObject;
    private AutomationDevice automationDevice;
    private int processType;
    private List<Type> listOfType, listOfTempType;
    private boolean isChangeType;
    private boolean isStockChange;
    private int oldStockId;

    @ManagedProperty(value = "#{automationDeviceItemService}")
    public IAutomationDeviceItemService automationDeviceItemService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;

    public List<AutomationDeviceItem> getListOfShelf() {
        return listOfShelf;
    }

    public void setListOfShelf(List<AutomationDeviceItem> listOfShelf) {
        this.listOfShelf = listOfShelf;
    }

    public AutomationDeviceItem getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AutomationDeviceItem selectedObject) {
        this.selectedObject = selectedObject;
    }

    public AutomationDevice getAutomationDevice() {
        return automationDevice;
    }

    public void setAutomationDevice(AutomationDevice automationDevice) {
        this.automationDevice = automationDevice;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setAutomationDeviceItemService(IAutomationDeviceItemService automationDeviceItemService) {
        this.automationDeviceItemService = automationDeviceItemService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public List<Type> getListOfType() {
        return listOfType;
    }

    public void setListOfType(List<Type> listOfType) {
        this.listOfType = listOfType;
    }

    public boolean isIsChangeType() {
        return isChangeType;
    }

    public void setIsChangeType(boolean isChangeType) {
        this.isChangeType = isChangeType;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("----AutomationDeviceTabBean---");
        selectedObject = new AutomationDeviceItem();
        automationDevice = new AutomationDevice();
        listOfShelf = new ArrayList<>();
        listOfType = new ArrayList<>();
        listOfTempType = new ArrayList<>();

        setListBtn(sessionBean.checkAuthority(new int[]{223, 224, 225,338}, 0));
    }

    public void createDialog(int type) {

        processType = type;
        if (automationDevice.getDeviceType().getId() == 91) {// Sigara Otomatı
            listOfTempType = sessionBean.getTypes(37);
        } else if (automationDevice.getDeviceType().getId() == 92) {// Ürün Otomatı
            listOfTempType = sessionBean.getTypes(38);
        }

        listOfType.clear();

        if (processType == 1) { //ekle
            selectedObject = new AutomationDeviceItem();
        } else if (processType == 2) {
            oldStockId = selectedObject.getStock().getId();

            if (automationDevice.getDeviceType().getId() == 91 && automationDevice.getProtocol().getProtocolNo() == 1) {
                if ((selectedObject.getShelfNo() >= 9 && selectedObject.getShelfNo() <= 23)
                          || (selectedObject.getShelfNo() >= 43 && selectedObject.getShelfNo() <= 57)
                          || (selectedObject.getShelfNo() >= 69 && selectedObject.getShelfNo() <= 83)) {
                    isChangeType = true;
                    for (Type t : listOfTempType) {
                        if (t.getId() == 93 || t.getId() == 94) {
                            listOfType.add(t);
                        }
                    }

                } else {
                    isChangeType = false;
                    for (Type t : listOfTempType) {
                        listOfType.add(t);
                    }
                }
            } else if (automationDevice.getDeviceType().getId() == 91 && automationDevice.getProtocol().getProtocolNo() == 2) {
                if ((selectedObject.getShelfNo() >= 9 && selectedObject.getShelfNo() <= 26)
                          || (selectedObject.getShelfNo() >= 43 && selectedObject.getShelfNo() <= 60)
                          || (selectedObject.getShelfNo() >= 69 && selectedObject.getShelfNo() <= 86)) {
                    isChangeType = true;
                    for (Type t : listOfTempType) {
                        if (t.getId() == 93 || t.getId() == 94 || t.getId() == 95) {
                            listOfType.add(t);
                        }
                    }

                } else {
                    isChangeType = false;
                    for (Type t : listOfTempType) {
                        listOfType.add(t);
                    }
                }
            } else {
                for (Type t : listOfTempType) {
                    listOfType.add(t);
                }
            }

        }
        RequestContext.getCurrentInstance().execute("PF('dlg_ShelfProcess').show()");
    }

    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null || stockBookFilterBean.isAll) {

            if (stockBookFilterBean.getSelectedData() != null) {
                selectedObject.setStock(stockBookFilterBean.getSelectedData());
            } else if (stockBookFilterBean.isAll) {
                selectedObject.setStock(new Stock(0, ""));
            }
            RequestContext.getCurrentInstance().update("frmShelfProcess:txtStock");
            stockBookFilterBean.setSelectedData(null);
            stockBookFilterBean.isAll = false;
        }

    }

    public void save() {
        int result = 0;
        boolean isThere = false;
        selectedObject.setAutomationDevice(automationDevice);
        for (AutomationDeviceItem automationDeviceItem : listOfShelf) {
            if (automationDeviceItem.getShelfNo() == selectedObject.getShelfNo() && automationDeviceItem.getId() != selectedObject.getId()) {
                isThere = true;
                break;
            }
        }
        if (isThere) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.getLoc().getString("warning"), sessionBean.getLoc().getString("thisshelfnoisavailable")));
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("grwProcessMessage");

        } else {
            if (processType == 1) {
                result = automationDeviceItemService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfShelf.add(selectedObject);
                }

            } else {
                if (oldStockId != selectedObject.getStock().getId()) {
                    isStockChange = true;
                } else {
                    isChangeType = false;
                }

                result = automationDeviceItemService.update(selectedObject, isStockChange);
            }
            if (result > 0) {

                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_ShelfProcess').hide();");
                context.update("tbvAutomationDeviceProcess:frmShelfTab:dtbShelf");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void delete() {
        int result = 0;
        result = automationDeviceItemService.delete(selectedObject);
        if (result > 0) {
            listOfShelf.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_ShelfProcess').hide();");
            context.update("tbvAutomationDeviceProcess:frmShelfTab:dtbShelf");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void changeItemType() {
        if (automationDevice.getDeviceType().getId() == 91 && automationDevice.getProtocol().getProtocolNo() == 1) {
            if ((selectedObject.getShelfNo() >= 9 && selectedObject.getShelfNo() <= 23)
                      || (selectedObject.getShelfNo() >= 43 && selectedObject.getShelfNo() <= 57)
                      || (selectedObject.getShelfNo() >= 69 && selectedObject.getShelfNo() <= 83)) {
                if (selectedObject.getType().getId() == 93) {
                    selectedObject.setMaxStockLevel(BigDecimal.valueOf(16));
                } else if (selectedObject.getType().getId() == 94) {
                    selectedObject.setMaxStockLevel(BigDecimal.valueOf(18));
                }
            }
        } else if (automationDevice.getDeviceType().getId() == 91 && automationDevice.getProtocol().getProtocolNo() == 2) {
            if ((selectedObject.getShelfNo() >= 9 && selectedObject.getShelfNo() <= 26)
                      || (selectedObject.getShelfNo() >= 43 && selectedObject.getShelfNo() <= 60)
                      || (selectedObject.getShelfNo() >= 69 && selectedObject.getShelfNo() <= 86)) {
                if (selectedObject.getType().getId() == 93) {
                    selectedObject.setMaxStockLevel(BigDecimal.valueOf(16));
                } else if (selectedObject.getType().getId() == 94) {
                    selectedObject.setMaxStockLevel(BigDecimal.valueOf(18));
                } else if (selectedObject.getType().getId() == 95) {
                    selectedObject.setMaxStockLevel(BigDecimal.valueOf(28));
                }
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmShelfProcess:txtMaxCapacity");
    }

}
