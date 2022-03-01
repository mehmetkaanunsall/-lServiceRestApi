/**
 * @author Mehmet ERGÜLCÜ
 * @date 23.02.2017 11:57:31
 */
package com.mepsan.marwiz.general.profile.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.dashboard.business.IDbObjectService;
import com.mepsan.marwiz.general.dashboard.business.IWidgetService;
import com.mepsan.marwiz.general.dashboard.business.IWidgetUserDataConService;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Grid;
import com.mepsan.marwiz.general.model.admin.GridColumn;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.Widget;
import com.mepsan.marwiz.general.model.general.WidgetUserDataCon;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped
public class ProfileWidgetTabBean {

    @ManagedProperty(value = "#{widgetUserDataConService}")
    IWidgetUserDataConService widgetUserDataConService;

    @ManagedProperty(value = "#{widgetService}")
    IWidgetService widgetService;

    @ManagedProperty(value = "#{dbObjectService}")
    IDbObjectService dbObjectService;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    private int moduleId, folderId, pageId;
    private Module selectedModule;
    private Folder selectedFolder;
    private List<Folder> selectedFolderList;
    private List<Page> selectedPageList;
    private List<GridColumn> listOfGridColumns;
    private List<GridColumn> selectedGridColumns;

    private List<Widget> listOfObjects;
    private List<Widget> selectedObjectList;
    private List<WidgetUserDataCon> widgetUserDataCons;
    private Widget processWidget;
    private Grid pageGrid;
    private String tableTag;

    // <editor-fold defaultstate="collapsed" desc="getter and setters">
    public IWidgetUserDataConService getWidgetUserDataConService() {
        return widgetUserDataConService;
    }

    public void setWidgetUserDataConService(IWidgetUserDataConService widgetUserDataConService) {
        this.widgetUserDataConService = widgetUserDataConService;
    }

    public IWidgetService getWidgetService() {
        return widgetService;
    }

    public void setWidgetService(IWidgetService widgetService) {
        this.widgetService = widgetService;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
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

    public List<Widget> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<Widget> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public List<Widget> getSelectedObjectList() {
        return selectedObjectList;
    }

    public void setSelectedObjectList(List<Widget> selectedObjectList) {
        this.selectedObjectList = selectedObjectList;
    }

    public List<WidgetUserDataCon> getWidgetUserDataCons() {
        return widgetUserDataCons;
    }

    public void setWidgetUserDataCons(List<WidgetUserDataCon> widgetUserDataCons) {
        this.widgetUserDataCons = widgetUserDataCons;
    }

    public Widget getProcessWidget() {
        return processWidget;
    }

    public void setProcessWidget(Widget processWidget) {
        this.processWidget = processWidget;
    }

    public Grid getPageGrid() {
        return pageGrid;
    }

    public void setPageGrid(Grid pageGrid) {
        this.pageGrid = pageGrid;
    }

    public List<GridColumn> getSelectedGridColumns() {
        return selectedGridColumns;
    }

    public void setSelectedGridColumns(List<GridColumn> selectedGridColumns) {
        this.selectedGridColumns = selectedGridColumns;
    }

    public List<GridColumn> getListOfGridColumns() {
        return listOfGridColumns;
    }

    public void setListOfGridColumns(List<GridColumn> listOfGridColumns) {
        this.listOfGridColumns = listOfGridColumns;
    }

    public IDbObjectService getDbObjectService() {
        return dbObjectService;
    }

    public void setDbObjectService(IDbObjectService dbObjectService) {
        this.dbObjectService = dbObjectService;
    }

    // </editor-fold>
    @PostConstruct
    public void init() {
        System.out.println("-------ProfileWidgetTabBean----");
        widgetUserDataCons = new ArrayList<>(widgetUserDataConService.findAll());
        listOfObjects = widgetService.findAll();
        selectedObjectList = new ArrayList<>(getUserWidgets());
        listOfGridColumns = new ArrayList<>();
        selectedGridColumns = new ArrayList<>();
    }

    public void createDialog(int type) {
        if (type == 1) //ekle
        {
            processWidget = new Widget();

        } else {

        }
        RequestContext.getCurrentInstance().execute("PF('dlg_widget').show();");
        System.out.println("type:" + type);
    }

    public void checkedChange() {
        System.out.println("slected:" + selectedObjectList.size());
        System.out.println("user:" + widgetUserDataCons.size());

        if (selectedObjectList.size() > widgetUserDataCons.size()) {
            createWidgetUserDataCon();
        } else {
            deleteWidgetUserDataCon();
        }

    }

    public void createWidgetUserDataCon() {
        int row = 0;
        int col = 0;

        for (Widget w : selectedObjectList) {
            if (!getUserWidgets().contains(w)) {
                WidgetUserDataCon widgetUserDataCon = new WidgetUserDataCon();
                widgetUserDataCon.setUserData(sessionBean.getUser());
                widgetUserDataCon.setWidget(w);
                outerloop:
                for (int r = 0; r < 100; r++) {
                    for (int c = 0; c < 3; c++) {
                        if (isPlaceAvailable(r, c)) {
                            row = r;
                            col = c;
                            break outerloop;
                        }
                    }
                }
                widgetUserDataCon.setRow(row);
                widgetUserDataCon.setCol(col);
                int id = widgetUserDataConService.create(widgetUserDataCon);
                widgetUserDataCon.setId(id);
                widgetUserDataCons.add(widgetUserDataCon);
            }
        }
    }

    public void deleteWidgetUserDataCon() {
        List<WidgetUserDataCon> cloneList = new ArrayList<>(widgetUserDataCons);
        for (WidgetUserDataCon widgetUserDataCon : cloneList) {
            if (!selectedObjectList.contains(widgetUserDataCon.getWidget())) {
                widgetUserDataConService.delete(widgetUserDataCon);
                widgetUserDataCons.remove(widgetUserDataCon);
            }
        }
    }

    public List<Widget> getUserWidgets() {
        List<Widget> widgets = new ArrayList<>();
        for (WidgetUserDataCon widgetUserDataCon : widgetUserDataCons) {
            widgets.add(widgetUserDataCon.getWidget());
        }
        return widgets;
    }

    public void updateFolders() {
        System.out.println("---" + moduleId);
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
            selectedPageList = new ArrayList<>();
            for (Page p : selectedFolder.getPages()) {
                selectedPageList.add(p);
            }
        }
    }

    public void updateGrid() {
        System.out.println("pageId " + pageId);
        pageGrid = widgetUserDataConService.getPageGrid(pageId);
        selectedGridColumns.clear();
        listOfGridColumns.clear();
        tableTag = null;
        if (pageGrid != null) {
            for (GridColumn gridColumn : pageGrid.getGridColumns()) {
                System.out.println("gridColumn:" + gridColumn.getColumnName());
                String[] split = gridColumn.getColumnName().split("\\.");
                String tablename = split[0];
                String colname = split[1];
                if (tableTag == null) {
                    tableTag = tablename;
                }
                System.out.println(gridColumn.isIsDetailFilter() + "--" + gridColumn.getColumnName());
                if (tableTag.equals(tablename) && !gridColumn.isIsDetailFilter()) {
                    System.out.println(gridColumn.getColumnName());
                    listOfGridColumns.add(gridColumn);
                }

            }
        }
    }

    private boolean isPlaceAvailable(int r, int c) {
        boolean available = true;
        for (WidgetUserDataCon widgetUserDataCon : widgetUserDataCons) {
            if (widgetUserDataCon.getCol() == c && widgetUserDataCon.getRow() == r) {
                available = false;
                break;
            }
        }
        return available;
    }

}
