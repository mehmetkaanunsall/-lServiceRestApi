/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 04.01.2019 13:55:54
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.automation.fuelshift.presentation.FuelShiftTransferBean;
import com.mepsan.marwiz.finance.bankaccount.presentation.BankAccountMovementTabBean;
import com.mepsan.marwiz.finance.bankaccount.presentation.BankAccountProcessBean;
import com.mepsan.marwiz.finance.financingdocument.presentation.FinancingDocumentBean;
import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.finance.safe.presentation.SafeMovementTabBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.presentation.MarketShiftTransferBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.pattern.BookFilterBean;
import com.mepsan.marwiz.inventory.stock.presentation.StockDetailTabBean;
import com.mepsan.marwiz.inventory.stocktaking.presentation.StockTakingEmployeeBean;
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
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class IncomeExpenseBookFilterBean extends BookFilterBean<IncomeExpense> {

    @ManagedProperty(value = "#{incomeExpenseService}")
    private IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private TreeNode root;
    private TreeNode selectedNode;
    public IncomeExpense incomeExpense;
    private List<IncomeExpense> listOfIncomeExpense;
    private String types;
    private boolean isIncome;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public boolean isIsIncome() {
        return isIncome;
    }

    public void setIsIncome(boolean isIncome) {
        this.isIncome = isIncome;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public List<IncomeExpense> getListOfIncomeExpense() {
        return listOfIncomeExpense;
    }

    public void setListOfIncomeExpense(List<IncomeExpense> listOfIncomeExpense) {
        this.listOfIncomeExpense = listOfIncomeExpense;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------IncomeExpenseBookCheckboxFilterBean-----------");
        incomeExpense = new IncomeExpense();
        root = new DefaultTreeNode();
        listOfIncomeExpense = new ArrayList<>();
    }

    @Override
    public List<IncomeExpense> callService(List<Object> param, String type) {
        types = type;
        root = new DefaultTreeNode();
        Branch branch = new Branch();

        if (param != null && !param.isEmpty()) {
            if ((boolean) param.get(0)) {
                isIncome = true;//gelir
            } else if (!(boolean) param.get(0)) {
                isIncome = false;//gider
            }

            branch.setId(((Branch) param.get(1)).getId());
        } else {
            isIncome = false;//gider
        }

        if (branch.getId() == 0) {
            branch.setId(sessionBean.getUser().getLastBranch().getId());
        }

        List<IncomeExpense> listIncExpense = incomeExpenseService.listofIncomeExpense(branch);
        for (IncomeExpense incex : listIncExpense) {
            if (incex.isIsIncome() == isIncome) {//gelir ise sadece gelirler,gider ise sadece giderler
                if (incex.getParentId().getId() == 0) {
                    DefaultTreeNode parentTreeNode = new DefaultTreeNode(incex, root);
                    parentTreeNode.setExpanded(true);
                    findChildren(parentTreeNode, listIncExpense);
                }
            }
        }
        return null;
    }

    public void findChildren(DefaultTreeNode treeNode, List<IncomeExpense> list) {
        for (IncomeExpense incex : list) {
            if (incex.isIsIncome() == isIncome) {//gelir ise sadece gelirler,gider ise sadece giderler
                if (incex.getParentId().getId() != 0) {
                    if (incex.getParentId().getId() == ((IncomeExpense) treeNode.getData()).getId()) {
                        DefaultTreeNode childTreeNode = new DefaultTreeNode(incex, treeNode);
                        childTreeNode.setExpanded(true);
                        findChildren(childTreeNode, list);
                    }
                }
            }
        }
    }

    public void onNodeSelect(NodeSelectEvent event) {
        setSelectedData((IncomeExpense) event.getTreeNode().getData());
        if (getSelectedData().getParentId() == null || getSelectedData().getParentId().getId() == 0) {
            //parent seçemez mesaj
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.loc.getString("warning"));
            message.setDetail(sessionBean.loc.getString("youcanonlyselectincome-expensecardsinsubcategories"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("grwProcessMessage");
            return;
        }

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        switch (types) {
            case "financingDocumentBean":
                FinancingDocumentBean financingBean = (FinancingDocumentBean) viewMap.get("financingDocumentBean");
                financingBean.updateAllInformation();
                break;
            case "bankAccountMovementTabBean":
                BankAccountMovementTabBean bankAccountMovementTabBean = (BankAccountMovementTabBean) viewMap.get("bankAccountMovementTabBean");
                bankAccountMovementTabBean.updateAllInformation();
                break;
            case "safeMovementTabBean":
                SafeMovementTabBean safeMovementTabBean = (SafeMovementTabBean) viewMap.get("safeMovementTabBean");
                safeMovementTabBean.updateAllInformation();
                break;
            case "marketShiftTransferBean":
                MarketShiftTransferBean marketShiftTransferBean = (MarketShiftTransferBean) viewMap.get("marketShiftTransferBean");
                marketShiftTransferBean.updateAllInformation();
                break;
            case "stockTakingEmployeeBean":
                StockTakingEmployeeBean stockTakingEmployeeBean = (StockTakingEmployeeBean) viewMap.get("stockTakingEmployeeBean");
                stockTakingEmployeeBean.updateAllInformation();
                break;
            case "fuelShiftTransferBean":
                FuelShiftTransferBean fuelShiftTransferBean = (FuelShiftTransferBean) viewMap.get("fuelShiftTransferBean");
                fuelShiftTransferBean.updateAllInformation();
                break;
            case "bankAccountProcessBean":
                BankAccountProcessBean bankAccountProcessBean = (BankAccountProcessBean) viewMap.get("bankAccountProcessBean");
                bankAccountProcessBean.updateAllInformation();
                break;
            case "stockDetailTabBean":
                StockDetailTabBean stockDetailTabBean = (StockDetailTabBean) viewMap.get("stockDetailTabBean");
                stockDetailTabBean.updateAllInformation();
                break;
            default:
                break;
        }
        RequestContext.getCurrentInstance().execute(dialogId);
    }

    @Override
    public LazyDataModel<IncomeExpense> callServiceLazyLoading(String where, List<Object> param, String type) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter(String type, List<Object> param) {

    }

}
