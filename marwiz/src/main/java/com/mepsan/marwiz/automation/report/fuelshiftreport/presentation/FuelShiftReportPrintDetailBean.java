package com.mepsan.marwiz.automation.report.fuelshiftreport.presentation;

import com.mepsan.marwiz.general.model.automation.FuelSaleType;
import com.mepsan.marwiz.automation.report.fuelshiftreport.business.IFuelShiftSaleService;
import com.mepsan.marwiz.automation.saletype.business.ISaleTypeService;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
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
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author Samet Dağ
 * @date 05.11.2018
 * @summary Bu CLass akaryakıt vardiya raporu detayı yazdırma ekranındaki
 * işlemleri gerçekleştirir.
 */
@ManagedBean
@ViewScoped
public class FuelShiftReportPrintDetailBean extends GeneralReportBean<FuelShift> {

    @ManagedProperty(value = "#{fuelShiftSaleService}")
    private IFuelShiftSaleService fuelShiftSaleService;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{saleTypeService}")
    private ISaleTypeService saleTypeService;

    FuelShift selectedFuelShift;
    FuelShiftSales selectedFuelShiftSale;

    List<FuelShiftSales> listOfSaleTypes;
    List<FuelSaleType> listFuelSaleTypes;

    int processType, saleCount, incomeExpense;//salecount : pompacının toplam satış sayısı , satış tipini hangi pompacının ne kadar sattığı vs.

    private HashMap<Unit, BigDecimal> groupStockTotal;

    private BranchSetting branchSetting;
    private String fuelSaleTypeName;
    private List<FuelSaleType> listForSaleTypeList;

    public int getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(int incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(int saleCount) {
        this.saleCount = saleCount;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public FuelShift getSelectedFuelShift() {
        return selectedFuelShift;
    }

    public void setSelectedFuelShift(FuelShift selectedFuelShift) {
        this.selectedFuelShift = selectedFuelShift;
    }

    public List<FuelSaleType> getListFuelSaleTypes() {
        return listFuelSaleTypes;
    }

    public void setListFuelSaleTypes(List<FuelSaleType> listFuelSaleTypes) {
        this.listFuelSaleTypes = listFuelSaleTypes;
    }

    public FuelShiftSales getSelectedFuelShiftSale() {
        return selectedFuelShiftSale;
    }

    public void setSelectedFuelShiftSale(FuelShiftSales selectedFuelShiftSale) {
        this.selectedFuelShiftSale = selectedFuelShiftSale;
    }

    public void setFuelShiftSaleService(IFuelShiftSaleService fuelShiftSaleService) {
        this.fuelShiftSaleService = fuelShiftSaleService;
    }

    public List<FuelShiftSales> getListOfSaleTypes() {
        return listOfSaleTypes;
    }

    public void setListOfSaleTypes(List<FuelShiftSales> listOfSaleTypes) {
        this.listOfSaleTypes = listOfSaleTypes;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public BranchSetting getBranchSetting() {
        return branchSetting;
    }

    public void setBranchSetting(BranchSetting branchSetting) {
        this.branchSetting = branchSetting;
    }

    public void setSaleTypeService(ISaleTypeService saleTypeService) {
        this.saleTypeService = saleTypeService;
    }

    public String getFuelSaleTypeName() {
        return fuelSaleTypeName;
    }

    public void setFuelSaleTypeName(String fuelSaleTypeName) {
        this.fuelSaleTypeName = fuelSaleTypeName;
    }

    public List<FuelSaleType> getListForSaleTypeList() {
        return listForSaleTypeList;
    }

    public void setListForSaleTypeList(List<FuelSaleType> listForSaleTypeList) {
        this.listForSaleTypeList = listForSaleTypeList;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------FuelShiftReportPrintDetailBean");

        listOfSaleTypes = new ArrayList<>();
        saleCount = 0;
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShift) {
                    selectedFuelShift = (FuelShift) ((ArrayList) sessionBean.parameter).get(i);
                    listOfSaleTypes = fuelShiftSaleService.listPrintRecords(selectedFuelShift);
                    for (FuelShiftSales fuelShiftSales : listOfSaleTypes) {
                        saleCount += fuelShiftSales.getSaleCount();
                    }
                }
            }
        }

        branchSetting = sessionBean.getLastBranchSetting();
        if (branchSetting.getAutomationId() == 5) {
            listForSaleTypeList = saleTypeService.findSaleTypeForBranch("AND fst.branch_id = " + sessionBean.getUser().getLastBranch().getId());
            listForSaleTypeList.forEach((saleTypelist) -> {
                if (saleTypelist.getTypeno() == 3) {
                    fuelSaleTypeName = saleTypelist.getName();
                }
            });
        }

        groupStockTotal = new HashMap<>();
        processType = 1;

    }

    /**
     * Bu metod hangi pompacının hangi satış türünden kaç adet satış yaptığını
     * veya hangi satış türünü hangi pompacının kaç adet satış yaptığını bulur.
     *
     * @param type 0:satış tipine göre ,1 :pompacıya göre
     * @param name satış tip no veya pompacı adı
     * @return
     */
    public int calculateSalesCount(int type, String name) {
        int count = 0;
        for (FuelShiftSales fuelShiftSales : listOfSaleTypes) {

            if (type == 1) {//popmacıya göre ise
                if (fuelShiftSales.getAttendant().equals(name)) {
                    count += fuelShiftSales.getSaleCount();
                }
            } else if (fuelShiftSales.getFuelSaleType().getTypeno() == Integer.parseInt(name)) {
                count += fuelShiftSales.getSaleCount();
            }
        }
        return count;
    }

    /**
     * Bu netod vardiyada yapılan toplam para satışını döndürür.
     *
     * @return
     */
    public Double calculateTotalMoney() {
        Double d = 0.0;
        for (FuelShiftSales fuelShiftSales : listOfSaleTypes) {
            d += fuelShiftSales.getTotalMoney().doubleValue();
        }
        return d;
    }

    public String calculateAccordingUnit() {
        groupStockTotal.clear();

        for (FuelShiftSales s : listOfSaleTypes) {
            Unit keyUnit = new Unit();
            keyUnit.setId(s.getUnit().getId());
            keyUnit.setSortName(s.getUnit().getSortName());
            keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
            if (groupStockTotal.containsKey(keyUnit)) {
                BigDecimal old = groupStockTotal.get(keyUnit);
                groupStockTotal.put(keyUnit, old.add(s.getLiter()));
            } else {
                groupStockTotal.put(keyUnit, s.getLiter());
            }
        }
        String t = "";
        int temp = 0;
        for (Map.Entry<Unit, BigDecimal> entry : groupStockTotal.entrySet()) {
            if (temp == 0) {
                temp = 1;
                t += String.valueOf(unitNumberFormat(entry.getKey().getUnitRounding()).format(entry.getValue()));
                if (entry.getKey().getId() != 0) {
                    t += " " + entry.getKey().getSortName();
                }
            } else {
                t += " + " + String.valueOf(unitNumberFormat(entry.getKey().getUnitRounding()).format(entry.getValue()));
                if (entry.getKey().getId() != 0) {
                    t += " " + entry.getKey().getSortName();
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

    public String calculateTotalStockForUnit(int id, String code, int type) {
        String t = "";
        groupStockTotal.clear();
        if (type == 1) {//Satış Tipi
            for (FuelShiftSales s : listOfSaleTypes) {
                if (s.getFuelSaleType().getId() == id) {
                    Unit keyUnit = new Unit();
                    keyUnit.setId(s.getUnit().getId());
                    keyUnit.setSortName(s.getUnit().getSortName());
                    keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
                    if (groupStockTotal.containsKey(keyUnit)) {
                        BigDecimal old = groupStockTotal.get(keyUnit);
                        groupStockTotal.put(keyUnit, old.add(s.getLiter()));
                    } else {
                        groupStockTotal.put(keyUnit, s.getLiter());
                    }
                }
            }

        } else if (type == 2) {//Pompacı
            for (FuelShiftSales s : listOfSaleTypes) {
                if (s.getAttendantCode().equals(code)) {
                    Unit keyUnit = new Unit();
                    keyUnit.setId(s.getUnit().getId());
                    keyUnit.setSortName(s.getUnit().getSortName());
                    keyUnit.setUnitRounding(s.getUnit().getUnitRounding());
                    if (groupStockTotal.containsKey(keyUnit)) {
                        BigDecimal old = groupStockTotal.get(keyUnit);
                        groupStockTotal.put(keyUnit, old.add(s.getLiter()));
                    } else {
                        groupStockTotal.put(keyUnit, s.getLiter());
                    }
                }
            }
        } else if (type == 3) {//satış tipi para
            BigDecimal total = BigDecimal.valueOf(0);
            for (FuelShiftSales s : listOfSaleTypes) {
                if (s.getFuelSaleType().getId() == id) {
                    total = total.add(s.getTotalMoney());
                }
            }
            t = String.valueOf(sessionBean.getNumberFormat().format(total)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);

        } else if (type == 4) {//Pompacı para
            BigDecimal total = BigDecimal.valueOf(0);
            for (FuelShiftSales s : listOfSaleTypes) {
                if (s.getAttendantCode().equals(code)) {
                    total = total.add(s.getTotalMoney());
                }
            }
            t = String.valueOf(sessionBean.getNumberFormat().format(total)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        }

        if (type == 1 || type == 2) {
            int temp = 0;
            for (Map.Entry<Unit, BigDecimal> entry : groupStockTotal.entrySet()) {
                if (temp == 0) {
                    temp = 1;
                    t += String.valueOf(unitNumberFormat(entry.getKey().getUnitRounding()).format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        t += " " + entry.getKey().getSortName();
                    }
                } else {
                    t += " + " + String.valueOf(unitNumberFormat(entry.getKey().getUnitRounding()).format(entry.getValue()));
                    if (entry.getKey().getId() != 0) {
                        t += " " + entry.getKey().getSortName();
                    }
                }
            }

        }

        return t;
    }

    @Override
    public void find() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<FuelShift> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
