/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 17:31:03
 */
package com.mepsan.marwiz.automation.nozzle.presentation;

import com.mepsan.marwiz.automation.nozzle.business.INozzleService;
import com.mepsan.marwiz.automation.tank.business.ITankService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
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
public class NozzleProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{nozzleService}")
    public INozzleService nozzleService;

    @ManagedProperty(value = "#{tankService}")
    private ITankService tankService;

    private Nozzle selectedObject;
    private int processType;

    private int activeIndex;
    private List<Warehouse> listOfWarehouse;
    private Warehouse warehouse;
    private boolean pageFromTank;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setNozzleService(INozzleService nozzleService) {
        this.nozzleService = nozzleService;
    }

    public void setTankService(ITankService tankService) {
        this.tankService = tankService;
    }

    public Nozzle getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Nozzle selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Warehouse> getListOfWarehouse() {
        return listOfWarehouse;
    }

    public void setListOfWarehouse(List<Warehouse> listOfWarehouse) {
        this.listOfWarehouse = listOfWarehouse;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public boolean isPageFromTank() {
        return pageFromTank;
    }

    public void setPageFromTank(boolean pageFromTank) {
        this.pageFromTank = pageFromTank;
    }

    @PostConstruct
    public void init() {
        listOfWarehouse = new ArrayList<>();
        selectedObject = new Nozzle();
        System.out.println("----------------------NozzleProcess Bean");
        processType = 1;
        pageFromTank = false;
        if (sessionBean.parameter instanceof ArrayList) {
            if (sessionBean.parameter != null) {
                for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                    if (((ArrayList) sessionBean.parameter).get(i) instanceof Nozzle) {//Tabanca ise
                        processType = 2;
                        //  activeIndex = 1;
                        selectedObject = (Nozzle) ((ArrayList) sessionBean.parameter).get(i);
                    } else if (((ArrayList) sessionBean.parameter).get(i) instanceof Warehouse) {//Depo Var İse 
                        warehouse = new Warehouse();
                        warehouse = (Warehouse) ((ArrayList) sessionBean.parameter).get(i);
                    }

                }
            }
        }
        if (warehouse != null) {//Tanklar Sayfasından Geliniyorsa 
            pageFromTank = true;
            selectedObject.setWarehouse(warehouse);
        }

        if (processType == 1) {//Ekleme İse Artan Seçildi
            selectedObject.setIsAscending(true);
        }

        listOfWarehouse = tankService.findAll();
        setListBtn(sessionBean.checkAuthority(new int[]{168, 169}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{48}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void save() {
        int result = 0;
        if (nozzleService.checkNozzle(selectedObject) > 0) {
            if (processType == 1) {
                result = nozzleService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                }
            } else if (processType == 2) {
                result = nozzleService.update(selectedObject);
            }
            sessionBean.createUpdateMessage(result);

            if (result > 0) {
                List<Object> list = new ArrayList<>();
                if (sessionBean.getParameter() != null) {
                    list.addAll((ArrayList) sessionBean.getParameter());
                }
                if (pageFromTank) {
                    marwiz.goToPage("/pages/automation/tank/tankprocess.xhtml", list, 1, 111);
                } else {
                    marwiz.goToPage("/pages/automation/nozzle/nozzle.xhtml", list, 1, 113);
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", sessionBean.getLoc().getString("nozzlenumberofthepumpconnectedtotheselectedtankisavailableinthesystem")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            //System.out.println("Aynı Tabanca Numarası Mevcut");
        }

    }

    public void testBeforeDelete() {
        int result = 0;
        result = nozzleService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmNozzleProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("nozzlecannotbedeletedbecausethereissalestowhichthenozzleisconnected")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = nozzleService.delete(selectedObject);
        if (result > 0) {
            List<Object> list = new ArrayList<>();
            if (sessionBean.getParameter() != null) {
                list.addAll((ArrayList) sessionBean.getParameter());
            }
            if (pageFromTank) {
                marwiz.goToPage("/pages/automation/tank/tankprocess.xhtml", list, 1, 111);
            } else {
                marwiz.goToPage("/pages/automation/nozzle/nozzle.xhtml", list, 1, 113);
            }
        }
        sessionBean.createUpdateMessage(result);
    }

}
