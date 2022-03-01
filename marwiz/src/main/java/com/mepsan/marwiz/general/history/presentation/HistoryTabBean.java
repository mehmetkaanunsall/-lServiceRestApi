/**
 * Bu sınıf ...
 *
 *
 * @author SALİM VELA ABDULHADİ
 *
 * @date   21.10.2016 09:54:38
 */
package com.mepsan.marwiz.general.history.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.IHistoryService;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean
@RequestScoped
public class HistoryTabBean extends GeneralDefinitionBean<History> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{historyService}")
    public IHistoryService historyService;

    private String createdPerson;
    private String createdDate;
    private int rowId;
    private String tableName;
    private String createWhere = "";

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------HistoryTabBean----------");
        FacesContext fcontext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fcontext.getExternalContext().getRequest();

        rowId = ((Integer) request.getAttribute("rowId"));

        createdPerson = (String) request.getAttribute("createdPerson");
        tableName = (String) request.getAttribute("tableName");
        if (request.getAttribute("createdTime") != null) {
            createdDate = request.getAttribute("createdTime").toString();
        }

        if (tableName.equals("inventory.stock")) {
            createWhere = " AND stcc.branch_id = " + sessionBean.getUser().getLastBranch().getId();

        } else {
            createWhere = "";
        }

        listOfObjects = findall();
        listOfFilteredObjects = new ArrayList<>();
        listOfFilteredObjects.addAll(listOfObjects);
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
    public List<History> findall() {
        List<History> result = historyService.findAll(0, 0, null, createWhere, rowId, tableName, marwiz.getPageIdOfGoToPage());
        return result;
    }

}
