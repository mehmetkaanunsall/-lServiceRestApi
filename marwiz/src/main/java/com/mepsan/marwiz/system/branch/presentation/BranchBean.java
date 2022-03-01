/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   02.02.2018 10:52:13
 */
package com.mepsan.marwiz.system.branch.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.CitiesAndCountiesBean;
import com.mepsan.marwiz.general.core.business.LeftMenuService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

@ManagedBean
@ViewScoped
public class BranchBean extends GeneralDefinitionBean<Branch> {

    @ManagedProperty(value = "#{branchService}")
    private IBranchService branchService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{citiesAndCountiesBean}")
    public CitiesAndCountiesBean citiesAndCountiesBean;

    @ManagedProperty(value = "#{leftMenu}")
    private LeftMenuService leftMenu;

    private List<String> listOfDateFormat;
    private String decimalSymbol;
    private Authorize authorize;
    private int processType;
    private boolean isCentralIntegration;

    public void setBranchService(IBranchService branchService) {
        this.branchService = branchService;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public List<String> getListOfDateFormat() {
        return listOfDateFormat;
    }

    public void setListOfDateFormat(List<String> listOfDateFormat) {
        this.listOfDateFormat = listOfDateFormat;
    }

    public String getDecimalSymbol() {
        return decimalSymbol;
    }

    public void setDecimalSymbol(String decimalSymbol) {
        this.decimalSymbol = decimalSymbol;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setCitiesAndCountiesBean(CitiesAndCountiesBean citiesAndCountiesBean) {
        this.citiesAndCountiesBean = citiesAndCountiesBean;
    }

    public Authorize getAuthorize() {
        return authorize;
    }

    public void setAuthorize(Authorize authorize) {
        this.authorize = authorize;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setLeftMenu(LeftMenuService leftMenu) {
        this.leftMenu = leftMenu;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public boolean isIsCentralIntegration() {
        return isCentralIntegration;
    }

    public void setIsCentralIntegration(boolean isCentralIntegration) {
        this.isCentralIntegration = isCentralIntegration;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------BranchBean--------");
        selectedObject = new Branch();
        listOfObjects = findall();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true);
        isCentralIntegration = false;

        setListBtn(sessionBean.checkAuthority(new int[]{170, 171}, 0));
    }

    @Override
    public List<Branch> findall() {
        return branchService.findAll("");
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

    @Override
    public void create() {
        processType = 1;

        authorize = new Authorize();
        selectedObject = new Branch();
        findDateFormat();
        RequestContext.getCurrentInstance().execute("PF('dlg_BranchProcess').show();");
    }

    @Override
    public void save() {
        int result = 0;

        if (selectedObject.getDateFormat().equals(listOfDateFormat.get(0))) {
            selectedObject.setDateFormat("dd.MM.yyyy");

        } else if (selectedObject.getDateFormat().equals(listOfDateFormat.get(1))) {
            selectedObject.setDateFormat("MM.dd.yyyy");
        } else {
            selectedObject.setDateFormat("yyyy.MM.dd");
        }

        selectedObject.getListOfAuthorizes().add(authorize);
        result = branchService.create(selectedObject);
        if (result > 0) {
            selectedObject.setId(result);
            applicationBean.refreshBranchSetting();
            sessionBean.getUser().getAuthorizedBranches().add(selectedObject);
            listOfObjects.add(selectedObject);

            RequestContext.getCurrentInstance().execute("PF('dlg_BranchProcess').hide();");
            leftMenu.createBranchs();
            leftMenu.createModules();
            RequestContext.getCurrentInstance().update("sm_leftmenuform");
            RequestContext.getCurrentInstance().execute("$(\".sm_modules > ul\").css(\"display\", \"block\");");

            marwiz.goToPage("/pages/system/branch/branchprocess.xhtml", selectedObject, 1, 21);

        }

        sessionBean.createUpdateMessage(result);

    }

}
