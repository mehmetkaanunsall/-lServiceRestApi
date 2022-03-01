/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 8:46:14 AM
 */
package com.mepsan.marwiz.automat.washingmachicneshift.presentation;

import com.mepsan.marwiz.automat.washingmachicneshift.business.GFWashingMachicneShiftService;
import com.mepsan.marwiz.automat.washingmachicneshift.business.IWashingMachicneShiftService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automat.AutomatShift;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class WashingMachicneShiftBean extends GeneralBean<AutomatShift> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{washingMachicneShiftService}")
    public IWashingMachicneShiftService washingMachicneShiftService;

    @ManagedProperty(value = "#{gfWashingMachicneShiftService}")
    public GFWashingMachicneShiftService gfWashingMachicneShiftService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private boolean haveOpenShift;
    private AutomatShift selectedConfirmShift;
    private AutomatShift openShift;
    private AutomatShift newShift;
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setWashingMachicneShiftService(IWashingMachicneShiftService washingMachicneShiftService) {
        this.washingMachicneShiftService = washingMachicneShiftService;
    }

    public void setGfWashingMachicneShiftService(GFWashingMachicneShiftService gfWashingMachicneShiftService) {
        this.gfWashingMachicneShiftService = gfWashingMachicneShiftService;
    }

    public boolean isHaveOpenShift() {
        return haveOpenShift;
    }

    public void setHaveOpenShift(boolean haveOpenShift) {
        this.haveOpenShift = haveOpenShift;
    }

    public AutomatShift getSelectedConfirmShift() {
        return selectedConfirmShift;
    }

    public void setSelectedConfirmShift(AutomatShift selectedConfirmShift) {
        this.selectedConfirmShift = selectedConfirmShift;
    }

    public AutomatShift getOpenShift() {
        return openShift;
    }

    public void setOpenShift(AutomatShift openShift) {
        this.openShift = openShift;
    }

    public AutomatShift getNewShift() {
        return newShift;
    }

    public void setNewShift(AutomatShift newShift) {
        this.newShift = newShift;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("=======WashingMachicneShiftBean=======");

        toogleList = createToggleList(sessionBean.getUser());

        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true);
        }
        listOfObjects = findall("");
        newShift = new AutomatShift();
        openShift = new AutomatShift();
        selectedConfirmShift = new AutomatShift();
        
        setListBtn(sessionBean.checkAuthority(new int[]{55}, 0));

    }

    @Override
    public void create() {
        newShift = new AutomatShift();
        openShift = new AutomatShift();
        message = "";
        openShift = washingMachicneShiftService.controlOpenShift();
        if (openShift.getId() == 0) {//Açık vardiya yoksa
            message = sessionBean.loc.getString("areyousureopennewshift");
            haveOpenShift = false;
            RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').show();");
        } else if (openShift.getId() > 0) {//Açık Vardiya Varsa
            message = sessionBean.loc.getString("areyousureclosethisshiftandopennewshift");
            haveOpenShift = true;
            RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').show();");
        }
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        String where = " ";
        if (autoCompleteValue == null) {
            listOfObjects = findall(where);
        } else {
            gfWashingMachicneShiftService.makeSearch(where, autoCompleteValue);
            listOfObjects = gfWashingMachicneShiftService.searchResult;
        }
    }

    @Override
    public LazyDataModel<AutomatShift> findall(String where) {
        return new CentrowizLazyDataModel<AutomatShift>() {
            @Override
            public List<AutomatShift> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                
                List<AutomatShift> result = washingMachicneShiftService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = washingMachicneShiftService.count(where);
                listOfObjects.setRowCount(count);
               
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void pressYesButtonForNewShift() {
        int result = 0;
        newShift.getStatus().setId(53); // aktif olarak status eklenir.
        result = washingMachicneShiftService.create(newShift);
        if (result > 0) {
            newShift.setId(result);
            RequestContext.getCurrentInstance().execute("PF('dlgNewShiftConfirm').hide();");
            RequestContext.getCurrentInstance().update("frmWashingMachicneShift:dtbWashingMachicneShift");
        }
    }

    public void goToShiftDetail() {
        marwiz.goToPage("/pages/automat/washingmachicneshift/washingmachicneshiftdetailreport.xhtml", selectedConfirmShift, 0, 134);
    }
}
