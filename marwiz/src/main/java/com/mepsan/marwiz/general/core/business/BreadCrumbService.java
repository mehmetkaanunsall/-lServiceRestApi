/**
 * Bu Sınıf BreadCrumb işlemşeri içindir.
 * (güncelleme ve ekleme )
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 11:20:58
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Module;
import java.io.Serializable;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;

public class BreadCrumbService implements Serializable, IBreadCrumpService {

    @Autowired
    private SessionBean sessionBean;

    private MenuModel breadCrumb;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public MenuModel getBreadCrumb() {
        return breadCrumb;
    }

    public void setBreadCrumb(MenuModel breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    /**
     * Yeni breadcrumb Oluşturma
     */
    @Override
    public void createBreadcrumb() {
        breadCrumb = new DefaultMenuModel();
        DefaultMenuItem item = new DefaultMenuItem("External");
        item.setCommand("#{marwiz.goToPage('/pages/general/dashboard.xhtml',null,1,0)}");
        item.setAjax(false);
        item.setGlobal(true);
        breadCrumb.addElement(item);
    }

    /*  public void addItemForMainPage(BreadCrumbService breadCrumb, int pageId, String pageUrl) {
        if (pageId == 0) {
            breadCrumb.createBreadcrumb();
        } else {

            DefaultMenuItem item = new DefaultMenuItem(sessionBean.auth.getString("p_" + pageId));
            item.setId("1");
            item.setCommand("#{marwiz.goToPage('" + pageUrl + "',null,1," + pageId + ")}");
            item.setGlobal(true);
            breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
            breadCrumb.getBreadCrumb().addElement(item);
        }
    }*/
    @Override
    public void addItemForDynamicPage(BreadCrumbService breadCrumb, int type) {
        DefaultMenuItem item = null;
        if (type == 2) {
            item = new DefaultMenuItem("Rapor");
        } else {
            item = new DefaultMenuItem("Tanım");
        }
        item.setId("1");
        item.setCommand("#{marwiz.createDyanmicPage(" + type + ")}");
        item.setGlobal(true);
        breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
        breadCrumb.getBreadCrumb().addElement(item);

    }

    /**
     * breadcrumb a yeni item(TopMenu) ekleme
     *
     * @param breadCrumb breadcrumb obesi
     * @param pcrdTopType item tipis
     */
    public void addItemForPcrdTop(BreadCrumbService breadCrumb, int pcrdTopType) {
        DefaultMenuItem item = new DefaultMenuItem();
        switch (pcrdTopType) {
            case 0:
                item.setValue(sessionBean.loc.getString("processes"));
                break;
            case 1:
                item.setValue(sessionBean.loc.getString("cards"));
                break;
            case 2:
                item.setValue(sessionBean.loc.getString("reports"));
                break;
            default:
                item.setValue(sessionBean.loc.getString("definitions"));
                break;
        }
        item.setId("1");
        item.setCommand("#{marwiz.goToPcrdTop(" + pcrdTopType + ")}");
        item.setGlobal(true);
        breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
        breadCrumb.getBreadCrumb().addElement(item);
    }

    /**
     * breadcrumb a yeni item(Page) ekleme
     *
     * @param breadCrumb breacrumb objesi
     * @param pageId eklenecek sayfanin id si
     * @param pageUrl eklenecek sayfanin url i
     */
    @Override
    public void addItemForPage(BreadCrumbService breadCrumb, int pageId, String pageUrl) {
        boolean t = false;
        DefaultMenuItem item = new DefaultMenuItem(sessionBean.auth.getString("p_" + pageId));
        item.setId(String.valueOf(breadCrumb.getBreadCrumb().getElements().size()));
        for (MenuElement menuItem : breadCrumb.getBreadCrumb().getElements()) {
            String s = ((DefaultMenuItem) menuItem).getValue().toString();
            if (s.equals(sessionBean.auth.getString("p_" + pageId))) {
                t = true;
                breadCrumb.getBreadCrumb().getElements().subList(breadCrumb.getBreadCrumb().getElements().indexOf(menuItem) + 1, breadCrumb.getBreadCrumb().getElements().size()).clear();
                break;
            }
        }
        if (!t) {
            switch (pageId) {
                case 0:
                    breadCrumb.createBreadcrumb();
                    break;

                default:
                    if (pageId != 96) {
                        item.setCommand("#{marwiz.goToPage('" + pageUrl + "',null,1," + pageId + ")}");
                    }
                    item.setGlobal(true);
                    breadCrumb.getBreadCrumb().getElements().add(item);
                    break;
            }
        }
    }

    /**
     * breadcrumb a yeni item(Module) ekleme
     *
     * @param breadCrumb breacrumb objesi
     * @param module eklenecek module objesi
     */
    public void addItemForModule(BreadCrumbService breadCrumb, Module module) {
        DefaultMenuItem item = new DefaultMenuItem(module.getNameMap().get(sessionBean.getLangId()).getName());
        item.setId("1");
        item.setCommand("#{marwiz.goToModule(" + module.getId() + ")}");
        item.setGlobal(true);
        breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
        breadCrumb.getBreadCrumb().addElement(item);
    }

    public void addItemForMyMenu(BreadCrumbService breadCrumb) {
        DefaultMenuItem item = new DefaultMenuItem();
        item.setValue(sessionBean.loc.getString("mymenu"));
        item.setId("1");
        item.setCommand("#{marwiz.goToPcrdMyMenu()}");
        item.setGlobal(true);
        item.setAsync(false);
        breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
        breadCrumb.getBreadCrumb().addElement(item);
    }

    public void addItemForSearchResult(BreadCrumbService breadCrumb, String search) {
        DefaultMenuItem item = new DefaultMenuItem();
        item.setValue(search);
        item.setId("1");
        item.setCommand("#{marwiz.doSearch}");
        item.setGlobal(true);
        breadCrumb.getBreadCrumb().getElements().subList(1, breadCrumb.getBreadCrumb().getElements().size()).clear();
        breadCrumb.getBreadCrumb().addElement(item);
    }

}
