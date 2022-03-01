/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 11:43:52
 */
package com.mepsan.marwiz.system.branch.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.business.LeftMenuService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.service.branchinfo.business.IGetBranchInfoService;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

@ManagedBean
@ViewScoped
public class BranchProcessBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{branchSettingTabBean}")
    private BranchSettingTabBean branchSettingTabBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{leftMenu}")
    private LeftMenuService leftMenu;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{getBranchInfoService}")
    public IGetBranchInfoService getBranchInfoService;

    private Branch selectedObject;
    private List<Branch> branchList;//Sistem Şube İşlemlerinden Geldi ise Bu Liste Kullanılır.
    private List<String> listOfDateFormat;
    private int processType, activeIndex;
    private boolean isCentralIntegration;
    private BranchSetting branchSetting;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public Branch getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Branch selectedObject) {
        this.selectedObject = selectedObject;
    }

    public List<String> getListOfDateFormat() {
        return listOfDateFormat;
    }

    public void setListOfDateFormat(List<String> listOfDateFormat) {
        this.listOfDateFormat = listOfDateFormat;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setLeftMenu(LeftMenuService leftMenu) {
        this.leftMenu = leftMenu;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }

    public boolean isIsCentralIntegration() {
        return isCentralIntegration;
    }

    public void setIsCentralIntegration(boolean isCentralIntegration) {
        this.isCentralIntegration = isCentralIntegration;
    }

    public void setGetBranchInfoService(IGetBranchInfoService getBranchInfoService) {
        this.getBranchInfoService = getBranchInfoService;
    }

    public void setBranchSettingTabBean(BranchSettingTabBean branchSettingTabBean) {
        this.branchSettingTabBean = branchSettingTabBean;
    }

    @PostConstruct
    public void init() {

        selectedObject = new Branch();
        branchSetting = new BranchSetting();
        if (marwiz.getPageIdOfGoToPage() == 105) {//Sistem Şube İşlemlerinden Geldi İSe
            branchList = new ArrayList<>();
            branchList = branchService.findAll(" AND br.id = " + sessionBean.getUser().getLastBranch().getId());
            if (branchList.size() > 0) {
                setSelectedObject(branchList.get(0));
            }
            sessionBean.setParameter(selectedObject);
        }

        System.out.println("----------BranchProcessBean----------");
        if (sessionBean.parameter instanceof Branch) {
            processType = 2;
            // activeIndex = 1;
            selectedObject = (Branch) sessionBean.parameter;
            findDateFormat();
            citiesAndCountiesBean.updateCityAndCounty(selectedObject.getCountry(), selectedObject.getCity());

            if (selectedObject.getDateFormat().equals("dd.MM.yyyy")) {
                selectedObject.setDateFormat(listOfDateFormat.get(0));
            } else if (selectedObject.getDateFormat().equals("MM.dd.yyyy")) {
                selectedObject.setDateFormat(listOfDateFormat.get(1));
            } else {
                selectedObject.setDateFormat(listOfDateFormat.get(2));

            }

            if (branchSettingTabBean.getBranchSetting().isIsCentralIntegration()) {
                isCentralIntegration = true;
                branchSetting = branchSettingTabBean.getBranchSetting();
            }
        }

        setListBtn(sessionBean.checkAuthority(new int[]{171, 172}, 0));
        setListTab(sessionBean.checkAuthority(new int[]{49, 76, 75}, 1));

        if (!getListTab().isEmpty()) {
            activeIndex = getListTab().get(marwiz.getTabIndex());
        }

    }

    public void save() {
        if (selectedObject.isIsLicenceCodeCheck() || !isCentralIntegration) {
            int result = 0;

            if (selectedObject.getDateFormat().equals(listOfDateFormat.get(0))) {
                selectedObject.setDateFormat("dd.MM.yyyy");

            } else if (selectedObject.getDateFormat().equals(listOfDateFormat.get(1))) {
                selectedObject.setDateFormat("MM.dd.yyyy");
            } else {
                selectedObject.setDateFormat("yyyy.MM.dd");
            }

            result = branchService.update(selectedObject);

            if (result > 0) {
                applicationBean.refreshBranchSetting();
                if (sessionBean.getUser().getLastBranch().getId() == selectedObject.getId()) {
                    sessionBean.getUser().getLastBranch().setLanguage(selectedObject.getLanguage());
                    sessionBean.getUser().getLastBranch().setCurrency(selectedObject.getCurrency());
                    sessionBean.getUser().getLastBranch().setDecimalsymbol(selectedObject.getDecimalsymbol());
                    sessionBean.getUser().getLastBranch().setCurrencyrounding(selectedObject.getCurrencyrounding());
                    sessionBean.getUser().getLastBranch().setDateFormat(selectedObject.getDateFormat());
                    sessionBean.getUser().getLastBranch().setTitle(selectedObject.getTitle());
                    sessionBean.getUser().getLastBranch().setName(selectedObject.getName());
                    sessionBean.getUser().getLastBranch().setLicenceCode(selectedObject.getLicenceCode());
                    sessionBean.getUser().getLastBranch().setStatus(selectedObject.getStatus());
                    sessionBean.getUser().getLastBranch().setType(selectedObject.getType());
                    sessionBean.getUser().getLastBranch().setTaxNo(selectedObject.getTaxNo());
                    sessionBean.getUser().getLastBranch().setTaxOffice(selectedObject.getTaxOffice());
                    sessionBean.getUser().getLastBranch().setIsAgency(selectedObject.isIsAgency());
                    sessionBean.getUser().getLastBranch().setIsCentral(selectedObject.isIsCentral());

                    sessionBean.getNumberFormat().setMaximumFractionDigits(selectedObject.getCurrencyrounding());
                    sessionBean.getNumberFormat().setMinimumFractionDigits(selectedObject.getCurrencyrounding());
                    DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) sessionBean.getNumberFormat()).getDecimalFormatSymbols();
                    decimalFormatSymbols.setMonetaryDecimalSeparator(selectedObject.getDecimalsymbol() == 1 ? '.' : ',');
                    decimalFormatSymbols.setGroupingSeparator(selectedObject.getDecimalsymbol() == 1 ? ',' : '.');
                    decimalFormatSymbols.setCurrencySymbol("");
                    ((DecimalFormat) sessionBean.getNumberFormat()).setDecimalFormatSymbols(decimalFormatSymbols);
                }

                for (Branch br : sessionBean.getUser().getAuthorizedBranches()) {
                    if (br.getId() == selectedObject.getId()) {
                        sessionBean.getUser().getAuthorizedBranches().remove(br);
                        break;
                    }
                }
                sessionBean.getUser().getAuthorizedBranches().add(selectedObject);

                leftMenu.createBranchs();
                leftMenu.createModules();
                RequestContext.getCurrentInstance().update("sm_leftmenuform");
                RequestContext.getCurrentInstance().execute("$(\".sm_modules > ul\").css(\"display\", \"block\");");
                if (marwiz.getPageIdOfGoToPage() == 21) {//Şubelerden Geldi İse
                    marwiz.goToPage("/pages/system/branch/branch.xhtml", null, 1, 8);
                } else if (marwiz.getPageIdOfGoToPage() == 105) {

                    if (selectedObject.getDateFormat().equals("dd.MM.yyyy")) {
                        selectedObject.setDateFormat(listOfDateFormat.get(0));
                    } else if (selectedObject.getDateFormat().equals("MM.dd.yyyy")) {
                        selectedObject.setDateFormat(listOfDateFormat.get(1));
                    } else {
                        selectedObject.setDateFormat(listOfDateFormat.get(2));

                    }
                }

            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("pleasechecklicencecodetoconfirm")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    /**
     * yeni şube eklerken şubenin tarih formatı comboboxının verileri listeye
     * atılır.
     */
    public void findDateFormat() {
        listOfDateFormat = new ArrayList<>();
        DateFormat dateFormatYear = new SimpleDateFormat("yyyy.MM.dd");
        DateFormat dateFormatMonth = new SimpleDateFormat("MM.dd.yyyy");
        DateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        listOfDateFormat.add(dateFormatDay.format(date));
        listOfDateFormat.add(dateFormatMonth.format(date));
        listOfDateFormat.add(dateFormatYear.format(date));
    }

    public void onTabChange(TabChangeEvent event) {
        activeIndex = Integer.parseInt(event.getTab().getId().substring(3, event.getTab().getId().length()));
        marwiz.settabIndex(getListTab().indexOf(activeIndex));
    }

    public void delete() {
        int result = 0;
        result = branchService.delete(selectedObject);
        if (result > 0) {
            marwiz.goToPage("/pages/system/branch/branch.xhtml", null, 1, 8);
        } else if (result == -100) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecauseithasrelatedrecords")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
        if (result != -100) {
            sessionBean.createUpdateMessage(result);
        }
    }

    public void checkLicenceCode() {
        branchSetting.getBranch().setId(selectedObject.getId());
        branchSetting.getBranch().setLicenceCode(selectedObject.getLicenceCode());
        getBranchInfoService.listBranchInfo(branchSetting);
        selectedObject = branchService.findBranch(selectedObject);
        if (selectedObject.getDateFormat().equals("dd.MM.yyyy")) {
            selectedObject.setDateFormat(listOfDateFormat.get(0));
        } else if (selectedObject.getDateFormat().equals("MM.dd.yyyy")) {
            selectedObject.setDateFormat(listOfDateFormat.get(1));
        } else {
            selectedObject.setDateFormat(listOfDateFormat.get(2));

        }
        RequestContext.getCurrentInstance().update("frmBranchProcess:pgrBranchProcess");
    }

}
