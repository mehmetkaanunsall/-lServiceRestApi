/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.01.2020 11:22:45
 */
package com.mepsan.marwiz.inventory.automationdevicefilling.presentation;

import com.mepsan.marwiz.general.model.inventory.AutomationDevice;
import com.mepsan.marwiz.general.model.inventory.AutomationDeviceItemMovement;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.automationdevicefilling.business.IAutomationDeviceMovementService;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class AutomationDeviceFillingMovementTabBean extends GeneralReportBean<AutomationDeviceItemMovement> {

    @ManagedProperty(value = "#{automationDeviceMovementService}")
    public IAutomationDeviceMovementService automationDeviceMovementService;

    private Date beginDate, endDate;
    private AutomationDevice automationDevice;
    private String createWhere;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public AutomationDevice getAutomationDevice() {
        return automationDevice;
    }

    public void setAutomationDevice(AutomationDevice automationDevice) {
        this.automationDevice = automationDevice;
    }

    public void setAutomationDeviceMovementService(IAutomationDeviceMovementService automationDeviceMovementService) {
        this.automationDeviceMovementService = automationDeviceMovementService;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("----------------------AutomationDeviceFillingMovementTabBean");
        Calendar calendar = GregorianCalendar.getInstance();
        automationDevice = new AutomationDevice();
        setEndDate(new Date());
        calendar.setTime(getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        setEndDate(calendar.getTime());

        calendar.setTime(getEndDate());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        setBeginDate(calendar.getTime());

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        AutomationDeviceFillingBean automationDeviceFillingBean = (AutomationDeviceFillingBean) viewMap.get("automationDeviceFillingBean");
        if (automationDeviceFillingBean != null) {
            automationDevice = automationDeviceFillingBean.getSelectedObject();
        }

        find();
        toogleList = Arrays.asList(true, true, true, true, true, true);
        selectedObject = new AutomationDeviceItemMovement();

    }

    @Override
    public void find() {
        isFind = true;
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmMovementDataTable:dtbMovement");

        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        createWhere = automationDeviceMovementService.createWhere(automationDevice, beginDate, endDate);
        listOfObjects = findall(createWhere);
    }

    @Override
    public LazyDataModel<AutomationDeviceItemMovement> findall(String where) {
        return new CentrowizLazyDataModel<AutomationDeviceItemMovement>() {
            @Override
            public List<AutomationDeviceItemMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<AutomationDeviceItemMovement> result = automationDeviceMovementService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = automationDeviceMovementService.count(where);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
