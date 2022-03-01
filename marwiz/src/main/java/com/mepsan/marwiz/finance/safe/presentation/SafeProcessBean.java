/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   12.01.2018 02:38:45
 */
package com.mepsan.marwiz.finance.safe.presentation;

import com.mepsan.marwiz.finance.safe.business.ISafeMovementService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
public class SafeProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{safeService}")
    private ISafeService safeService;

    @ManagedProperty(value = "#{safeMovementService}")
    private ISafeMovementService safeMovementService;

    private int processType;
    private Safe selectedObject;
    private int movesize;
    private int activeIndex;

    private List<Safe> listSafe;

    public List<Safe> getListSafe() {
        return listSafe;
    }

    public void setListSafe(List<Safe> listSafe) {
        this.listSafe = listSafe;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Safe getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Safe selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getMovesize() {
        return movesize;
    }

    public void setMovesize(int movesize) {
        this.movesize = movesize;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSafeService(ISafeService safeService) {
        this.safeService = safeService;
    }

    public void setSafeMovementService(ISafeMovementService safeMovementService) {
        this.safeMovementService = safeMovementService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @PostConstruct
    public void init() {
        System.out.println("--------SafeProcessBean------------------");
        listSafe = safeService.findAll();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Safe) {
                    processType = 2;
                    selectedObject = (Safe) ((ArrayList) sessionBean.parameter).get(i);
                    movesize = safeMovementService.count(" ", selectedObject, sessionBean.getUser().getLastBranch());
                    break;

                }
            }
        } else {
            selectedObject = new Safe();
            selectedObject.getCurrency().setId(sessionBean.getUser().getLastBranch().getCurrency().getId());
            processType = 1;
            movesize = 0;
        }
        List<Safe> tempList = new ArrayList<>();

        for (int i = 0; i < listSafe.size(); i++) {
            if (listSafe.get(i).getId() != selectedObject.getId() && listSafe.get(i).getCurrency().getId() == selectedObject.getCurrency().getId()
                      && selectedObject.getId() != listSafe.get(i).getShiftmovementsafe_id()) {
                tempList.add(listSafe.get(i));
            }
        }
        listSafe.clear();
        listSafe.addAll(tempList);
        setListBtn(sessionBean.checkAuthority(new int[]{117, 118}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{29}, 1));

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
        System.out.println("-sessionBean.getUser().getLastBranchSetting().isIsMinusMainSafe()---" + sessionBean.getUser().getLastBranchSetting().isIsMinusMainSafe());
        if (processType == 1) {
            if (!sessionBean.getUser().getLastBranchSetting().isIsMinusMainSafe() && selectedObject.getShiftmovementsafe_id() == 0 && selectedObject.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("mainsafecannotbereducedtonegativebalance")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                result = safeService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    List<Object> list = new ArrayList<>();
                    list.add(selectedObject);
                    marwiz.goToPage("/pages/finance/safe/safeprocess.xhtml", list, 1, 19);
                    sessionBean.createUpdateMessage(result);
                }
            }
        } else if (processType == 2) {

            result = safeService.update(selectedObject);
            if (result > 0) {
                marwiz.goToPage("/pages/finance/safe/safe.xhtml", null, 1, 6);
                sessionBean.createUpdateMessage(result);
            }
        }

    }

    public void goToBack() {
        marwiz.goToPage("/pages/finance/safe/safe.xhtml", null, 1, 6);
    }

    public void testBeforeDelete() {
        if (movesize > 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausesafehasmovement")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().update("frmSafeProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");

        }
    }

    public void delete() {
        int result = 0;
        result = safeService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/finance/safe/safe.xhtml", null, 1, 6);
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Kasa Tutanağı Oluşturmak İçin Kullanılır.
     */
    public void createSafeRecord() {
        selectedObject.setReportDate(new Date());

        RequestContext.getCurrentInstance().execute("PF('dlg_PrintSafeReport').show();");
        RequestContext.getCurrentInstance().update("dlgPrintSafeReport");

    }

    public void printSafeRecord() {

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getReportDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setReportDate(calendar.getTime());

        List<Safe> listReportSafe = safeService.findSafeBalanceForDate(selectedObject);
        if (listReportSafe.size() > 0) {
            selectedObject.setReportBalance(listReportSafe.get(0).getReportBalance());
        }
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(safeService.exportSafeReport(selectedObject)) + "');$(\"#printerPanel\").css('display','block');print_saferecord();$(\"#printerPanel\").css('display','none');");

    }

}
