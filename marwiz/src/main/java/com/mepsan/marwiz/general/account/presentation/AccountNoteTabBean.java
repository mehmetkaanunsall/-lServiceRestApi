/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.11.2019 10:37:33
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountNoteService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountNote;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class AccountNoteTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountNoteService}")
    public IAccountNoteService accountNoteService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Account selectedAccount;
    private int processType;
    private List<AccountNote> listOfObjects;
    private AccountNote selectedObject;

    public Account getSelectedAccount() {
        return selectedAccount;
    }

    public void setSelectedAccount(Account selectedAccount) {
        this.selectedAccount = selectedAccount;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<AccountNote> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<AccountNote> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public AccountNote getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(AccountNote selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setAccountNoteService(IAccountNoteService accountNoteService) {
        this.accountNoteService = accountNoteService;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------AccountNoteTabBean------");
        selectedObject = new AccountNote();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    selectedAccount = (Account) ((ArrayList) sessionBean.parameter).get(i);
                    listOfObjects = findall();
                }
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 11) {//Cari İşlemleri sayfası için
            setListBtn(sessionBean.checkAuthority(new int[]{268, 270, 271}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{269, 272, 273}, 0));
        }

    }

    public void createDialog(int type) {
        processType = type;
        if (type == 1) {
            selectedObject = new AccountNote();
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_NoteProcess').show()");
    }

    public List<AccountNote> findall() {
        return accountNoteService.findAccountNote(selectedAccount);
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        selectedObject.setAccount(selectedAccount);

        if (processType == 1) {

            result = accountNoteService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);              
            }
        } else {
            result = accountNoteService.update(selectedObject);
        }
        if (result > 0) {
            context.execute("PF('dlg_NoteProcess').hide()");
            context.update("tbvAccountProc:frmAccountNoteTab:dtbNotes");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = accountNoteService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            context.update("tbvAccountProc:frmAccountNoteTab:dtbNotes");
            context.execute("PF('dlg_NoteProcess').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

}
