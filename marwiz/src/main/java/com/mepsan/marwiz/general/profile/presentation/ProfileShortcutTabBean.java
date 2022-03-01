/**
 * This class ...
 *
 *
 * @author Gozde Gursel
 *
 * @date   18.04.2017 09:56:05
 */
package com.mepsan.marwiz.general.profile.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.general.HotKey;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.general.profile.business.IHotKeyService;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

@ManagedBean
@ViewScoped

public class ProfileShortcutTabBean extends GeneralDefinitionBean<HotKey> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{hotKeyService}")
    public IHotKeyService hotKeyService;



    private int processType;
    private int moduleId, folderId;
    private Module selectedModule;
    private Folder selectedFolder;

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Module getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(Module selectedModule) {
        this.selectedModule = selectedModule;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setHotKeyService(IHotKeyService hotKeyService) {
        this.hotKeyService = hotKeyService;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
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

  

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-----------Short Cut Bean----");
       // listOfObjects = findall();

    }

    @Override
    public void create() {
        processType = 1;
        selectedObject = new HotKey();
        selectedModule = new Module();
        selectedFolder = new Folder();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_sortcutproc').show();");
    }

    public void update() {
        processType = 2;
        for (Module m : sessionBean.getAuthorizedModules()) {
            for (Folder f : m.getFolders()) {
                for (Page p : f.getPages()) {
                    if (selectedObject.getPage().getId() == p.getId()) {
                        moduleId = m.getId();
                        selectedModule = m;
                        folderId = f.getId();
                        selectedFolder = f;
                        break;
                    }
                }

            }
        }
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlg_sortcutproc').show();");
    }

    @Override
    public void save() {
        RequestContext context = RequestContext.getCurrentInstance();
        int result = 0;
        if (processType == 1) {//create
            if (keyControl(selectedObject.getHotkey())) {
                result = hotKeyService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    listOfObjects.add(selectedObject);
                    context.update("frmShortcutTab:dtbSortcut");
                    context.execute("PF('sortcutPF').filter();");
                    context.execute("PF('dlg_sortcutproc').hide();");
                   // sessionBean.getUser().getHotKeys().add(selectedObject);
                    context.update("frmtopbar:repeat");
                }
            }
        } else if (processType == 2) {//update
            if (keyControl(selectedObject.getHotkey())) {
                result = hotKeyService.update(selectedObject);
                if (result > 0) {
                    context.execute("PF('sortcutPF').filter();");
                    context.execute("PF('dlg_sortcutproc').hide();");
//                    for (HotKey hot : sessionBean.getUser().getHotKeys()) {
//                        if (hot.getId() == selectedObject.getId()) {
//                            hot.setHotkey(selectedObject.getHotkey());
//                            hot.getPage().setId(selectedObject.getPage().getId());
//                            hot.getPage().setUrl(selectedObject.getPage().getUrl());
//                            break;
//                        }
//                    }
                    context.update("frmtopbar:repeat");
                }
            }
        }
        sessionBean.createUpdateMessage(result);
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
                    selectedObject.getPage().setId(0);
                    break;
                }
            }
        }
    }

    public void updatePages() {
        if (folderId == 0) {
            selectedFolder = null;
        } else {
            for (Folder f : selectedModule.getFolders()) {
                if (folderId == f.getId()) {
                    selectedFolder = f;
                    selectedObject.getPage().setId(0);
                    break;
                }
            }
        }
    }

    public void bringPage() {
        for (Page page : selectedFolder.getPages()) {
            if (page.getId() == selectedObject.getPage().getId()) {
             //   selectedObject.getPage().setTag(page.getNameMap().get(sessionBean.getUser().getLanguage().getId()).getName());
                selectedObject.getPage().setUrl(page.getUrl());
            }
        }
    }

    @Override
    public List<HotKey> findall() {
        return hotKeyService.listHotKeys();
    }

    /**
     *
     * @param key
     */
    public boolean keyControl(String key) {

        FacesContext context = FacesContext.getCurrentInstance();
        System.out.println("-------" + key);
        if (key.trim().toLowerCase().equals("ctrl+shift+a") || key.trim().toLowerCase().equals("ctrl+shift+s")) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("systemshortcutscannotbeadded")));
            return false;

        } else {
            for (HotKey hotkey : listOfObjects) {

                if (hotkey.getId() != selectedObject.getId()) {
                    if (hotkey.getHotkey().trim().toUpperCase().equals(key.trim().toUpperCase())) {
                        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("shortcutpreviouslycreated")));
                        return false;
                    }
                }

            }
        }

        return true;
    }

    public void testBeforeDelete() {
//        deleteBean.setMessege(sessionBean.loc.getString("areyousureyouwanttodelete"));
//        deleteBean.setRendered(true);
    }

    public void delete() {
        sessionBean.createUpdateMessage(hotKeyService.delete(selectedObject));
        RequestContext.getCurrentInstance().execute("PF('dlg_sortcutproc').hide();");
        listOfObjects.remove(selectedObject);
        RequestContext.getCurrentInstance().execute("PF('sortcutPF').filter();");

    }

}
