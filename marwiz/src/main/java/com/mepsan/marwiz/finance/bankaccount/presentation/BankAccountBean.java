/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.01.2018 09:18:11
 */
package com.mepsan.marwiz.finance.bankaccount.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class BankAccountBean extends GeneralDefinitionBean<BankAccount> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{bankAccountService}")
    public IBankAccountService bankAccountService;

    private Object object;  // yeniye bastığında gotopage fonksiyonuna parametre olarak bunu gönderiyoruz. ekleme modunda açılsın diye

    private HashMap<Integer, BankAccount> subTotals;
    private String subTotalBalance;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public HashMap<Integer, BankAccount> getSubTotals() {
        return subTotals;
    }

    public void setSubTotals(HashMap<Integer, BankAccount> subTotals) {
        this.subTotals = subTotals;
    }

    public String getSubTotalBalance() {
        return subTotalBalance;
    }

    public void setSubTotalBalance(String subTotalBalance) {
        this.subTotalBalance = subTotalBalance;
    }

   
    

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------BankAccountBean");
        listOfFilteredObjects = new ArrayList<>();
        listOfObjects = findall();
        listOfFilteredObjects.addAll(listOfObjects);
        subTotals = new HashMap<>();
        if (!listOfObjects.isEmpty()) {
            calcSubTotals();
        }

        object = new Object();
        toogleList = new ArrayList<>();
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true);
        }

        setListBtn(sessionBean.checkAuthority(new int[]{110}, 0));

    }

    /**
     * Yeni hesap eklemek için işlem sayfasını açar.
     */
    @Override
    public void create() {
        marwiz.goToPage("/pages/finance/bankaccount/bankaccountprocess.xhtml", object, 0, 20);
    }

    @Override
    public List<BankAccount> findall() {
        return bankAccountService.findAll();
    }

    public void goToProcess() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/finance/bankaccount/bankaccountprocess.xhtml", list, 0, 20);
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean renderedColumnValue(BigDecimal balance, int type) {
        if (type == 1) {

            if (balance.compareTo(BigDecimal.valueOf(0)) == -1) {
                return true;
            } else {
                return false;
            }

        } else if (type == 2) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public void calcSubTotals() {
        subTotalBalance = "";

        subTotals.clear();

        for (BankAccount u : listOfFilteredObjects) {

            if (subTotals.containsKey(u.getCurrency().getId())) {
                BankAccount old = new BankAccount();
                old.getBankAccountBranchCon().setBalance(BigDecimal.ZERO);
                old.getBankAccountBranchCon().setBalance(subTotals.get(u.getCurrency().getId()).getBankAccountBranchCon().getBalance().add(u.getBankAccountBranchCon().getBalance()));
                subTotals.put(u.getCurrency().getId(), old);
            } else {
                subTotals.put(u.getCurrency().getId(), u);
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, BankAccount> entry : subTotals.entrySet()) {
            if (temp == 0) {
                temp = 1;

                if (entry.getKey() != 0) {

                    subTotalBalance += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBankAccountBranchCon().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

                }
            } else if (entry.getKey() != 0) {

                subTotalBalance += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue().getBankAccountBranchCon().getBalance())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);

            }

        }
        if (subTotalBalance.isEmpty() || subTotalBalance.equals("")) {
            subTotalBalance = "0";
        }
    }

}
