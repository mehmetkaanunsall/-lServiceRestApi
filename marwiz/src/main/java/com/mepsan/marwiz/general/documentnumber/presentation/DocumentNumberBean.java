/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   01.02.2018 11:42:04
 */
package com.mepsan.marwiz.general.documentnumber.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.documentnumber.business.IDocumentNumberService;
import com.mepsan.marwiz.general.model.general.DocumentNumber;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
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
public class DocumentNumberBean extends GeneralDefinitionBean<DocumentNumber> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{documentNumberService}")
    private IDocumentNumberService documentNumberService;

    private List<Item> listOfItem;
    private int processType;
    private DocumentNumber tempDocumentNumber;
    int result;

    public List<Item> getListOfItem() {
        return listOfItem;
    }

    public void setListOfItem(List<Item> listOfItem) {
        this.listOfItem = listOfItem;
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

    public void setDocumentNumberService(IDocumentNumberService documentNumberService) {
        this.documentNumberService = documentNumberService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------DocumentNumberBean--------");
        listOfItem = new ArrayList<>();
        listOfItem.add(new Item(17, sessionBean.getLoc().getString("invoice")));
        listOfItem.add(new Item(16, sessionBean.getLoc().getString("waybill")));
        listOfItem.add(new Item(18, sessionBean.getLoc().getString("cheque")));
        listOfItem.add(new Item(18, sessionBean.getLoc().getString("cheque")));
        listOfItem.add(new Item(40, sessionBean.getLoc().getString("order")));

        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{189, 190, 191}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new DocumentNumber();

        RequestContext.getCurrentInstance().execute("PF('dlg_DocumentNumberProcess').show();");
    }

    public void update() {
        processType = 2;
        result = 0;
        tempDocumentNumber = new DocumentNumber();
        tempDocumentNumber.setId(selectedObject.getId());
        tempDocumentNumber.setName(selectedObject.getName());
        tempDocumentNumber.getItem().setId(selectedObject.getItem().getId());
        tempDocumentNumber.setSerial(selectedObject.getSerial());
        tempDocumentNumber.setBeginNumber(selectedObject.getBeginNumber());
        tempDocumentNumber.setEndNumber(selectedObject.getEndNumber());
        tempDocumentNumber.setActualNumber(selectedObject.getActualNumber());

        RequestContext.getCurrentInstance().execute("PF('dlg_DocumentNumberProcess').show();");
    }

    @Override
    public void save() {
        result = 0;
        boolean isSave = true;

        List<DocumentNumber> tempList = new ArrayList<>();
        if (selectedObject.getBeginNumber() == 0 || selectedObject.getEndNumber() == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("beginandendnumbercannotbezero")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else if (selectedObject.getBeginNumber() >= selectedObject.getEndNumber()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("beginnumbercannotbebiggerthanendnumber")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            for (DocumentNumber d : listOfObjects) {
                tempList.add(d);
            }
            if (processType == 2) {
                tempList.remove(selectedObject);
            }
            for (DocumentNumber doc : tempList) {
                if (selectedObject.getItem().getId() == doc.getItem().getId() && selectedObject.getName().equals(doc.getName())) {
                    if (selectedObject.getBeginNumber() > doc.getBeginNumber() && selectedObject.getBeginNumber() < doc.getEndNumber()) {
                        isSave = false;
                        break;
                    } else if (selectedObject.getEndNumber() > doc.getBeginNumber() && selectedObject.getEndNumber() < doc.getEndNumber()) {
                        isSave = false;
                        break;
                    } else if (selectedObject.getEndNumber() == doc.getBeginNumber() || selectedObject.getEndNumber() == doc.getEndNumber()
                            || selectedObject.getBeginNumber() == doc.getBeginNumber() || selectedObject.getBeginNumber() == doc.getEndNumber()) {
                        isSave = false;
                        break;
                    } else if (selectedObject.getBeginNumber() < doc.getBeginNumber() && selectedObject.getEndNumber() > doc.getEndNumber()) {
                        isSave = false;
                        break;
                    }
                }
            }
            if (isSave) {
                for (DocumentNumber documentNumber : listOfObjects) {
                    if (documentNumber.getSerial().equalsIgnoreCase(selectedObject.getSerial()) && documentNumber.getItem().getId() == selectedObject.getItem().getId() && documentNumber.getId() != selectedObject.getId()) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("documentalreadyavailable")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        return;
                    }
                }
                if (processType == 1) {
                    result = documentNumberService.create(selectedObject);
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                } else if (processType == 2) {
                    result = documentNumberService.update(selectedObject);
                }
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_DocumentNumberProcess').hide();");
                    RequestContext.getCurrentInstance().update("frmDocumentNumber:dtbDocumentNumber");
                    RequestContext.getCurrentInstance().execute("PF('documentNumberPF').filter();");
                }
                sessionBean.createUpdateMessage(result);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("selectedrangeisalreadyavailableforthisnameandtype")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }
    }

    @Override
    public List<DocumentNumber> findall() {
        return documentNumberService.listOfDocumentNumber();
    }

    public void bringActualNumber() {
        selectedObject.setActualNumber(selectedObject.getBeginNumber());
    }

    /**
     * HAtalı set edip uyarı mesajı verdikten sonra listeye o sekilde eklediği
     * için doğru kayıtları bile kaydetmedi, o yüzden eski değerler tekrar set
     * edildi.
     *
     */
    public void hideDialog() {

        if (result <= 0 && processType == 2) {
            selectedObject.setId(tempDocumentNumber.getId());
            selectedObject.setName(tempDocumentNumber.getName());
            selectedObject.getItem().setId(tempDocumentNumber.getItem().getId());
            selectedObject.setSerial(tempDocumentNumber.getSerial());
            selectedObject.setBeginNumber(tempDocumentNumber.getBeginNumber());
            selectedObject.setEndNumber(tempDocumentNumber.getEndNumber());
            selectedObject.setActualNumber(tempDocumentNumber.getActualNumber());
            RequestContext.getCurrentInstance().update("frmDocumentNumber:dtbDocumentNumber");
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        result = documentNumberService.testBeforeDelete(selectedObject);
        if (result == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausethisdocumentseriesisused")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        }
    }

    public void delete() {
        int result = 0;
        result = documentNumberService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_DocumentNumberProcess').hide();");
            context.update("frmDocumentNumber:dtbDocumentNumber");
            context.execute("PF('documentNumberPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
