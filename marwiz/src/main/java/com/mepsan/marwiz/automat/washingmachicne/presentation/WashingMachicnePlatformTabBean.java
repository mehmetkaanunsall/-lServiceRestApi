/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 2:01:39 PM
 */
package com.mepsan.marwiz.automat.washingmachicne.presentation;

import com.mepsan.marwiz.automat.washingmachicne.business.IWashingMachicnePlatformService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.WashingMachicne;
import com.mepsan.marwiz.general.model.automat.WashingPlatform;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class WashingMachicnePlatformTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{washingMachicnePlatformService}")
    public IWashingMachicnePlatformService washingMachicnePlatformService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setWashingMachicnePlatformService(IWashingMachicnePlatformService washingMachicnePlatformService) {
        this.washingMachicnePlatformService = washingMachicnePlatformService;
    }

    private WashingPlatform selectedObject;
    private List<WashingPlatform> listOfObject;
    private int processType;
    private int activeIndex;
    private WashingMachicne selectedWashingMachicne;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public WashingPlatform getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(WashingPlatform selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<WashingPlatform> getListOfObject() {
        return listOfObject;
    }

    public void setListOfObject(List<WashingPlatform> listOfObject) {
        this.listOfObject = listOfObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public WashingMachicne getSelectedWashingMachicne() {
        return selectedWashingMachicne;
    }

    public void setSelectedWashingMachicne(WashingMachicne selectedWashingMachicne) {
        this.selectedWashingMachicne = selectedWashingMachicne;
    }

    @PostConstruct
    public void init() {
        System.out.println("======WashingMachicnePlatformTabBean========");
        selectedObject = new WashingPlatform();
        selectedWashingMachicne = new WashingMachicne();
        if (sessionBean.parameter instanceof WashingMachicne) {
            selectedWashingMachicne = (WashingMachicne) sessionBean.parameter;
        }
        findAll();

        setListBtn(sessionBean.checkAuthority(new int[]{239, 240, 241}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{59}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void createDialog(int type) {
        processType = type;
        if (processType == 1) {
            selectedObject = new WashingPlatform();
            selectedObject.setIsActive(true);
            selectedObject.setIsActiveBarcode(true);
        } else {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            WashingMachicneNozzleTabBean washingMachicneNozzleTabBean = (WashingMachicneNozzleTabBean) viewMap.get("washingMachicneNozzleTabBean");
            washingMachicneNozzleTabBean.setSelectedPlatform(selectedObject);
            washingMachicneNozzleTabBean.findAll();

            RequestContext.getCurrentInstance().update("pngNozzleTab");

        }
        selectedObject.setWashingMachicne(selectedWashingMachicne);
        RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicnePlatformDlg').show()");
    }

    public void findAll() {
        listOfObject = new ArrayList<>();
        listOfObject = washingMachicnePlatformService.findAll(selectedWashingMachicne);
    }

    /**
     * Eğer peronun statüsü aktife çekilirse kullanıcıyı bilgilendirmek amaçlı
     * konfigurasyon gönderin mesajı çıkartmak için kullanılır.
     */
    public void changeStatus() {
        if (selectedObject.isIsActive()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("dontforgettosendtheconfigurationforthechangingstatusinformationoftherelevantplatform")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void save() {
        int result = 0;
        boolean isThere = false;
        if (processType == 1) {
            for (WashingPlatform washingPlatform : listOfObject) { // listeyi kontrol eder aynı peron numarası var mı diye 
                if (washingPlatform.getPlatformNo().equals(selectedObject.getPlatformNo())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisplatformnumberisavailableinthesystem")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                if (!selectedObject.getBarcodePortNo().equals("0")) {
                    if (selectedObject.getBarcodePortNo().equals(selectedObject.getPort())) { // barkod port no ile port no her zaman aynı olmalıdır.
                        result = washingMachicnePlatformService.create(selectedObject);

                        if (result > 0) {
                            if (processType == 1) {
                                selectedObject.setId(result);
                                listOfObject.add(selectedObject);

                            }
                        }
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thebarcodeportnumberinformationandplatformportinformationmustbethesame")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {
                    result = washingMachicnePlatformService.create(selectedObject);

                    if (result > 0) {
                        if (processType == 1) {
                            selectedObject.setId(result);
                            listOfObject.add(selectedObject);
                        }
                    }
                }

            }
        } else {
            for (WashingPlatform washingPlatform : listOfObject) { // listeyi kontrol eder aynı peron numarası var mı diye 
                if (washingPlatform.getId() != selectedObject.getId() && washingPlatform.getPlatformNo().equals(selectedObject.getPlatformNo())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisplatformnumberisavailableinthesystem")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    isThere = true;
                    break;
                }
            }
            if (!isThere) {
                if (!selectedObject.getBarcodePortNo().equals("0")) {
                    if (selectedObject.getBarcodePortNo().equals(selectedObject.getPort())) { // barkod port no ile port no her zaman aynı olmalıdır.
                        result = washingMachicnePlatformService.update(selectedObject);
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thebarcodeportnumberinformationandplatformportinformationmustbethesame")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {
                    result = washingMachicnePlatformService.update(selectedObject);
                }
            }
        }
        if (!isThere) {
            if (result > 0) {
                RequestContext.getCurrentInstance().update("tbvWashingMachicneProc:frmWashingMachicnePlatformTab");
                RequestContext.getCurrentInstance().execute("PF('dlg_washingMachicnePlatformDlg').hide()");
                sessionBean.createUpdateMessage(result);

            } else {
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    public void testBeforeDelete() {
        int result = 0;
        result = washingMachicnePlatformService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmWashingMachicnePlatformProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausethereisanozzleattachedtothisplatform")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void delete() {
        int result = 0;
        result = washingMachicnePlatformService.delete(selectedObject);
        if (result > 0) {
            listOfObject.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_washingMachicnePlatformDlg').hide();");
            context.update("tbvWashingMachicneProc:frmWashingMachicnePlatformTab:dtbWashingMachicnePlatform");
        }
        sessionBean.createUpdateMessage(result);
    }

}
