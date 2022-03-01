/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 03:39:45
 */
package com.mepsan.marwiz.system.authorize.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.authorize.business.IAuthorizeService;
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
public class AuthorizeProcessBean extends AuthenticationLists {

    private Authorize selectedObject;
    private int processType;
    private int activeIndex;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value = "#{authorizeService}")
    private IAuthorizeService authorizeService;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setAuthorizeService(IAuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
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

    public Authorize getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Authorize selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @PostConstruct
    public void init() {

        System.out.println("--------AuthorizeProcessBean----------");
        if (sessionBean.parameter instanceof Authorize) {
            selectedObject = (Authorize) sessionBean.parameter;
            processType = 2;
            // activeIndex=1;
        }

        setListBtn(sessionBean.checkAuthority(new int[]{181, 182}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{51, 52, 79}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void save() {
        int result = 0;
        result = authorizeService.update(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/system/authorize/authorize.xhtml", null, 1, 15);
        }

        sessionBean.createUpdateMessage(result);

    }

    public void goToBack() {
        marwiz.goToPage("/pages/system/authorize/authorize.xhtml", null, 1, 15);
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void testBeforeDelete() {

        int result = 0;
        result = authorizeService.testBeforeDelete(selectedObject);
        if (result == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausetheauthorizationisrelatedtotheuser")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().update("frmNewAuthorization:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }

    public void delete() {

        int result = 0;
        result = authorizeService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/system/authorize/authorize.xhtml", null, 1, 15);
        }
        sessionBean.createUpdateMessage(result);

    }
}
