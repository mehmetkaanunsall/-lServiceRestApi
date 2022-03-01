/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 04.02.2019 17:09:33
 */
package com.mepsan.marwiz.automation.tank.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.automation.tank.business.ITankMovementTabService;
import com.mepsan.marwiz.automation.tank.dao.TankMovement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class TankMovementTabBean extends GeneralBean<TankMovement> {

    @ManagedProperty(value = "#{tankMovementTabService}")
    public ITankMovementTabService tankMovementTabService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    private Date beginDate, endDate;
    private int opType;
    private Warehouse selectedWarehouse;

    public Warehouse getSelectedWarehouse() {
        return selectedWarehouse;
    }

    public void setSelectedWarehouse(Warehouse selectedWarehouse) {
        this.selectedWarehouse = selectedWarehouse;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setTankMovementTabService(ITankMovementTabService tankMovementTabService) {
        this.tankMovementTabService = tankMovementTabService;
    }

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

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("Tank Movement Tab Bean");

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Warehouse) {//Tabanca Sayfasından Geri Geldi İse
                    selectedWarehouse = (Warehouse) ((ArrayList) sessionBean.parameter).get(i);
                    Calendar calendar = GregorianCalendar.getInstance();
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

                    setOpType(3);

                    find();
                    toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                    break;
                }
            }
        }
       
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<TankMovement> findall(String where) {
        return new CentrowizLazyDataModel<TankMovement>() {
            @Override
            public List<TankMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<TankMovement> result = tankMovementTabService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, opType, beginDate, endDate, selectedWarehouse);

                int count = tankMovementTabService.count(where, opType, beginDate, endDate, selectedWarehouse);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void find() {

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("tbvTankProc:dtbItems");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        listOfObjects = findall(" ");

    }

    public void createExcel() {

        System.out.println("Export Excell");
        tankMovementTabService.exportExcel(selectedWarehouse, toogleList, "", opType, beginDate, endDate);

    }
}
