/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 18:26:18
 */
package com.mepsan.marwiz.automation.nozzle.presentation;

import com.mepsan.marwiz.automation.nozzle.business.INozzleMovementService;
import com.mepsan.marwiz.automation.nozzle.dao.NozzleMovement;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import java.util.ArrayList;
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
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class NozzleMovementBean extends GeneralBean<NozzleMovement> {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{nozzleMovementService}")
    public INozzleMovementService nozzleMovementService;

    private Date beginDate, endDate;
    private Nozzle selectedNozzle;
    private String createWhere;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setNozzleMovementService(INozzleMovementService nozzleMovementService) {
        this.nozzleMovementService = nozzleMovementService;
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

    @Override
    @PostConstruct
    public void init() {
        System.out.println("--------------------Nozzle Movement Bean---------------------------");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Nozzle) {//Tabanca ise
                    selectedNozzle = (Nozzle) ((ArrayList) sessionBean.parameter).get(i);
                    System.out.println("Tabanca Id = " + selectedNozzle.getId());

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
                    toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true);

                    find();

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
    public LazyDataModel<NozzleMovement> findall(String where) {

        return new CentrowizLazyDataModel<NozzleMovement>() {
            @Override
            public List<NozzleMovement> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<NozzleMovement> result = nozzleMovementService.findAll(first, pageSize, sortField, sortOrder.toString(), filters, where);
                int count = nozzleMovementService.count(where);
                listOfObjects.setRowCount(count);
                return result;
            }
        };
    }

    @Override
    public void detailFilter() {
    }

    public void find() {
        createWhere = nozzleMovementService.createWhere(beginDate, endDate, selectedNozzle);
        listOfObjects = findall(createWhere);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("tbvNozzleProc:frmNozzleMovementDatatable:dtbMovement");
        System.out.println("createWhere="+createWhere);

    }

}
