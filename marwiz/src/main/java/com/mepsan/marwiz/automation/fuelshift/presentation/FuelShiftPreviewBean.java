/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   22.02.2019 11:31:06
 */
package com.mepsan.marwiz.automation.fuelshift.presentation;

import com.mepsan.marwiz.automation.fuelshift.business.IFuelShiftTransferService;
import com.mepsan.marwiz.automation.fuelshift.dao.FuelShiftPreview;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.unit.business.IUnitService;
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

@ManagedBean
@ViewScoped
public class FuelShiftPreviewBean {

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{fuelShiftTransferService}")
    private IFuelShiftTransferService fuelShiftTransferService;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    private List<FuelShiftPreview> listOfStockSales;
    private FuelShift selectedShift;
    private List<FuelShiftPreview> listOfCashDelivery;
    private List<FuelShiftPreview> listOfCreditCardDelivery;
    private List<FuelShiftPreview> listOfCredit;
    private List<FuelShiftPreview> listOfAccountRecoveries;
    private List<FuelShiftPreview> listOfGeneralTotal;
    private BranchSetting branchSetting;

    private List<Unit> unitList;

    private HashMap<Integer, BigDecimal> groupTotal;

    private BigDecimal total1, total2, total3, total4;
    private String total5;

    public List<FuelShiftPreview> getListOfStockSales() {
        return listOfStockSales;
    }

    public void setListOfStockSales(List<FuelShiftPreview> listOfStockSales) {
        this.listOfStockSales = listOfStockSales;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public FuelShift getSelectedShift() {
        return selectedShift;
    }

    public void setSelectedShift(FuelShift selectedShift) {
        this.selectedShift = selectedShift;
    }

    public void setFuelShiftTransferService(IFuelShiftTransferService fuelShiftTransferService) {
        this.fuelShiftTransferService = fuelShiftTransferService;
    }

    public BigDecimal getTotal1() {
        return total1;
    }

    public void setTotal1(BigDecimal total1) {
        this.total1 = total1;
    }

    public BigDecimal getTotal2() {
        return total2;
    }

    public void setTotal2(BigDecimal total2) {
        this.total2 = total2;
    }

    public BigDecimal getTotal3() {
        return total3;
    }

    public void setTotal3(BigDecimal total3) {
        this.total3 = total3;
    }

    public BigDecimal getTotal4() {
        return total4;
    }

    public void setTotal4(BigDecimal total4) {
        this.total4 = total4;
    }

    public List<FuelShiftPreview> getListOfCashDelivery() {
        return listOfCashDelivery;
    }

    public void setListOfCashDelivery(List<FuelShiftPreview> listOfCashDelivery) {
        this.listOfCashDelivery = listOfCashDelivery;
    }

    public List<FuelShiftPreview> getListOfCreditCardDelivery() {
        return listOfCreditCardDelivery;
    }

    public void setListOfCreditCardDelivery(List<FuelShiftPreview> listOfCreditCardDelivery) {
        this.listOfCreditCardDelivery = listOfCreditCardDelivery;
    }

    public List<FuelShiftPreview> getListOfCredit() {
        return listOfCredit;
    }

    public void setListOfCredit(List<FuelShiftPreview> listOfCredit) {
        this.listOfCredit = listOfCredit;
    }

    public List<FuelShiftPreview> getListOfAccountRecoveries() {
        return listOfAccountRecoveries;
    }

    public void setListOfAccountRecoveries(List<FuelShiftPreview> listOfAccountRecoveries) {
        this.listOfAccountRecoveries = listOfAccountRecoveries;
    }

    public List<FuelShiftPreview> getListOfGeneralTotal() {
        return listOfGeneralTotal;
    }

    public void setListOfGeneralTotal(List<FuelShiftPreview> listOfGeneralTotal) {
        this.listOfGeneralTotal = listOfGeneralTotal;
    }

    public String getTotal5() {
        return total5;
    }

    public void setTotal5(String total5) {
        this.total5 = total5;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    @PostConstruct
    public void init() {
        listOfStockSales = new ArrayList<>();
        listOfCashDelivery = new ArrayList<>();
        listOfCreditCardDelivery = new ArrayList<>();
        listOfCredit = new ArrayList<>();
        listOfAccountRecoveries = new ArrayList<>();
        listOfGeneralTotal = new ArrayList<>();
        selectedShift = new FuelShift();
        branchSetting = new BranchSetting();
        groupTotal = new HashMap<>();
        unitList = new ArrayList<>();

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShift) {
                    selectedShift = (FuelShift) ((ArrayList) sessionBean.parameter).get(i);
                    findUnit();
                    ///Stok Miktarına Göre
                    listOfStockSales = fuelShiftTransferService.findSalesAccordingToStockForPreview(selectedShift);
                    ///Nakit Teslimat
                    listOfCashDelivery = fuelShiftTransferService.shiftPaymentCashDetailForPreview(selectedShift);
                    //Kredi Kartı teslimatı
                    listOfCreditCardDelivery = fuelShiftTransferService.shiftPaymentCreditCardDetailForPreview(selectedShift);
                    ////Veresiye teslimatı
                    listOfCredit = fuelShiftTransferService.shiftPaymentCreditForPreview(selectedShift);
                    ////CAri teslimatları
                    listOfAccountRecoveries = fuelShiftTransferService.shiftPaymentDeficitExcessForPreview(selectedShift);
                    ////Genel Toplam
                    branchSetting = sessionBean.getLastBranchSetting();
                    listOfGeneralTotal = fuelShiftTransferService.shiftGeneralTotalForPreview(selectedShift, branchSetting);
                }
            }
        }
    }

    public BigDecimal calculateAvUnitPrice(BigDecimal amount, BigDecimal price) {
        if (amount != null && amount.compareTo(BigDecimal.valueOf(0)) != 0) {
            return price.divide(amount, 4, RoundingMode.HALF_EVEN);
        } else {
            return BigDecimal.valueOf(0);
        }
    }

    public BigDecimal calculateStockTotal() {
        total1 = BigDecimal.valueOf(0);

        for (FuelShiftPreview s : listOfStockSales) {
            total1 = total1.add(s.getTotalMoney());
        }

        return total1;

    }

    public String calculateAccordingUnit(int type) {
        groupTotal.clear();

        for (FuelShiftPreview s : listOfStockSales) {
            switch (type) {
                case 1:
                    //Önceki Miktar
                    if (groupTotal.containsKey(s.getStock().getUnit().getId())) {
                        BigDecimal old = groupTotal.get(s.getStock().getUnit().getId());
                        groupTotal.put(s.getStock().getUnit().getId(), old.add(s.getPreviousAmount()));
                    } else {
                        groupTotal.put(s.getStock().getUnit().getId(), s.getPreviousAmount());
                    }
                    break;
                case 2:
                    //Satış Miktarı
                    if (groupTotal.containsKey(s.getStock().getUnit().getId())) {
                        BigDecimal old = groupTotal.get(s.getStock().getUnit().getId());
                        groupTotal.put(s.getStock().getUnit().getId(), old.add(s.getAmount()));
                    } else {
                        groupTotal.put(s.getStock().getUnit().getId(), s.getAmount());
                    }
                    break;
                case 3:
                    //Kalan Miktar
                    if (groupTotal.containsKey(s.getStock().getUnit().getId())) {
                        BigDecimal old = groupTotal.get(s.getStock().getUnit().getId());
                        groupTotal.put(s.getStock().getUnit().getId(), old.add(s.getRemainingAmount()));
                    } else {
                        groupTotal.put(s.getStock().getUnit().getId(), s.getRemainingAmount());
                    }
                    break;
                default:
                    break;
            }

        }
        String t = "";
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupTotal.entrySet()) {

            if (temp == 0) {
                temp = 1;
                Unit unit = new Unit();
                for (Unit unit1 : unitList) {
                    if (unit1.getId() == entry.getKey()) {
                        unit.setSortName(unit1.getSortName());
                        unit.setUnitRounding(unit1.getUnitRounding());
                        break;
                    }
                }

                t += String.valueOf(unitNumberFormat(unit.getUnitRounding()).format(entry.getValue()));
                if (entry.getKey() != 0) {
                    t += " " + unit.getSortName();
                }
            } else {
                Unit unit = new Unit();
                for (Unit unit1 : unitList) {
                    if (unit1.getId() == entry.getKey()) {
                        unit.setSortName(unit1.getSortName());
                        unit.setUnitRounding(unit1.getUnitRounding());
                        break;
                    }
                }
                t += " + " + String.valueOf(unitNumberFormat(unit.getUnitRounding()).format(entry.getValue()));
                if (entry.getKey() != 0) {
                    t += " " + unit.getSortName();
                }
            }
        }

        return t;
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

    public BigDecimal calculateCashDelivery() {
        total1 = BigDecimal.valueOf(0);
        for (FuelShiftPreview s : listOfCashDelivery) {
            total1 = total1.add(s.getPrice());
        }
        return total1;
    }

    public String calculateCreditCard() {
        total1 = BigDecimal.valueOf(0);
        total5 = "";
        Currency currency = new Currency();
        for (FuelShiftPreview s : listOfCreditCardDelivery) {
            total1 = total1.add(s.getPrice());
            currency.setId(s.getBankAccount().getCurrency().getId());
        }
        total5 = sessionBean.getNumberFormat().format(total1) + sessionBean.currencySignOrCode(currency.getId(), 0);
        return total5;
    }

    public BigDecimal calculateCredit() {
        total1 = BigDecimal.valueOf(0);
        total2 = BigDecimal.valueOf(0);
        for (FuelShiftPreview s : listOfCredit) {
            total1 = total1.add(BigDecimal.valueOf(s.getSaleCount()));
            total2 = total2.add(s.getPrice());
        }
        return total1;
    }

    public BigDecimal calculateAccountRecoveries() {
        total1 = BigDecimal.valueOf(0);
        total2 = BigDecimal.valueOf(0);
        for (FuelShiftPreview s : listOfAccountRecoveries) {
            total1 = total1.add(s.getIncomingAmount());
            total2 = total2.add(s.getOutcomingAmount());
        }
        return total1;
    }

    public BigDecimal calculateGeneralDifference() {
        BigDecimal entryttotal = BigDecimal.valueOf(0);
        BigDecimal exittotal = BigDecimal.valueOf(0);
        if (!listOfGeneralTotal.isEmpty()) {
            entryttotal = listOfGeneralTotal.get(0).getEntrySubTotal().add(selectedShift.getTotalMoney());
            exittotal = listOfGeneralTotal.get(0).getExitSubTotal();
        }

        total1 = BigDecimal.ZERO;
        total2 = BigDecimal.ZERO;
        total3 = BigDecimal.ZERO;
        total4 = BigDecimal.ZERO;

        if (entryttotal.compareTo(exittotal) > 0) {
            total1 = BigDecimal.ZERO;
            total2 = entryttotal.subtract(exittotal);
        } else if (exittotal.compareTo(entryttotal) > 0) {
            total2 = BigDecimal.ZERO;
            total1 = exittotal.subtract(entryttotal);
        }

        total3 = total1.add(entryttotal);
        total4 = total2.add(exittotal);

        return total1;
    }

    /**
     * Bu metot merkezi entegrasyona göre tüm birimleri çekmek için kullanılır.
     */
    public void findUnit() {
        unitList = new ArrayList<>();
        unitList = unitService.findAll();
    }

}
