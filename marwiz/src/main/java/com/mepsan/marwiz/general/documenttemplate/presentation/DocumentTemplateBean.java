/**
 * This class ...
 *
 *
 * @author Cihat Küçükbğarıçaık
 *
 * @date   08.03.2018 16:13:00
 */
package com.mepsan.marwiz.general.documenttemplate.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documenttemplate.business.IDocumentTemplateService;
import com.mepsan.marwiz.general.model.general.DocumentTemplate;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
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
public class DocumentTemplateBean extends GeneralDefinitionBean<DocumentTemplate> {

    @ManagedProperty(value = "#{documentTemplateService}")
    private IDocumentTemplateService documentTemplateService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    public void setDocumentTemplateService(IDocumentTemplateService documentTemplateService) {
        this.documentTemplateService = documentTemplateService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------DocumentTemplateBean--------");
        selectedObject = new DocumentTemplate();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{260, 261, 262, 263, 264}, 0));
    }

    @Override
    public List<DocumentTemplate> findall() {
        return documentTemplateService.listOfDocumentTemplate();
    }

    @Override
    public void create() {
        selectedObject = new DocumentTemplate();
        RequestContext.getCurrentInstance().execute("PF('dlg_DocumentTemplateProcess').show();");
        selectedObject.setIsVertical(true);
        selectedObject.setPaperSize(1);
        selectedObject.setIsUseTemplate(true);
        changePaperSize(selectedObject.isIsVertical());
    }

    public void goToProcess() {
        System.out.println("Goto Process");
        marwiz.goToPage("/pages/general/documenttemplate/documenttemplateprocess.xhtml", selectedObject, 0, 96);

    }

    @Override
    public void save() {
        int result = 0;
        if (selectedObject.getType().getId() == 96) {
            selectedObject.setJson("{\"listOfObjects\":[{\"id\":1,\"fontSize\":11,\"fontStyle\":[],\"fontAlign\":\"left\",\"left\":47.0,\"top\":0.0,\"width\":26.0,\"height\":7.0,\"name\":\"Fatura Tarihi\",\"keyWord\":\"receiptdatepnl\",\"label\":false},{\"id\":2,\"fontSize\":9,\"fontStyle\":[],\"fontAlign\":\"left\",\"left\":0.0,\"top\":51.0,\"width\":80.25,\"height\":88.0,\"name\":\" Satış Satır Bilgileri\",\"keyWord\":\"receiptitempnl\",\"label\":false},{\"id\":3,\"fontSize\":11,\"fontStyle\":[\"bold\"],\"fontAlign\":\"left\",\"left\":0.0,\"top\":105.0,\"width\":80.0,\"height\":29.25,\"name\":\"Alt Satır Bilgileri\",\"keyWord\":\"receiptunderlineinfopnl\",\"label\":false},{\"id\":4,\"fontSize\":11,\"fontStyle\":[\"bold\"],\"fontAlign\":\"left\",\"left\":5.25,\"top\":137.5,\"width\":80.0,\"height\":29.25,\"name\":\"Yalnız\",\"keyWord\":\"receiptamountpnl\",\"label\":false}]}");
        }

        result = documentTemplateService.create(selectedObject);
        if (result > 0) {
            selectedObject.setId(result);
            listOfObjects.add(selectedObject);

            RequestContext.getCurrentInstance().execute("PF('dlg_DocumentTemplateProcess').hide();");

            marwiz.goToPage("/pages/general/documenttemplate/documenttemplateprocess.xhtml", selectedObject, 1, 96);
        }

        if (result == -1) {//başka default varsa
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("defaulttemplatealreadyexists")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            sessionBean.createUpdateMessage(result);
        }
    }

    public void changePaperSize(boolean isVertical) {
        selectedObject.setIsVertical(isVertical);

        if (selectedObject.getPaperSize() == 1) {
            if (isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(210));
                selectedObject.setHeight(BigDecimal.valueOf(297));
            } else if (!isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(297));
                selectedObject.setHeight(BigDecimal.valueOf(210));
            }
        } else if (selectedObject.getPaperSize() == 2) {
            if (isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(148));
                selectedObject.setHeight(BigDecimal.valueOf(210));
            } else if (!isVertical) {
                selectedObject.setWidth(BigDecimal.valueOf(210));
                selectedObject.setHeight(BigDecimal.valueOf(148));
            }
        }
    }

}
