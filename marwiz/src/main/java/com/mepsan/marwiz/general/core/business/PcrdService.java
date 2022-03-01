/**
 * This class ... for Main page (pcrd )
 * processes cards reports definitions
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 11:21:46
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;

public class PcrdService implements IPcrdService, Serializable {

    @Autowired
    public SessionBean sessionBean;

    @Autowired
    private BreadCrumbService breadCrumb;

    @Autowired
    private ApplicationBean applicationBean;

    public MenuModel process;
    public MenuModel card;
    public MenuModel report;
    public MenuModel definition;
    public boolean expandedProcess, expandedCard, expandedReport, expandedDefinition;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setBreadCrumb(BreadCrumbService breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public MenuModel getProcess() {
        return process;
    }

    public void setProcess(MenuModel process) {
        this.process = process;
    }

    public MenuModel getCard() {
        return card;
    }

    public void setCard(MenuModel card) {
        this.card = card;
    }

    public MenuModel getReport() {
        return report;
    }

    public void setReport(MenuModel report) {
        this.report = report;
    }

    public MenuModel getDefinition() {
        return definition;
    }

    public void setDefinition(MenuModel definition) {
        this.definition = definition;
    }

    /**
     * this method to create module and fill the menus of pcrd page
     *
     * @param id module id
     */
    @Override
    public void createModule(int id) {
        Module selectedModule = new Module();
        process = new DefaultMenuModel();
        card = new DefaultMenuModel();
        report = new DefaultMenuModel();
        definition = new DefaultMenuModel();
        List<Integer> folders = sessionBean.getUser().getLastAuthorize().getListOfFolders();
        List<Integer> pages = sessionBean.getUser().getLastAuthorize().getListOfPages();

        for (Module m : applicationBean.getListOfModules()) {
            if (m.getId() == id) {
                selectedModule = m;
                break;
            }
        }
        //------------------- breadCrump ----------------------
        breadCrumb.addItemForModule(breadCrumb, selectedModule);
        // ---------------------------------------
        for (Folder folder : selectedModule.getFolders()) {
            if (folders.contains(folder.getId())) {
                String folderName;
                if (folder.getNameMap().get(sessionBean.getLangId()) != null) {
                    folderName = folder.getNameMap().get(sessionBean.getLangId()).getName();
                } else {
                    folderName = folder.getNameMap().get(1).getName();
                }
                DefaultSubMenu folderSubmenu = new DefaultSubMenu(folderName);
                switch (folder.getType()) {
                    //process
                    case 0:
                        folderSubmenu.setStyleClass("folderSubMenu folderSubMenuProcess");
                        break;
                    //card
                    case 1:
                        folderSubmenu.setStyleClass("folderSubMenu folderSubMenuCard");
                        break;
                    //report
                    case 2:
                        folderSubmenu.setStyleClass("folderSubMenu folderSubMenuReport");
                        break;
                    //definition
                    case 3:
                        folderSubmenu.setStyleClass("folderSubMenu folderSubMenuDefinition");
                        break;
                    default:
                        break;
                }

                folderSubmenu.setExpanded(true);
                if (folder.getPages() != null) {
                    for (Page page : folder.getPages()) {
                        if (pages.contains(page.getId())) {
                            String pageName;
                            if (page.getNameMap().get(sessionBean.getLangId()) != null) {
                                pageName = page.getNameMap().get(sessionBean.getLangId()).getName();
                            } else {
                                pageName = page.getNameMap().get(1).getName();
                            }
                            DefaultMenuItem menuitem = null;
                            switch (folder.getType()) {
                                //process
                                case 0:
                                    menuitem = new DefaultMenuItem(pageName, "fa fa-cogs");
                                    menuitem.setStyleClass("pageSubMenu pageSubMenuProcess");
                                    break;
                                //card
                                case 1:
                                    menuitem = new DefaultMenuItem(pageName, "fa fa-id-card-o");
                                    menuitem.setStyleClass("pageSubMenu pageSubMenuCard");
                                    break;
                                //report
                                case 2:
                                    menuitem = new DefaultMenuItem(pageName, "fa fa-bar-chart");
                                    menuitem.setStyleClass("pageSubMenu pageSubMenuReport");
                                    break;
                                //definition
                                case 3:
                                    menuitem = new DefaultMenuItem(pageName, "fa fa-thumb-tack");
                                    menuitem.setStyleClass("pageSubMenu pageSubMenuDefinition");
                                    break;
                                default:
                                    break;
                            }

                            menuitem.setId(String.valueOf(page.getId()));
                            menuitem.setCommand(String.format("#{marwiz.goToPage('" + page.getUrl() + "',null,1," + page.getId() + ")}"));
                            menuitem.setGlobal(true);
                            folderSubmenu.addElement(menuitem);
                        }
                    }

                }

                switch (folder.getType()) {
                    //process
                    case 0:
                        process.addElement(folderSubmenu);
                        break;
                    //card
                    case 1:
                        card.addElement(folderSubmenu);
                        break;
                    //report
                    case 2:
                        report.addElement(folderSubmenu);
                        break;
                    //definition
                    case 3:
                        definition.addElement(folderSubmenu);
                        break;
                    default:
                        break;
                }

            }
        }

    }

}
