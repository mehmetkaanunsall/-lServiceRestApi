/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.note.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.note.business.IUserDataNoteService;
import com.mepsan.marwiz.general.note.dao.UserDataNote;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DashboardColumn;

/**
 *
 * @author Gozde Gursel
 */
@ManagedBean
@ViewScoped
public class NoteBean {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{userDataNoteService}") // session
    public IUserDataNoteService userDataNoteService;

    public UserDataNote selectedNote;
    public DefaultDashboardModel model;
    public List<UserDataNote> userNoteList;
    private int processType = 0;
    private String dateFormat;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setUserDataNoteService(IUserDataNoteService userDataNoteService) {
        this.userDataNoteService = userDataNoteService;
    }

    public UserDataNote getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(UserDataNote selectedNote) {
        this.selectedNote = selectedNote;
    }

    public DefaultDashboardModel getModel() {
        return model;
    }

    public void setModel(DefaultDashboardModel model) {
        this.model = model;
    }

    public List<UserDataNote> getUserNoteList() {
        return userNoteList;
    }

    public void setUserNoteList(List<UserDataNote> userNoteList) {
        this.userNoteList = userNoteList;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @PostConstruct
    public void init() {
        System.out.println("**NoteBean***");
        userNoteList = new ArrayList<>();
        selectedNote = new UserDataNote();
        model = new DefaultDashboardModel();

        userNoteList = userDataNoteService.find();

        for (UserDataNote userDataNote : userNoteList) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            dateFormat = formatter.format(userDataNote.getDateCreated());

            userDataNote.setFormatDate(dateFormat);
        }
        for (int i = 0; i < userNoteList.size(); i++) {
            int remaining = i % 10;
            userNoteList.get(i).setItemValue(remaining);
        }

        DefaultDashboardColumn defaultDashboardColumn = null;
        for (int i = 0; i < 4; i++) {
            defaultDashboardColumn = new DefaultDashboardColumn();
            defaultDashboardColumn.setStyleClass("Container25 Responsive");
            model.addColumn(defaultDashboardColumn);
        }
        transferListtoColumns(); //listeyi kolomlara ekler.
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("dshWidget");
    }

    public void transferListtoColumns() {
        DashboardColumn column;
        for (int j = 0; j < userNoteList.size(); j++) {
            switch (j % 4) {
                case 0:
                    column = model.getColumn(0);
                    column.addWidget("welcome" + String.valueOf(userNoteList.get(j).getId()));
                    break;

                case 1:
                    column = model.getColumn(1);
                    column.addWidget("welcome" + String.valueOf(userNoteList.get(j).getId()));
                    break;

                case 2:
                    column = model.getColumn(2);
                    column.addWidget("welcome" + String.valueOf(userNoteList.get(j).getId()));
                    break;

                case 3:
                    column = model.getColumn(3);
                    column.addWidget("welcome" + String.valueOf(userNoteList.get(j).getId()));
                    break;

            }

        }
    }

    public void save() throws ParseException {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;

        if (processType == 2) { // güncelleme ise
            result = userDataNoteService.update(selectedNote);
        } else {// ekleme ise 
            result = userDataNoteService.create(selectedNote);
        }
        if (result > 0) {
            if (processType == 2) {
                context.update("welcome" + selectedNote.getId());
            } else {
                userNoteList = userDataNoteService.find();

                for (UserDataNote userDataNote : userNoteList) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    dateFormat = formatter.format(userDataNote.getDateCreated());

                    userDataNote.setFormatDate(dateFormat);

                }
                DashboardColumn column;
                switch (userNoteList.size() % 4) {
                    case 0:
                        column = model.getColumn(3);
                        column.addWidget("welcome" + String.valueOf(userNoteList.get(userNoteList.size() - 1).getId()));
                        break;

                    case 1:
                        column = model.getColumn(0);
                        column.addWidget("welcome" + String.valueOf(userNoteList.get(userNoteList.size() - 1).getId()));
                        break;

                    case 2:
                        column = model.getColumn(1);
                        column.addWidget("welcome" + String.valueOf(userNoteList.get(userNoteList.size() - 1).getId()));
                        break;

                    case 3:
                        column = model.getColumn(2);
                        column.addWidget("welcome" + String.valueOf(userNoteList.get(userNoteList.size() - 1).getId()));
                        break;

                }
                context.update("dshWidget");
            }
            for (int i = 0; i < userNoteList.size(); i++) {
                int remaining = i % 10;
                userNoteList.get(i).setItemValue(remaining);
            }
            context.update("dshWidget");
            context.execute("PF('dlg_notes').hide()");

        }

        sessionBean.createUpdateMessage(result); // result değerine göre bildirim gösteriliir.
    }

    /**
     * Bu metot not bilgisi silinmek istenildiğinde çalışır.
     *
     * @param event
     */
    public void delete(ActionEvent event) {
        int result = 0;
        int widgetId = (int) event.getComponent().getAttributes().get("widgetid");
        for (int i = 0; i < model.getColumnCount(); i++) {
            DashboardColumn column = model.getColumn(i);
            for (int j = 0; j < column.getWidgetCount(); j++) {
                String s = column.getWidget(j).substring(7, column.getWidget(j).length());
                if (Integer.parseInt(s) == widgetId) {
                    column.removeWidget(s);
                }
            }
        }
        for (UserDataNote userWidget : userNoteList) {
            if (userWidget.getId() == widgetId) {
                result = userDataNoteService.deleteNote(widgetId);
            }
//            } else {
//                widgets.add("widget" + String.valueOf(userWidget.getId()));
//            }
        }
        if (result > 0) {
            for (Iterator<UserDataNote> iterator = userNoteList.iterator(); iterator.hasNext();) {
                UserDataNote value = iterator.next();
                if (value.getId() == widgetId) {
                    iterator.remove();
                }
            }

            RequestContext context = RequestContext.getCurrentInstance();
            context.update("dshWidget");
        }
        sessionBean.createUpdateMessage(result);

    }

    public void delete() {

    }

    /**
     * Bu metot eklenen not bilgisini dialog üzerinde göstermek için kullanılır.
     *
     * @param event
     */
    public void showDialog(ActionEvent event) {
        int widgetId = (int) event.getComponent().getAttributes().get("widgetid");
        processType = 2;
        for (UserDataNote userDataNote : userNoteList) {
            if (userDataNote.getId() == widgetId) {
                setSelectedNote(userDataNote);
            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_notes').show()");
        context.update("dlgNotes");

    }

    public void clearData() {
        selectedNote = new UserDataNote();
        processType = 1;

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_notes').show()");
        context.update("dlgNotes");
    }

}
