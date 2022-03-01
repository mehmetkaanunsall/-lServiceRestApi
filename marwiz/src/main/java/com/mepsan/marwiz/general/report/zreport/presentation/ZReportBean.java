/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 4:00:13 PM
 */
package com.mepsan.marwiz.general.report.zreport.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.zreport.business.IZReportService;
import com.mepsan.marwiz.general.report.zreport.dao.ZReport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.model.LazyDataModel;

@ManagedBean
@ViewScoped
public class ZReportBean extends GeneralReportBean<ZReport> {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{zReportService}")
    private IZReportService zReportService;

    private List<ZReport> listOfTaxList;
    private List<ZReport> listOfCategoryList;
    private List<ZReport> listOfSalesTypeList;
    private List<ZReport> listOfReceiptCount;
    private List<ZReport> listOfSaleTotals;
    private BigDecimal salesTotalMoney;
    private BigDecimal salesTotalPrice;
    private BigDecimal salesReturnTotalMoney;
    private BigDecimal salesTotalMoneyIncludeReturn;
    private List<ZReport> listOfCashierList;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setzReportService(IZReportService zReportService) {
        this.zReportService = zReportService;
    }

    public List<ZReport> getListOfTaxList() {
        return listOfTaxList;
    }

    public void setListOfTaxList(List<ZReport> listOfTaxList) {
        this.listOfTaxList = listOfTaxList;
    }

    public List<ZReport> getListOfCategoryList() {
        return listOfCategoryList;
    }

    public void setListOfCategoryList(List<ZReport> listOfCategoryList) {
        this.listOfCategoryList = listOfCategoryList;
    }

    public List<ZReport> getListOfSalesTypeList() {
        return listOfSalesTypeList;
    }

    public void setListOfSalesTypeList(List<ZReport> listOfSalesTypeList) {
        this.listOfSalesTypeList = listOfSalesTypeList;
    }

    public List<ZReport> getListOfReceiptCount() {
        return listOfReceiptCount;
    }

    public void setListOfReceiptCount(List<ZReport> listOfReceiptCount) {
        this.listOfReceiptCount = listOfReceiptCount;
    }

    public List<ZReport> getListOfSaleTotals() {
        return listOfSaleTotals;
    }

    public void setListOfSaleTotals(List<ZReport> listOfSaleTotals) {
        this.listOfSaleTotals = listOfSaleTotals;
    }

    public BigDecimal getSalesTotalMoney() {
        return salesTotalMoney;
    }

    public void setSalesTotalMoney(BigDecimal salesTotalMoney) {
        this.salesTotalMoney = salesTotalMoney;
    }

    public BigDecimal getSalesTotalPrice() {
        return salesTotalPrice;
    }

    public void setSalesTotalPrice(BigDecimal salesTotalPrice) {
        this.salesTotalPrice = salesTotalPrice;
    }

    public BigDecimal getSalesReturnTotalMoney() {
        return salesReturnTotalMoney;
    }

    public void setSalesReturnTotalMoney(BigDecimal salesReturnTotalMoney) {
        this.salesReturnTotalMoney = salesReturnTotalMoney;
    }

    public BigDecimal getSalesTotalMoneyIncludeReturn() {
        return salesTotalMoneyIncludeReturn;
    }

    public void setSalesTotalMoneyIncludeReturn(BigDecimal salesTotalMoneyIncludeReturn) {
        this.salesTotalMoneyIncludeReturn = salesTotalMoneyIncludeReturn;
    }

    public List<ZReport> getListOfCashierList() {
        return listOfCashierList;
    }

    public void setListOfCashierList(List<ZReport> listOfCashierList) {
        this.listOfCashierList = listOfCashierList;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---ZReportBean--");
        selectedObject = new ZReport();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 01);
        selectedObject.setBeginDate(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        selectedObject.setEndDate(calendar.getTime());
        salesReturnTotalMoney = BigDecimal.ZERO;
        salesTotalMoney = BigDecimal.ZERO;
        salesTotalPrice = BigDecimal.ZERO;
        salesTotalMoneyIncludeReturn = BigDecimal.ZERO;
    }

    @Override
    public void find() {
        isFind = true;
        listOfCategoryList = zReportService.listOfCateroyList(selectedObject.getBeginDate(), selectedObject.getEndDate());
        listOfCategoryList.addAll(zReportService.listOfStockGroupWithoutCategoies(selectedObject.getBeginDate(), selectedObject.getEndDate()));
        listOfSalesTypeList = zReportService.listOfSalesTypeList(selectedObject.getBeginDate(), selectedObject.getEndDate());
        List<ZReport> listOfOpenPayment = zReportService.listOfOpenPayment(selectedObject.getBeginDate(), selectedObject.getEndDate());
        if (listOfOpenPayment.get(0).getSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            listOfSalesTypeList.addAll(listOfOpenPayment);
        }
        listOfTaxList = zReportService.listOfTaxList(selectedObject.getBeginDate(), selectedObject.getEndDate());

        listOfCashierList = zReportService.listOfCashierList(selectedObject.getBeginDate(), selectedObject.getEndDate());

        listOfReceiptCount = zReportService.listReceiptCount(selectedObject.getBeginDate(), selectedObject.getEndDate());

        listOfSaleTotals = zReportService.listReceiptTotal(selectedObject.getBeginDate(), selectedObject.getEndDate());
        if (listOfSaleTotals.isEmpty()) {
            salesReturnTotalMoney = BigDecimal.ZERO;
            salesTotalMoney = BigDecimal.ZERO;
            salesTotalPrice = BigDecimal.ZERO;
            salesTotalMoneyIncludeReturn = BigDecimal.ZERO;
        } else {
            salesTotalMoney = listOfSaleTotals.get(0).getTotalSaleMoney();
            salesReturnTotalMoney = listOfSaleTotals.get(0).getTotalReturnPrice();
            salesTotalPrice = listOfSaleTotals.get(0).getTotalSalePrice();
            salesTotalMoneyIncludeReturn = listOfSaleTotals.get(0).getTotalMoneyIncludeReturn();
        }
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
    public LazyDataModel<ZReport> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Satış Kategori Toplam Dökümü Tablosunda Toplam Alt Toplam Hesaplamalarını
     * Yapar
     *
     * @param columnId
     * @return
     */
    public String clcSubTotalSalesCategoryListUnit(int columnId) {
        BigDecimal total = BigDecimal.ZERO;
        String currency = "";
        String result = "";
        for (ZReport zReport : listOfCategoryList) {
            switch (columnId) {

                case 1:
                    total = total.add(zReport.getSaleAmount());
                    break;
                case 2:
                    total = total.add(zReport.getReturnAmount());
                    break;
                case 3:
                    total = total.add(zReport.getSalePrice());
                    break;
                case 4:
                    total = total.add(zReport.getReturnPrice());
                    break;

                default:
                    total = total.add(BigDecimal.ZERO);
                    break;
            }
        }
        if (columnId == 4 || columnId == 3) {
            currency = sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        }

        result = String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranch().getCurrencyrounding()).format(total)) + " " + currency;
        return result;
    }

    public NumberFormat unitNumberFormat(int currencyRounding) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        formatter.setMaximumFractionDigits(currencyRounding);
        formatter.setMinimumFractionDigits(currencyRounding);
        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
        return formatter;
    }

    /**
     * Kdv Toplam Tablosu İçin Alt Toplam Hesaplaması Yapar.
     *
     * @param columnId Alt Toplam Yapılacak Colon Bilgisi
     * @return
     */
    public BigDecimal clcSubTotalTaxRateList(int columnId) {
        BigDecimal result = BigDecimal.ZERO;

        switch (columnId) {
            case 1:
                for (ZReport mp : listOfTaxList) {
                    if (mp.getSaleAmount() != null) {
                        result = result.add(mp.getSaleAmount());
                    }
                }
                break;
            case 2:
                for (ZReport mp : listOfTaxList) {
                    if (mp.getReturnAmount() != null) {
                        result = result.add(mp.getReturnAmount());
                    }
                }
                break;
            case 3:
                for (ZReport mp : listOfTaxList) {
                    if (mp.getSalePrice() != null) {
                        result = result.add(mp.getSalePrice());
                    }
                }
                break;
            case 4:
                for (ZReport mp : listOfTaxList) {
                    if (mp.getReturnPrice() != null) {
                        result = result.add(mp.getReturnPrice());
                    }
                }
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    public String clcSubTotalSalesSalesListUnit(int columnId) {
        BigDecimal total = BigDecimal.ZERO;
        String currency = "";
        String result = "";

        switch (columnId) {
            case 1:
                for (ZReport mp : listOfSalesTypeList) {
                    if (mp.getSalePrice() != null) {
                        total = total.add(mp.getSalePrice());
                    }
                }
                break;
            case 2:
                for (ZReport mp : listOfSalesTypeList) {
                    if (mp.getReturnPrice() != null) {
                        total = total.add(mp.getReturnPrice());
                    }
                }
                break;
            default:
                total = BigDecimal.ZERO;
                break;
        }

        currency = sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        result = String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranch().getCurrencyrounding()).format(total)) + " " + currency;
        return result;
    }
    
    /**
     * Kasiyer Toplam Tablosu İçin Alt Toplam Hesaplaması Yapar.
     *
     * @param columnId Alt Toplam Yapılacak Colon Bilgisi
     * @return
     */
    public BigDecimal clcSubTotalCashierList(int columnId) {
        BigDecimal result = BigDecimal.ZERO;

        switch (columnId) {
            case 1:
                for (ZReport mp : listOfCashierList) {
                    if (mp.getSaleAmount() != null) {
                        result = result.add(mp.getSaleAmount());
                    }
                }
                break;
            case 2:
                for (ZReport mp : listOfCashierList) {
                    if (mp.getReturnAmount() != null) {
                        result = result.add(mp.getReturnAmount());
                    }
                }
                break;
            case 3:
                for (ZReport mp : listOfCashierList) {
                    if (mp.getSalePrice() != null) {
                        result = result.add(mp.getSalePrice());
                    }
                }
                break;
            case 4:
                for (ZReport mp : listOfCashierList) {
                    if (mp.getReturnPrice() != null) {
                        result = result.add(mp.getReturnPrice());
                    }
                }
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

}
