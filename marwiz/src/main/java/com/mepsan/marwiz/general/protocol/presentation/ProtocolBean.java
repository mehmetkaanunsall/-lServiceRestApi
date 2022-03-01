/**
 * Bu sınıf protokol tanımları sayfaları için kullanılan ortak bir bean dir.
 *
 *
 * @author Esra Çabuk
 *
 * @date   23.11.2016 05:13:11
 */
package com.mepsan.marwiz.general.protocol.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.general.Protocol;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.protocol.business.IProtocolService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class ProtocolBean extends GeneralDefinitionBean<Protocol> {

    @ManagedProperty(value = "#{protocolService}")
    private IProtocolService protocolService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int processType;
    private Item item;

    public void setProtocolService(IProtocolService protocolService) {
        this.protocolService = protocolService;
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
        System.out.println("----------ProtocolBean--------");
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        Long btnNew = (Long) request.getAttribute("btnNew");
        Long btnSave = (Long) request.getAttribute("btnSave");
        Long btnDelete = (Long) request.getAttribute("btnDelete");
        Long itemId = (Long) request.getAttribute("itemId");
        item = new Item();
        item.setId((int) (long) itemId);

        listOfObjects = protocolService.findAll(item);
        setListBtn(sessionBean.checkAuthority(new int[]{(int) (long) btnNew, (int) (long) btnSave, (int) (long) btnDelete}, 0));
    }

    /**
     * Bu metot ekleme işlemi için yeni dialog açar
     */
    @Override
    public void create() {
        processType = 1;
        selectedObject = new Protocol();
        selectedObject.setItem(item);
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_protocolproc').show();");
    }

    /**
     * Bu metot güncelleştirme işlemi için yeni dialog açar
     */
    public void update() {
        processType = 2;
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_protocolproc').show();");
    }

    /**
     * Bu metot protokol kaydetmeye veya güncelleştirmeye yarar.
     */
    @Override
    public void save() {
        boolean b = true;
        for (Protocol protocol : listOfObjects) {
            if (selectedObject.getProtocolNo() == protocol.getProtocolNo() && protocol.getId() != selectedObject.getId()) {
                b = false;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("thisnoisavailableinthesystem")));
                break;
            }
        }
        if (b) {
            RequestContext context = RequestContext.getCurrentInstance();
            int result = 0;
            if (processType == 1) {
                result = protocolService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                }
            } else if (processType == 2) {
                result = protocolService.update(selectedObject);

            }
            if (result > 0) {
                context.execute("PF('dlg_protocolproc').hide();");
                context.update("frmProtocol:dtbProtocol");
                context.execute("PF('protocolPF').filter();");
            }
            sessionBean.createUpdateMessage(result);
        }
    }

    @Override
    public List<Protocol> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void testBeforeDelete() {
        int result = 0;
        result = protocolService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmProtocolProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseprotocolisrelatedtoautomationdevice")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = protocolService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("frmProtocol:dtbProtocol");
            context.execute("PF('protocolPF').filter();");
            context.execute("PF('dlg_protocolproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
