/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   01.02.2018 04:54:22
 */
package com.mepsan.marwiz.system.userdata.presentation;

import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.authorize.business.IAuthorizeService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

@ManagedBean
@ViewScoped
public class UserDataProcessBean extends AuthenticationLists {

    private List<Branch> branchList;
    private List<Authorize> authorizeList;
    private UserData selectedObject;
    private int processType;
    private String accountName;
    private int activeIndex;

    private List<Option> importReportOptionsList;
    private List<String> selectedOptions;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{userDataService}")
    private IUserDataService userDataService;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{authorizeService}")
    private IAuthorizeService authorizeService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{userDataBean}")
    public UserDataBean userDataBean;

    public List<Authorize> getAuthorizeList() {
        return authorizeList;
    }

    public void setAuthorizeList(List<Authorize> authorizeList) {
        this.authorizeList = authorizeList;
    }

    public UserData getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(UserData selectedObject) {
        this.selectedObject = selectedObject;
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

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setAuthorizeService(IAuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public List<Branch> getBranchList() {
        return branchList;
    }

    public void setBranchList(List<Branch> branchList) {
        this.branchList = branchList;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public List<Option> getImportReportOptionsList() {
        return importReportOptionsList;
    }

    public void setImportReportOptionsList(List<Option> importReportOptionsList) {
        this.importReportOptionsList = importReportOptionsList;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public UserDataBean getUserDataBean() {
        return userDataBean;
    }

    public void setUserDataBean(UserDataBean userDataBean) {
        this.userDataBean = userDataBean;
    }

    public class Option {

        private int id;
        private String tableName;

        public Option(int id, String tableName) {
            this.id = id;
            this.tableName = tableName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

    }

    @PostConstruct
    public void init() {
        System.out.println("------UserDataProcessBean-------------");
        branchList = new ArrayList<>();
        authorizeList = new ArrayList<>();
        importReportOptionsList = new ArrayList<>();
        selectedOptions = new ArrayList<>();
        createOptionList();
        branchList = branchService.selectBranchs();
        if (sessionBean.parameter instanceof UserData) {

            selectedObject = (UserData) sessionBean.parameter;
            processType = 2;

            // tip 2 de username kontrolü yapmak için
            userDataBean.setListOfObjects(userDataBean.findall());

            String resultReport = "";
            resultReport = selectedObject.getMposPages();
            System.out.println("resultReport" + resultReport);
            if (resultReport != null && !resultReport.equals("")) {
                String[] tempArray = resultReport.split(",");
                for (int i = 0; i < tempArray.length; i++) {
                    String string = tempArray[i];
                    selectedOptions.add(Integer.toString(importReportOptionsList.get(Integer.valueOf(tempArray[i]) - 1).getId()));

                }
            }

            accountName = selectedObject.getAccount().getName();

            citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getCity());
            authorizeList = authorizeService.selectAuthorizeToTheBranch(selectedObject.getLastBranch());

        } else {

            processType = 1;
            selectedObject = new UserData();
            authorizeList = authorizeService.selectAuthorizeToTheBranch(branchList.get(0));

        }

        setListBtn(sessionBean.checkAuthority(new int[]{175, 176}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{50, 78}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void changeBranch() {

        selectedObject.getLastAuthorize().setId(0);
        authorizeList = authorizeService.selectAuthorizeToTheBranch(selectedObject.getLastBranch());
    }

    public void save() {
        int result = 0;
        boolean isLongName = false;
        if (selectedObject.getType().getId() == 2) {
            String namesurname = "";
            namesurname = selectedObject.getName() + selectedObject.getSurname();
            if (namesurname.length() > 24) {
                isLongName = true;
            }
        }
        if (isLongName) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisnameandsurnamearelongforcashier")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            boolean isThere = false;
            if (processType == 1) {
                for (UserData usr : userDataBean.getListOfObjects()) {
                    isThere = false;
                    if (usr.getUsername().equals(selectedObject.getUsername())) {
                        isThere = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisusernamealreadyexistsinthesystem")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    } else {
                        isThere = false;
                    }
                }
                if (!isThere) {
                    String str = "";
                    for (String st : selectedOptions) {
                        str = str + st + ",";
                    }
                    if (str != null && !str.equals("")) {
                        str = str.substring(0, str.length() - 1);
                    }
                    selectedObject.setMposPages(str);
                    result = userDataService.create(selectedObject);
                    selectedObject.setPassword(null);
                    if (result > 0) {
                        selectedObject.setId(result);
                        marwiz.goToPage("/pages/system/userdata/userdataprocess.xhtml", selectedObject, 1, 13);
                    }
                    sessionBean.createUpdateMessage(result);
                }

            } else if (processType == 2) {
                for (UserData usr : userDataBean.getListOfObjects()) {
                    isThere = false;
                    if (usr.getUsername().equals(selectedObject.getUsername()) && selectedObject.getId() != usr.getId()) {
                        isThere = true;
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thisusernamealreadyexistsinthesystem")));
                        RequestContext.getCurrentInstance().update("grwProcessMessage");
                        break;
                    } else {
                        isThere = false;
                    }
                }
                if (!isThere) {
                    String str = "";
                    for (String st : selectedOptions) {
                        str = str + st + ",";
                    }
                    if (str != null && !str.equals("")) {
                        str = str.substring(0, str.length() - 1);
                    }
                    selectedObject.setMposPages(str);
                    result = userDataService.update(selectedObject);
                    if (result > 0) {
                        if (sessionBean.getUser().getId() == selectedObject.getId()) {
                            sessionBean.getUser().setIsAuthorized(selectedObject.isIsAuthorized());
                        }
                        marwiz.goToPage("/pages/system/userdata/userdata.xhtml", null, 1, 3);
                    }
                    sessionBean.createUpdateMessage(result);
                }

            }
        }

    }

    public void createOptionList() {
        selectedOptions.clear();
        importReportOptionsList.clear();
        importReportOptionsList.add(new Option(1, sessionBean.getLoc().getString("cashierreport")));
        importReportOptionsList.add(new Option(2, sessionBean.getLoc().getString("shiftreport")));
        importReportOptionsList.add(new Option(3, sessionBean.getLoc().getString("salereport")));
        importReportOptionsList.add(new Option(4, sessionBean.getLoc().getString("xreport")));
        importReportOptionsList.add(new Option(5, sessionBean.getLoc().getString("zreport")));
        importReportOptionsList.add(new Option(6, sessionBean.getLoc().getString("endofdayreport")));

    }

    public void updateAllInformation() {

        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account acc = new Account(0);
                acc.setName(sessionBean.loc.getString("nott"));
                accountName = sessionBean.loc.getString("nott");
                selectedObject.setAccount(acc);

                selectedObject.setName("");
                selectedObject.setSurname("");
                selectedObject.setMail("");
                selectedObject.setPhone("");
                selectedObject.setCountry(new Country(0));
                citiesAndCountiesBean.updateCity(selectedObject.getCountry(), selectedObject.getCity(), selectedObject.getCounty());
                selectedObject.getCity().setId(0);
                citiesAndCountiesBean.updateCounty(selectedObject.getCountry(), selectedObject.getCity(), selectedObject.getCounty());
                selectedObject.getCounty().setId(0);
                selectedObject.setAddress("");
            } else {
                selectedObject.setAccount(accountBookFilterBean.getSelectedData());
                accountName = selectedObject.getAccount().getName();

                selectedObject.setName(accountBookFilterBean.getSelectedData().getOnlyAccountName());
                selectedObject.setSurname(accountBookFilterBean.getSelectedData().getTitle());
                selectedObject.setMail(accountBookFilterBean.getSelectedData().getEmail());
                selectedObject.setPhone(accountBookFilterBean.getSelectedData().getPhone());
                selectedObject.setCountry(accountBookFilterBean.getSelectedData().getCountry());
                citiesAndCountiesBean.updateCity(selectedObject.getCountry(), selectedObject.getCity(), selectedObject.getCounty());
                selectedObject.getCity().setId(accountBookFilterBean.getSelectedData().getCity().getId());
                citiesAndCountiesBean.updateCounty(selectedObject.getCountry(), selectedObject.getCity(), selectedObject.getCounty());
                selectedObject.getCounty().setId(accountBookFilterBean.getSelectedData().getCounty().getId());
                selectedObject.setAddress(accountBookFilterBean.getSelectedData().getAddress());
            }

            RequestContext.getCurrentInstance().update("frmNewUser:pgrNewUser");
            accountBookFilterBean.setSelectedData(null);
            accountBookFilterBean.isAll = false;
        }

    }

    public void delete() {
        int result = 0;
        result = userDataService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/system/userdata/userdata.xhtml", null, 1, 3);
        }
        sessionBean.createUpdateMessage(result);
    }

}
