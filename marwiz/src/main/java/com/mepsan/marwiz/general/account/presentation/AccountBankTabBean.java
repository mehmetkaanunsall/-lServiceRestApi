/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.11.2019 06:19:25
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.finance.bank.business.IBankBranchService;
import com.mepsan.marwiz.general.account.business.IAccountBankService;
import com.mepsan.marwiz.general.common.BankBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountBank;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
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
public class AccountBankTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountBankService}")
    public IAccountBankService accountBankService;

    @ManagedProperty(value = "#{bankBranchService}")
    private IBankBranchService bankBranchService;

    @ManagedProperty(value = "#{bankBookFilterBean}")
    private BankBookFilterBean bankBookFilterBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Account selectedAccount;
    private int processType;
    private List<AccountBank> listOfObjects;
    private AccountBank selectedObject;
    private List<BankBranch> listBankBranch;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountBankService(IAccountBankService accountBankService) {
        this.accountBankService = accountBankService;
    }

    public void setBankBranchService(IBankBranchService bankBranchService) {
        this.bankBranchService = bankBranchService;
    }

    public BankBookFilterBean getBankBookFilterBean() {
        return bankBookFilterBean;
    }

    public void setBankBookFilterBean(BankBookFilterBean bankBookFilterBean) {
        this.bankBookFilterBean = bankBookFilterBean;
    }

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<AccountBank> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<AccountBank> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public AccountBank getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AccountBank selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<BankBranch> getListBankBranch() {
        return listBankBranch;
    }

    public void setListBankBranch(List<BankBranch> listBankBranch) {
        this.listBankBranch = listBankBranch;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------AccountBankTabBean------");
        selectedObject = new AccountBank();
        listBankBranch = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedAccount = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = findall();
                }
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {//Cari İşlemleri sayfası için
            setListBtn(sessionBean.checkAuthority(new int[]{286, 287, 288}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{289, 290, 291}, 0));
        }

    }

    public void createDialog(int type) {
        processType = type;

        if (type == 1) {
            selectedObject = new AccountBank();
            selectedObject.setAccount(selectedAccount);
        } else {
            listBankBranch = bankBranchService.selectBankBranchForBank(selectedObject.getBankBranch().getBank());
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_AccountBankProcess').show()");
    }

    public void updateAllInformation() {
        listBankBranch = bankBookFilterBean.getSelectedData().getListOfBranchs();
        if (listBankBranch.size() > 0) {
            selectedObject.setBankBranch(listBankBranch.get(0));
        } else {
            selectedObject.setBankBranch(new BankBranch());
        }
        selectedObject.getBankBranch().setBank(bankBookFilterBean.getSelectedData());

        RequestContext.getCurrentInstance().update("frmAccountBankProcess:txtBankName");
        RequestContext.getCurrentInstance().update("frmAccountBankProcess:slcBranch");
        RequestContext.getCurrentInstance().update("frmAccountBankProcess:txtBranchCode");

    }

    public void updateBranchCode() {

        for (BankBranch bankBranch : listBankBranch) {
            if (bankBranch.getId() == selectedObject.getBankBranch().getId()) {
                selectedObject.getBankBranch().setCode(bankBranch.getCode());
            }
        }
    }

    public List<AccountBank> findall() {
        return accountBankService.findAccountBank(selectedAccount);
    }

    public void save() {

        int result = 0;
        selectedObject.setAccount(selectedAccount);
        RequestContext context = RequestContext.getCurrentInstance();

        if (processType == 1) {

            result = accountBankService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);

            }
        } else {
            result = accountBankService.update(selectedObject);

        }
        if (result > 0) {
            bringStatus();
            bringType();
            bringCurrency();
            bringBranch();
            context.execute("PF('dlg_AccountBankProcess').hide();");
            context.update("tbvAccountProc:frmAccountBankTab:dtbBank");

        }

        sessionBean.createUpdateMessage(result);

    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = accountBankService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvAccountProc:frmAccountBankTab:dtbBank");
            context.execute("PF('dlg_AccountBankProcess').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringStatus() {
        for (Status statu : sessionBean.getStatus(13)) {
            if (selectedObject.getStatus().getId() == statu.getId()) {
                selectedObject.getStatus().setTag(statu.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void bringType() {
        for (Type type : sessionBean.getTypes(13)) {
            if (selectedObject.getType().getId() == type.getId()) {
                selectedObject.getType().setTag(type.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void bringCurrency() {
        for (Currency c : sessionBean.getCurrencies()) {
            if (selectedObject.getCurrency().getId() == c.getId()) {
                selectedObject.getCurrency().setTag(c.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    public void bringBranch() {
        for (BankBranch b : listBankBranch) {
            if (selectedObject.getBankBranch().getId() == b.getId()) {
                selectedObject.getBankBranch().setName(b.getName());
            }
        }
    }

}
