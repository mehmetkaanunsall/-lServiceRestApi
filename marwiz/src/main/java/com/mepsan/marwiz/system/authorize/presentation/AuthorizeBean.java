/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   17.01.2018 02:31:19
 */

package com.mepsan.marwiz.system.authorize.presentation; 

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.authorize.business.IAuthorizeService;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.security.access.prepost.PostFilter;

@ManagedBean
@ViewScoped
public class AuthorizeBean extends GeneralDefinitionBean<Authorize> {

    int processType;
     
    @ManagedProperty(value="#{sessionBean}")
    private SessionBean sessionBean;
    
    @ManagedProperty(value="#{marwiz}")
    private Marwiz marwiz;

    @ManagedProperty(value="#{authorizeService}")
    private IAuthorizeService authorizeService;
    
    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAuthorizeService(IAuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }
    
    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------AuthorizeBean--------");
        listOfObjects=findall();
        toogleList = Arrays.asList(true);
        
        setListBtn(sessionBean.checkAuthority(new int[]{180, 181}, 0));
        
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new Authorize();
        RequestContext.getCurrentInstance().execute("PF('dlg_autherizationproc').show();");
    }

    @Override
    public void save() {
        int result=authorizeService.create(selectedObject);
        
        if(result>0){
            selectedObject.setId(result);
            listOfObjects.add(selectedObject);
            RequestContext.getCurrentInstance().execute("PF('dlg_autherizationproc').hide();");
            marwiz.goToPage("/pages/system/authorize/authorizeprocess.xhtml", selectedObject, 0, 16);

        }
        sessionBean.createUpdateMessage(result);
    }


    @Override
    public List<Authorize> findall() {
        return authorizeService.findAll();
    }

}
