/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.incomeexpense.presentation;

import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.finance.IncomeExpenseMovement;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class ExpenseBean extends GeneralBean<IncomeExpense> {

    private TreeNode root;
    private TreeNode selectedIncomeExpense;
    private IncomeExpense selectedParent;
    private int processType;
    private boolean expanded;
    List<IncomeExpense> listOfIncomeExpense;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{incomeExpenseService}")
    private IIncomeExpenseService incomeExpenseService;

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

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedIncomeExpense() {
        return selectedIncomeExpense;
    }

    public void setSelectedIncomeExpense(TreeNode selectedIncomeExpense) {
        this.selectedIncomeExpense = selectedIncomeExpense;
    }

    public IncomeExpense getSelectedParent() {
        return selectedParent;
    }

    public void setSelectedParent(IncomeExpense selectedParent) {
        this.selectedParent = selectedParent;
    }

    public List<IncomeExpense> getListOfIncomeExpense() {
        return listOfIncomeExpense;
    }

    public void setListOfIncomeExpense(List<IncomeExpense> listOfIncomeExpense) {
        this.listOfIncomeExpense = listOfIncomeExpense;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------ExpenseBean");

        toogleList = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true);
        }
        selectedParent=new IncomeExpense();
        expanded=true;
        listOfIncomeExpense = incomeExpenseService.selectIncomeExpense(false);
        createTree();
        
        setListBtn(sessionBean.checkAuthority(new int[]{158}, 0));
    }
    
    public TreeNode createTree() {
        root = new DefaultTreeNode(new IncomeExpense(), null);
        root.setExpanded(expanded);
        for (IncomeExpense incomeExpense1 : listOfIncomeExpense) {
            if (incomeExpense1.getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(incomeExpense1, root);
                parentTreeNode.setExpanded(expanded);
                findChildren(parentTreeNode, listOfIncomeExpense);
            }
        }

        return root;
    }

    public void findChildren(DefaultTreeNode treeNode, List<IncomeExpense> list) {

        for (IncomeExpense incomeExpense : list) {
            if (incomeExpense.getParentId().getId() != 0) {
                if (incomeExpense.getParentId().getId() == ((IncomeExpense) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(incomeExpense, treeNode);
                    childTreeNode.setExpanded(expanded);
                    findChildren(childTreeNode, list);
                }
            }
        }
    }
    
    /**
     * TreeTable üzerinde kullanılan açma kapama ikonu için yazılmıştır.Veriler
     * sayfa ilk açıldığında açık gelir.
     */
    public void expanded() {
        if (expanded) {
            expanded = false;
        } else {
            expanded = true;
        }
        changeExpand(root);
    }

    public void changeExpand(TreeNode node) {
        for (TreeNode treeNode : node.getChildren()) {
            treeNode.setExpanded(expanded);
            changeExpand(treeNode);
        }
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new IncomeExpense();
        selectedObject.setIsIncome(false);
        selectedObject.setParentId(new IncomeExpense());
        selectedObject.setIsProfitMarginReport(true);
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomeExpenseProcessBean incomeExpenseProcessBean = (IncomeExpenseProcessBean) viewMap.get("incomeExpenseProcessBean");
        if (incomeExpenseProcessBean != null) {
            incomeExpenseProcessBean.setSelectedObject(selectedObject);
            incomeExpenseProcessBean.setIncomeExpenseMovement(new IncomeExpenseMovement());
            incomeExpenseProcessBean.setProcessType(1);
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_IncomeExpense').show();");
    }
    
    public void createChild() {
        /*  çocuk ekleme  */
        processType=3;
        selectedObject = new IncomeExpense();
        selectedObject.setParentId(selectedParent);
        selectedObject.setIsIncome(false);
        selectedObject.setIsProfitMarginReport(true);
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomeExpenseProcessBean incomeExpenseProcessBean = (IncomeExpenseProcessBean) viewMap.get("incomeExpenseProcessBean");
        if (incomeExpenseProcessBean != null) {
            incomeExpenseProcessBean.setSelectedObject(selectedObject);
            incomeExpenseProcessBean.setIncomeExpenseMovement(new IncomeExpenseMovement());
            incomeExpenseProcessBean.setProcessType(3);
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_IncomeExpense').show();");
    }

    public void update() {
        processType = 2;
        selectedObject = (IncomeExpense) selectedIncomeExpense.getData();
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        IncomeExpenseProcessBean incomeExpenseProcessBean = (IncomeExpenseProcessBean) viewMap.get("incomeExpenseProcessBean");
        if (incomeExpenseProcessBean != null) {
            incomeExpenseProcessBean.setSelectedObject(selectedObject);
            incomeExpenseProcessBean.setProcessType(2);
        }
        
        IncomeExpenseMovementTabBean incomeExpenseMovementTabBean = (IncomeExpenseMovementTabBean) viewMap.get("incomeExpenseMovementTabBean");
        if(incomeExpenseMovementTabBean!= null){
            incomeExpenseMovementTabBean.setIncomeExpense(selectedObject);
            incomeExpenseMovementTabBean.init();
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_IncomeExpense').show();");
    }

    @Override
    public void save() {

    }

    @Override
    public void generalFilter() {
        root = new DefaultTreeNode(new IncomeExpense(), null);
        if (autoCompleteValue != null) {
            for (IncomeExpense incomeExpense : listOfIncomeExpense) {
                if (incomeExpense.getName().toLowerCase().contains(autoCompleteValue.toLowerCase())) {
                    new DefaultTreeNode(incomeExpense, root);
                }    
            }
        } else {
            createTree();
        }
    }

    @Override
    public LazyDataModel<IncomeExpense> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
