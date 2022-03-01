/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   25.12.2018 05:31:27
 */
package com.mepsan.marwiz.general.marketshift.presentation;

import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.marketshift.business.MarketShiftService;
import com.mepsan.marwiz.general.marketshift.dao.MarketShiftPreview;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Warehouse;
import com.mepsan.marwiz.general.model.system.Currency;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ManagedBean
@ViewScoped
public class MarketShiftPreviewBean {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{marketShiftService}")
    public MarketShiftService marketShiftService;

    private Shift selectedShift;
    private List<MarketShiftPreview> listOfSalesList, listOfCurrencyTotal, listOfTaxList, listOfDeficitSurplusEmployee, listOfDeficitSurplusIncomeExpense, listOfCashDelivery,
            listOfCreditCardDelivery, listOfCreditDelivery, listOfShiftGeneral, listOfShiftSummary, listOfShiftSummaryDb, listOfStockGroupList, listOfAccountGroup, listOfSafeTransfer;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setMarketShiftService(MarketShiftService marketShiftService) {
        this.marketShiftService = marketShiftService;
    }

    public Shift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(Shift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public List<MarketShiftPreview> getListOfSalesList() {
        return listOfSalesList;
    }

    public void setListOfSalesList(List<MarketShiftPreview> listOfSalesList) {
        this.listOfSalesList = listOfSalesList;
    }

    public List<MarketShiftPreview> getListOfCurrencyTotal() {
        return listOfCurrencyTotal;
    }

    public void setListOfCurrencyTotal(List<MarketShiftPreview> listOfCurrencyTotal) {
        this.listOfCurrencyTotal = listOfCurrencyTotal;
    }

    public List<MarketShiftPreview> getListOfTaxList() {
        return listOfTaxList;
    }

    public void setListOfTaxList(List<MarketShiftPreview> listOfTaxList) {
        this.listOfTaxList = listOfTaxList;
    }

    public List<MarketShiftPreview> getListOfDeficitSurplusEmployee() {
        return listOfDeficitSurplusEmployee;
    }

    public void setListOfDeficitSurplusEmployee(List<MarketShiftPreview> listOfDeficitSurplusEmployee) {
        this.listOfDeficitSurplusEmployee = listOfDeficitSurplusEmployee;
    }

    public List<MarketShiftPreview> getListOfDeficitSurplusIncomeExpense() {
        return listOfDeficitSurplusIncomeExpense;
    }

    public void setListOfDeficitSurplusIncomeExpense(List<MarketShiftPreview> listOfDeficitSurplusIncomeExpense) {
        this.listOfDeficitSurplusIncomeExpense = listOfDeficitSurplusIncomeExpense;
    }

    public List<MarketShiftPreview> getListOfCashDelivery() {
        return listOfCashDelivery;
    }

    public void setListOfCashDelivery(List<MarketShiftPreview> listOfCashDelivery) {
        this.listOfCashDelivery = listOfCashDelivery;
    }

    public List<MarketShiftPreview> getListOfCreditCardDelivery() {
        return listOfCreditCardDelivery;
    }

    public void setListOfCreditCardDelivery(List<MarketShiftPreview> listOfCreditCardDelivery) {
        this.listOfCreditCardDelivery = listOfCreditCardDelivery;
    }

    public List<MarketShiftPreview> getListOfCreditDelivery() {
        return listOfCreditDelivery;
    }

    public void setListOfCreditDelivery(List<MarketShiftPreview> listOfCreditDelivery) {
        this.listOfCreditDelivery = listOfCreditDelivery;
    }

    public List<MarketShiftPreview> getListOfShiftGeneral() {
        return listOfShiftGeneral;
    }

    public void setListOfShiftGeneral(List<MarketShiftPreview> listOfShiftGeneral) {
        this.listOfShiftGeneral = listOfShiftGeneral;
    }

    public List<MarketShiftPreview> getListOfShiftSummary() {
        return listOfShiftSummary;
    }

    public void setListOfShiftSummary(List<MarketShiftPreview> listOfShiftSummary) {
        this.listOfShiftSummary = listOfShiftSummary;
    }

    public List<MarketShiftPreview> getListOfStockGroupList() {
        return listOfStockGroupList;
    }

    public void setListOfStockGroupList(List<MarketShiftPreview> listOfStockGroupList) {
        this.listOfStockGroupList = listOfStockGroupList;
    }

    public List<MarketShiftPreview> getListOfAccountGroup() {
        return listOfAccountGroup;
    }

    public void setListOfAccountGroup(List<MarketShiftPreview> listOfAccountGroup) {
        this.listOfAccountGroup = listOfAccountGroup;
    }

    public List<MarketShiftPreview> getListOfSafeTransfer() {
        return listOfSafeTransfer;
    }

    public void setListOfSafeTransfer(List<MarketShiftPreview> listOfSafeTransfer) {
        this.listOfSafeTransfer = listOfSafeTransfer;
    }

    @PostConstruct
    public void init() {
        listOfSalesList = new ArrayList<>();
        listOfStockGroupList = new ArrayList<>();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof Shift) {
                    selectedShift = (Shift) ((ArrayList) sessionBean.parameter).get(i);

                }
            }
        }
        if (selectedShift.getId() > 0) {
            listOfSalesList = marketShiftService.shiftStockDetailList(selectedShift);
            listOfCurrencyTotal = marketShiftService.shiftCurrencyDetailList(selectedShift);
            listOfTaxList = marketShiftService.shiftTaxRateDetailList(selectedShift);
            listOfDeficitSurplusEmployee = marketShiftService.shiftDeficitGiveMoneyEmployeeList(selectedShift);
            listOfDeficitSurplusIncomeExpense = marketShiftService.shiftDeficitGiveMoneyList(selectedShift);
            listOfCashDelivery = marketShiftService.shiftCashierPaymentCashDetailList(selectedShift);
            listOfCreditCardDelivery = marketShiftService.shiftCashierPaymentBankDetailList(selectedShift);
            listOfCreditDelivery = marketShiftService.shiftCreditPaymentDetailList(selectedShift);
            listOfShiftGeneral = marketShiftService.shiftGeneralList(selectedShift);
            listOfShiftSummaryDb = marketShiftService.shiftSummaryList(selectedShift);
            listOfStockGroupList = marketShiftService.shiftStockGroupList(selectedShift);
            listOfStockGroupList.addAll(marketShiftService.shiftStockGroupListWithoutCategories(selectedShift));
            listOfAccountGroup = marketShiftService.shiftAccountGroupList(selectedShift);
            listOfSafeTransfer = marketShiftService.shiftSafeTransferList(selectedShift);
            shiftSummaryParser();
        }
    }

    /**
     * Veri Tabanından Tek Liste Halinde Gelen Vardiya Özet Sorgusunu Listelere
     * Ayrılmasını Sağlar
     */
    public void shiftSummaryParser() {
        if (listOfShiftSummaryDb.size() > 0) {
            listOfShiftSummary = new ArrayList<>();
            MarketShiftPreview preview;

            // Satış Tutarı
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("salesreturnprice"));
            preview.setInComing(listOfShiftSummaryDb.get(0).getSalePrice());
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getReturnPrice());
            listOfShiftSummary.add(preview);

            //Nakit Toplamı
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofcash"));
            preview.setInComing(BigDecimal.ZERO);
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getCashPrice());
            listOfShiftSummary.add(preview);

            //Kredi Kartı Teslimat
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofpos"));
            preview.setInComing(BigDecimal.ZERO);
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getCreditCardPrice());
            listOfShiftSummary.add(preview);

            //Veresiye Satış
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofpostpaid"));
            preview.setInComing(listOfShiftSummaryDb.get(0).getCreditPrice());
            preview.setOutGoing(BigDecimal.ZERO);
            listOfShiftSummary.add(preview);

            //Açık Satış
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofopen"));
            preview.setInComing(BigDecimal.ZERO);
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getOpenPrice());
            listOfShiftSummary.add(preview);

            //Gelir
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofincome"));
            preview.setInComing(listOfShiftSummaryDb.get(0).getInComing());
            preview.setOutGoing(BigDecimal.ZERO);
            listOfShiftSummary.add(preview);

            //Gider
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofexpense"));
            preview.setInComing(BigDecimal.ZERO);
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getOutGoing());
            listOfShiftSummary.add(preview);

            //Personel Toplamı
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("totalofemployee"));
            preview.setInComing(listOfShiftSummaryDb.get(0).getEmployeInComing());
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getEmployeOutGoing());
            listOfShiftSummary.add(preview);

            //Ara Toplam
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("subtotal"));
            preview.setInComing(listOfShiftSummaryDb.get(0).getTotalOfInComing());
            preview.setOutGoing(listOfShiftSummaryDb.get(0).getTotalOfOutGoing());
            listOfShiftSummary.add(preview);

            //Fark
            BigDecimal diffin = BigDecimal.ZERO;
            BigDecimal diffout = BigDecimal.ZERO;
            if (listOfShiftSummaryDb.get(0).getTotalOfInComing().compareTo(listOfShiftSummaryDb.get(0).getTotalOfOutGoing()) > 0) {
                diffin = BigDecimal.ZERO;
                diffout = listOfShiftSummaryDb.get(0).getTotalOfInComing().subtract(listOfShiftSummaryDb.get(0).getTotalOfOutGoing());
            } else if (listOfShiftSummaryDb.get(0).getTotalOfOutGoing().compareTo(listOfShiftSummaryDb.get(0).getTotalOfInComing()) > 0) {
                diffout = BigDecimal.ZERO;
                diffin = listOfShiftSummaryDb.get(0).getTotalOfOutGoing().subtract(listOfShiftSummaryDb.get(0).getTotalOfInComing());
            }

            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("difference"));
            preview.setInComing(diffin);
            preview.setOutGoing(diffout);
            listOfShiftSummary.add(preview);

            //Genel Toplam
            BigDecimal overralIn = diffin.add(listOfShiftSummaryDb.get(0).getTotalOfInComing());
            BigDecimal overralOut = diffout.add(listOfShiftSummaryDb.get(0).getTotalOfOutGoing());
            preview = new MarketShiftPreview();
            preview.setDescription(sessionBean.getLoc().getString("overalltotal"));
            preview.setInComing(overralIn);
            preview.setOutGoing(overralOut);
            listOfShiftSummary.add(preview);

        }
    }

    /**
     * Satış listesi Tablosu İçin Alt Toplam Hesaplaması Yapar.
     *
     * @param columnId Alt Toplam Yapılacak Colon Bilgisi
     * @return
     */
    public BigDecimal clcSubTotalSalesList(int columnId) {
        BigDecimal result = BigDecimal.ZERO;

        switch (columnId) {
            case 2:
                for (MarketShiftPreview mp : listOfSalesList) {
                    result = result.add(mp.getSaleAmount());
                }
                break;
            case 3:
                for (MarketShiftPreview mp : listOfSalesList) {
                    result = result.add(mp.getReturnAmount());
                }
                break;
            case 4:
                for (MarketShiftPreview mp : listOfSalesList) {
                    result = result.add(mp.getSalePrice());
                }
                break;
            case 5:
                for (MarketShiftPreview mp : listOfSalesList) {
                    result = result.add(mp.getReturnPrice());
                }
                break;
            case 7:
                for (MarketShiftPreview mp : listOfSalesList) {
                    result = result.add(mp.getTotalTaxPrice());
                }
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    /**
     * Satış listesi Tablosu İçin Alt Toplam Hesaplaması Yapar.
     *
     * @return
     */
    public String clcSubTotalSalesListUnit(boolean isRetunCount) {

        String total = "";
        HashMap<Integer, Unit> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfSalesList) {
            hm.put(listOfObject.getStock().getUnit().getId(), listOfObject.getStock().getUnit());
        }

        for (Map.Entry<Integer, Unit> entry : hm.entrySet()) {
            Unit value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfSalesList) {
                if (listOfObject.getStock().getUnit().getId() == entry.getKey()) {
                    if (isRetunCount) {
                        totalValue = totalValue.add(listOfObject.getReturnAmount());
                    } else {
                        totalValue = totalValue.add(listOfObject.getSaleAmount());
                    }

                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getSortName() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Satış İade Kategori Toplam Dökümü Tablosunda Toplam Alt Toplam
     * Hesaplamalarını Yapar
     *
     * @param columnId
     * @return
     */
    public String clcSubTotalSalesCategoryListUnit(int columnId) {
        BigDecimal total = BigDecimal.ZERO;
        String currency = "";
        String result = "";
        for (MarketShiftPreview marketShiftPreview : listOfStockGroupList) {
            switch (columnId) {
                case 1:
                    total = total.add(marketShiftPreview.getPreviousSaleAmount());
                    break;
                case 2:
                    total = total.add(marketShiftPreview.getPreviousSalePrice());
                    break;
                case 3:
                    total = total.add(marketShiftPreview.getPreviousAmount());
                    break;
                case 4:
                    total = total.add(marketShiftPreview.getPreviousPrice());
                    break;
                case 5:
                    total = total.add(marketShiftPreview.getSaleAmount());
                    break;
                case 6:
                    total = total.add(marketShiftPreview.getSalePrice());
                    break;
                case 7:
                    total = total.add(marketShiftPreview.getRemainingQuantity());
                    break;
                case 8:
                    total = total.add(marketShiftPreview.getRemainingPrice());
                    break;
                default:
                    total = total.add(BigDecimal.ZERO);
                    break;
            }
        }
        if (columnId == 2 || columnId == 4 || columnId == 6) {
            currency = sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        }

        result = String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(total)) + " " + currency;
        return result;
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
            case 2:
                for (MarketShiftPreview mp : listOfTaxList) {
                    if (mp.getSaleAmount() != null) {
                        result = result.add(mp.getSaleAmount());
                    }
                }
                break;
            case 3:
                for (MarketShiftPreview mp : listOfTaxList) {
                    if (mp.getReturnAmount() != null) {
                        result = result.add(mp.getReturnAmount());
                    }
                }
                break;
            case 4:
                for (MarketShiftPreview mp : listOfTaxList) {
                    if (mp.getSalePrice() != null) {
                        result = result.add(mp.getSalePrice());
                    }
                }
                break;
            case 5:
                for (MarketShiftPreview mp : listOfTaxList) {
                    if (mp.getReturnPrice() != null) {
                        result = result.add(mp.getReturnPrice());
                    }
                }
                break;
            case 6:
                for (MarketShiftPreview mp : listOfTaxList) {
                    if (mp.getTotalTaxPrice() != null) {
                        result = result.add(mp.getTotalTaxPrice());
                    }
                }
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    /**
     * Cari Toplam Tablosu İçin Alt Toplam Hesaplaması Yapar.
     */
    public String clcSubTotalAccountGrouplist() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfAccountGroup) {
            hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfAccountGroup) {
                if (listOfObject.getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSalePrice());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Kasa Devirleri Tablosu İçin Alt Toplam Hesaplaması Yapar.
     */
    public String clcSubTotalSafeTransferlist() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfSafeTransfer) {
            hm.put(listOfObject.getSafe().getCurrency().getId(), listOfObject.getSafe().getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfSafeTransfer) {
                if (listOfObject.getSafe().getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSafe().getBalance());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Açık Fazla Tabloları İçin Alt Toplam Hesaplaması Yapar.
     *
     * @param isEmployee Personele Göre İse True,Gelir-Gidere Göre İse False
     * @param columnId Alt Toplam Yapılacak Colon Bilgisi
     * @return
     */
    public BigDecimal clcDeficitSurplus(boolean isEmployee, int columnId) {
        BigDecimal result = BigDecimal.ZERO;
        switch (columnId) {
            case 1:
                if (isEmployee) {
                    for (MarketShiftPreview mp : listOfDeficitSurplusEmployee) {
                        result = result.add(mp.getInComing());
                    }
                } else {
                    for (MarketShiftPreview mp : listOfDeficitSurplusIncomeExpense) {
                        result = result.add(mp.getInComing());
                    }
                }
                break;
            case 2:
                if (isEmployee) {
                    for (MarketShiftPreview mp : listOfDeficitSurplusEmployee) {
                        result = result.add(mp.getOutGoing());
                    }
                } else {
                    for (MarketShiftPreview mp : listOfDeficitSurplusIncomeExpense) {
                        result = result.add(mp.getOutGoing());
                    }
                }
                break;
            default:
                result = BigDecimal.ZERO;
                break;
        }

        return result;
    }

    /**
     * Dövizli Toplamlar Alt Toplam Hesaplaması Yapar.
     *
     * @return
     */
    public String clcSubTotalCurrencyTotal() {

        String total = "";
        HashMap<Integer, Currency> hm = new HashMap();

        for (MarketShiftPreview listOfObject : listOfCurrencyTotal) {
            hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
        }

        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            for (MarketShiftPreview listOfObject : listOfCurrencyTotal) {
                if (listOfObject.getCurrency().getId() == entry.getKey()) {
                    totalValue = totalValue.add(listOfObject.getSalePrice());
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
    }

    /**
     * Kasiyer Nakit Teslimat,Kredi Kartı Teslimat ve Veresiye Teslimat
     * Tablolarının Alt Toplam İşlemlerini Yapar
     *
     * @param type
     * @param isTotalExchange
     * @return
     */
    public String clcCashierPaymentTotal(int type, boolean isTotalExchange) {
        String total = "";

        HashMap<Integer, Currency> hm = new HashMap();
        if (type == 1) {
            for (MarketShiftPreview listOfObject : listOfCashDelivery) {
                hm.put(listOfObject.getSafe().getCurrency().getId(), listOfObject.getSafe().getCurrency());
            }
        } else if (type == 2) {
            for (MarketShiftPreview listOfObject : listOfCreditCardDelivery) {
                hm.put(listOfObject.getBankAccount().getCurrency().getId(), listOfObject.getBankAccount().getCurrency());
            }
        } else if (type == 3) {
            for (MarketShiftPreview listOfObject : listOfCreditDelivery) {
                hm.put(listOfObject.getCurrency().getId(), listOfObject.getCurrency());
            }
        }
        for (Map.Entry<Integer, Currency> entry : hm.entrySet()) {
            Currency value = entry.getValue();
            BigDecimal totalValue = BigDecimal.ZERO;
            if (type == 1) {
                for (MarketShiftPreview listOfObject : listOfCashDelivery) {
                    if (listOfObject.getSafe().getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());

                        }
                    }
                }
            } else if (type == 2) {
                for (MarketShiftPreview listOfObject : listOfCreditCardDelivery) {
                    if (listOfObject.getBankAccount().getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());

                        }
                    }
                }
            } else if (type == 3) {
                for (MarketShiftPreview listOfObject : listOfCreditDelivery) {
                    if (listOfObject.getCurrency().getId() == entry.getKey()) {
                        if (isTotalExchange) {
                            totalValue = totalValue.add(listOfObject.getExchangePrice());
                        } else {
                            totalValue = totalValue.add(listOfObject.getSalePrice());
                        }
                    }
                }
            }
            total = total + " " + String.valueOf(unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(totalValue)) + entry.getValue().getCode() + " +";

        }

        if (total.length() > 0) {
            total = total.substring(0, total.length() - 1);
        }
        return total;
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

    public String readXml(String totalSaleAmount) {

        String totalAmount = "";
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource src = new InputSource();
            src.setCharacterStream(new StringReader(totalSaleAmount));
            src.setEncoding("UTF-8");
            org.w3c.dom.Document doc = builder.parse(src);
            doc.getDocumentElement().normalize();
            int count = 0;
            NodeList list = doc.getElementsByTagName("shiftsaletotal");
            if (list.getLength() == 0) {
                totalAmount = Integer.toString(0);
            }
            for (int s = 0; s < list.getLength(); s++) {
                NodeList elements = list.item(s).getChildNodes();
                if (count == 0) {
                    totalAmount += (unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                } else {
                    totalAmount += " + " + (unitNumberFormat(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding()).format(new BigDecimal(elements.item(0).getTextContent()))).toString() + " " + sessionBean.currencySignOrCode(Integer.valueOf(elements.item(1).getTextContent()), 0);
                }
                count = 1;

            }
        } catch (ParserConfigurationException ex) {
        } catch (SAXException | IOException ex) {
        }
        return totalAmount;
    }

}
