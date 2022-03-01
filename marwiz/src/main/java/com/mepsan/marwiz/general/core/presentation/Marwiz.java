/**
 * Bu Sınıf ... çekirdek sistem için yöntemler
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   02.08.2016 14:56:19
 *
 *
 */
package com.mepsan.marwiz.general.core.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.business.BreadCrumbService;
import com.mepsan.marwiz.general.core.business.LeftMenuService;
import com.mepsan.marwiz.general.core.business.MarwizService;
import com.mepsan.marwiz.general.core.business.PcrdService;
import com.mepsan.marwiz.general.core.business.PcrdTopService;
import com.mepsan.marwiz.general.exchange.business.ExchangeService;
import com.mepsan.marwiz.general.model.admin.Folder;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.admin.Page;
import com.mepsan.marwiz.general.model.wot.Authentication;
import com.mepsan.marwiz.general.model.wot.DataTableColumn;
import com.mepsan.marwiz.general.profile.business.IProfileService;
import com.mepsan.marwiz.service.branchinfo.business.GetBranchInfoService;
import com.mepsan.marwiz.service.item.business.CheckItemService;
import com.mepsan.marwiz.service.order.business.SendOrderService;
import com.mepsan.marwiz.service.paro.business.CallCampaignInfoService;
import com.mepsan.marwiz.service.paro.business.ParoOfflineSalesService;
import com.mepsan.marwiz.service.price.business.SendPriceChangeRequestService;
import com.mepsan.marwiz.service.purchace.business.SendPurchaseService;
import com.mepsan.marwiz.service.sale.business.SendSaleService;
import com.mepsan.marwiz.service.stock.business.SendStockInfoService;
import com.mepsan.marwiz.service.stock.business.SendStockRequestService;
import com.mepsan.marwiz.service.waste.business.SendWasteService;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.view.facelets.FaceletContext;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

@ManagedBean
@SessionScoped
public class Marwiz implements Serializable {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{leftMenu}")
    private LeftMenuService leftMenu;

    @ManagedProperty(value = "#{breadCrumb}")
    private BreadCrumbService breadCrumb;

    @ManagedProperty(value = "#{profileService}")
    private IProfileService profileService;

    @ManagedProperty(value = "#{marwizService}")
    private MarwizService marwizService;

    @ManagedProperty(value = "#{pcrd}")
    private PcrdService pcrd;

    @ManagedProperty(value = "#{pcrdTop}")
    private PcrdTopService pcrdTop;

    @ManagedProperty(value = "#{applicationBean}")
    private ApplicationBean applicationBean;

    @ManagedProperty(value = "#{checkItemService}")
    private CheckItemService checkItemService;

    @ManagedProperty(value = "#{sendStockInfoService}")
    private SendStockInfoService sendStockInfoService;

    @ManagedProperty(value = "#{sendSaleService}")
    private SendSaleService sendSaleService;

    @ManagedProperty(value = "#{sendPurchaseService}")
    private SendPurchaseService sendPurchaseService;

    @ManagedProperty(value = "#{sendStockRequestService}")
    private SendStockRequestService sendStockRequestService;

    @ManagedProperty(value = "#{sendPriceChangeRequestService}")
    private SendPriceChangeRequestService sendPriceChangeRequestService;

    @ManagedProperty(value = "#{callCampaignInfoService}")
    private CallCampaignInfoService callCampaignInfoService;

    @ManagedProperty(value = "#{exchangeService}")
    private ExchangeService exchangeService;

    @ManagedProperty(value = "#{paroOfflineSalesService}")
    private ParoOfflineSalesService paroOfflineSalesService;

    @ManagedProperty(value = "#{sendWasteService}")
    private SendWasteService sendWasteService;

    @ManagedProperty(value = "#{getBranchInfoService}")
    private GetBranchInfoService getBranchInfoService;

    @ManagedProperty(value = "#{sendOrderService}")
    private SendOrderService sendOrderService;

    private String renderdPage;
    public Object parameter;
    private String oldUrl = "";
    private String searchText;
    private int oldId = 0;
    private String pageUrlOfGoToPage;
    private Object paramOfGoToPage;
    private int typeOfGoToPage;
    private int pageIdOfGoToPage;
    private int selectedModuleId;
    public Object oldParameter;
    private Map<Integer, Integer> tabMap;
    public List<DataTableColumn> columns = new ArrayList<>();

    //Senkronize ol butonunda açılan dialog parametreleri için class oluşturuldu.
    public class SynchronizeParam {

        private int id;
        private String name;
        private boolean isSelected;

        public SynchronizeParam() {

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isIsSelected() {
            return isSelected;
        }

        public void setIsSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }

    }

    private SynchronizeParam synchronizeParam;
    private List<SynchronizeParam> listOfSynchronizeParameters, listOfSynchronizeParameters1, listOfSynchronizeParameters2;
    private boolean isAll;

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setMarwizService(MarwizService marwizService) {
        this.marwizService = marwizService;
    }

    public void setPcrdTop(PcrdTopService pcrdTop) {
        this.pcrdTop = pcrdTop;
    }

    public PcrdService getPcrd() {
        return pcrd;
    }

    public PcrdTopService getPcrdTop() {
        return pcrdTop;
    }

    public void setPcrd(PcrdService pcrd) {
        this.pcrd = pcrd;
    }

    public Map<Integer, Integer> getTabMap() {
        return tabMap;
    }

    public void setTabMap(Map<Integer, Integer> tabMap) {
        this.tabMap = tabMap;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public String getRenderdPage() {
        return renderdPage;
    }

    public void setRenderdPage(String renderdPage) {
        this.renderdPage = renderdPage;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public LeftMenuService getLeftMenu() {
        return leftMenu;
    }

    public void setLeftMenu(LeftMenuService leftMenu) {
        this.leftMenu = leftMenu;
    }

    public BreadCrumbService getBreadCrumb() {
        return breadCrumb;
    }

    public void setBreadCrumb(BreadCrumbService breadCrumb) {
        this.breadCrumb = breadCrumb;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public String getPageUrlOfGoToPage() {
        return pageUrlOfGoToPage;
    }

    public void setPageUrlOfGoToPage(String pageUrlOfGoToPage) {
        this.pageUrlOfGoToPage = pageUrlOfGoToPage;
    }

    public Object getParamOfGoToPage() {
        return paramOfGoToPage;
    }

    public void setParamOfGoToPage(Object paramOfGoToPage) {
        this.paramOfGoToPage = paramOfGoToPage;
    }

    public int getTypeOfGoToPage() {
        return typeOfGoToPage;
    }

    public void setTypeOfGoToPage(int typeOfGoToPage) {
        this.typeOfGoToPage = typeOfGoToPage;
    }

    public int getPageIdOfGoToPage() {
        return pageIdOfGoToPage;
    }

    public void setPageIdOfGoToPage(int pageIdOfGoToPage) {
        this.pageIdOfGoToPage = pageIdOfGoToPage;
    }

    public int getOldId() {
        return oldId;
    }

    public void setOldId(int oldId) {
        this.oldId = oldId;
    }

    public String getOldUrl() {
        return oldUrl;
    }

    public void setOldUrl(String oldUrl) {
        this.oldUrl = oldUrl;
    }

    public void setProfileService(IProfileService profileService) {
        this.profileService = profileService;
    }

    public void setCheckItemService(CheckItemService checkItemService) {
        this.checkItemService = checkItemService;
    }

    public void setSendStockInfoService(SendStockInfoService sendStockInfoService) {
        this.sendStockInfoService = sendStockInfoService;
    }

    public void setSendSaleService(SendSaleService sendSaleService) {
        this.sendSaleService = sendSaleService;
    }

    public void setSendPurchaseService(SendPurchaseService sendPurchaseService) {
        this.sendPurchaseService = sendPurchaseService;
    }

    public void setSendStockRequestService(SendStockRequestService sendStockRequestService) {
        this.sendStockRequestService = sendStockRequestService;
    }

    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public void setSendPriceChangeRequestService(SendPriceChangeRequestService sendPriceChangeRequestService) {
        this.sendPriceChangeRequestService = sendPriceChangeRequestService;
    }

    public void setParoOfflineSalesService(ParoOfflineSalesService paroOfflineSalesService) {
        this.paroOfflineSalesService = paroOfflineSalesService;
    }

    public List<DataTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DataTableColumn> columns) {
        this.columns = columns;
    }

    public void setSendWasteService(SendWasteService sendWasteService) {
        this.sendWasteService = sendWasteService;
    }

    public void setGetBranchInfoService(GetBranchInfoService getBranchInfoService) {
        this.getBranchInfoService = getBranchInfoService;
    }

    public void setSendOrderService(SendOrderService sendOrderService) {
        this.sendOrderService = sendOrderService;
    }

    public SynchronizeParam getSynchronizeParam() {
        return synchronizeParam;
    }

    public void setSynchronizeParam(SynchronizeParam synchronizeParam) {
        this.synchronizeParam = synchronizeParam;
    }

    public List<SynchronizeParam> getListOfSynchronizeParameters() {
        return listOfSynchronizeParameters;
    }

    public void setListOfSynchronizeParameters(List<SynchronizeParam> listOfSynchronizeParameters) {
        this.listOfSynchronizeParameters = listOfSynchronizeParameters;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public void setCallCampaignInfoService(CallCampaignInfoService callCampaignInfoService) {
        this.callCampaignInfoService = callCampaignInfoService;
    }

    @PostConstruct
    public void init() {
        try {
            //try {
            leftMenu.createBranchs();
            leftMenu.createModules();
            breadCrumb.createBreadcrumb();
            renderdPage = "/pages/general/dashboard.xhtml";
            /* } catch (Exception e) {
            try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            ec.redirect((ec.getApplicationContextPath()));
            
            } catch (IOException ex) {
            Logger.getLogger(Marwiz.class
            .getName()).log(Level.SEVERE, null, ex);
            }
            }*/
            //   getBios();
            //   System.out.println("cpu: "+getWindowsCPU_SerialNumber());
            //   System.out.println("license "+generateLicenseKey());
            tabMap = new HashMap<>();
            synchronizeParam = new SynchronizeParam();
            listOfSynchronizeParameters = new ArrayList<>();
            listOfSynchronizeParameters1 = new ArrayList<>();
            listOfSynchronizeParameters2 = new ArrayList<>();
            listOfSynchronizeParameters = listOfSynchorizeParam();

        } catch (Throwable ex) {
            Logger.getLogger(Marwiz.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<SynchronizeParam> getListOfSynchronizeParameters1() {
        return listOfSynchronizeParameters1;
    }

    public void setListOfSynchronizeParameters1(List<SynchronizeParam> listOfSynchronizeParameters1) {
        this.listOfSynchronizeParameters1 = listOfSynchronizeParameters1;
    }

    public List<SynchronizeParam> getListOfSynchronizeParameters2() {
        return listOfSynchronizeParameters2;
    }

    public void setListOfSynchronizeParameters2(List<SynchronizeParam> listOfSynchronizeParameters2) {
        this.listOfSynchronizeParameters2 = listOfSynchronizeParameters2;
    }

    public ActionListener gotoBackActionListener() {
        return new ActionListener() {
            @Override
            public void processAction(ActionEvent event) throws AbortProcessingException {
//                for (UserData key : applicationBean.getUserPageRecordMap().keySet()) {
//                    System.out.println("***user " + key.getFullName());
//                    System.out.println("***user " + applicationBean.getUserPageRecordMap().get(key).getObject().getClass().getSimpleName());
//                }
                if (sessionBean.parameter != null) {
                    if (sessionBean.parameter.getClass().getSimpleName().equals("ArrayList")) {
                        if (((List<Object>) sessionBean.parameter).size() > 1) {
                            System.out.println("------old url--" + oldUrl);
                            System.out.println("------old ıd--" + oldId);
                            List<Object> list = new ArrayList<>();
                            for (Object object : (List) sessionBean.parameter) {
                                list.add(object);
                            }
                            list.remove(list.size() - 1);
                            goToPage(oldUrl, list, 1, oldId);
                        } else {
                            goToPage(oldUrl, oldParameter, 1, oldId);
                        }
                    } else {

                        goToPage(oldUrl, oldParameter, 1, oldId);

                    }
                } else {
                    goToPage(oldUrl, null, 1, oldId);
                }

            }
        };
    }

    public void goToModule(int id) {
        /*  UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
        Dashboard dshWidget = (Dashboard) root.findComponent("dshWidget");
        if (dshWidget != null) {
            dshWidget.getChildren().clear();
        } */
        if (id != 0) {
            selectedModuleId = id;
            tabMap = new HashMap<>();
            oldId = 0;
            oldUrl = "";
            //  applicationBean.getUserPageRecordMap().remove(sessionBean.getUser());
            sessionBean.setParameter(null);
            resetView();

            renderdPage = "/pages/general/pcrd.xhtml";
            pcrd.createModule(id);
            RequestContext.getCurrentInstance().update("breadcrumb");
            RequestContext.getCurrentInstance().update("mainPanel");
        }
    }

    public boolean goToPage(String pageUrl, Object param, int type, int pageId) {

        if (sessionBean.checkPageAuthentication(pageId)) {
            //Vardiya sayfası, VArdiya Raporu ise veya şubenin market vardiyalarının ödemeleri kapatıldı ise sayfa yönlendirmesine devam et.
            if (pageId == 62) {
                FacesContext context = FacesContext.getCurrentInstance();
                if (context.getViewRoot().findComponent("droppable") != null) {
                    context.getViewRoot().findComponent("droppable").getChildren().clear();
                }
            }
            if (pageId <= 0 || pageId == 66 || pageId == 46 || pageId == 48 || pageId == 49 || pageId == 104 || pageId == 8 || pageId == 21 || pageId == 105 || (!sessionBean.getUser().getLastBranchSetting().isIsShiftControl() || (sessionBean.getUser().getLastBranchSetting().isIsShiftControl() && applicationBean.getBranchShiftPaymentMap().get(sessionBean.getUser().getLastBranch().getId())))) {
                UIViewRoot root = FacesContext.getCurrentInstance().getViewRoot();
                Dashboard dshWidget = (Dashboard) root.findComponent("dshWidget");
                if (dshWidget != null) {
                    dshWidget.getChildren().clear();
                }
                pageIdOfGoToPage = pageId;
                pageUrlOfGoToPage = pageUrl;
                paramOfGoToPage = param;
                typeOfGoToPage = type;
                this.parameter = param;
                if (type != 0) {
                    resetView();
                }
                oldParameter = sessionBean.parameter;
                if (param != null) {
                    sessionBean.parameter = param;
                }
                renderdPage = pageUrl;
                breadCrumb.addItemForPage(breadCrumb, pageId, pageUrl);
                try {
                    String old = ((DefaultMenuItem) breadCrumb.getBreadCrumb().getElements().get(breadCrumb.getBreadCrumb().getElements().size() - 2)).getCommand().substring(26);
                    int iend = old.indexOf("'");
                    if (iend > -1) {
                        String url = old.substring(0, iend);
                        String[] parts = old.split(",");
                        int id = Integer.valueOf(parts[parts.length - 1].substring(0, parts[parts.length - 1].length() - 2));
                        if (id != pageId) {
                            oldId = id;
                            oldUrl = url;

                        }
                    }
                } catch (Exception e) {
                    // System.out.println("this is Search");
                }

                RequestContext.getCurrentInstance().update("mainPanel");
                RequestContext.getCurrentInstance().update("breadcrumb");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseyouentershiftpayments")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, sessionBean.loc.getString("warning"), sessionBean.loc.getString("youarenotallowedtoenterthispage")));
            RequestContext.getCurrentInstance().update("authenticationMessege");
            return false;
        }

        return true;
    }

    public void settabIndex(int id) {
        tabMap.put(pageIdOfGoToPage, id);
    }

    public int getTabIndex() {
        if (tabMap.get(pageIdOfGoToPage) != null) {
            return tabMap.get(pageIdOfGoToPage);
        } else {
            return 0;
        }
    }

    public boolean checckBeforeMove(Object param, int pageId) {
        if (sessionBean.checkPageAuthentication(pageId)) {
            return true;
        }
        return false;

    }

    /**
     *
     * @param fileName
     * @return
     */
    public boolean fileExists(String fileName) {
        if (fileName != null) {
            URL resource;
            try {
                resource = FacesContext.getCurrentInstance().getExternalContext().getResource(fileName);
                if (resource != null) {
                    return true;

                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(Marwiz.class
                          .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * clear view objects
     */
    public void resetView() {
        FacesContext.getCurrentInstance().getViewRoot().getViewMap().clear();

    }

    /**
     * İnsert ve update işlemlerinin başarılı ve başarısız olma durumlarını
     * kontrol ederek, kullanıcıya growl ile gösteren fonksiyondur.
     *
     * @param result welcome fonksiyonundan dönen 0:update başarılı, >0: insert
     * başarılı, -1:update başarısız, -2:insert başarısız değerlerini temsil
     * eder.
     */
    public void showProcessMessage(int result) {
        FacesMessage message = new FacesMessage();
        if (result >= 0) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("succesfuloperation"));
        } else if (result == -1 || result == -2) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, sessionBean.loc.getString("error"), sessionBean.loc.getString("unsuccesfuloperation"));
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void include(String container, String url) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FaceletContext faceletContext = (FaceletContext) facesContext.getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        try {
            faceletContext.includeFacelet(facesContext.getViewRoot().findComponent(container), url);

        } catch (IOException ex) {
            Logger.getLogger(Marwiz.class
                      .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Kullanıcının renk temasını seçmesini ve bu kaydı veritabanına kayıt
     * etmesini sağlar.
     *
     * @param themeName seçilen temanın ismini barındıran değişkendir.
     *
     */
    public void themeChange(String themeName) {
        String appName = FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath();
        RequestContext.getCurrentInstance().execute("$(\".themeColor\").remove(); $(\"head\").append('<link class=\"themeColor\" type=\"text/css\" rel=\"stylesheet\" href=\"" + appName + "/javax.faces.resource/theme/" + themeName + ".css.xhtml?ln=sentinel-layout\">')");
        profileService.themeChange(themeName);
        sessionBean.getUser().setLastTheme(themeName);
    }

    /**
     * Aç / kapat menüler
     *
     * @param tip menü tipi
     */
    public void changeMenuState(int tip) {
        MenuModel m;
        boolean expanded;
        switch (tip) {
            case 0:
                m = pcrd.process;
                expanded = pcrd.expandedProcess;
                pcrd.expandedProcess = (!pcrd.expandedProcess);
                break;
            case -1:
                m = pcrd.card;
                expanded = pcrd.expandedCard;
                pcrd.expandedCard = (!pcrd.expandedCard);
                break;
            case -2:
                m = pcrd.report;
                expanded = pcrd.expandedReport;
                pcrd.expandedReport = (!pcrd.expandedReport);
                break;
            case -3:
                m = pcrd.definition;
                expanded = pcrd.expandedDefinition;
                pcrd.expandedDefinition = (!pcrd.expandedDefinition);
                break;
            default:
                m = pcrdTop.getPcrdTopMenues().get(tip);
                expanded = pcrdTop.expanded;
                pcrdTop.expanded = (!pcrdTop.expanded);
                break;
        }
        if (m != null) {
            m.getElements().stream().map((menuElement) -> (DefaultSubMenu) menuElement).filter((subMenu) -> (!subMenu.isExpanded() == expanded)).forEach((subMenu) -> {
                subMenu.setExpanded(expanded);
            });
        }
    }

    /**
     * bu metod top menu açmak için
     *
     * @param id menu tipi
     */
    public void goToPcrdTop(int id) {
        resetView();
        renderdPage = "/pages/general/pcrdtop.xhtml";
        pcrdTop.createPcrdTop(id);
        RequestContext.getCurrentInstance().update("breadcrumb");
        RequestContext.getCurrentInstance().update("mainPanel");
    }

    /**
     * bu metod My menu açmak için
     *
     */
    public String goToPcrdMyMenu() {
        resetView();
        breadCrumb.addItemForMyMenu(breadCrumb);
        renderdPage = "/pages/general/pcrdmymenu.xhtml";
        RequestContext.getCurrentInstance().update("breadcrumb");
        RequestContext.getCurrentInstance().update("mainPanel");
        // createDyanmicPage(0) ;
        return null;//"/pages/marwiz?faces-redirect=true";
    }

    public String updateBranch(int branchId) {
        //  System.out.println("update branch "+ branchId);
        String rowSelect = sessionBean.getUser().getGridRowSelect();
        sessionBean.setUser(marwizService.updateBranch(sessionBean.getUser().getUsername(), branchId));
        sessionBean.getUser().getLastBranch().setCurrency(sessionBean.getCurrency(sessionBean.getUser().getLastBranch().getCurrency().getId()));
        sessionBean.getUser().setLastBranchSetting(applicationBean.getBranchSettingMap().get(sessionBean.getUser().getLastBranch().getId()));

        sessionBean.getUser().setGridRowSelect(rowSelect);

        leftMenu.createBranchs();
        leftMenu.createModules();
        goToPage("/pages/general/dashboard.xhtml", null, 1, 0);
        return "/pages/marwiz?faces-redirect=true";

    }

    public void doSearch() {
        resetView();
        renderdPage = "/pages/general/pcrd.xhtml";
        pcrd.process = new DefaultMenuModel();
        pcrd.card = new DefaultMenuModel();
        pcrd.report = new DefaultMenuModel();
        pcrd.definition = new DefaultMenuModel();
        breadCrumb.addItemForSearchResult(breadCrumb, searchText);
        for (Module m : sessionBean.getAuthorizedModules()) {
            DefaultSubMenu moduleSubmenu0 = new DefaultSubMenu(m.getNameMap().get(sessionBean.getLangId()).getName());
            DefaultSubMenu moduleSubmenu1 = new DefaultSubMenu(m.getNameMap().get(sessionBean.getLangId()).getName());
            DefaultSubMenu moduleSubmenu2 = new DefaultSubMenu(m.getNameMap().get(sessionBean.getLangId()).getName());
            DefaultSubMenu moduleSubmenu3 = new DefaultSubMenu(m.getNameMap().get(sessionBean.getLangId()).getName());

            for (Folder f : m.getFolders()) {

                int i = 0;

                switch (f.getType()) {
                    //process
                    case 0:
                        for (Page p : f.getPages()) {
                            System.out.println("*-*-*-* " + p.getParent_id());
                            if (p.getNameMap().get(sessionBean.getLangId()) != null) {
                                if (p.getNameMap().
                                          get(sessionBean.getLangId()).
                                          getName().
                                          toLowerCase().
                                          contains(searchText.toLowerCase()) && p.getParent_id() == null) {
                                    DefaultMenuItem menuitem = new DefaultMenuItem(p.getNameMap().get(sessionBean.getLangId()).getName(), "fa icon-doc-text");
                                    menuitem.setStyleClass("pageSubMenu");
                                    menuitem.setId(String.valueOf(p.getId()));
                                    menuitem.setCommand(String.format("#{marwiz.goToPage('" + p.getUrl() + "',null,1," + p.getId() + ")}"));
                                    moduleSubmenu0.addElement(menuitem);
                                    i++;
                                }
                            }
                        }
                        break;
                    //card
                    case 1:
                        for (Page p : f.getPages()) {
                            if (p.getNameMap().get(sessionBean.getLangId()).getName().toLowerCase().contains(searchText.toLowerCase()) && p.getParent_id() == null) {
                                DefaultMenuItem menuitem = new DefaultMenuItem(p.getNameMap().get(sessionBean.getLangId()).getName(), "fa icon-doc-text");
                                menuitem.setStyleClass("pageSubMenu");
                                menuitem.setId(String.valueOf(p.getId()));
                                menuitem.setCommand(String.format("#{marwiz.goToPage('" + p.getUrl() + "',null,1," + p.getId() + ")}"));
                                moduleSubmenu1.addElement(menuitem);
                                i++;
                            }
                        }
                        break;
                    //report

                    case 2:
                        for (Page p : f.getPages()) {
                            if (p.getNameMap().get(sessionBean.getLangId()).getName().toLowerCase().contains(searchText.toLowerCase()) && p.getParent_id() == null) {
                                DefaultMenuItem menuitem = new DefaultMenuItem(p.getNameMap().get(sessionBean.getLangId()).getName(), "fa icon-doc-text");
                                menuitem.setStyleClass("pageSubMenu");
                                menuitem.setId(String.valueOf(p.getId()));
                                menuitem.setCommand(String.format("#{marwiz.goToPage('" + p.getUrl() + "',null,1," + p.getId() + ")}"));
                                moduleSubmenu2.addElement(menuitem);
                                i++;
                            }
                        }
                        break;
                    //definition

                    case 3:
                        for (Page p : f.getPages()) {
                            if (p.getNameMap().get(sessionBean.getLangId()).getName().toLowerCase().contains(searchText.toLowerCase()) && p.getParent_id() == null) {
                                DefaultMenuItem menuitem = new DefaultMenuItem(p.getNameMap().get(sessionBean.getLangId()).getName(), "fa icon-doc-text");
                                menuitem.setStyleClass("pageSubMenu");
                                menuitem.setId(String.valueOf(p.getId()));
                                menuitem.setCommand(String.format("#{marwiz.goToPage('" + p.getUrl() + "',null,1," + p.getId() + ")}"));
                                moduleSubmenu3.addElement(menuitem);
                                i++;
                            }
                        }
                        break;

                    default:
                        break;
                }

            }
            if (moduleSubmenu0.getElements().size() > 0) {
                pcrd.process.addElement(moduleSubmenu0);
                moduleSubmenu0.setExpanded(true);
            }
            if (moduleSubmenu1.getElements().size() > 0) {
                pcrd.card.addElement(moduleSubmenu1);
                moduleSubmenu1.setExpanded(true);
            }
            if (moduleSubmenu2.getElements().size() > 0) {
                pcrd.report.addElement(moduleSubmenu2);
                moduleSubmenu2.setExpanded(true);
            }
            if (moduleSubmenu3.getElements().size() > 0) {
                pcrd.definition.addElement(moduleSubmenu3);
                moduleSubmenu3.setExpanded(true);
            }

        }
        RequestContext.getCurrentInstance().update("breadcrumb");
        RequestContext.getCurrentInstance().update("mainPanel");
    }

    public void gotoNotes() {
        goToPage("/pages/general/notes.xhtml", null, 1, -3);
    }

    public void synchronize() {
        RequestContext.getCurrentInstance().update("frmtopbar:dlgSynchronizeProcess");
        RequestContext.getCurrentInstance().execute("PF('dlg_synchronizeprocess').show();");

    }

    public boolean checkAuthentication(int pageId, int userId, int proccessId) {
        for (Authentication a : sessionBean.getUser().getAuthentications()) {
            if (a.getPageId() == pageId && a.getUserId() == userId && a.getList().contains(proccessId)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAuthentication(int proccessId) {
        boolean process;
        for (Page p : applicationBean.getListOfPages()) {
            if (p.getId() == pageIdOfGoToPage) {
                if (p.getParent_id() != null) {
                    process = true;
                }
                break;
            }
        }
        for (Authentication a : sessionBean.getUser().getAuthentications()) {
            System.out.println(proccessId + " - " + a.getList() + " - " + a.getPageId());
            if (a.getPageId() == pageIdOfGoToPage && a.getUserId() == sessionBean.getUser().getId() && a.getList().contains(proccessId)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAuthentication() {
        boolean process = checkIfProcessPage(pageIdOfGoToPage);
        System.out.println(pageIdOfGoToPage + " *-*-*-*- process : " + process);
        for (Authentication a : sessionBean.getUser().getAuthentications()) {
            System.out.println(a.getList() + " - " + a.getPageId());
            if (process) {
                if (a.getPageId() == pageIdOfGoToPage && (a.getUserId() == sessionBean.getUser().getId()) && a.getList().contains(2)) {
                    System.out.println("*--*-*-  test   " + a.getList().contains(2));
                    return true;
                }

            } else if (a.getPageId() == pageIdOfGoToPage && (a.getUserId() == sessionBean.getUser().getId()) && a.getList().contains(1)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkIfProcessPage(int pageId) {
        for (Module m : applicationBean.getListOfModules()) {
            for (Folder f : m.getFolders()) {
                for (Page p : f.getPages()) {
                    if (p.getId() == pageIdOfGoToPage) {
                        System.out.println("paren " + p.getParent_id());
                        /* if (p.getParent_id() != null) {
                            return true;
                        }*/
                        return false;
                    } else if (p.getSubPages() != null) {
                        for (Page subPage : p.getSubPages()) {
                            if (subPage.getId() == pageIdOfGoToPage) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<SynchronizeParam> listOfSynchorizeParam() {

        listOfSynchronizeParameters = new ArrayList<>();
        listOfSynchronizeParameters1.clear();
        listOfSynchronizeParameters2.clear();

        SynchronizeParam obj1 = new SynchronizeParam();
        obj1.setId(1);
        obj1.setName(sessionBean.getLoc().getString("getexchangerates"));
        listOfSynchronizeParameters1.add(obj1);

        SynchronizeParam obj26 = new SynchronizeParam();
        obj26.setId(26);
        obj26.setName(sessionBean.getLoc().getString("getvideos"));
        listOfSynchronizeParameters1.add(obj26);

        SynchronizeParam obj6 = new SynchronizeParam();
        obj6.setId(6);
        obj6.setName(sessionBean.getLoc().getString("getbrands"));
        listOfSynchronizeParameters1.add(obj6);

        SynchronizeParam obj7 = new SynchronizeParam();
        obj7.setId(7);
        obj7.setName(sessionBean.getLoc().getString("getunits"));
        listOfSynchronizeParameters1.add(obj7);

        SynchronizeParam obj8 = new SynchronizeParam();
        obj8.setId(8);
        obj8.setName(sessionBean.getLoc().getString("gettaxgroups"));
        listOfSynchronizeParameters1.add(obj8);

        SynchronizeParam obj9 = new SynchronizeParam();
        obj9.setId(9);
        obj9.setName(sessionBean.getLoc().getString("getcentralsuppliers"));
        listOfSynchronizeParameters1.add(obj9);

        SynchronizeParam obj10 = new SynchronizeParam();
        obj10.setId(10);
        obj10.setName(sessionBean.getLoc().getString("getstocks"));
        listOfSynchronizeParameters1.add(obj10);

        SynchronizeParam obj27 = new SynchronizeParam();
        obj27.setId(27);
        obj27.setName(sessionBean.getLoc().getString("getcentralcategories"));
        listOfSynchronizeParameters1.add(obj27);

        SynchronizeParam obj12 = new SynchronizeParam();
        obj12.setId(12);
        obj12.setName(sessionBean.getLoc().getString("getnotifications"));
        listOfSynchronizeParameters1.add(obj12);

        SynchronizeParam obj17 = new SynchronizeParam();
        obj17.setId(17);
        obj17.setName(sessionBean.getLoc().getString("getcampaigns"));
        listOfSynchronizeParameters1.add(obj17);

        SynchronizeParam obj18 = new SynchronizeParam();
        obj18.setId(18);
        obj18.setName(sessionBean.getLoc().getString("getaccounts"));
        listOfSynchronizeParameters1.add(obj18);

        SynchronizeParam obj19 = new SynchronizeParam();
        obj19.setId(19);
        obj19.setName(sessionBean.getLoc().getString("getwastereasondefinitions"));
        listOfSynchronizeParameters1.add(obj19);

        SynchronizeParam obj20 = new SynchronizeParam();
        obj20.setId(20);
        obj20.setName(sessionBean.getLoc().getString("getstarbucksproducts"));
        listOfSynchronizeParameters1.add(obj20);

        SynchronizeParam obj21 = new SynchronizeParam();
        obj21.setId(21);
        obj21.setName(sessionBean.getLoc().getString("getbranchinformation"));
        listOfSynchronizeParameters1.add(obj21);

        SynchronizeParam obj14 = new SynchronizeParam();
        obj14.setId(14);
        obj14.setName(sessionBean.getLoc().getString("getpricechanges"));
        listOfSynchronizeParameters1.add(obj14);

        SynchronizeParam obj23 = new SynchronizeParam();
        obj23.setId(23);
        obj23.setName(sessionBean.getLoc().getString("getcurrencies"));
        listOfSynchronizeParameters1.add(obj23);

        SynchronizeParam obj24 = new SynchronizeParam();
        obj24.setId(24);
        obj24.setName(sessionBean.getLoc().getString("getcampaigninfo"));
        listOfSynchronizeParameters1.add(obj24);

        SynchronizeParam obj25 = new SynchronizeParam();
        obj25.setId(25);
        obj25.setName(sessionBean.getLoc().getString("getparocampignsettings"));
        listOfSynchronizeParameters1.add(obj25);

        SynchronizeParam obj5 = new SynchronizeParam();
        obj5.setId(5);
        obj5.setName(sessionBean.getLoc().getString("checkstockrequests"));
        listOfSynchronizeParameters1.add(obj5);

        SynchronizeParam obj2 = new SynchronizeParam();
        obj2.setId(2);
        obj2.setName(sessionBean.getLoc().getString("sendunsentsales"));
        listOfSynchronizeParameters2.add(obj2);

        SynchronizeParam obj3 = new SynchronizeParam();
        obj3.setId(3);
        obj3.setName(sessionBean.getLoc().getString("sendunsentpurchases"));
        listOfSynchronizeParameters2.add(obj3);

        SynchronizeParam obj4 = new SynchronizeParam();
        obj4.setId(4);
        obj4.setName(sessionBean.getLoc().getString("submitunsentstockrequests"));
        listOfSynchronizeParameters2.add(obj4);

        SynchronizeParam obj11 = new SynchronizeParam();
        obj11.setId(11);
        obj11.setName(sessionBean.getLoc().getString("sendproductinformation"));
        listOfSynchronizeParameters2.add(obj11);

        SynchronizeParam obj13 = new SynchronizeParam();
        obj13.setId(13);
        obj13.setName(sessionBean.getLoc().getString("sendpricechanges"));
        listOfSynchronizeParameters2.add(obj13);

        SynchronizeParam obj15 = new SynchronizeParam();
        obj15.setId(15);
        obj15.setName(sessionBean.getLoc().getString("sendparosales"));
        listOfSynchronizeParameters2.add(obj15);

        SynchronizeParam obj16 = new SynchronizeParam();
        obj16.setId(16);
        obj16.setName(sessionBean.getLoc().getString("sendwastes"));
        listOfSynchronizeParameters2.add(obj16);

        SynchronizeParam obj22 = new SynchronizeParam();
        obj22.setId(22);
        obj22.setName(sessionBean.getLoc().getString("sendordersthathavenotbeensent"));
        listOfSynchronizeParameters2.add(obj22);

        listOfSynchronizeParameters.addAll(listOfSynchronizeParameters1);
        listOfSynchronizeParameters.addAll(listOfSynchronizeParameters2);

        return listOfSynchronizeParameters;

    }

    public void isAll() {

        if (isAll) {

            if (!listOfSynchronizeParameters.isEmpty()) {
                for (SynchronizeParam obj : listOfSynchronizeParameters) {
                    obj.setIsSelected(true);
                }
            }
        } else {

            if (!listOfSynchronizeParameters.isEmpty()) {
                for (SynchronizeParam obj : listOfSynchronizeParameters) {
                    obj.setIsSelected(false);
                }
            }
        }
        RequestContext.getCurrentInstance().update("frmSynchronizeProcess:pgrSynchronizeProcess");
    }

    public void synchronizeDetail() {
        boolean isSelected = false;

        for (SynchronizeParam sync : listOfSynchronizeParameters) {
            if (sync.isSelected) {
                isSelected = true;
                break;
            }
        }

        if (isSelected) {

            for (SynchronizeParam obj : listOfSynchronizeParameters) {

                switch (obj.getId()) {
                    case 1:
                        if (obj.isSelected) {
                            exchangeService.updateExchange();
                        }
                        break;
                    case 2:
                        if (obj.isSelected) {
                            sendSaleService.sendSaleNotSendedToCenterAsync();
                        }
                        break;
                    case 3:
                        if (obj.isSelected) {
                            sendPurchaseService.sendPurchaseNotSendedToCenterAsync();
                        }
                        break;
                    case 4:
                        if (obj.isSelected) {
                            sendStockRequestService.sendNotSendedStockRequestAsync();
                        }
                        break;
                    case 5:
                        if (obj.isSelected) {
                            sendStockRequestService.checkStockRequestAsync();
                        }
                        break;
                    case 6:
                        if (obj.isSelected) {
                            checkItemService.listBrand();
                        }
                        break;
                    case 7:
                        if (obj.isSelected) {
                            checkItemService.listUnit();
                        }
                        break;
                    case 8:
                        if (obj.isSelected) {
                            checkItemService.listTax();
                        }
                        break;
                    case 9:
                        if (obj.isSelected) {
                            checkItemService.listCentralSupplier();
                        }
                        break;
                    case 10:
                        if (obj.isSelected) {
                            checkItemService.listStock();
                        }
                        break;
                    case 11:
                        if (obj.isSelected) {
                            sendStockInfoService.sendStockInfoAsync();
                        }
                        break;
                    case 12:
                        if (obj.isSelected) {
                            checkItemService.listNotificationAsync();
                        }
                        break;
                    case 13:
                        if (obj.isSelected) {
                            sendPriceChangeRequestService.sendNotSendedPriceChangeRequestAsync();
                        }
                        break;
                    case 14:
                        if (obj.isSelected) {
                            sendPriceChangeRequestService.checkPriceChangeRequestAsync();
                        }
                        break;
                    case 15:
                        if (obj.isSelected) {
                            paroOfflineSalesService.sendSalesAsync();
                        }
                        break;
                    case 16:
                        if (obj.isSelected) {
                            sendWasteService.sendWasteAsync();
                        }
                        break;
                    case 17:
                        if (obj.isSelected) {
                            checkItemService.listCampaign();
                        }
                        break;
                    case 18:
                        if (obj.isSelected) {
                            checkItemService.listAccount();
                        }
                        break;
                    case 19:
                        if (obj.isSelected) {
                            checkItemService.listWasteReason();
                        }
                        break;
                    case 20:
                        if (obj.isSelected) {
                            checkItemService.listStarbucksStock();
                        }
                        break;
                    case 21:
                        if (obj.isSelected) {
                            getBranchInfoService.callBranchInfoForAllBranches();
                        }
                        break;
                    case 22:
                        if (obj.isSelected) {
                            sendOrderService.sendOrderNotSendedToCenterAsync();
                        }
                        break;
                    case 23:
                        if (obj.isSelected) {
                            checkItemService.listCurrency();
                        }
                        break;
                    case 24:
                        if (obj.isSelected) {
                            checkItemService.listCampaingInfo();
                        }
                        break;
                    case 25:
                        if (obj.isSelected) {
                            callCampaignInfoService.callBranchCampaignInfoForAllBranches();
                        }
                        break;
                    case 26:
                        if (obj.isSelected) {
                            checkItemService.listVideos();
                        }
                        break;
                    case 27:
                        if (obj.isSelected) {
                            checkItemService.listCentralCategories();
                        }
                        break;
                    default:
                        break;
                }
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, sessionBean.loc.getString("notification"), sessionBean.loc.getString("synchronizationprocesscompleted")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

            RequestContext.getCurrentInstance().execute("PF('dlg_synchronizeprocess').hide();");

        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectatleastoneprocess")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");

        }

        for (SynchronizeParam obj : listOfSynchronizeParameters) {
            obj.setIsSelected(false);
        }
        if (isAll) {
            isAll = false;
        }

    }

}
