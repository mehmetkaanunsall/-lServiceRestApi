/*
 * Bu sınıf stok sayfasının vergi grupları tabı için yazılmıştır.
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.history.business.HistoryService;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockTaxGroupConnection;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.inventory.stock.business.IStockTaxGroupService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class StockTaxGroupsTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{marwiz}")  //marwiz
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockTaxGroupService}")
    public IStockTaxGroupService stockTaxGroupService;

    @ManagedProperty(value = "#{historyService}")
    public HistoryService historyService;

    private List<StockTaxGroupConnection> stocktaxgroupList;
    private StockTaxGroupConnection stocktaxgroup;
    private List<TaxGroup> taxGroupList;
    private Stock selectedObject;
    private int processType;
    private boolean isPurchase;
    private List<History> listOfHistoryObjects;
    private String createdPerson;
    private String createdDate;

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setStockTaxGroupService(IStockTaxGroupService stockTaxGroupService) {
        this.stockTaxGroupService = stockTaxGroupService;
    }

    public List<StockTaxGroupConnection> getStocktaxgroupList() {
        return stocktaxgroupList;
    }

    public void setStocktaxgroupList(List<StockTaxGroupConnection> stocktaxgroupList) {
        this.stocktaxgroupList = stocktaxgroupList;
    }

    public List<TaxGroup> getTaxGroupList() {
        return taxGroupList;
    }

    public void setTaxGroupList(List<TaxGroup> taxGroupList) {
        this.taxGroupList = taxGroupList;
    }

    public StockTaxGroupConnection getStocktaxgroup() {
        return stocktaxgroup;
    }

    public void setStocktaxgroup(StockTaxGroupConnection stocktaxgroup) {
        this.stocktaxgroup = stocktaxgroup;
    }

    public Stock getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(Stock selectedObject) {
        this.selectedObject = selectedObject;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<History> getListOfHistoryObjects() {
        return listOfHistoryObjects;
    }

    public void setListOfHistoryObjects(List<History> listOfHistoryObjects) {
        this.listOfHistoryObjects = listOfHistoryObjects;
    }
   
    public String getCreatedPerson() {
        return createdPerson;
    }

    public void setCreatedPerson(String createdPerson) {
        this.createdPerson = createdPerson;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * Stoğun vergi grupları listesini çeker.Butonların yetki kontrolünü yazar.
     */
    @PostConstruct
    public void init() {

        System.out.println("--StockTaxGroupsTabBean----");
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Stock) {
                    selectedObject = (Stock) ((ArrayList) sessionBean.parameter).get(i);
                    stocktaxgroupList = stockTaxGroupService.listStokTaxGroup(selectedObject);
                }
            }
        }

       setListBtn(sessionBean.checkAuthority(new int[]{126, 127, 128}, 0));

    }

    /**
     * vergi grubu eklemek ya da güncelemek için dialog açar.
     *
     * @param type
     */
    public void createDialog(int type) {

        processType = type;
        listOfHistoryObjects=new ArrayList<>();

        if (type == 1) { //ekle
            stocktaxgroup = new StockTaxGroupConnection();
            stocktaxgroup.setStock(selectedObject);
            selectTaxGroup();
        } else {
            selectTaxGroup();
            isPurchase = stocktaxgroup.isIsPurchase();
        }
        RequestContext.getCurrentInstance().execute("PF('dlg_stocktaxgroupproc').show()");
    }

    /**
     * Kaydet butonuna basıldığında işlem tipine göre ekleme ya da güncelleme
     * yapar.
     */
    public void save() {

        stocktaxgroup.setIsPurchase(isPurchase);

        boolean isThere = false;
        bringTaxGroup();
        for (StockTaxGroupConnection stockTaxGroupConnection : stocktaxgroupList) {
            if (stockTaxGroupConnection.isIsPurchase() == stocktaxgroup.isIsPurchase() && stockTaxGroupConnection.getTaxGroup().getType().getId() == stocktaxgroup.getTaxGroup().getType().getId() && stocktaxgroup.getId() != stockTaxGroupConnection.getId()) {

                isThere = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("thereisataxgroupofthistype")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");

                break;

            }

        }
        if (!isThere) {

            int result = 0;
            if (processType == 1) {
                result = stockTaxGroupService.create(stocktaxgroup);
                if (result > 0) {
                    stocktaxgroup.setId(result);
                    UserData userData = new UserData();
                    userData.setId(sessionBean.getUser().getId());
                    userData.setName(sessionBean.getUser().getName());
                    userData.setSurname(sessionBean.getUser().getSurname());
                    userData.setUsername(sessionBean.getUser().getUsername());
                    userData.setDateCreated(new Date());

                    stocktaxgroup.setUserCreated(userData);
                    stocktaxgroup.setDateCreated(userData.getDateCreated());
                    stocktaxgroupList.add(stocktaxgroup);
                }
            } else {
                result = stockTaxGroupService.update(stocktaxgroup);

            }

            if (result > 0) {
                bringTaxGroup();
                RequestContext.getCurrentInstance().execute("PF('dlg_stocktaxgroupproc').hide()");
                RequestContext.getCurrentInstance().update("tbvStokProc:frmTaxGroup:dtbTaxGroup");

            }
            sessionBean.createUpdateMessage(result);
        }

    }

    /**
     * vergi tipi değiştikce listeyi çekip yeniler. (satın alma veya satış)
     */
    public void selectTaxGroup() {

        if (processType == 1) { // ekleme ise daha önce eklenmiş vergi gruplarının tipindeki vergi grupları gelmicek.
            taxGroupList = stockTaxGroupService.selectStokTaxGroup(selectedObject, isPurchase);
        } else if (processType == 2) { // güncelleme ise daha önce eklenmiş vergi gruplarının tipindeki vergi grupları gelmicek. Ama kendi tipindeki vergi grupları gelcek.
            taxGroupList = stockTaxGroupService.selectStokTaxGroup(selectedObject, isPurchase, stocktaxgroup.getTaxGroup().getType().getId());
        }

    }

    /**
     * Vergi grubunu kaydettikten sonra adını set ederek gridde görünmesini
     * sağlar.
     */
    public void bringTaxGroup() {
        for (TaxGroup tg : taxGroupList) {
            if (tg.getId() == stocktaxgroup.getTaxGroup().getId()) {
                stocktaxgroup.getTaxGroup().setName(tg.getName());
                stocktaxgroup.getTaxGroup().setRate(tg.getRate());
                stocktaxgroup.getTaxGroup().getType().setId(tg.getType().getId());

            }
        }
    }

    public void goToHistory() {
        System.out.println("---------stocktaxgroup--------" + stocktaxgroup.getId());
        System.out.println("-----------date-------" + stocktaxgroup.getDateCreated());
        System.out.println("+++++++++++++stocktaxgroup.getUserCreated().getFullName()+++++++++" + stocktaxgroup.getUserCreated().getFullName());
        System.out.println("-----geliyor mu ");
        listOfHistoryObjects = historyService.findAll(0, 0, null, "", stocktaxgroup.getId(), "inventory.stock_taxgroup_con", 0);

        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), stocktaxgroup.getDateCreated());
        createdPerson = stocktaxgroup.getUserCreated().getFullName() + " - " + stocktaxgroup.getUserCreated().getUsername();
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");

    }


    public void delete() {
        int result = 0;
        result = stockTaxGroupService.delete(stocktaxgroup);
        if (result > 0) {
            stocktaxgroupList.remove(stocktaxgroup);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_stocktaxgroupproc').hide();");
            context.update("tbvStokProc:frmTaxGroup:dtbTaxGroup");
        }
        sessionBean.createUpdateMessage(result);
    }

}
