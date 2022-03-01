/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.03.2018 10:52:58
 */
package com.mepsan.marwiz.inventory.automationdevice.presentation;

import com.google.gson.JsonObject;
import com.mepsan.marwiz.automat.washingmachicne.presentation.WashingMachicneNozzleTabBean;
import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.Protocol;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.protocol.business.IProtocolService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceCardService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceItemService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class AutomationDeviceBean extends GeneralDefinitionBean<AutomationDevice> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{automationDeviceTabBean}")
    public AutomationDeviceTabBean automationDeviceTabBean;

    @ManagedProperty(value = "#{automationDeviceCardTabBean}")
    public AutomationDeviceCardTabBean automationDeviceCardTabBean;

    @ManagedProperty(value = "#{automationDeviceService}")
    public IAutomationDeviceService automationDeviceService;

    @ManagedProperty(value = "#{automationDeviceCardService}")
    public IAutomationDeviceCardService automationDeviceCardService;

    @ManagedProperty(value = "#{automationDeviceItemService}")
    public IAutomationDeviceItemService automationDeviceItemService;

    @ManagedProperty(value = "#{protocolService}")
    public IProtocolService protocolService;

    @ManagedProperty(value = "#{brandService}")
    public IBrandService brandService;

    private int processType, activeIndex;
    private List<Protocol> listOfProtocol;
    private List<Brand> listOfBrand;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public void setAutomationDeviceService(IAutomationDeviceService automationDeviceService) {
        this.automationDeviceService = automationDeviceService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAutomationDeviceTabBean(AutomationDeviceTabBean automationDeviceTabBean) {
        this.automationDeviceTabBean = automationDeviceTabBean;
    }

    public void setAutomationDeviceItemService(IAutomationDeviceItemService automationDeviceItemService) {
        this.automationDeviceItemService = automationDeviceItemService;
    }

    public void setAutomationDeviceCardTabBean(AutomationDeviceCardTabBean automationDeviceCardTabBean) {
        this.automationDeviceCardTabBean = automationDeviceCardTabBean;
    }

    public void setAutomationDeviceCardService(IAutomationDeviceCardService automationDeviceCardService) {
        this.automationDeviceCardService = automationDeviceCardService;
    }

    public List<Protocol> getListOfProtocol() {
        return listOfProtocol;
    }

    public void setListOfProtocol(List<Protocol> listOfProtocol) {
        this.listOfProtocol = listOfProtocol;
    }

    public void setProtocolService(IProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    public List<Brand> getListOfBrand() {
        return listOfBrand;
    }

    public void setListOfBrand(List<Brand> listOfBrand) {
        this.listOfBrand = listOfBrand;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------AutomationDeviceBean----------------");
        listOfObjects = new ArrayList();
        listOfProtocol = new ArrayList<>();
        listOfBrand = new ArrayList<>();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{216, 217, 218, 219, 220, 221, 222}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{54, 55}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    @Override
    public void create() {
        processType = 1;

        listOfProtocol = protocolService.findAll(new Item(36));

        listOfBrand = brandService.findAll(new Item(36));

        selectedObject = new AutomationDevice();
        RequestContext.getCurrentInstance().execute("PF('dlg_AutomationDeviceProcess').show();");
    }

    public void update() {
        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
        processType = 2;

        listOfProtocol = protocolService.findAll(new Item(36));

        listOfBrand = brandService.findAll(new Item(36));

        automationDeviceCardTabBean.setAutomationDevice(selectedObject);
        automationDeviceCardTabBean.setListOfObject(automationDeviceCardService.listOfCard(selectedObject));
        automationDeviceTabBean.setAutomationDevice(selectedObject);
        automationDeviceTabBean.setListOfShelf(automationDeviceItemService.listOfShelf(selectedObject));

        RequestContext.getCurrentInstance().update("dlgAutomationDeviceProcess");

        RequestContext.getCurrentInstance().execute("PF('dlg_AutomationDeviceProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = automationDeviceService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
              //  listOfObjects.add(selectedObject);
                listOfObjects =findall();
                for (AutomationDevice a : listOfObjects) {
                    if(a.getId() == selectedObject.getId()){
                        selectedObject.getWarehouse().setId(a.getWarehouse().getId());
                        break;
                    }
                }

                automationDeviceCardTabBean.setAutomationDevice(selectedObject);
                automationDeviceCardTabBean.setListOfObject(new ArrayList<>());
                automationDeviceTabBean.setAutomationDevice(selectedObject);
                automationDeviceTabBean.setListOfShelf(new ArrayList<>());
                processType = 2;
                // activeIndex = 1;

               // automationDeviceTabBean.setAutomationDevice(selectedObject);
                //automationDeviceTabBean.setListOfShelf(automationDeviceItemService.listOfShelf(selectedObject));

                RequestContext.getCurrentInstance().execute("PF('dlg_AutomationDeviceProcess').hide();");
                RequestContext.getCurrentInstance().execute("reOpenDialog();");
                // RequestContext.getCurrentInstance().update("pngAutomationDeviceTab");
                //  RequestContext.getCurrentInstance().update("tbvAutomationDeviceProcess:frmShelfTab");

            }

        } else if (processType == 2) {
            result = automationDeviceService.update(selectedObject);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_AutomationDeviceProcess').hide();");
            }

        }
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('automationDevicePF').filter();");
            RequestContext.getCurrentInstance().update("frmAutomationDevice:dtbAutomationDevice");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<AutomationDevice> findall() {
        return automationDeviceService.findAll(" ");
    }

    public void testBeforeDelete() {
        if (automationDeviceTabBean.getListOfShelf().isEmpty()) {//Sil
            RequestContext.getCurrentInstance().update("frmAutomationDeviceProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            RequestContext.getCurrentInstance().update("dlg_ConfirmDeleteDevice");
            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmDeleteDevice').show();");
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = automationDeviceService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("frmAutomationDevice:dtbAutomationDevice");
            context.execute("PF('automationDevicePF').filter();");
            context.execute("PF('dlg_AutomationDeviceProcess').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void sendConfiguration() {
        boolean result = automationDeviceService.configureDetail(selectedObject);
        if (result) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("warning"), sessionBean.loc.getString("succesfuloperation")));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("warning"), sessionBean.loc.getString("unsuccesfuloperation")));
        }
        RequestContext.getCurrentInstance().update("grwProcessMessage");
    }

    /**
     * Bu metot komutlar yardımı ile cihazda kapı açıp kapatma için kullanılır.
     *
     * @param type 1 : açma , 2 kapatma
     */
    public void processDoor(int type) {
        System.out.println("openCloseDoor");
        JsonObject object = new JsonObject();
        if (type == 1) { // kapı açma 
            object.addProperty("Command", "OpenDoor");
            boolean result = automationDeviceService.sendCommand(object.toString(), selectedObject);
            sessionBean.createUpdateMessage(result == Boolean.TRUE ? 1 : 0);
        } else if (type == 2) { // kapı kapatma
            object.addProperty("Command", "CloseDoor");
            boolean result = automationDeviceService.sendCommand(object.toString(), selectedObject);
            sessionBean.createUpdateMessage(result == Boolean.TRUE ? 1 : 0);
        }
    }

    /**
     * Bu metot komutlar yardımı ile cihazda smartglass açıp kapatma için
     * kullanılır.
     *
     * @param type 1 :açma , 2 kapatma
     */
    public void processSmartGlass(int type) {
        System.out.println("****openCloseSmartGlass******");
        JsonObject object = new JsonObject();
        if (type == 1) { // smartglass açma 
            object.addProperty("Command", "OpenSmartGlass");
            boolean result = automationDeviceService.sendCommand(object.toString(), selectedObject);
            sessionBean.createUpdateMessage(result == Boolean.TRUE ? 1 : 0);
        } else if (type == 2) { //smartglass kapatma
            object.addProperty("Command", "CloseSmartGlass");
            boolean result = automationDeviceService.sendCommand(object.toString(), selectedObject);
            sessionBean.createUpdateMessage(result == Boolean.TRUE ? 1 : 0);
        }

    }

    public void reOpenDialog() {
        update();
    }
}
