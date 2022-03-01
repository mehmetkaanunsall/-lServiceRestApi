/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.presentation;

import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseMovementService;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
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
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class IncomeExpenseProcessBean extends AuthenticationLists {

    private int processType;
    private IncomeExpense selectedObject;
    private TreeNode findTreeNode;
    private IncomeExpenseMovement incomeExpenseMovement;
    private int activeIndex;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{incomeExpenseService}")
    private IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{incomeExpenseMovementService}")
    private IIncomeExpenseMovementService incomeExpenseMovementService;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public IncomeExpense getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(IncomeExpense selectedObject) {
        this.selectedObject = selectedObject;
    }

    public IncomeExpenseMovement getIncomeExpenseMovement() {
        return incomeExpenseMovement;
    }

    public void setIncomeExpenseMovement(IncomeExpenseMovement incomeExpenseMovement) {
        this.incomeExpenseMovement = incomeExpenseMovement;
    }

    public void setIncomeExpenseMovementService(IIncomeExpenseMovementService incomeExpenseMovementService) {
        this.incomeExpenseMovementService = incomeExpenseMovementService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @PostConstruct
    public void init() {
        System.out.println("------IncomeExpenseProcessBean");
        incomeExpenseMovement = new IncomeExpenseMovement();

        setListBtn(sessionBean.checkAuthority(new int[]{159, 160}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{45}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void save() {
        int result = 0;

        if (processType == 1 || processType == 3) {
            List<IncomeExpense> tempList = new ArrayList();
            if (selectedObject.isIsIncome()) {//gelir
                tempList = incomeExpenseService.selectIncomeExpense(true);
            } else {//gider
                tempList = incomeExpenseService.selectIncomeExpense(false);
            }

            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i).getName().equalsIgnoreCase(selectedObject.getName())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("therecordwithnameyouwanttoaddalreadyexists")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }

            result = incomeExpenseService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                incomeExpenseMovement.setIncomeExpense(selectedObject);
                result = incomeExpenseMovementService.create(incomeExpenseMovement);
                if (result > 0) {
                    selectedObject.setBalance(incomeExpenseMovement.getPrice());
                    if (selectedObject.isIsIncome()) {
                        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                        IncomeBean incomeBean = (IncomeBean) viewMap.get("incomeBean");
                        incomeBean.getListOfIncomeExpense().add(selectedObject);
                        incomeBean.createTree();

                    } else {
                        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                        ExpenseBean expenseBean = (ExpenseBean) viewMap.get("expenseBean");
                        expenseBean.getListOfIncomeExpense().add(selectedObject);
                        expenseBean.createTree();

                    }
                }
            }
        } else if (processType == 2) {
            result = incomeExpenseService.update(selectedObject);
        }
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_IncomeExpense').hide();");
            if (selectedObject.isIsIncome()) {
                RequestContext.getCurrentInstance().update("frmIncome:dtbIncome");
            } else {
                RequestContext.getCurrentInstance().update("frmExpense:dtbExpense");
            }
        }
        sessionBean.createUpdateMessage(result);

    }

    public void convertIncomeExpenseToTreeeNode(TreeNode node, IncomeExpense incomeExpense) {
        List<TreeNode> children = node.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {

                if (((IncomeExpense) treeNode.getData()).getId() == incomeExpense.getId()) {
                    findTreeNode = treeNode;
                    break;

                } else {
                    convertIncomeExpenseToTreeeNode(treeNode, incomeExpense);
                }

            }
        }
    }

    public void testBeforeDelete() {

        int result = 0;
        result = incomeExpenseService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmNewIncomeExpense:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");

        } else if (result == 1) {
            if (selectedObject.isIsIncome()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseincomehassubincome")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseexpensehassubexpense")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        } else if (result == 2) {
            if (selectedObject.isIsIncome()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseincomehasmovement")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseexpensehasmovement")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
            }
        }

    }

    public void delete() {
        int result = 0;
        result = incomeExpenseService.delete(selectedObject);
        if (result > 0) {
            RequestContext.getCurrentInstance().execute("PF('dlg_IncomeExpense').hide();");
            if (selectedObject.isIsIncome()) {
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                IncomeBean incomeBean = (IncomeBean) viewMap.get("incomeBean");
                incomeBean.getListOfIncomeExpense().remove(selectedObject);
                incomeBean.createTree();

                RequestContext.getCurrentInstance().update("frmIncome:dtbIncome");
            } else {
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                ExpenseBean expenseBean = (ExpenseBean) viewMap.get("expenseBean");
                expenseBean.getListOfIncomeExpense().remove(selectedObject);
                expenseBean.createTree();

                RequestContext.getCurrentInstance().update("frmExpense:dtbExpense");
            }
        }
        sessionBean.createUpdateMessage(result);
    }

}
