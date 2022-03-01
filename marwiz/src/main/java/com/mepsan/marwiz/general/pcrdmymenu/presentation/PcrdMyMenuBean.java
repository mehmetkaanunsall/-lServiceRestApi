/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   17.10.2016 11:11:56
 */
package com.mepsan.marwiz.general.pcrdmymenu.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.UserDataMenuConnection;
import com.mepsan.marwiz.general.pcrdmymenu.business.IPcrdMyMenuService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.primefaces.component.commandlink.CommandLink;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.outputlabel.OutputLabel;
import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.component.panel.Panel;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

@ManagedBean
@ViewScoped
public class PcrdMyMenuBean {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{pcrdMyMenuService}")
    private IPcrdMyMenuService pcrdMyMenuService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private int moduleId, folderId, pageId;
    private Module selectedModule;
    private Folder selectedFolder;
    private List<Folder> selectedFolderList;
    private List<Page> selectedPageList;
    private List<UserDataMenuConnection> listOfObjects;
    private UserDataMenuConnection selectedObject;
    private String color;
    private String icon;
    private List<Integer> pages;
    private List<String> iconList;
    private int folderType;
    private int deletePageId;

    private DashboardModel model;

    public List<String> getIconList() {
        return iconList;
    }

    public void setIconList(List<String> iconList) {
        this.iconList = iconList;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public List<UserDataMenuConnection> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<UserDataMenuConnection> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public int getFolderType() {
        return folderType;
    }

    public void setFolderType(int folderType) {
        reset();
        this.folderType = folderType;
    }

    public void setPcrdMyMenuService(IPcrdMyMenuService pcrdMyMenuService) {
        this.pcrdMyMenuService = pcrdMyMenuService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getModuleId() {
        return moduleId;
    }

    public void setModuleId(int moduleId) {
        this.moduleId = moduleId;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public Module getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(Module selectedModule) {
        this.selectedModule = selectedModule;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public List<Folder> getSelectedFolderList() {
        return selectedFolderList;
    }

    public void setSelectedFolderList(List<Folder> selectedFolderList) {
        this.selectedFolderList = selectedFolderList;
    }

    public List<Page> getSelectedPageList() {
        return selectedPageList;
    }

    public void setSelectedPageList(List<Page> selectedPageList) {
        this.selectedPageList = selectedPageList;
    }

    public Page getSelectedPage() {
        if (selectedFolder != null) {
            for (Page p : selectedFolder.getPages()) {
                if (p.getId() == pageId) {
                    return p;
                }
            }
        }
        return null;

    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public UserDataMenuConnection getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(UserDataMenuConnection selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public int getDeletePageId() {
        return deletePageId;
    }

    public void setDeletePageId(int deletePageId) {
        this.deletePageId = deletePageId;
    }

    @PostConstruct
    public void init() {
        iconList = StaticMethods.iconList();
        System.out.println("-*-**-*-*-*-*- " + iconList.size());
        listOfObjects = pcrdMyMenuService.findMyModules();
        createMyMenu();
    }

    public void createMyMenu() {

        UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Dashboard dshWidget = (Dashboard) root.findComponent("dshWidget");

        dshWidget.getChildren().clear();
        for (UserDataMenuConnection menuConnection : listOfObjects) {
            Panel p = new Panel();
            p.setId("p" + menuConnection.getPage().getId());
            p.setStyleClass("dshPanel Mar5");

            CommandLink commandLink = new CommandLink();
            commandLink.setStyle("min-height: 100px;padding: 0px");
            commandLink.setOnclick("rcGoToMyPage([{name:'url', value:'" + menuConnection.getPage().getUrl() + "'}, {name:'id', value:'" + menuConnection.getPage().getId()+ "'}])");
            commandLink.setGlobal(true);

            OutputPanel outputPanel = new OutputPanel();
            outputPanel.setStyleClass(menuConnection.getIcon() + " Fs60  white");
            outputPanel.setStyle("background-color:#" + menuConnection.getColor() + ";text-align: center;margin-bottom: -39px;margin-top: -20px;height: 167px");
            OutputLabel label = new OutputLabel();

            label.setValue(menuConnection.getPage().getName());
            label.setStyle("display: block;color: white !important");
            label.setStyleClass("white Fs15");

            outputPanel.getChildren().add(label);
            commandLink.getChildren().add(outputPanel);

            CommandLink clClose = new CommandLink();
            clClose.setStyleClass("Fright panelIcon icon-cancel-2 white");
            clClose.setOnclick("rcDeletePage([{name:'iddelete', value:'" + menuConnection.getPage().getId()+ "'}])");

            CommandLink clIcon = new CommandLink();
            clIcon.setStyleClass("Fright panelIcon icon-color-adjust white");
            clIcon.setOnclick("rcEditDesign([{name:'idedit', value:'" + menuConnection.getPage().getId()+ "'}])");

            OutputPanel op = new OutputPanel();
            op.getChildren().add(commandLink);
            op.getChildren().add(clClose);
            op.getChildren().add(clIcon);

            p.getFacets().put("header", op);

            dshWidget.getChildren().add(p);

        }

        model = new DefaultDashboardModel();

        int mod = listOfObjects.size() % 8;
        int count = 0;

        if (mod > 0) {
            count = (listOfObjects.size() - mod) / 8;
        } else {
            count = listOfObjects.size() / 8;
        }
        if (count > 0) {
            List<DefaultDashboardColumn> listColumn = new ArrayList<>();
            for (int z = 0; z < 8; z++) {
                DefaultDashboardColumn column = new DefaultDashboardColumn();
                column.setStyleClass("Container10 Responsive");
                for (int i = 0; i < count; i++) {
                    column.addWidget("p" + listOfObjects.get(z + (i * 8)).getPage().getId());
                }
                listColumn.add(column);
                model.addColumn(column);
            }
            if (mod > 0) {
                for (int z = 0; z < mod; z++) {
                    listColumn.get(z).addWidget("p" + listOfObjects.get(listOfObjects.size() - mod + z).getPage().getId());
                }
            }

        } else {
            for (int z = 0; z < listOfObjects.size(); z++) {
                DefaultDashboardColumn column = new DefaultDashboardColumn();
                column.setStyleClass("Container10 Responsive");
                column.addWidget("p" + listOfObjects.get(z).getPage().getId());
                model.addColumn(column);
            }
        }
    }

    public void goToMyPage() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String url = params.get("url");
        String id = params.get("id");
        marwiz.goToPage(url, null, 1, Integer.valueOf(id));

    }

    public void handleReorder(DashboardReorderEvent event) {
        for (UserDataMenuConnection udmc : listOfObjects) {
            if (udmc.getPage().getId() == Integer.valueOf(event.getWidgetId().replace("p", ""))) {
                listOfObjects.remove(udmc);
                int order = ((event.getItemIndex().intValue()) * 8) + (event.getColumnIndex().intValue());
                if (order >= listOfObjects.size()) {
                    listOfObjects.add(listOfObjects.size(), udmc);
                } else {
                    listOfObjects.add(order, udmc);
                }
                break;
            }
        }
        pcrdMyMenuService.reOrder(listOfObjects);
        createMyMenu();
        RequestContext.getCurrentInstance().update("dshWidget");
    }
    
    public void showDeleteDialog(){
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String page_id = params.get("iddelete");
        deletePageId=Integer.valueOf(page_id);

        RequestContext.getCurrentInstance().execute("PF('dlgDeleteVarMyMenu').show();");
               
        
    }


    public void deletePage() {

        for (UserDataMenuConnection udmc : listOfObjects) {
            if (udmc.getPage().getId() == deletePageId) {
                pcrdMyMenuService.delete(udmc);
                listOfObjects.remove(udmc);
                createMyMenu();
                RequestContext.getCurrentInstance().update("dshWidget");

                break;
            }
        }

    }

    public void saveDesign() {
        System.out.println(color + " -****- " + icon);
        if (color != null) {
            selectedObject.setColor(color);
        }
        if (icon != null) {
            selectedObject.setIcon(icon);
        }

        pcrdMyMenuService.update(selectedObject);
        RequestContext.getCurrentInstance().execute("PF('dlg_design').hide();");
        createMyMenu();
        RequestContext.getCurrentInstance().update("dshWidget");
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }

    public void delete(int pageId, int folderId, int moduleId) {
        UserDataMenuConnection userDataMenuConnection = new UserDataMenuConnection();
        Page p = new Page(pageId, null);
        Folder f = new Folder(null, folderId);
        Module m = new Module();
        m.setId(moduleId);
        userDataMenuConnection.setPage(p);
        pcrdMyMenuService.delete(userDataMenuConnection);

        for (UserDataMenuConnection udmc : listOfObjects) {
            if (udmc.getId() == folderId && udmc.getPage().getId() == pageId) {
                listOfObjects.remove(udmc);
                break;
            }
        }

    }

    public void updateFolders() {
        if (moduleId == 0) {
            selectedModule = null;
            selectedFolder = null;
            folderId = 0;
        } else {
            for (Module m : sessionBean.getAuthorizedModules()) {
                if (moduleId == m.getId()) {
                    selectedModule = m;
                    selectedFolder = null;
                    break;
                }
            }
            updateFolderList();
        }
    }

    public void updateFolderList() {
        selectedFolderList = new ArrayList<>();
        for (Folder f : selectedModule.getFolders()) {

            selectedFolderList.add(f);

        }
    }

    public void updatePages() {
        if (folderId == 0) {
            selectedFolder = null;
            pageId = 0;
        } else {
            for (Folder f : selectedModule.getFolders()) {
                if (folderId == f.getId()) {
                    selectedFolder = f;
                    break;
                }
            }

            updatePageList();
        }
    }

    public void updatePageList() {
        selectedPageList = new ArrayList<>();
        for (Page p : selectedFolder.getPages()) {
            selectedPageList.add(p);
        }
    }

    public void reset() {
        moduleId = 0;
        folderId = 0;
        pageId = 0;
        selectedFolder = null;
        selectedModule = null;

    }

    public void save() {
        System.out.println("save");
        if (selectedModule != null && selectedFolder != null && getSelectedPage() != null) {
            boolean find = false;
            for (UserDataMenuConnection userDataMenuConnection1 : listOfObjects) {
                if (userDataMenuConnection1.getPage().getId() == getSelectedPage().getId()) {
                    find = true;
                    break;
                }
            }

            if (!find) {
                UserDataMenuConnection userDataMenuConnection = new UserDataMenuConnection();
                getSelectedPage().setName(getSelectedPage().getNameMap().get(sessionBean.getLangId()).getName());
                userDataMenuConnection.setPage(getSelectedPage());
                userDataMenuConnection.setColor("27aae1");
                userDataMenuConnection.setIcon("icon-address");
                userDataMenuConnection.setOrder(listOfObjects.size() + 1);

                int result = pcrdMyMenuService.create(userDataMenuConnection);
                if (result > 0) {
                    listOfObjects.add(userDataMenuConnection);
                    createMyMenu();
                    RequestContext.getCurrentInstance().execute("PF('dlg_mymenu').hide()");
                    RequestContext.getCurrentInstance().update("dshWidget");
                }
                sessionBean.createUpdateMessage(result);
            } else {
                FacesMessage message = new FacesMessage();
                message.setSeverity(FacesMessage.SEVERITY_FATAL);
                message.setSummary(sessionBean.getLoc().getString("error"));
                message.setDetail(sessionBean.getLoc().getString("thispagehasbeenaddedbefore"));
                addMessage(message);
            }
        }

    }

    public void updateColor() {
        System.out.println("testtttt " + color);
    }

    public void updateIcon() {
        System.out.println("*-*-*-*-**-* " + icon);
        selectedObject.setIcon(icon);
    }

    public void editDesign() {

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String page_id = params.get("idedit");
        for (UserDataMenuConnection connection : listOfObjects) {
            if (connection.getPage().getId() == Integer.valueOf(page_id)) {
                selectedObject = connection;
                System.out.println(connection.getPage().getName());
                RequestContext.getCurrentInstance().execute("PF('dlg_design').loadContents();");
                color = null;
                icon = null;
                break;
            }
        }

    }

}
