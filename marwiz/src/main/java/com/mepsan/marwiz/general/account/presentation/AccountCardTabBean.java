/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 6:20:24 PM
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountCardService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountCard;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AccountCardTabBean extends GeneralDefinitionBean<AccountCard> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountCardService}")
    public IAccountCardService accountCardService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Account selectedAccount;
    private int processType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountCardService(IAccountCardService accountCardService) {
        this.accountCardService = accountCardService;
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

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("--AccountCardTabBean-------");
        selectedObject = new AccountCard();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedAccount = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    selectedObject.setAccount(selectedAccount);

                    listOfObjects = findall();
                }
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {//Cari İşlemleri sayfası için
            setListBtn(sessionBean.checkAuthority(new int[]{81, 83, 84}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{82, 85, 86}, 0));
        }

    }

    @Override
    public void create() {
        processType = 1;

        selectedObject = new AccountCard();
        selectedObject.setAccount(selectedAccount);
        RequestContext.getCurrentInstance().execute("PF('dlg_AccountCardProc').show()");
    }

    public void update() {
        processType = 2;

        RequestContext.getCurrentInstance().execute("PF('dlg_AccountCardProc').show()");

    }

    @Override
    public void save() {
        int result = 0;
        bringStatus();
        RequestContext context = RequestContext.getCurrentInstance();
        if (processType == 1) {
            result = accountCardService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);
            }
        } else {
            result = accountCardService.update(selectedObject);
        }

        if (result > 0) {
            context.execute("PF('dlg_AccountCardProc').hide()");
            context.update("tbvAccountProc:frmCardTab:dtbCard");
            context.execute("PF('cardPF').filter();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void bringStatus() {
        for (Status statu : sessionBean.getStatus(30)) {
            if (selectedObject.getStatus().getId() == statu.getId()) {
                selectedObject.getStatus().setTag(statu.getNameMap().get(sessionBean.getLangId()).getName());
                break;
            }
        }
    }

    @Override
    public List<AccountCard> findall() {
        return accountCardService.findAccountCard(selectedObject);
    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = accountCardService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvAccountProc:frmCardTab:dtbCard");
            context.execute("PF('cardPF').filter();");
            context.execute("PF('dlg_AccountCardProc').hide()");
        }
        sessionBean.createUpdateMessage(result);
    }

}
