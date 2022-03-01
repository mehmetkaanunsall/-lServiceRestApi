/**
 * This class ...
 *
 *
 * @author Emrullah YAKIŞAN
 *
 * @date   11.04.2019 09:15:18
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.finance.discount.business.IDiscountAccountTabService;
import com.mepsan.marwiz.general.common.CategoryBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountAccountConnection;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
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
public class DiscountAccountTabCategoryBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{discountAccountTabService}")
    private IDiscountAccountTabService discountAccountTabService;

    @ManagedProperty(value = "#{categoryBookFilterBean}")
    private CategoryBookFilterBean categoryBookFilterBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setDiscountAccountTabService(IDiscountAccountTabService discountAccountTabService) {
        this.discountAccountTabService = discountAccountTabService;
    }

    public void setCategoryBookFilterBean(CategoryBookFilterBean categoryBookFilterBean) {
        this.categoryBookFilterBean = categoryBookFilterBean;
    }

    private int processType;

    private Discount selectedDiscount;
    private DiscountAccountConnection selectedObject;
    private List<DiscountAccountConnection> listOfCategory;

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public Discount getSelectedDiscount() {
        return selectedDiscount;
    }

    public void setSelectedDiscount(Discount selectedDiscount) {
        this.selectedDiscount = selectedDiscount;
    }

    public DiscountAccountConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(DiscountAccountConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<DiscountAccountConnection> getListOfCategory() {
        return listOfCategory;
    }

    public void setListOfCategory(List<DiscountAccountConnection> listOfCategory) {
        this.listOfCategory = listOfCategory;
    }

    @PostConstruct
    public void init() {
        System.out.println("------DiscountAccountTabCategoryBean---");

        if (sessionBean.parameter instanceof Discount) {
            selectedDiscount = (Discount) sessionBean.parameter;
            selectedObject = new DiscountAccountConnection();
            listOfCategory = discountAccountTabService.listofDiscountAccount(selectedDiscount, " AND  dac.accountcategorization_id > 0 ");

        }
        
        setListBtn(sessionBean.checkAuthority(new int[]{102, 105, 106}, 0));

    }

    public void createDialog(int type) {

        processType = type;
        RequestContext context = RequestContext.getCurrentInstance();
        if (!selectedDiscount.isIsAllCustomer()) {

            if (processType == 1) { //ekle
                selectedObject = new DiscountAccountConnection();

            } else if (processType == 2) {

            }
            context.execute("PF('dlg_CategoryProcess').show()");

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("youdontselectcategorybecauseofallcustomerissigned")));
            RequestContext.getCurrentInstance().update("tbvDiscount:frmCustomerTab:grwDiscountCustomerTab");
        }
    }

    public void save() {
        int result = 0;
        selectedObject.setDiscount(selectedDiscount);
        if (processType == 1) {
            result = discountAccountTabService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfCategory.add(selectedObject);
            }
        } else {

            result = discountAccountTabService.update(selectedObject);
        }
        if (result > 0) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_CategoryProcess').hide();");
            context.update("tbvDiscount:frmCustomerTab:dtbCategory");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void updateAllInformation() {

        boolean isThere = false;
        if (categoryBookFilterBean.getSelectedNode() != null) {
            for (int i = 0; i < listOfCategory.size(); i++) {

                if (listOfCategory.get(i).getId() == ((Categorization) categoryBookFilterBean.getSelectedNode().getData()).getId()) {
                    isThere = true;
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thiscategoryisavailableinthisdiscount")));
                    RequestContext.getCurrentInstance().update("frmDiscountCategoryProcess:grwCategoryControl");
                }
            }

            if (!isThere) {

                selectedObject.setAccountCategorization((Categorization) categoryBookFilterBean.getSelectedNode().getData());

                RequestContext.getCurrentInstance().update("frmDiscountCategoryProcess:txtCategory");
            }
            categoryBookFilterBean.setSelectedNode(null);

        }

    }

    public void delete() {
        int result = 0;
        result = discountAccountTabService.delete(selectedObject);
        if (result > 0) {
            listOfCategory.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_CategoryProcess').hide();");
            context.update("tbvDiscount:frmCustomerTab:dtbCategory");
        }
        sessionBean.createUpdateMessage(result);
    }

}
