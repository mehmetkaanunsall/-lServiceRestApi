/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 08.02.2019 15:45:16
 */
package com.mepsan.marwiz.automation.nozzle.presentation;

import com.mepsan.marwiz.automation.nozzle.business.GFNozzleService;
import com.mepsan.marwiz.automation.nozzle.business.INozzleService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.Nozzle;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
public class NozzleBean extends GeneralBean<Nozzle> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{nozzleService}")
    public INozzleService nozzleService;

    @ManagedProperty(value = "#{gfNozzleService}")
    private GFNozzleService gfNozzleService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private String createWhere;
    private Date beginDate, endDate;
    private String formId;
    private boolean pageFromTank;//Tanlardan veya Ana Sayfadan Gelindiğini Anlamamız İçin Kullanılır.

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setNozzleService(INozzleService nozzleService) {
        this.nozzleService = nozzleService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setGfNozzleService(GFNozzleService gfNozzleService) {
        this.gfNozzleService = gfNozzleService;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------NozzleBean");
        pageFromTank = false;
        createWhere = " ";
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Warehouse) {
                    Warehouse warehouse = (Warehouse) ((ArrayList) sessionBean.parameter).get(i);
                    createWhere = " AND nz.warehouse_id =  " + warehouse.getId() + " ";
                    formId = "tbvTankProc:";
                    pageFromTank = true;
                }
            }
        }

        listOfObjects = findall(createWhere);
        selectedObject = new Nozzle();

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);
        setListBtn(sessionBean.checkAuthority(new int[]{167}, 0));
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
    public void create() {
        int type = 0;
        List<Object> list = new ArrayList<>();
        if (pageFromTank) {
            list = (ArrayList) sessionBean.parameter;
        }
        System.out.println("Typeeee = " + type);
        marwiz.goToPage("/pages/automation/nozzle/nozzleprocess.xhtml", list, 1, 114);

    }

    public void checckBeforeMove() {
        if (pageFromTank) {//TankLardan Gelindi İse Efekt Geçiş Olunmasını Sağlar
            if (marwiz.checckBeforeMove(null, 114)) {
                RequestContext.getCurrentInstance().execute("Centrowiz.panelEffect('#mainPanel','left');");
            }
        }

    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generalFilter() {
        if (autoCompleteValue == null) {
            listOfObjects = findall(createWhere);
        } else {
            gfNozzleService.makeSearch(autoCompleteValue, createWhere);
            listOfObjects = gfNozzleService.searchResult;
        }
    }

    public void find() {
        listOfObjects = findall(createWhere);
        RequestContext.getCurrentInstance().update(formId + "frmNozzle:dtbNozzle");
    }

    @Override
    public LazyDataModel<Nozzle> findall(String where) {
        return new CentrowizLazyDataModel<Nozzle>() {
            @Override
            public List<Nozzle> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Nozzle> result = nozzleService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = nozzleService.count(where);
                listOfObjects.setRowCount(count);
                // RequestContext.getCurrentInstance().execute("count=" + count + ";");
                DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(formId + "frmNozzle:dtbNozzle");
                if (dataTable != null) {
                    dataTable.setFirst(0);
                }
                return result;
            }

        };
    }

    public void goToProcess() {
        List<Object> list = new ArrayList<>();
        if (sessionBean.getParameter() != null) {
            list.addAll((ArrayList) sessionBean.getParameter());
        }
        list.add(selectedObject);
        marwiz.goToPage("/pages/automation/nozzle/nozzleprocess.xhtml", list, 1, 114);
    }

    public void showNozzleMovements(Nozzle nozzle) {
        setSelectedObject(nozzle);
        RequestContext context = RequestContext.getCurrentInstance();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Nozzle) {//Tabanca ise
                    ((ArrayList) sessionBean.parameter).remove(((ArrayList) sessionBean.parameter).get(i));
                    System.out.println("Silindi=");
                }
            }
        }
        List<Object> list = new ArrayList<>();
        if (sessionBean.getParameter() != null) {
            list.addAll((ArrayList) sessionBean.getParameter());
        }
        list.add(selectedObject);
        sessionBean.setParameter(list);
        System.out.println("list.size()===" + list.size());
        System.out.println("nozzle===" + nozzle.getId());

        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        NozzleMovementBean nozzleMovementBean = (NozzleMovementBean) viewMap.get("nozzleMovementBean");
        context.update("dlgNozzleDetailDtb");
        context.execute("PF('nozzleDetailPF').show();");
        if (nozzleMovementBean != null) {
            nozzleMovementBean.init();
        }

    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
