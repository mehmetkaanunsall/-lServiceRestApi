/**
 *Bu sınıf otomasyon cihazları için kart tanımlar , kart ekleme ve silme işlemlerini yapar.
 *
 *
 * @author Gozde Gursel
 *
 * Created on 1:52:42 PM
 */
package com.mepsan.marwiz.inventory.automationdevice.presentation;

import com.google.gson.JsonObject;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceCard;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceCardService;
import com.mepsan.marwiz.inventory.automationdevice.business.IAutomationDeviceService;
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
public class AutomationDeviceCardTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{automationDeviceCardService}")
    public IAutomationDeviceCardService automationDeviceCardService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{automationDeviceService}")
    public IAutomationDeviceService automationDeviceService;

    private AutomationDeviceCard selectedObject;
    private List<AutomationDeviceCard> listOfObject;
    private AutomationDevice automationDevice;
    private int processType;
    private int commandType;
    private boolean isRead;

    public void setAutomationDeviceCardService(IAutomationDeviceCardService automationDeviceCardService) {
        this.automationDeviceCardService = automationDeviceCardService;
    }

    public AutomationDeviceCard getSelectedObject() {
        return selectedObject;
    }

    public void setAutomationDeviceService(IAutomationDeviceService automationDeviceService) {
        this.automationDeviceService = automationDeviceService;
    }

    public void setSelectedObject(AutomationDeviceCard selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<AutomationDeviceCard> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<AutomationDeviceCard> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
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

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------AutomationDeviceCardTabBean---");
        automationDevice = new AutomationDevice();
        isRead = false;
        listOfObject = automationDeviceCardService.listOfCard(automationDevice);

        setListBtn(sessionBean.checkAuthority(new int[]{226, 227, 228}, 0));
    }

    public void createDialog(int type) {
        System.out.println("************CreateDialog");
     
        commandType = 1; // yani normal commandsız ekleme.
        processType = type;
        if (processType == 1) {
            selectedObject = new AutomationDeviceCard();
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_CardProcess').show()");
    }

    public void save() {
        int result = 0;
        selectedObject.setAutomationDevice(automationDevice);
        if (processType == 1) {
            for (AutomationDeviceCard card : listOfObject) { // card bilgisi listede varsa aynı kart eklenemez uyarısı verilid
                if (card.getRfNo().equals(selectedObject.getRfNo())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cardpreviouslyadded")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            result = automationDeviceCardService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObject.add(selectedObject);
            }
        } else {

            for (AutomationDeviceCard card : listOfObject) {// listede başka aynı rf no yoksa ekle
                if (card.getRfNo().equals(selectedObject.getRfNo()) && card.getId() != selectedObject.getId()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("cardpreviouslyadded")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            result = automationDeviceCardService.update(selectedObject);
        }

        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_CardProcess').hide()");
            RequestContext.getCurrentInstance().update("tbvAutomationDeviceProcess:frmCardTab:dtbCard");
        }
        sessionBean.createUpdateMessage(result);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("dontforgettosendtheconfigurationforthechangedoraddedprocessinformationofthevendingmachine")));
        RequestContext.getCurrentInstance().update("grwProcessMessage");
    }

    public List<AutomationDeviceCard> findAll() {
        return automationDeviceCardService.listOfCard(automationDevice);
    }

    public void readCard() {
        if (selectedObject.getType().getId() != 0) {
            isRead = true;
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("Command", "ReadCard");
            jsonObject.addProperty("CardType", selectedObject.getType().getId() == 78 ? "Master" : "Custom");

            AutomationDeviceCard result = automationDeviceCardService.sendCommand(jsonObject.toString(), automationDevice);

            if (result.getResultId() == 0) { // kart okutma başarısız
                sessionBean.createUpdateMessage(-1);
                isRead = false;
            } else { // başarılı
                isRead = false;
                selectedObject.setRfNo(result.getRfNo());
                selectedObject.getStatus().setId(57);
            }

        } else { // kart tipi seçili değilse 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectcardtype")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void bringStatus() {
        for (Status statu : sessionBean.getStatus(31)) {
            if (selectedObject.getStatus().getId() == statu.getId()) {
                selectedObject.getStatus().setTag(statu.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void bringType() {
        for (Type type : sessionBean.getTypes(31)) {
            if (selectedObject.getType().getId() == type.getId()) {
                selectedObject.getType().setTag(type.getNameMap().get(sessionBean.getLangId()).getName());

                break;
            }
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
//
//        JsonObject object = new JsonObject();
//        object.addProperty("Command", "DeleteCard");
//        object.addProperty("CardNo", selectedObject.getRfNo());
//
//        AutomationDeviceCard card = automationDeviceCardService.sendCommand(object.toString(), automationDevice);
//
//        if (card.getResultId() == 0) { // silme başarısız
//            sessionBean.createUpdateMessage(-1);
//        } else if (card.getResultId() == 1) { // kart silme başarılı
        result = automationDeviceCardService.delete(selectedObject);
        if (result > 0) {
            listOfObject.remove(selectedObject);
            RequestContext.getCurrentInstance().update("tbvAutomationDeviceProcess:frmCardTab:dtbCard");
            context.execute("PF('automationCardPF').filter();");
            context.execute("PF('dlg_CardProcess').hide()");
        }
        sessionBean.createUpdateMessage(result);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("dontforgettosendtheconfigurationforthechangedoraddedprocessinformationofthevendingmachine")));
        RequestContext.getCurrentInstance().update("grwProcessMessage");
    }

}
