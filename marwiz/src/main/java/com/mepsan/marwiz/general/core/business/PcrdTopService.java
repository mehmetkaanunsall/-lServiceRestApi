/**
 * Bu Sınıf TopMenu İçin (İşlemler , Kartlar , Raporlar , Tanımlar )
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 11:21:13
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;

public class PcrdTopService implements Serializable, IPcrdTopService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private BreadCrumbService breadCrumb;

    @Autowired
    private ApplicationBean applicationBean;

    private String pcrdTopType;
    private Map<Integer, MenuModel> pcrdTopMenues;
    public Boolean expanded = false;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBreadCrumb(BreadCrumbService breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public String getPcrdTopType() {
        return pcrdTopType;
    }

    public void setPcrdTopType(String pcrdTopType) {
        this.pcrdTopType = pcrdTopType;
    }

    public Map<Integer, MenuModel> getPcrdTopMenues() {
        return pcrdTopMenues;
    }

    public void setPcrdTopMenues(Map<Integer, MenuModel> pcrdTopMenues) {
        this.pcrdTopMenues = pcrdTopMenues;
    }

    public ApplicationBean getApplicationBean() {
        return applicationBean;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    /**
     * bu method doldurma top menu için
     *
     * @param type menu tipisi
     */
    @Override
    public void createPcrdTop(int type) {
        pcrdTopMenues = new HashMap<Integer, MenuModel>();
        //------------------- breadCrump ----------------------
        breadCrumb.addItemForPcrdTop(breadCrumb, type);
        // ---------------------------------------
        for (Module module : applicationBean.getListOfModules()) {
            if (sessionBean.getUser().getLastAuthorize().getListOfModules().contains(module.getId())) {
                MenuModel element = new DefaultMenuModel();
                for (Folder folder : module.getFolders()) {
                    if (sessionBean.getUser().getLastAuthorize().getListOfFolders().contains(folder.getId())) {
                        if (folder.getType() == type) {
                            DefaultSubMenu folderSubmenu = new DefaultSubMenu(folder.getNameMap().get(sessionBean.getLangId()).getName());
                            folderSubmenu.setStyleClass("folderSubMenu");
                            folderSubmenu.setExpanded(true);
                            if (folder.getPages() != null) {
                                for (Page page : folder.getPages()) {
                                    if (sessionBean.getUser().getLastAuthorize().getListOfPages().contains(page.getId())) {
                                        DefaultMenuItem menuitem = new DefaultMenuItem(page.getNameMap().get(sessionBean.getLangId()).getName(), "fa icon-doc-text");
                                        menuitem.setStyleClass("pageSubMenu");
                                        menuitem.setId(String.valueOf(page.getId()));
                                        menuitem.setCommand(String.format("#{marwiz.goToPage('" + page.getUrl() + "',null,1," + page.getId() + ")}"));
                                        menuitem.setGlobal(true);
                                        folderSubmenu.addElement(menuitem);
                                    }
                                }
                                element.addElement(folderSubmenu);
                            }
                        }
                    }
                }
                pcrdTopMenues.put(module.getId(), element);
            }
        }
    }

}
