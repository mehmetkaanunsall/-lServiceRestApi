/**
 *
 * @author Emrullah YAKIŞAN
 *
 * @date 09.04.2019 08:15:47
 */
package com.mepsan.marwiz.finance.discount.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.Discount;
import com.mepsan.marwiz.general.model.finance.DiscountItem;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import com.mepsan.marwiz.finance.discount.business.IDiscountItemService;
import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.history.business.IHistoryService;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.History;
import com.mepsan.marwiz.general.model.inventory.PriceList;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.inventory.pricelist.business.IPriceListService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class DiscountDiscountTabBean extends AuthenticationLists {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{discountItemService}")
    private IDiscountItemService discountItemService;

    @ManagedProperty(value = "#{brandService}")
    private IBrandService brandService;

    @ManagedProperty(value = "#{priceListService}")
    private IPriceListService priceListService;

    @ManagedProperty(value = "#{historyService}")
    public IHistoryService historyService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setDiscountItemService(IDiscountItemService discountItemService) {
        this.discountItemService = discountItemService;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public void setPriceListService(IPriceListService priceListService) {
        this.priceListService = priceListService;
    }

    public void setHistoryService(IHistoryService historyService) {
        this.historyService = historyService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    private List<DiscountItem> listOfObjects;
    private DiscountItem selectedObject;
    private Discount selectedDiscount;

    private int processType;
    private List<String> listOfDaysOfMonth;
    private List<String> listOfDays;
    private List<String> listOfMonths;

    private List<Brand> listOfBrand;

    private List<PriceList> listOfPriceList;

    private List<History> listOfHistoryObjects;
    private String createdPerson;
    private String createdDate;

    private boolean isAmount;
    private boolean isPromotionStock;
    private String selectedNecessaryStockCount;
    private String selectedPromotionStockCount;

    public List<DiscountItem> getListOfObjects() {
        return listOfObjects;
    }

    public void setListOfObjects(List<DiscountItem> listOfObjects) {
        this.listOfObjects = listOfObjects;
    }

    public DiscountItem getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(DiscountItem selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Discount getSelectedDiscount() {
        return selectedDiscount;
    }

    public void setSelectedDiscount(Discount selectedDiscount) {
        this.selectedDiscount = selectedDiscount;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public List<String> getListOfDaysOfMonth() {
        return listOfDaysOfMonth;
    }

    public void setListOfDaysOfMonth(List<String> listOfDaysOfMonth) {
        this.listOfDaysOfMonth = listOfDaysOfMonth;
    }

    public List<String> getListOfDays() {
        return listOfDays;
    }

    public void setListOfDays(List<String> listOfDays) {
        this.listOfDays = listOfDays;
    }

    public List<String> getListOfMonths() {
        return listOfMonths;
    }

    public void setListOfMonths(List<String> listOfMonths) {
        this.listOfMonths = listOfMonths;
    }

    public List<Brand> getListOfBrand() {
        return listOfBrand;
    }

    public void setListOfBrand(List<Brand> listOfBrand) {
        this.listOfBrand = listOfBrand;
    }

    public List<PriceList> getListOfPriceList() {
        return listOfPriceList;
    }

    public void setListOfPriceList(List<PriceList> listOfPriceList) {
        this.listOfPriceList = listOfPriceList;
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

    public boolean isIsAmount() {
        return isAmount;
    }

    public void setIsAmount(boolean isAmount) {
        this.isAmount = isAmount;
    }

    public String getSelectedNecessaryStockCount() {
        return selectedNecessaryStockCount;
    }

    public void setSelectedNecessaryStockCount(String selectedNecessaryStockCount) {
        this.selectedNecessaryStockCount = selectedNecessaryStockCount;
    }

    public String getSelectedPromotionStockCount() {
        return selectedPromotionStockCount;
    }

    public void setSelectedPromotionStockCount(String selectedPromotionStockCount) {
        this.selectedPromotionStockCount = selectedPromotionStockCount;
    }

    @PostConstruct
    public void init() {
        System.out.println("----DiscountDiscountTabBean");
        if (sessionBean.parameter instanceof Discount) {
            selectedDiscount = (Discount) sessionBean.parameter;
            selectedObject = new DiscountItem();
            listOfObjects = discountItemService.listofDiscountItem(selectedDiscount);
            for (DiscountItem discountItem : listOfObjects) {
                discountItemService.customizeDayMonth(discountItem);
            }

        }
        
        setListBtn(sessionBean.checkAuthority(new int[]{98, 99 ,100}, 0));
    }

    public void createDialog(int type) {

        processType = type;
        listOfBrand = brandService.findAll(new Item(2));
        listOfDaysOfMonth = discountItemService.listMonthDay();
        listOfDays = discountItemService.listDay();
        listOfMonths = discountItemService.listMonth();

        selectedObject.getPromotionStockList().clear();
        selectedObject.getNecessaryStockList().clear();
        selectedObject.getPromotionBrandList().clear();
        selectedObject.getNecessaryBrandList().clear();
        
        listOfHistoryObjects=new ArrayList<>();

        RequestContext context = RequestContext.getCurrentInstance();
        listOfPriceList = priceListService.listofPriceList();

        if (processType == 1) { //ekle
            selectedObject = new DiscountItem();
            isAmount = true;
            setSelectedPromotionStockCount(sessionBean.loc.getString("choose"));
            setSelectedNecessaryStockCount(sessionBean.loc.getString("choose"));

            Calendar calendar = GregorianCalendar.getInstance();
            selectedObject.setBeginDate(calendar.getTime());
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            selectedObject.setEndDate(calendar.getTime());
            selectedObject.setSaleCount(1);
        } else if (processType == 2) {
            discountItemService.customizeDayMonth(selectedObject);

            selectedObject.setNecessaryStockList(discountItemService.convertStock(selectedObject.getNecessaryStocks()));
            selectedObject.setPromotionStockList(discountItemService.convertStock(selectedObject.getPromotionStocks()));

            selectedObject.setNecessaryBrandList(discountItemService.convertBrand(selectedObject.getNecessaryBrands(), listOfBrand));
            selectedObject.setPromotionBrandList(discountItemService.convertBrand(selectedObject.getPromotionBrands(), listOfBrand));

            if (selectedObject.getNecessaryStockList().isEmpty()) {
                setSelectedNecessaryStockCount(sessionBean.loc.getString("choose"));
            } else if (selectedObject.getNecessaryStockList().get(0).getId() == 0) {
                setSelectedNecessaryStockCount(sessionBean.loc.getString("choose"));
            } else {
                setSelectedNecessaryStockCount(selectedObject.getNecessaryStockList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }

            if (selectedObject.getPromotionStockList().isEmpty()) {
                setSelectedPromotionStockCount(sessionBean.loc.getString("choose"));
            } else if (selectedObject.getPromotionStockList().get(0).getId() == 0) {
                setSelectedPromotionStockCount(sessionBean.loc.getString("choose"));
            } else {
                setSelectedPromotionStockCount(selectedObject.getPromotionStockList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }

            if (selectedObject.getDiscountAmount() != null) {
                if (selectedObject.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                    isAmount = true;
                } else {
                    isAmount = false;
                }
            } else {
                isAmount = false;
            }

        }

        context.execute("PF('dlg_DiscountTabProcess').show()");
    }

    public void save() {
        int result = 0;
        boolean isConrol = true;
        selectedObject.setDiscount(selectedDiscount);
        if (isConrol) {
            if (processType == 1) {
                result = discountItemService.create(selectedObject);
                if (result > 0) {
                    selectedObject.setId(result);
                    selectedObject.setDateCreated(new Date());
                    selectedObject.setUserCreated(sessionBean.getUser());
                    listOfObjects.add(selectedObject);
                }

            } else {
                if (isAmount) {
                    selectedObject.setDiscountRate(null);
                } else if (!isAmount) {
                    selectedObject.setDiscountAmount(null);
                    // selectedObject.setIsTaxIncluded(null);
                }
                result = discountItemService.update(selectedObject);
            }
            if (result > 0) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.execute("PF('dlg_DiscountTabProcess').hide();");
                if (listOfObjects.size() <= 1) {
                    RequestContext.getCurrentInstance().update("frmDiscountProcess:sbcInvoice");
                }
                RequestContext.getCurrentInstance().update("tbvDiscount:frmDiscountTab:dtbDiscountTab");

            }
            sessionBean.createUpdateMessage(result);
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("notification"), sessionBean.loc.getString("discountisavailableinthisinterval")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void updateAllInformation(ActionEvent event) {
        if (stockBookCheckboxFilterBean.isAll) {
            Stock s = new Stock(0);
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Stock stock = new Stock(0);
                stock.setName(sessionBean.loc.getString("choose"));
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        if (isPromotionStock) {
            selectedObject.getPromotionStockList().clear();
            selectedObject.getPromotionStockList().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                setSelectedPromotionStockCount(sessionBean.loc.getString("choose"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                setSelectedPromotionStockCount(sessionBean.loc.getString("choose"));
            } else {
                setSelectedPromotionStockCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }

        } else {
            selectedObject.getNecessaryStockList().clear();
            selectedObject.getNecessaryStockList().addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

            if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                setSelectedNecessaryStockCount(sessionBean.loc.getString("choose"));
            } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                setSelectedNecessaryStockCount(sessionBean.loc.getString("choose"));
            } else {
                setSelectedNecessaryStockCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
            }
        }

        if (isPromotionStock) {
            RequestContext.getCurrentInstance().update("frmDiscountDiscountTabProcess:txtStockPromotion");
        } else {
            RequestContext.getCurrentInstance().update("frmDiscountDiscountTabProcess:txtStockNecessary");
        }

    }

    public void openDialog(boolean from) {
        isPromotionStock = from;
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        stockBookCheckboxFilterBean.autoCompleteValue = "";
        if (!isPromotionStock) {
            if (!selectedObject.getNecessaryStockList().isEmpty()) {
                if (selectedObject.getNecessaryStockList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }

            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getNecessaryStockList());
        } else if (!selectedObject.getPromotionStockList().isEmpty()) {
            if (selectedObject.getPromotionStockList().get(0).getId() == 0) {
                stockBookCheckboxFilterBean.isAll = true;
            } else {
                stockBookCheckboxFilterBean.isAll = false;
            }

            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(selectedObject.getPromotionStockList());
        }

    }

    public void renderDiscountRateAmount() {
        if (isAmount) {
            isAmount = false;
            //  selectedObject.setIsTaxIncluded(null);
        } else if (!isAmount) {
            isAmount = true;

        }
    }

    public void bringPriceList() {
        for (PriceList p : listOfPriceList) {
            if (selectedObject.getPriceList().getId() == p.getId()) {
                selectedObject.getPriceList().setName(p.getName());
            }
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        result = discountItemService.testBeforeDelete(selectedObject);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmDiscountDiscountTabProcess:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteVar').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thiscampaigndetailcannotbedeletedbecauseitwaspreviouslyusedinasale")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = discountItemService.delete(selectedObject);
        if (result > 0) {
            listOfObjects.remove(selectedObject);
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("PF('dlg_DiscountTabProcess').hide();");
            context.update("tbvDiscount:frmDiscountTab:dtbDiscountTab");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void goToHistory() {
        listOfHistoryObjects =historyService.findAll(0, 0, null, "", selectedObject.getId(), "finance.discountitem", 0);
        createdDate = StaticMethods.convertToDateFormatWithSeconds(sessionBean.getUser().getLastBranch().getDateFormat(), selectedObject.getDateCreated());
        try {
            createdPerson = selectedObject.getUserCreated().getFullName() + " - " + selectedObject.getUserCreated().getUsername();

        } catch (Exception e) {
        }
        RequestContext.getCurrentInstance().execute("PF('ovlHistory').loadContents()");

    }

}
