/**
 * This class ...for the Left menu of system
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   22.07.2016 11:28:43
 */
package com.mepsan.marwiz.general.core.business;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.general.Branch;
import java.io.Serializable;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.beans.factory.annotation.Autowired;

public class LeftMenuService implements Serializable, ILeftMenuService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private ApplicationBean applicationBean;

    private MenuModel menu;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public MenuModel getMenu() {
        return menu;
    }

    public void setMenu(MenuModel menu) {
        this.menu = menu;
    }

    /**
     * bu method şirketleri oluşturmak için
     */
    /**
     * bu method moduleri oluşturmak için
     *
     * @param type
     */
    public void initialize(int type) {
        System.out.println("initialize");

        // createPostPayment();
    }

    public void createBranchs() {
        menu = new DefaultMenuModel();

        DefaultSubMenu lastBranch = new DefaultSubMenu(sessionBean.getUser().getLastBranch().getName(), "icon-pin-outline");
        lastBranch.setStyleClass("sm_defaultcompany");
        for (Branch s : sessionBean.getUser().getAuthorizedBranches()) {
            if (s.getId() != sessionBean.getUser().getLastBranch().getId()) {
                DefaultMenuItem branch = new DefaultMenuItem(s.getName(), "icon-pin-outline");
                branch.setCommand(String.format("#{marwiz.updateBranch("+s.getId()+")}"));
                branch.setAjax(false);
                lastBranch.addElement(branch);
            }
        }
        menu.addElement(lastBranch);
    }

    /**
     * bu method moduleri oluşturmak için
     */
    // @Override
    public void createModules() {
        //Moduls

        System.out.println(sessionBean.loc.getString("modules"));
        DefaultSubMenu smModuller = new DefaultSubMenu(" " + sessionBean.loc.getString("modules"), "icon-sitemap");
        smModuller.setStyleClass("activeSubMenu sm_modules");
        menu.addElement(smModuller);
        for (Module module : applicationBean.getListOfModules()) {
            if (sessionBean.getUser().getLastAuthorize().getListOfModules().contains(module.getId())) {
                System.out.println("icon "+module.getIcon());
                DefaultMenuItem miModul = new DefaultMenuItem(" " + module.getNameMap().get(sessionBean.getLangId()).getName(), module.getIcon());
                miModul.setId(String.valueOf(module.getId()));
                miModul.setCommand(String.format("#{marwiz.goToModule('%d')}", module.getId()));
                miModul.setGlobal(false);
                smModuller.addElement(miModul);
            }
        }
        smModuller.setExpanded(true);
    }

    public void createPostPayment() {

        DefaultSubMenu smModuller = new DefaultSubMenu(" " + sessionBean.loc.getString("menues"), "icon-sitemap");
        smModuller.setStyleClass("activeSubMenu sm_modules");
        menu.addElement(smModuller);

        DefaultMenuItem report = new DefaultMenuItem("Rapor", "icon-sitemap");
        report.setCommand(String.format("#{marwiz.createDyanmicPage(2)}"));
        DefaultMenuItem definition = new DefaultMenuItem("Tanım", "icon-sitemap");
        definition.setCommand(String.format("#{marwiz.createDyanmicPage(3)}"));

        DefaultMenuItem safe = new DefaultMenuItem(sessionBean.loc.getString("safe"), "icon-pin-outline");
        safe.setCommand(String.format("#{marwiz.goToMainPage('/pages/finance/safe/safe.xhtml',null,1,0)}"));
        safe.setGlobal(true);
        DefaultMenuItem p1 = new DefaultMenuItem("Banka", "icon-pin-outline");
        p1.setCommand(String.format("#{marwiz.goToMainPage('/pages/finance/bankaccount/bankaccount.xhtml',null,1,1)}"));
        p1.setGlobal(true);
        DefaultMenuItem f = new DefaultMenuItem("Finansman Belge", "icon-pin-outline");
        f.setCommand(String.format("#{marwiz.goToMainPage('/pages/finance/financingdocument/financingdocument.xhtml',null,1,0)}"));
        f.setGlobal(true);
        DefaultMenuItem d = new DefaultMenuItem("Depo", "icon-pin-outline");
        d.setCommand(String.format("#{marwiz.goToMainPage('/pages/inventory/warehouse/warehouse.xhtml',null,1,0)}"));
        d.setGlobal(true);
        DefaultMenuItem df = new DefaultMenuItem("Depo Fişi", "icon-pin-outline");
        df.setCommand(String.format("#{marwiz.goToMainPage('/pages/inventory/warehousereceipt/warehousereceipt.xhtml',null,1,39)}"));
        df.setGlobal(true);
        DefaultMenuItem account = new DefaultMenuItem(sessionBean.loc.getString("account"), "icon-pin-outline");
        account.setCommand(String.format("#{marwiz.goToMainPage('/pages/general/account/account.xhtml',null,1,0)}"));
        account.setGlobal(true);
        DefaultMenuItem stocks = new DefaultMenuItem("Stok", "icon-sitemap");
        stocks.setCommand(String.format("#{marwiz.goToMainPage('/pages/inventory/stock/stock.xhtml', null, 1, 2)}"));
        stocks.setGlobal(true);
        DefaultMenuItem fatura = new DefaultMenuItem("Fatura", "icon-sitemap");
        fatura.setCommand(String.format("#{marwiz.goToMainPage('/pages/finance/invoice/invoice.xhtml', null, 1, 24)}"));
        fatura.setGlobal(true);
        DefaultMenuItem irsaliye = new DefaultMenuItem("İrsaliye", "icon-sitemap");
        irsaliye.setCommand(String.format("#{marwiz.goToMainPage('/pages/finance/waybill/waybill.xhtml', null, 1, 25)}"));
        irsaliye.setGlobal(true);

        smModuller.addElement(account);
        smModuller.addElement(stocks);
        smModuller.addElement(fatura);
        smModuller.addElement(irsaliye);
        smModuller.addElement(d);
        smModuller.addElement(safe);
        smModuller.addElement(p1);
        smModuller.addElement(f);
        smModuller.addElement(df);

        smModuller.addElement(report);
        smModuller.addElement(definition);

    }

}
