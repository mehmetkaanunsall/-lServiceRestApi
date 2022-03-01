/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.automation.cardtype.presentation;

import com.mepsan.marwiz.automation.cardtype.business.ICardTypeService;
import com.mepsan.marwiz.automation.saletype.business.ISaleTypeService;
import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelCardType;
import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
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

/**
 *
 * @author asli.can
 */
@ManagedBean
@ViewScoped
public class CardTypeBean extends GeneralDefinitionBean<FuelCardType> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{cardTypeService}")
    public ICardTypeService cardTypeService;

    @ManagedProperty(value = "#{saleTypeService}")
    public ISaleTypeService saleTypeService;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    private List<FuelSaleType> listForSaleTypeList;//Satış tipi listesi
    private int processType;
    private List<Account> listOfAccount; // Cari kitabı için 
    private List<BankAccount> listOfBankAccount; // Banka Cari listesi 
    private boolean changeAccountBook;
    private boolean changeBankAccount;

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCardTypeService(ICardTypeService cardTypeService) {
        this.cardTypeService = cardTypeService;
    }

    public List<FuelSaleType> getListForSaleTypeList() {
        return listForSaleTypeList;
    }

    public void setListForSaleTypeList(List<FuelSaleType> listForSaleTypeList) {
        this.listForSaleTypeList = listForSaleTypeList;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setSaleTypeService(ISaleTypeService saleTypeService) {
        this.saleTypeService = saleTypeService;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public List<BankAccount> getListOfBankAccount() {
        return listOfBankAccount;
    }

    public void setListOfBankAccount(List<BankAccount> listOfBankAccount) {
        this.listOfBankAccount = listOfBankAccount;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public boolean isChangeAccountBook() {
        return changeAccountBook;
    }

    public void setChangeAccountBook(boolean changeAccountBook) {
        this.changeAccountBook = changeAccountBook;
    }

    public boolean isChangeBankAccount() {
        return changeBankAccount;
    }

    public void setChangeBankAccount(boolean changeBankAccount) {
        this.changeBankAccount = changeBankAccount;
    }

    @PostConstruct
    @Override
    public void init() {

        System.out.println("---------CardTypeBean-----------------");
        listOfObjects = findall();
        listOfAccount = new ArrayList<>();
        listOfBankAccount = new ArrayList<>();
        listForSaleTypeList = new ArrayList<>();
        selectedObject = new FuelCardType();
        toogleList = Arrays.asList(true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{354, 355, 356}, 0));

    }

    @Override
    public void create() {

        processType = 1;
        selectedObject = new FuelCardType();
        changeRendered();
        listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN (16, 104) ", sessionBean.getUser().getLastBranch());
        listForSaleTypeList = saleTypeService.findSaleTypeForBranch("AND fst.branch_id = " + sessionBean.getUser().getLastBranch().getId());

        RequestContext.getCurrentInstance().execute("PF('dlg_FuelCardTypeProcess').show();");
    }

    public void update() {

        processType = 2;
        changeRendered();
        listOfBankAccount = bankAccountService.bankAccountForSelect(" AND bka.type_id IN (16, 104) ", sessionBean.getUser().getLastBranch());
        listForSaleTypeList = saleTypeService.findSaleTypeForBranch("AND fst.branch_id = " + sessionBean.getUser().getLastBranch().getId());
        RequestContext.getCurrentInstance().execute("PF('dlg_FuelCardTypeProcess').show();");

    }

    @Override
    public void save() {
        int result = 0;

        for (FuelCardType cardType : listOfObjects) {

            if (cardType.getTypeNo() == selectedObject.getTypeNo() && cardType.getSaleType().getId() == selectedObject.getSaleType().getId() && selectedObject.getId() != cardType.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("asalestypeofthesamenumberalreadyavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }
        if (processType == 1) {

            result = cardTypeService.create(selectedObject);

            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }

        } else if (processType == 2) {

            result = cardTypeService.update(selectedObject);

        }

        if (result > 0) {

            bringBankAccount();
            bringFuelSaleType();

            RequestContext.getCurrentInstance().execute("PF('dlg_FuelCardTypeProcess').hide();");
            RequestContext.getCurrentInstance().update("frmCardType:dtbCardType");
            RequestContext.getCurrentInstance().execute("PF('cardTypePF').filter();");

        }

        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<FuelCardType> findall() {
        return cardTypeService.findAll();
    }

    public void updateAllInformation() {

        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account(0);
                account.setIsPerson(false);
                account.setName(sessionBean.loc.getString("all"));
                selectedObject.setAccount(account);
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("frmFuelCardTypeProcess:txtCustomer");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
        }

    }

    /**
     * Banka Hesaplarının çekildiği metod
     */
    public void bringBankAccount() {

        for (BankAccount bankAccount : listOfBankAccount) {
            if (bankAccount.getId() == selectedObject.getBankacount().getId()) {
                selectedObject.getBankacount().setName(bankAccount.getName());
            }
        }
    }

    /**
     * Satış Tiplerinin çekildiği metod
     */
    public void bringFuelSaleType() {

        for (FuelSaleType fuelSaleType : listForSaleTypeList) {
            if (fuelSaleType.getId() == selectedObject.getSaleType().getId()) {
                selectedObject.getSaleType().setName(fuelSaleType.getName());
            }
        }

    }

    public void delete() {
        int result = 0;
        result = cardTypeService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_FuelCardTypeProcess').hide();");
            context.update("frmCardType:dtbCardType");
            RequestContext.getCurrentInstance().execute("PF('cardTypePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Dialog içeirisindeki Satış Tİpi combobox'ı değiştikçe typeNo'yu set
     * edebilmek için
     */
    public void changeEvent() {

        for (FuelSaleType list : listForSaleTypeList) {
            if (selectedObject.getSaleType().getId() == list.getId()) {
                selectedObject.getSaleType().setTypeno(list.getTypeno());
            }
        }

        changeRendered();

    }

    /**
     * Dialoğun close eventinde çağırılıyor. Diyalog kapatıp açtığında
     * değişikler kaldığı için liste terkar çekildi.
     */
    public void closeDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        listOfObjects = findall();
        context.execute("PF('cardTypePF').filter();");
        context.update("frmCardType:dtbCardType");
    }

    public void changeRendered() {
        changeAccountBook = false;
        changeBankAccount = false;

        if ((selectedObject.getSaleType().getTypeno() == 1 && (selectedObject.getTypeNo() == 1 || selectedObject.getTypeNo() == 2 || selectedObject.getTypeNo() == 3 || selectedObject.getTypeNo() == 4 ||
                selectedObject.getTypeNo() == 6 || selectedObject.getTypeNo() == 8 || selectedObject.getTypeNo() == 9)) || selectedObject.getSaleType().getTypeno() == 3) {

            changeAccountBook = true;
        }

        if ((selectedObject.getSaleType().getTypeno() == 1 && ( selectedObject.getTypeNo() == 5 ||  selectedObject.getTypeNo() == 7 ))) {

            changeBankAccount = true;
            changeAccountBook = true;
        }

        if (!changeBankAccount) {
            selectedObject.setBankacount(new BankAccount());
        }

        if (!changeAccountBook) {
            Account account = new Account();
            account.setName("");
            selectedObject.setAccount(account);
        }
    }
}
