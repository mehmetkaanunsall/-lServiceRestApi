/**
 * This class ...
 *
 *
 * @author Emrullah Yakışan
 *
 * @date   18.07.2018 17:50:31
 */
package com.mepsan.marwiz.finance.salereturn.presentation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.safe.business.ISafeService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.finance.salereturn.business.ISaleReturnService;
import com.mepsan.marwiz.finance.salereturn.dao.ResponseSalesReturn;
import com.mepsan.marwiz.finance.salereturn.dao.SaleReturnReport;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.model.finance.Safe;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.pointofsale.business.IPointOfSaleService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import com.mepsan.marwiz.system.userdata.business.IUserDataService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import sun.nio.cs.ext.Big5;

@ManagedBean
@ViewScoped
public class SaleReturnBean extends GeneralReportBean<SaleReturnReport> {

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{saleReturnService}")
    public ISaleReturnService saleReturnService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{userDataService}")
    private IUserDataService userDataService;

    private String receiptNumber;
    private Date receiptProcessDate;
    private Date beginDate;
    private Date endDate;

    private BigDecimal totalPaidMoney;
    private BigDecimal totalRemainingMoney;
    private BigDecimal totalMoney;
    private BigDecimal totalMoneyUnselect, totalPriceUnselect, totalTaxUnselect, totalDiscountUnselect;
    private BigDecimal totalMoneySelect, totalPriceSelect, totalTaxSelect, totalDiscountSelect;

    private boolean isWithReceipt;
    private boolean isAuthSalesReturnWithoutReceipt, isFindSalesReturnWithoutReceipt;
    private BigDecimal paidExchangerate, paidPrice, paidTotalPrice;

    private List<SaleReturnReport> listOfSale;
    private List<SaleReturnReport> listOfSaleWithoutReceipt;
    private SaleReturnReport selectedSale;
    private List<SaleReturnReport> listOfSalePayment;
    private List<SaleReturnReport> listOfCreditPayment;
    private List<SaleReturnReport> listOfCreditPaymentDetail;
    private List<Stock> listOfStock;

    private JsonObject jsonSaleObject;
    private List<BranchSetting> listOfBranch;
    private BranchSetting branchSetting;

    private List<UserData> listOfCashierUsers;
    private UserData users;
    private Date receiptProcessHours;

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setSaleReturnService(ISaleReturnService saleReturnService) {
        this.saleReturnService = saleReturnService;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public IUserDataService getUserDataService() {
        return userDataService;
    }

    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public List<SaleReturnReport> getListOfSale() {
        return listOfSale;
    }

    public void setListOfSale(List<SaleReturnReport> listOfSale) {
        this.listOfSale = listOfSale;
    }

    public List<SaleReturnReport> getListOfSalePayment() {
        return listOfSalePayment;
    }

    public void setListOfSalePayment(List<SaleReturnReport> listOfSalePayment) {
        this.listOfSalePayment = listOfSalePayment;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public Date getReceiptProcessDate() {
        return receiptProcessDate;
    }

    public void setReceiptProcessDate(Date receiptProcessDate) {
        this.receiptProcessDate = receiptProcessDate;
    }

    public BigDecimal getTotalPaidMoney() {
        return totalPaidMoney;
    }

    public void setTotalPaidMoney(BigDecimal totalPaidMoney) {
        this.totalPaidMoney = totalPaidMoney;
    }

    public List<SaleReturnReport> getListOfCreditPayment() {
        return listOfCreditPayment;
    }

    public void setListOfCreditPayment(List<SaleReturnReport> listOfCreditPayment) {
        this.listOfCreditPayment = listOfCreditPayment;
    }

    public BigDecimal getTotalRemainingMoney() {
        return totalRemainingMoney;
    }

    public void setTotalRemainingMoney(BigDecimal totalRemainingMoney) {
        this.totalRemainingMoney = totalRemainingMoney;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public List<SaleReturnReport> getListOfCreditPaymentDetail() {
        return listOfCreditPaymentDetail;
    }

    public void setListOfCreditPaymentDetail(List<SaleReturnReport> listOfCreditPaymentDetail) {
        this.listOfCreditPaymentDetail = listOfCreditPaymentDetail;
    }

    public boolean isIsWithReceipt() {
        return isWithReceipt;
    }

    public void setIsWithReceipt(boolean isWithReceipt) {
        this.isWithReceipt = isWithReceipt;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isIsAuthSalesReturnWithoutReceipt() {
        return isAuthSalesReturnWithoutReceipt;
    }

    public void setIsAuthSalesReturnWithoutReceipt(boolean isAuthSalesReturnWithoutReceipt) {
        this.isAuthSalesReturnWithoutReceipt = isAuthSalesReturnWithoutReceipt;
    }

    public List<SaleReturnReport> getListOfSaleWithoutReceipt() {
        return listOfSaleWithoutReceipt;
    }

    public void setListOfSaleWithoutReceipt(List<SaleReturnReport> listOfSaleWithoutReceipt) {
        this.listOfSaleWithoutReceipt = listOfSaleWithoutReceipt;
    }

    public boolean isIsFindSalesReturnWithoutReceipt() {
        return isFindSalesReturnWithoutReceipt;
    }

    public void setIsFindSalesReturnWithoutReceipt(boolean isFindSalesReturnWithoutReceipt) {
        this.isFindSalesReturnWithoutReceipt = isFindSalesReturnWithoutReceipt;
    }

    public SaleReturnReport getSelectedSale() {
        return selectedSale;
    }

    public void setSelectedSale(SaleReturnReport selectedSale) {
        this.selectedSale = selectedSale;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public BigDecimal getPaidExchangerate() {
        return paidExchangerate;
    }

    public void setPaidExchangerate(BigDecimal paidExchangerate) {
        this.paidExchangerate = paidExchangerate;
    }

    public BigDecimal getPaidPrice() {
        return paidPrice;
    }

    public void setPaidPrice(BigDecimal paidPrice) {
        this.paidPrice = paidPrice;
    }

    public BigDecimal getPaidTotalPrice() {
        return paidTotalPrice;
    }

    public void setPaidTotalPrice(BigDecimal paidTotalPrice) {
        this.paidTotalPrice = paidTotalPrice;
    }

    public BigDecimal getTotalMoneyUnselect() {
        return totalMoneyUnselect;
    }

    public void setTotalMoneyUnselect(BigDecimal totalMoneyUnselect) {
        this.totalMoneyUnselect = totalMoneyUnselect;
    }

    public BigDecimal getTotalPriceUnselect() {
        return totalPriceUnselect;
    }

    public void setTotalPriceUnselect(BigDecimal totalPriceUnselect) {
        this.totalPriceUnselect = totalPriceUnselect;
    }

    public BigDecimal getTotalTaxUnselect() {
        return totalTaxUnselect;
    }

    public void setTotalTaxUnselect(BigDecimal totalTaxUnselect) {
        this.totalTaxUnselect = totalTaxUnselect;
    }

    public BigDecimal getTotalDiscountUnselect() {
        return totalDiscountUnselect;
    }

    public void setTotalDiscountUnselect(BigDecimal totalDiscountUnselect) {
        this.totalDiscountUnselect = totalDiscountUnselect;
    }

    public BigDecimal getTotalMoneySelect() {
        return totalMoneySelect;
    }

    public void setTotalMoneySelect(BigDecimal totalMoneySelect) {
        this.totalMoneySelect = totalMoneySelect;
    }

    public BigDecimal getTotalPriceSelect() {
        return totalPriceSelect;
    }

    public void setTotalPriceSelect(BigDecimal totalPriceSelect) {
        this.totalPriceSelect = totalPriceSelect;
    }

    public BigDecimal getTotalTaxSelect() {
        return totalTaxSelect;
    }

    public void setTotalTaxSelect(BigDecimal totalTaxSelect) {
        this.totalTaxSelect = totalTaxSelect;
    }

    public BigDecimal getTotalDiscountSelect() {
        return totalDiscountSelect;
    }

    public void setTotalDiscountSelect(BigDecimal totalDiscountSelect) {
        this.totalDiscountSelect = totalDiscountSelect;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public List<UserData> getListOfCashierUsers() {
        return listOfCashierUsers;
    }

    public void setListOfCashierUsers(List<UserData> listOfCashierUsers) {
        this.listOfCashierUsers = listOfCashierUsers;
    }

    public UserData getUsers() {
        return users;
    }

    public void setUsers(UserData users) {
        this.users = users;
    }

    public Date getReceiptProcessHours() {
        return receiptProcessHours;
    }

    public void setReceiptProcessHours(Date receiptProcessHours) {
        this.receiptProcessHours = receiptProcessHours;
    }

    @PostConstruct
    @Override
    public void init() {
        isAuthSalesReturnWithoutReceipt = sessionBean.getUser().getLastBranchSetting().isIsReturnWithoutReceipt();
        listOfSale = new ArrayList<>();
        listOfStock = new ArrayList<>();

        listOfCashierUsers = new ArrayList<>();
        listOfCashierUsers = userDataService.listOfCashierUsers();

        branchSetting = new BranchSetting();
        isWithReceipt = true;
        users = new UserData();
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        receiptProcessDate = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        beginDate = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        endDate = cal.getTime();

        listOfBranch = saleReturnService.findBranchSetting();

        for (BranchSetting b : listOfBranch) {
            if (b.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                branchSetting.getBranch().setId(b.getBranch().getId());
                branchSetting.setIsCentralIntegration(b.isIsCentralIntegration());
                branchSetting.setParoUrl(b.getParoUrl());
                branchSetting.setParoResponsibleCode(b.getParoResponsibleCode());
                branchSetting.setParoBranchCode(b.getParoBranchCode());
                branchSetting.setParoAccountCode(b.getParoAccountCode());
                break;
            }
        }
    }

    public void changeBranchSetting() {

        listOfStock.clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        for (BranchSetting b : listOfBranch) {
            if (b.getBranch().getId() == branchSetting.getBranch().getId()) {
                branchSetting.setIsCentralIntegration(b.isIsCentralIntegration());
                branchSetting.setParoUrl(b.getParoUrl());
                branchSetting.setParoResponsibleCode(b.getParoResponsibleCode());
                branchSetting.setParoBranchCode(b.getParoBranchCode());
                branchSetting.setParoAccountCode(b.getParoAccountCode());
                break;
            }
        }

    }

    @Override
    public void find() {
        if (sessionBean.getUser().getAccount().getId() == 0) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                    sessionBean.loc.getString("warning"),
                    sessionBean.loc.getString("personsotherthantheemployeecannotmakeareturnprocess"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            return;
        }

        isFindSalesReturnWithoutReceipt = false;
        isFind = false;
        if (isWithReceipt) {//Fişli İade İşlemi İse 
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(receiptProcessDate);
            Calendar cal2 = GregorianCalendar.getInstance();
            cal2.setTime(receiptProcessHours);
            cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
            receiptProcessDate = cal.getTime();
            findReceipt(String.valueOf(receiptNumber), receiptProcessDate, branchSetting.getBranch().getId());
        } else {
            listOfSaleWithoutReceipt = new ArrayList<>();
            listOfSaleWithoutReceipt = saleReturnService.findSaleWithoutReceipt(listOfStock, beginDate, endDate, branchSetting.getBranch().getId());

            RequestContext context = RequestContext.getCurrentInstance();
            FacesMessage message = new FacesMessage();
            if (listOfSaleWithoutReceipt.size() > 0) {
                int resultSafeStatus = saleReturnService.checkSafeStatus(listOfSaleWithoutReceipt.get(0).getSales().getId());
                if (resultSafeStatus == -100) {//Ana Kasa Eksiye Düşülemez Hatası
                    isFindSalesReturnWithoutReceipt = false;
                    isFind = false;
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("processcannotbedonebecausemainsafedropdowntominus"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("frmSaleReturn:growlMessage");
                } else if (listOfSaleWithoutReceipt.get(0).isIsUsedStock()) {
                    isFindSalesReturnWithoutReceipt = false;
                    isFind = false;
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("salesreturnprocesscannotbeperformedsincetheserviceproducthasbeenusedbefore"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("frmSaleReturn:growlMessage");
                } else {
                    isFindSalesReturnWithoutReceipt = true;
                    isFind = false;
                }

            } else {
                isFindSalesReturnWithoutReceipt = false;
                isFind = false;
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("receiptnotfound"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("frmSaleReturn:growlMessage");
            }

        }
    }

    public void findReceipt(String receiptNo, Date receiptDate, int branchId) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        listOfSale = new ArrayList<>();
        listOfSalePayment = new ArrayList<>();
        listOfSale = saleReturnService.findReceipt(String.valueOf(receiptNo), receiptDate, branchId);
        if (listOfSale.size() > 0) {
            int resultSafeStatus = saleReturnService.checkSafeStatus(listOfSale.get(0).getSales().getId());
            if (resultSafeStatus == -100) {//Ana Kasa Eksiye Düşülemez Hatası
                isFind = Boolean.FALSE;
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("processcannotbedonebecausemainsafedropdowntominus"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("frmSaleReturn:growlMessage");
            } else if (listOfSale.get(0).getSales().getReceipt().isIsReturn()) {
                isFind = Boolean.FALSE;
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("thereceiptwascanceledearlier"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("frmSaleReturn:growlMessage");
            } else if (listOfSale.get(0).isIsUsedStock()) {
                isFind = Boolean.FALSE;
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                message.setSummary(sessionBean.getLoc().getString("warning"));
                message.setDetail(sessionBean.getLoc().getString("salesreturnprocesscannotbeperformedsincetheserviceproducthasbeenusedbefore"));
                FacesContext.getCurrentInstance().addMessage(null, message);
                context.update("frmSaleReturn:growlMessage");
            } else {
                isFind = Boolean.TRUE;
                listOfSalePayment = saleReturnService.findSalePayment(listOfSale.get(0).getSales().getId());
                int countMobileWallet = 0;

                if (!listOfSalePayment.isEmpty()) {
                    for (SaleReturnReport salePayment : listOfSalePayment) {
                        if (salePayment.getType().getId() == 105) {
                            countMobileWallet++;
                        }
                    }
                }

                if (countMobileWallet > 0) {
                    isFind = Boolean.FALSE;
                    message.setSeverity(FacesMessage.SEVERITY_WARN);
                    message.setSummary(sessionBean.getLoc().getString("warning"));
                    message.setDetail(sessionBean.getLoc().getString("thisvoucherisnonrefundableaspaymentismadewithamobilewallet"));
                    FacesContext.getCurrentInstance().addMessage(null, message);
                    context.update("frmSaleReturn:growlMessage");

                }

                calculateTotal(listOfSalePayment);
            }

        } else {
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            message.setDetail(sessionBean.getLoc().getString("receiptnotfound"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.update("frmSaleReturn:growlMessage");
            isFind = false;
        }
    }

    public void findReceiptFromSale() {
        findReceipt(selectedSale.getSales().getReceipt().getReceiptNo(), selectedSale.getSales().getReceipt().getProcessDate(), branchSetting.getBranch().getId());
    }

    public void acceptReturn() {
        FacesMessage message = new FacesMessage();
        RequestContext context = RequestContext.getCurrentInstance();
        int receiptId = saleReturnService.acceptReturn(listOfSale.get(0), branchSetting, users);
        if (receiptId == -2) {
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            message.setDetail(sessionBean.getLoc().getString("shiftisclose"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.update("frmSaleReturn:growlMessage");
        }
        if (receiptId == -3) {
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            message.setDetail(sessionBean.getLoc().getString("thereceiptwasreturnedearlier"));
            FacesContext.getCurrentInstance().addMessage(null, message);
            context.update("frmSaleReturn:growlMessage");
        } else if (receiptId > 0) {
            sessionBean.createUpdateMessage(receiptId);

            if (branchSetting.getParoUrl() != null && branchSetting.getParoAccountCode() != null && branchSetting.getParoBranchCode() != null && branchSetting.getParoResponsibleCode() != null) {
                saleReturnService.createParoSales(listOfSale.get(0).getSales().getId());
            }

            isFind = false;
            isFindSalesReturnWithoutReceipt = false;
            context.update("pgrSalesReturnWithoutReceipt");
            context.update("pgrSaleReturnDatatable");
            context.execute("PF('dlgDeleteVar').hide()");
        } else {
            sessionBean.createUpdateMessage(receiptId);
        }

    }

    public void calculateTotal(List<SaleReturnReport> result) {
        totalPaidMoney = BigDecimal.valueOf(0);
        totalMoney = BigDecimal.valueOf(0);
        totalRemainingMoney = BigDecimal.valueOf(0);
        for (SaleReturnReport saleReturnReport : result) {
            if (saleReturnReport.getType().getId() != 19) {
                totalPaidMoney = totalPaidMoney.add(saleReturnReport.getPrice().multiply(saleReturnReport.getExchangeRate()));
            }
            totalMoney = totalMoney.add(saleReturnReport.getPrice().multiply(saleReturnReport.getExchangeRate()));
            totalPaidMoney = totalPaidMoney.add(saleReturnReport.getTotalCreditPayment());
        }
        totalRemainingMoney = totalMoney.subtract(totalPaidMoney);
    }

    public void showCreditPaymentDetail() {
        listOfCreditPayment = new ArrayList<>();
        listOfCreditPaymentDetail = new ArrayList<>();
        listOfCreditPayment = saleReturnService.findCreditPayment(listOfSale.get(0).getSales().getId());
        listOfCreditPaymentDetail = saleReturnService.findCreditPaymentDetail(listOfSale.get(0).getSales().getId());
        RequestContext.getCurrentInstance().update("dlgCreditPaymentDetail");
        RequestContext.getCurrentInstance().execute("PF('dlg_CreditPaymentDetail').show()");

    }

    public void updateAllInformation(ActionEvent event) {
        listOfStock.clear();
        if (stockBookCheckboxFilterBean.isAll) {
            Stock s = new Stock(0);
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                Stock stock = new Stock(0);
                stock.setName(sessionBean.loc.getString("all"));
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

        if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
        }
        RequestContext.getCurrentInstance().update("frmSaleReturn:txtStock");

    }

    public void openDialog() {
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        if (!listOfStock.isEmpty()) {
            if (listOfStock.get(0).getId() == 0) {
                stockBookCheckboxFilterBean.isAll = true;
            } else {
                stockBookCheckboxFilterBean.isAll = false;
            }
        }
        stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
    }

    public void preDelete() {
        FacesMessage message = new FacesMessage();
        RequestContext context = RequestContext.getCurrentInstance();

        context.execute("PF('dlgDeleteVar').show()");

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<SaleReturnReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
