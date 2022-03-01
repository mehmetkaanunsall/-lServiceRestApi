/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   19.02.2018 04:50:44
 */
package com.mepsan.marwiz.general.creditcardmachine.presentation;

import com.mepsan.marwiz.finance.bankaccount.business.IBankAccountService;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.creditcardmachine.business.ICreditCardMachineService;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.general.CreditCardMachine;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class CreditCardMachineBean extends GeneralDefinitionBean<CreditCardMachine> {

    @ManagedProperty(value = "#{creditCardMachineService}")
    private ICreditCardMachineService creditCardMachineService;

    @ManagedProperty(value = "#{bankAccountService}")
    private IBankAccountService bankAccountService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private int processType;
    private List<BankAccount> bankAccountList;

    public void setCreditCardMachineService(ICreditCardMachineService creditCardMachineService) {
        this.creditCardMachineService = creditCardMachineService;
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

    public List<BankAccount> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccount> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

    public void setBankAccountService(IBankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------CreditCardMachineBean--------");

        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true);

        setListBtn(sessionBean.checkAuthority(new int[]{213, 214, 215}, 0));
    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new CreditCardMachine();
        bankAccountList = bankAccountService.bankAccountForSelect(" AND bka.type_id = 16 ", sessionBean.getUser().getLastBranch());

        RequestContext.getCurrentInstance().execute("PF('dlg_CreditCardMachineProcess').show();");

    }

    public void update() {
        processType = 2;
        bankAccountList = bankAccountService.bankAccountForSelect("AND bka.type_id = 16 ", sessionBean.getUser().getLastBranch());

        RequestContext.getCurrentInstance().execute("PF('dlg_CreditCardMachineProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (processType == 1) {
            result = creditCardMachineService.create(selectedObject);
            selectedObject.setId(result);
            listOfObjects.add(selectedObject);

        } else if (processType == 2) {
            result = creditCardMachineService.update(selectedObject);

        }

        if (result > 0) {
            bringAll();
            RequestContext.getCurrentInstance().execute("PF('dlg_CreditCardMachineProcess').hide();");
            RequestContext.getCurrentInstance().update("frmCreditCardMachine:dtbCreditCardMachine");
            RequestContext.getCurrentInstance().execute("PF('creditCardMachinePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    @Override
    public List<CreditCardMachine> findall() {
        return creditCardMachineService.listOfCreditCardMachine();
    }

    public void bringAll() {

        for (Status status : sessionBean.getStatus(21)) {
            if (status.getId() == selectedObject.getStatus().getId()) {
                selectedObject.getStatus().setTag(status.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
        for (BankAccount bankAccount : bankAccountList) {
            if (bankAccount.getId() == selectedObject.getBankAccount().getId()) {
                selectedObject.getBankAccount().setName(bankAccount.getName());
            }
        }
    }

    public void delete(){
        int result=0;
        result=creditCardMachineService.delete(selectedObject);
        if(result>0){
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_CreditCardMachineProcess').hide();");
            context.update("frmCreditCardMachine:dtbCreditCardMachine");
            context.execute("PF('creditCardMachinePF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
