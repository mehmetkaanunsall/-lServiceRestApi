/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.authorize.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Button;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.admin.Tab;
import com.mepsan.marwiz.general.model.general.Authorize;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.system.authorize.dao.AuthorizeHistory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;

/**
 *
 * @author sinem.arslan
 */
@ManagedBean
@ViewScoped
public class AuthorizeHistoryTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    private History selectedHistoryDetail;
    private SimpleDateFormat dateFormat;
    private List<AuthorizeHistory> listOfAllValueIds; //yetkiler tarihçesindeki eklenen ya da silinen değerlerin idlerini tutan liste
    private AuthorizeHistory selectedHistory;
    private Authorize selectedAuthorize;

    public History getSelectedHistoryDetail() {
        return selectedHistoryDetail;
    }

    public void setSelectedHistoryDetail(History selectedHistoryDetail) {
        this.selectedHistoryDetail = selectedHistoryDetail;
    }

    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(SimpleDateFormat dateFormat) {
        this.dateFormat = dateFormat;
    }

    public List<AuthorizeHistory> getListOfAllValueIds() {
        return listOfAllValueIds;
    }

    public void setListOfAllValueIds(List<AuthorizeHistory> listOfAllValueIds) {
        this.listOfAllValueIds = listOfAllValueIds;
    }

    public AuthorizeHistory getSelectedHistory() {
        return selectedHistory;
    }

    public void setSelectedHistory(AuthorizeHistory selectedHistory) {
        this.selectedHistory = selectedHistory;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public Authorize getSelectedAuthorize() {
        return selectedAuthorize;
    }

    public void setSelectedAuthorize(Authorize selectedAuthorize) {
        this.selectedAuthorize = selectedAuthorize;
    }

    @PostConstruct
    public void init() {

        System.out.println("--------AuthorizeHistoryTabBean----------");
        if (sessionBean.parameter instanceof Authorize) {
            selectedAuthorize = (Authorize) sessionBean.parameter;
        }
        selectedHistoryDetail = new History();
        listOfAllValueIds = new ArrayList<>();
        selectedHistory = new AuthorizeHistory();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    }

    public void goToHistoryDetail() {

        listOfAllValueIds.clear();
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmHistoryDetail:dtbHistoryDetail");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        String oldValueIds = "";
        String newValueIds = "";
        List<String> oldList = new ArrayList<>();
        List<String> newList = new ArrayList<>();

        if (!selectedHistoryDetail.getOldValue().equals("")) {
            String[] oldvalue = selectedHistoryDetail.getOldValue().split(",");
            for (int j = 0; j < oldvalue.length; j++) {
                oldList.add(oldvalue[j]);
            }
        }
        if (!selectedHistoryDetail.getNewValue().equals("")) {
            String[] newvalue = selectedHistoryDetail.getNewValue().split(",");
            for (int k = 0; k < newvalue.length; k++) {
                newList.add(newvalue[k]);
            }
        }
        oldValueIds = controlList(oldList, newList);
        newValueIds = controlList(newList, oldList);

        String[] oldIds = oldValueIds.split(",");
        String[] newIds = newValueIds.split(",");
        if (!oldValueIds.equals("")) {
            for (int i = 0; i < oldIds.length; i++) {
                if (selectedHistoryDetail.getColumnName().equals("ga.name")) {
                    selectedHistory = new AuthorizeHistory();
                    selectedHistory.setName(oldIds[i]);
                    selectedHistory.setValueName(sessionBean.getLoc().getString("oldvalue"));
                    listOfAllValueIds.add(selectedHistory);
                } else {
                    selectedHistory = new AuthorizeHistory();
                    selectedHistory.setId(Integer.parseInt(oldIds[i]));
                    selectedHistory.setValueName(sessionBean.getLoc().getString("deleted"));
                    listOfAllValueIds.add(selectedHistory);
                }
            }
        }
        if (!newValueIds.equals("")) {
            for (int j = 0; j < newIds.length; j++) {
                if (selectedHistoryDetail.getColumnName().equals("ga.name")) {
                    selectedHistory = new AuthorizeHistory();
                    selectedHistory.setName(newIds[j]);
                    selectedHistory.setValueName(sessionBean.getLoc().getString("newvalue"));
                    listOfAllValueIds.add(selectedHistory);
                } else {
                    selectedHistory = new AuthorizeHistory();
                    selectedHistory.setId(Integer.parseInt(newIds[j]));
                    selectedHistory.setValueName(sessionBean.getLoc().getString("inserted"));
                    listOfAllValueIds.add(selectedHistory);
                }
            }
        }

        if (selectedHistoryDetail.getColumnName().equals("ga.modules")) {
            for (Module m : applicationBean.getListOfModules()) {
                for (AuthorizeHistory module : listOfAllValueIds) {
                    if (module.getId() == m.getId()) {
                        module.setName(m.getNameMap().get(sessionBean.getLangId()).getName());
                    }
                }
            }
        } else if (selectedHistoryDetail.getColumnName().equals("ga.folders")) {
            for (Module m : applicationBean.getListOfModules()) {
                if (m.getFolders() != null) {
                    for (Folder mn : m.getFolders()) {
                        for (AuthorizeHistory folder : listOfAllValueIds) {
                            if (folder.getId() == mn.getId()) {
                                folder.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName());
                            }
                        }
                    }
                }
            }
        } else if (selectedHistoryDetail.getColumnName().equals("ga.pages")) {
            for (Module m : applicationBean.getListOfModules()) {
                if (m.getFolders() != null) {
                    for (Folder mn : m.getFolders()) {
                        if (mn.getPages() != null) {
                            for (Page p : mn.getPages()) {
                                for (AuthorizeHistory page : listOfAllValueIds) {
                                    if (page.getId() == p.getId()) {
                                        page.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p.getNameMap().get(sessionBean.getLangId()).getName());
                                    }
                                }
                                if (p.getSubPages() != null) {
                                    for (Page p1 : p.getSubPages()) {
                                        for (AuthorizeHistory spage : listOfAllValueIds) {
                                            if (spage.getId() == p1.getId()) {
                                                spage.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p1.getNameMap().get(sessionBean.getLangId()).getName());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedHistoryDetail.getColumnName().equals("ga.tabs")) {
            for (Module m : applicationBean.getListOfModules()) {
                if (m.getFolders() != null) {
                    for (Folder mn : m.getFolders()) {
                        if (mn.getPages() != null) {
                            for (Page p : mn.getPages()) {
                                if (p.getTabs() != null) {
                                    for (Tab t : p.getTabs()) {
                                        for (AuthorizeHistory tab : listOfAllValueIds) {
                                            if (tab.getId() == t.getId()) {
                                                tab.setName(mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p.getNameMap().get(sessionBean.getLangId()).getName() + "/" + t.getNameMap().get(sessionBean.getLangId()).getName());
                                            }
                                        }
                                    }
                                }
                                if (p.getSubPages() != null) {
                                    for (Page p1 : p.getSubPages()) {
                                        if (p1.getTabs() != null) {
                                            for (Tab t1 : p1.getTabs()) {
                                                for (AuthorizeHistory stab : listOfAllValueIds) {
                                                    if (stab.getId() == t1.getId()) {
                                                        stab.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p1.getNameMap().get(sessionBean.getLangId()).getName() + "/" + t1.getNameMap().get(sessionBean.getLangId()).getName());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedHistoryDetail.getColumnName().equals("ga.buttons")) {
            for (Module m : applicationBean.getListOfModules()) {
                if (m.getFolders() != null) {
                    for (Folder mn : m.getFolders()) {
                        if (mn.getPages() != null) {
                            for (Page p : mn.getPages()) {
                                if (p.getButtons() != null) {
                                    for (Button b : p.getButtons()) {
                                        for (AuthorizeHistory btn : listOfAllValueIds) {
                                            if (btn.getId() == b.getId()) {
                                                btn.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p.getNameMap().get(sessionBean.getLangId()).getName() + "/" + btnNameToPropertiesFile(b.getName()));
                                            }
                                        }
                                    }
                                }

                                if (p.getTabs() != null) {
                                    for (Tab t : p.getTabs()) {
                                        if (t.getListOfButtons() != null) {
                                            for (Button bb : t.getListOfButtons()) {
                                                for (AuthorizeHistory sbtnn : listOfAllValueIds) {
                                                    if (sbtnn.getId() == bb.getId()) {
                                                        sbtnn.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p.getNameMap().get(sessionBean.getLangId()).getName() + "/" + t.getNameMap().get(sessionBean.getLangId()).getName() + "/" + btnNameToPropertiesFile(bb.getName()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (p.getSubPages() != null) {
                                    for (Page p1 : p.getSubPages()) {
                                        if (p1.getTabs() != null) {
                                            for (Tab t1 : p1.getTabs()) {
                                                if (t1.getListOfButtons() != null) {
                                                    for (Button b1 : t1.getListOfButtons()) {
                                                        for (AuthorizeHistory sbtn : listOfAllValueIds) {
                                                            if (sbtn.getId() == b1.getId()) {
                                                                sbtn.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p1.getNameMap().get(sessionBean.getLangId()).getName() + "/" + t1.getNameMap().get(sessionBean.getLangId()).getName() + "/" + btnNameToPropertiesFile(b1.getName()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (p1.getButtons() != null) {
                                            for (Button b2 : p1.getButtons()) {
                                                for (AuthorizeHistory sbtn1 : listOfAllValueIds) {
                                                    if (sbtn1.getId() == b2.getId()) {
                                                        sbtn1.setName(m.getNameMap().get(sessionBean.getLangId()).getName() + "/" + mn.getNameMap().get(sessionBean.getLangId()).getName() + "/" + p1.getNameMap().get(sessionBean.getLangId()).getName() + "/" + btnNameToPropertiesFile(b2.getName()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        RequestContext.getCurrentInstance().update("dlgHistoryDetail");
        RequestContext.getCurrentInstance().execute("PF('dlg_historyDetail').show();");
    }

    public String controlList(List<String> oldList, List<String> newList) {

        String Ids = "";
        boolean isthere;

        for (String old : oldList) {
            isthere = false;
            for (String nw : newList) {
                if (old.equals(nw)) {
                    isthere = true;
                }
            }
            if (!isthere) {
                if (Ids.equals("")) {
                    Ids = String.valueOf(old);
                } else {
                    Ids = Ids + "," + String.valueOf(old);
                }
            }
        }
        return Ids;
    }

    public String btnNameToPropertiesFile(String name) {
        return sessionBean.loc.getString(name);
    }

}
