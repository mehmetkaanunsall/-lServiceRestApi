/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   28.08.2020 03:49:29
 */
package com.mepsan.marwiz.general.printer.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Printer;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.printer.business.IPrinterService;
import java.util.ArrayList;
import java.util.Arrays;
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
public class PrinterBean extends GeneralDefinitionBean<Printer> {

    @ManagedProperty(value = "#{printerService}")
    private IPrinterService printerService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private int processType;

    public void setPrinterService(IPrinterService printerService) {
        this.printerService = printerService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{321, 322, 323}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new Printer();

        RequestContext.getCurrentInstance().execute("PF('dlg_PrinterProcess').show();");
    }

    public void update() {
        processType = 2;
        RequestContext.getCurrentInstance().execute("PF('dlg_PrinterProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;
        boolean isThere = false;
        if (processType == 1) {
            if (selectedObject.isIsDefault()) {
                for (Printer p : listOfObjects) {
                    if (p.getType().getId() == selectedObject.getType().getId() && p.isIsDefault()) {
                        isThere = true;
                        break;
                    }
                }
            }
            if (isThere) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("defaultprinterisavailable"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            } else {
                result = printerService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                }
            }

        } else if (processType == 2) {
            if (selectedObject.isIsDefault()) {
                for (Printer p : listOfObjects) {
                    if (p.getType().getId() == selectedObject.getType().getId() && p.isIsDefault() && p.getId() != selectedObject.getId()) {
                        isThere = true;
                        break;
                    }
                }
            }
            if (isThere) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("defaultprinterisavailable"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            } else {
                result = printerService.update(selectedObject);
            }
        }
        if (result > 0) {
            bringType();
            RequestContext.getCurrentInstance().execute("PF('dlg_PrinterProcess').hide();");
            RequestContext.getCurrentInstance().update("frmPrinter:dtbPrinter");
            RequestContext.getCurrentInstance().execute("PF('printerPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<Printer> findall() {
        return printerService.listOfPrinter();
    }

    public void bringType() {
        for (Type t : sessionBean.getTypes(39)) {
            if (t.getId() == selectedObject.getType().getId()) {
                selectedObject.getType().setTag(t.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
    }

    public void delete() {
        int result = 0;
        result = printerService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_PrinterProcess').hide();");
            context.update("frmPrinter:dtbPrinter");
            context.execute("PF('printerPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void closeDialog() {
        listOfObjects = findall();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmPrinter:dtbPrinter");
        context.execute("PF('printerPF').filter();");

    }

}
