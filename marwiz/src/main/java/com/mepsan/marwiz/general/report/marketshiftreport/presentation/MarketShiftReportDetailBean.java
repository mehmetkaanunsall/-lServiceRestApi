/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   12.02.2018 08:47:00
 */
package com.mepsan.marwiz.general.report.marketshiftreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.business.IMarketShiftService;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.general.model.general.SaleItem;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportDetailService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import java.util.HashMap;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

@ManagedBean
@ViewScoped
public class MarketShiftReportDetailBean extends GeneralReportBean<Sales> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftReportDetailService}")
    private IMarketShiftReportDetailService marketShiftReportDetailService;

    @ManagedProperty(value = "#{marketShiftService}")
    private IMarketShiftService marketShiftService;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    private Shift selectedShift;
    private List<SaleItem> shiftSaleItemList;
    private List<SalePayment> listOfTotalSaleType;
    private List<SalePayment> listOfTotals;
    private HashMap<Integer, BigDecimal> groupCurrencyPaymentTotal;
    private String saleTotal;
    private Boolean isStockView;
    private LazyDataModel<SaleItem> listStockDetail;
    private List<SalePayment> listStockDetailOfTotals;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarketShiftReportDetailService(IMarketShiftReportDetailService marketShiftReportDetailService) {
        this.marketShiftReportDetailService = marketShiftReportDetailService;
    }

    public List<SaleItem> getShiftSaleItemList() {
        return shiftSaleItemList;
    }

    public void setShiftSaleItemList(List<SaleItem> shiftSaleItemList) {
        this.shiftSaleItemList = shiftSaleItemList;
    }

    public List<SalePayment> getListOfTotalSaleType() {
        return listOfTotalSaleType;
    }

    public void setListOfTotalSaleType(List<SalePayment> listOfTotalSaleType) {
        this.listOfTotalSaleType = listOfTotalSaleType;
    }

    public List<SalePayment> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<SalePayment> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public void setMarketShiftService(IMarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public Shift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(Shift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public Boolean getIsStockView() {
        return isStockView;
    }

    public void setIsStockView(Boolean isStockView) {
        this.isStockView = isStockView;
    }

    public LazyDataModel<SaleItem> getListStockDetail() {
        return listStockDetail;
    }

    public void setListStockDetail(LazyDataModel<SaleItem> listStockDetail) {
        this.listStockDetail = listStockDetail;
    }

    public List<SalePayment> getListStockDetailOfTotals() {
        return listStockDetailOfTotals;
    }

    public void setListStockDetailOfTotals(List<SalePayment> listStockDetailOfTotals) {
        this.listStockDetailOfTotals = listStockDetailOfTotals;
    }

    public Marwiz getMarwiz() {
        return marwiz;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("------------ShiftDetailBean");
        selectedObject = new Sales();
        selectedShift = new Shift();
        shiftSaleItemList = new ArrayList<>();
        listOfTotalSaleType = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        listStockDetailOfTotals = new ArrayList<>();
        groupCurrencyPaymentTotal = new HashMap<>();
        isStockView = false;

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Shift) {
                    selectedShift = (Shift) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }

        selectedObject.setShift(selectedShift);
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true);

        find();
        setListBtn(sessionBean.checkAuthority(new int[]{51}, 0));
    }

    @Override
    public void find() {
        isFind = true;
        listOfObjects = findall(" ");
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    //Ürün görünümünde çalışır
    public LazyDataModel<SaleItem> findStockDetailList(String where) {
        return new CentrowizLazyDataModel<SaleItem>() {

            @Override
            public List<SaleItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<SaleItem> result = marketShiftReportDetailService.findStockDetailList(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedShift);
                listStockDetailOfTotals = marketShiftReportDetailService.totalsStockDetailList(where, selectedShift);
                int count = 0;
                if (!listStockDetailOfTotals.isEmpty()) {
                    count = listStockDetailOfTotals.get(0).getId();
                }
                listStockDetail.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    //Fiş görünümünde çalışır
    @Override
    public LazyDataModel<Sales> findall(String where) {
        return new CentrowizLazyDataModel<Sales>() {
            @Override
            public List<Sales> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<Sales> result = marketShiftReportDetailService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedShift);
                listOfTotals = marketShiftReportDetailService.totals(where, selectedShift);
                int count = 0;
                if (!listOfTotals.isEmpty()) {
                    count = listOfTotals.get(0).getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void createDialog() {
        selectedObject.setShift(selectedShift);
        shiftSaleItemList = marketShiftReportDetailService.find(selectedObject);
        listOfTotalSaleType = marketShiftReportDetailService.listOfSaleType(selectedObject);

        RequestContext.getCurrentInstance().execute("PF('dlg_ShiftSales').show();");
    }

    public void createPdf() {
        marketShiftReportDetailService.exportPdf(selectedShift, toogleList, listOfTotals, saleTotal, isStockView, listStockDetailOfTotals, marwiz.getOldId());
    }

    public void createExcel() throws IOException {
        marketShiftReportDetailService.exportExcel(selectedShift, toogleList, listOfTotals, saleTotal, isStockView, listStockDetailOfTotals, marwiz.getOldId());
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(marketShiftReportDetailService.exportPrinter(selectedShift, toogleList, listOfTotals, saleTotal, isStockView, listStockDetailOfTotals, marwiz.getOldId())) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void unlock() {
        int control = 0;
        control = marketShiftService.controlReopenShift(selectedShift);
        switch (control) {
            case 1://Yeni vardiya açılmış
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("shiftcannotbeopenbecauseofopeningnewshift")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 2://Ödeme Girilmiş
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("shiftcannotbeopenbecauseofenteringpaymentbelongtoshift")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                break;
            case 0://Açılabilir
                RequestContext.getCurrentInstance().update("dlgReOpenShift");
                RequestContext.getCurrentInstance().execute("PF('dlg_ReOpenShift').show();");
                break;
            default:
                break;
        }
    }

    public void reopenShift() {
        int result = 0;
        result = marketShiftService.reopenShift(selectedShift);

        if (result > 0) {
            selectedShift.getStatus().setId(7);
            RequestContext.getCurrentInstance().update("frmGridToolbar");

        }
        sessionBean.createUpdateMessage(result);
        List<PointOfSale> listOfPos = new ArrayList<>();
        listOfPos = marketShiftService.listPointOfSale();
        if (!listOfPos.isEmpty()) {
            marketShiftService.transferShiftAndPos(listOfPos);
        }

    }

    public String calculateTotal() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        numberFormat.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        groupCurrencyPaymentTotal.clear();
        if (!isStockView) {

            for (SalePayment s : listOfTotals) {
                if (groupCurrencyPaymentTotal.containsKey(s.getSales().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyPaymentTotal.get(s.getSales().getCurrency().getId());
                    groupCurrencyPaymentTotal.put(s.getSales().getCurrency().getId(), old.add(s.getPrice()));
                } else {
                    groupCurrencyPaymentTotal.put(s.getSales().getCurrency().getId(), s.getPrice());
                }
            }

        } else {

            for (SalePayment s : listStockDetailOfTotals) {
                if (groupCurrencyPaymentTotal.containsKey(s.getSales().getCurrency().getId())) {
                    BigDecimal old = groupCurrencyPaymentTotal.get(s.getSales().getCurrency().getId());
                    groupCurrencyPaymentTotal.put(s.getSales().getCurrency().getId(), old.add(s.getPrice()));
                } else {
                    groupCurrencyPaymentTotal.put(s.getSales().getCurrency().getId(), s.getPrice());
                }
            }

        }

        saleTotal = "";
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupCurrencyPaymentTotal.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTotal += String.valueOf(marwiz.getOldId() == 66 ?  numberFormat.format(entry.getValue()) : sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTotal += " + " + String.valueOf(marwiz.getOldId() == 66 ? numberFormat.format(entry.getValue()) : sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }

        return saleTotal;
    }

    public BigDecimal calculateProductBasedDiscount() {
        BigDecimal total = BigDecimal.valueOf(0);
        if (selectedObject.getTotalDiscount() != null && selectedObject.getDiscountPrice() != null) {
            total = selectedObject.getTotalDiscount().subtract(selectedObject.getDiscountPrice());
        }

        return total;
    }

    //Ürün görünümüne geç butonuna basınca çalışır
    public void stockView() {
        System.out.println("-----marwiz old ıd---" + marwiz.getOldId());
        if (!isStockView) {
            clearList();
            isStockView = true;
            listStockDetail = findStockDetailList("");

        } else {
            clearList();
            isStockView = false;
            find();

        }
        RequestContext.getCurrentInstance().update("pgrMarketShiftReportDetail");
        System.out.println("-----marwiz old ıd--2-");

        System.out.println("-----marwiz old ıd--2-" + marwiz.getOldId());
    }

    public void clearList() {

        if (!isStockView) {
            if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(0);
            } else {
                toogleList = Arrays.asList(true, true, true, true, true, true, false, true, true, true, true, true, true, true);
                setCountToggle(1);
            }
            listStockDetailOfTotals.clear();
            groupCurrencyPaymentTotal.clear();
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true);
            setCountToggle(0);
            shiftSaleItemList.clear();
            listOfTotalSaleType.clear();
            listOfTotals.clear();
            groupCurrencyPaymentTotal.clear();
        }

    }

}
