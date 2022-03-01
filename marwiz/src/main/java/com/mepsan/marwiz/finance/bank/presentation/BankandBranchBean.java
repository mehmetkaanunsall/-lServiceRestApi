/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2018 03:44:50
 */
package com.mepsan.marwiz.finance.bank.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.finance.bank.business.IBankService;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Bank;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.responsible.presentation.ResponsibleBean;
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
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class BankandBranchBean extends GeneralBean<Bank> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{bankService}")
    public IBankService bankService;

    @ManagedProperty(value = "#{bankBranchService}")
    public IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    private TreeNode rootBank;
    private List<Bank> listBank;
    private int processType;
    private Boolean expanded = false;
    private TreeNode selectedNode;
    private BankBranch selectedBankBranch;
    private int activeIndex;

    public TreeNode getRootBank() {
        return rootBank;
    }

    public void setRootBank(TreeNode rootBank) {
        this.rootBank = rootBank;
    }

    public List<Bank> getListBank() {
        return listBank;
    }

    public void setListBank(List<Bank> listBank) {
        this.listBank = listBank;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setBankService(IBankService bankService) {
        this.bankService = bankService;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public BankBranch getSelectedBankBranch() {
        return selectedBankBranch;
    }

    public void setSelectedBankBranch(BankBranch selectedBankBranch) {
        this.selectedBankBranch = selectedBankBranch;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------bankBean");
        rootBank = new DefaultTreeNode();
        listBank = bankService.findAll();
        findBank();
        setListBtn(sessionBean.checkAuthority(new int[]{192, 193, 194, 195, 196, 197}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{64}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    /**
     * Bu metot tüm bankaların çekildiği banka listesi içerisinden sadece merkez
     * olanları root bank olarak ekler.
     */
    public void findBank() {
        rootBank = new DefaultTreeNode(new Bank(0), null);
        TreeNode bankTree = new DefaultTreeNode();

        for (Bank bank : listBank) {
            bankTree = new DefaultTreeNode(bank, rootBank);
            bankTree.setExpanded(expanded);
            findBankBranch(bankTree, bank);
        }
    }

    /**
     * +
     * Bu metot liste içerisinden çekilen her bankanın içerisinde bulunan şube
     * listesindeki elemanları banka altına child olarak ekler.
     *
     * @param bankTree
     * @param bank
     */
    public void findBankBranch(TreeNode bankTree, Bank bank) {
        TreeNode bankBranchTree = new DefaultTreeNode();
        for (BankBranch bankBranch : bank.getListOfBranchs()) {
            bankBranchTree = new DefaultTreeNode(bankBranch, bankTree);

            bankBranchTree.setExpanded(expanded);
        }
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new Bank();
        if (!sessionBean.getCountries().isEmpty()) {
            selectedObject.setCountry(new Country(213));
            citiesAndCountiesBean.updateCity(selectedObject.getCountry(), selectedObject.getCity(), selectedObject.getCounty());
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_bankproc').show();");
    }

    public void createSubBranch() {
        processType = 3;
        selectedBankBranch = new BankBranch();
        if (!sessionBean.getCountries().isEmpty()) {
            selectedBankBranch.setCountry(new Country(213));
            citiesAndCountiesBean.updateCity(selectedBankBranch.getCountry(), selectedBankBranch.getCity(), selectedBankBranch.getCounty());
        }

        if (getRendered(64, 1)) {
            Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
            ResponsibleBean responsibleBean = (ResponsibleBean) viewMap.get("responsibleBean");
            responsibleBean.getResponsible().setBankBranch(selectedBankBranch);
            responsibleBean.setPageType(3);
            responsibleBean.getListOfObjects().clear();
            responsibleBean.getListOfObjects().addAll(responsibleBean.findall());
        }

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_branchproc').show();");

    }

    /**
     * TreeTable üzerinde kullanılan açma kapama ikonu için yazılmıştır.Veriler
     * sayfa ilk açıldığında kapalı gelir.
     */
    public void expanded() {
        if (expanded) {
            expanded = false;
        } else {
            expanded = true;
        }
        findChildren(rootBank);
    }

    public void findChildren(TreeNode node) {
        for (TreeNode treeNode : node.getChildren()) {
            treeNode.setExpanded(expanded);
            findChildren(treeNode);
        }
    }

    /**
     * Bu metot güncelleştirme işlemi için işlem sayfasını açar.
     *
     */
    public void update() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (selectedNode.getData() instanceof Bank) {
            processType = 2;
            selectedObject = (Bank) selectedNode.getData();

            citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getCity());
            context.execute("PF('dlg_bankproc').show();");

        } else if (selectedNode.getData() instanceof BankBranch) {
            processType = 4;
            selectedBankBranch = (BankBranch) selectedNode.getData();

            if (getRendered(64, 1)) {
                Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                ResponsibleBean responsibleBean = (ResponsibleBean) viewMap.get("responsibleBean");
                responsibleBean.getResponsible().setBankBranch(selectedBankBranch);
                responsibleBean.setPageType(3);
                responsibleBean.getListOfObjects().clear();
                responsibleBean.getListOfObjects().addAll(responsibleBean.findall());
            }

            citiesAndCountiesBean.updateCityAndCounty(selectedBankBranch.getCountry(), selectedBankBranch.getCity());
            context.execute("PF('dlg_branchproc').show();");
        }

    }

    @Override
    public void generalFilter() {
        rootBank = new DefaultTreeNode(new Bank(0), null);
        if (autoCompleteValue != null) {
            for (Bank b : listBank) {
                if (b.getCode().toLowerCase().contains(autoCompleteValue.toLowerCase())
                          || b.getName().toLowerCase().contains(autoCompleteValue.toLowerCase())
                          || b.getPhone().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b.getEmail().toLowerCase().contains(autoCompleteValue.toLowerCase())
                          || b.getAddress().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b.getCountry().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase())
                          || b.getCity().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b.getCounty().getName().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b.getStatus().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase())) {
                    new DefaultTreeNode(b, rootBank);
                }
                for (BankBranch b1 : b.getListOfBranchs()) {
                    if (b1.getCode().toLowerCase().contains(autoCompleteValue.toLowerCase())
                              || b1.getName().toLowerCase().contains(autoCompleteValue.toLowerCase())
                              || b1.getPhone().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b1.getEmail().toLowerCase().contains(autoCompleteValue.toLowerCase())
                              || b1.getAddress().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b1.getCountry().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase())
                              || b1.getCity().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b1.getCounty().getName().toLowerCase().contains(autoCompleteValue.toLowerCase()) || b1.getStatus().getTag().toLowerCase().contains(autoCompleteValue.toLowerCase())) {
                        new DefaultTreeNode(b1, rootBank);
                    }
                }

            }
        } else {
            reset();
        }
    }

    /**
     * Toolbar içierisinde aramanın sıfırlanıp listenin tekrar çekilmesi
     * sağlanır.
     */
    public void reset() {
        autoCompleteValue = null;
        findBank();
    }

    /**
     * Bu metot banka yada şube kaydetmek için kullanılır.
     */
    @Override
    public void save() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();

        if (processType == 1 || processType == 2) {
            for (Bank bank : listBank) {
                if (bank.getId() != selectedObject.getId() && bank.getCode().equalsIgnoreCase(selectedObject.getCode())) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("bankalreadyavailable")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    return;
                }
            }
        }

        switch (processType) {
            case 1:
                result = bankService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    context.execute("PF('dlg_bankproc').hide();");
                }
                break;
            case 2:
                result = bankService.update(selectedObject);
                if (result > 0) {
                    context.execute("PF('dlg_bankproc').hide();");
                }
                break;
            case 4:
                result = bankBranchService.update(selectedBankBranch);
                if (result > 0) {
                    context.execute("PF('dlg_branchproc').hide();");
                }
                break;
            default:
                selectedBankBranch.setBank(selectedObject);
                result = bankBranchService.create(selectedBankBranch);
                if (result > 0) {
                    selectedBankBranch.setId(result);
                    processType = 4;
                    context.update("frmBranchProcess");
                    context.update("pngBranchResponsibleTab");

                    if (getRendered(64, 1)) {
                        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
                        ResponsibleBean responsibleBean = (ResponsibleBean) viewMap.get("responsibleBean");
                        responsibleBean.getResponsible().setBankBranch(selectedBankBranch);
                        responsibleBean.setPageType(3);
                        responsibleBean.getListOfObjects().clear();
                        responsibleBean.getListOfObjects().addAll(responsibleBean.findall());
                    }

                }
                break;
        }

        if (result > 0) {
            listBank = bankService.findAll();
            findBank();
            context.update("frmBankandBranch:ttbBankandBranch");

        }
        sessionBean.createUpdateMessage(result);

    }

    public void testBeforeDelete() {
        int result = 0;
        switch (processType) {
            case 2://Banka
                String branchList = "";

                for (BankBranch bankBranch : selectedObject.getListOfBranchs()) {
                    branchList = branchList + "," + String.valueOf(bankBranch.getId());
                }

                if (!branchList.equals("")) {
                    branchList = branchList.substring(1, branchList.length());
                }
                if (!branchList.equals("")) {
                    result = bankService.testBeforeDelete(branchList);
                    if (result == 0) {
                        RequestContext.getCurrentInstance().update("frmBankProcess:dlgDelete");
                        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebankisrelatedtobankaccount")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                    }
                } else {
                    RequestContext.getCurrentInstance().update("frmBankProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                }

                break;
            case 4://Şube
                result = bankBranchService.testBeforeDelete(selectedBankBranch);
                if (result == 0) {
                    RequestContext.getCurrentInstance().update("frmBranchProcess:dlgDelete");
                    RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausebranchisrelatedtobankaccount")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                }
                break;

        }

    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        switch (processType) {
            case 2://Banka
                result = bankService.delete(selectedObject);
                if (result > 0) {
                    context.execute("PF('dlg_bankproc').hide();");
                }
                break;
            case 4://Şube
                result = bankBranchService.delete(selectedBankBranch);
                if (result > 0) {
                    context.execute("PF('dlg_branchproc').hide();");
                }
                break;

        }
        if (result > 0) {
            listBank = bankService.findAll();
            findBank();
            context.update("frmBankandBranch:ttbBankandBranch");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public LazyDataModel<Bank> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
