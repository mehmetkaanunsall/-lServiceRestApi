/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.11.2019 10:39:02
 */
package com.mepsan.marwiz.general.responsible.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccount;
import com.mepsan.marwiz.general.model.finance.BankBranch;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Responsible;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.responsible.business.IResponsibleService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class ResponsibleBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{responsibleService}")
    public IResponsibleService responsibleService;

    private Responsible responsible;
    private int processType;
    private List<Responsible> listOfObjects;
    private Responsible selectedObject;
    private int activeIndex;
    private int pageType;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<Responsible> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<Responsible> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public Responsible getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Responsible selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setResponsibleService(IResponsibleService responsibleService) {
        this.responsibleService = responsibleService;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public Responsible getResponsible() {
        return responsible;
    }

    public void setResponsible(Responsible responsible) {
        this.responsible = responsible;
    }

    public int getPageType() {
        return pageType;
    }

    public void setPageType(int pageType) {
        this.pageType = pageType;
    }

    @PostConstruct
    public void init() {
        System.out.println("---------ResponsibleTabBean------");
        selectedObject = new Responsible();
        responsible = new Responsible();
        listOfObjects = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Account) {
                    responsible.setAccount((Account) ((ArrayList) sessionBean.parameter).get(i));
                    pageType = 1;
                    listOfObjects = findall();
                    break;
                } else if (((ArrayList) sessionBean.parameter).get(i) instanceof BankAccount) {
                    responsible.setBankBranch(((BankAccount) ((ArrayList) sessionBean.parameter).get(i)).getBankBranch());
                    pageType = 2;
                    listOfObjects = findall();
                    break;
                }
            }
        } else {
            pageType = 3;
        }

        if (pageType == 1) {
            setListBtn(sessionBean.checkAuthority(new int[]{72, 73, 74}, 0));
            setListTab(sessionBean.checkAuthority(new int[]{23}, 1));
        } else if (pageType == 2) {
            setListBtn(sessionBean.checkAuthority(new int[]{274, 275, 276}, 0));
            setListTab(sessionBean.checkAuthority(new int[]{63}, 1));
        } else if (pageType == 3) {
            setListBtn(sessionBean.checkAuthority(new int[]{280, 281, 282}, 0));
            setListTab(sessionBean.checkAuthority(new int[]{65}, 1));
        }

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(0);
        }
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
    }

    public void createDialog(int type) {
        processType = type;
        if (type == 1) {
            selectedObject = new Responsible();
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_responsibleproc').show()");
    }

    public List<Responsible> findall() {
        return responsibleService.findResponsible(responsible, pageType);
    }

    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        if (pageType == 1) {
            selectedObject.setAccount(responsible.getAccount());
        } else if (pageType == 2) {
            selectedObject.setBankBranch(responsible.getBankBranch());
        } else if (pageType == 3) {
            selectedObject.setBankBranch(responsible.getBankBranch());
        }

        if (processType == 1) {

            result = responsibleService.create(selectedObject);
            if (result > 0) {
                selectedObject.setId(result);
                listOfObjects.add(selectedObject);

                processType = 2;
                context.update("frmResponsibleProc");
                context.update("pngResponsibleTab");
            }
        } else {
            result = responsibleService.update(selectedObject);
            if (result > 0) {
                context.execute("PF('dlg_responsibleproc').hide();");

            }
        }
        if (result > 0) {
            if (pageType == 1) {
                context.update("tbvAccountProc:frmResponsibleTab:dtbResponsible");
            } else if (pageType == 2) {
                context.update("tbvBankAccountProc:frmResponsibleTab:dtbResponsible");
            } else if (pageType == 3) {
                context.update("tbvBankBranchProc:frmResponsibleTab:dtbResponsible");
            }

        }

        sessionBean.createUpdateMessage(result);

    }

    public void delete() {
        int result = 0;
        RequestContext context = RequestContext.getCurrentInstance();
        result = responsibleService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            if (pageType == 1) {
                context.update("tbvAccountProc:frmResponsibleTab:dtbResponsible");
            } else if (pageType == 2) {
                context.update("tbvBankAccountProc:frmResponsibleTab:dtbResponsible");
            } else if (pageType == 3) {
                context.update("tbvBankBranchProc:frmResponsibleTab:dtbResponsible");
            }
            context.execute("PF('dlg_responsibleproc').hide();");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void closeDialog() {
        RequestContext context = RequestContext.getCurrentInstance();
        listOfObjects = findall();
        if (pageType == 1) {
            context.update("tbvAccountProc:frmResponsibleTab:dtbResponsible");
        } else if (pageType == 2) {
            context.update("tbvBankAccountProc:frmResponsibleTab:dtbResponsible");
        } else if (pageType == 3) {
            context.update("tbvBankBranchProc:frmResponsibleTab:dtbResponsible");
        }
    }

}
