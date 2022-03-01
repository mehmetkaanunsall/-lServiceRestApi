package com.mepsan.marwiz.automation.report.fuelshiftreport.presentation;

import com.mepsan.marwiz.automation.report.fuelshiftreport.business.IFuelShiftSaleService;
import com.mepsan.marwiz.automation.report.fuelshiftreport.dao.IFuelShiftSaleDao;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.automation.FuelShift;
import com.mepsan.marwiz.general.model.automation.FuelShiftSales;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * Bu class vardiya süresince yapılan satışları listeler.
 *
 * @author Samet Dağ
 */
@ManagedBean
@ViewScoped
public class FuelShiftSaleReportBean extends GeneralReportBean<FuelShiftSales> {

    @ManagedProperty(value = "#{fuelShiftSaleService}")
    public IFuelShiftSaleService fuelShiftSaleService;

    @ManagedProperty(value = "#{fuelShiftSaleDao}")
    public IFuelShiftSaleDao fuelShiftSaleDao;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;


    FuelShift fuelShift;
    private List<FuelShiftSales> listOfTotals;
    private HashMap<Unit, BigDecimal> groupStockTotal;
    private BigDecimal totalMoney;

    public List<FuelShiftSales> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<FuelShiftSales> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public FuelShift getFuelShift() {
        return fuelShift;
    }

    public void setFuelShift(FuelShift fuelShift) {
        this.fuelShift = fuelShift;
    }

    public void setFuelShiftSaleDao(IFuelShiftSaleDao fuelShiftSaleDao) {
        this.fuelShiftSaleDao = fuelShiftSaleDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFuelShiftSaleService(IFuelShiftSaleService fuelShiftSaleService) {
        this.fuelShiftSaleService = fuelShiftSaleService;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------FuelShiftSaleReportBean");

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof FuelShift) {
                    fuelShift = (FuelShift) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }

        listOfTotals = new ArrayList<>();
        groupStockTotal = new HashMap<>();
        totalMoney = BigDecimal.valueOf(0);
        listOfObjects = findall(" ");

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
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
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public LazyDataModel<FuelShiftSales> findall(String where) {
        return new CentrowizLazyDataModel<FuelShiftSales>() {
            @Override
            public List<FuelShiftSales> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<FuelShiftSales> result = fuelShiftSaleService.findAll(first, pageSize, sortField, "", filters, where, fuelShift);
                listOfTotals = fuelShiftSaleService.totals(where, fuelShift);
                int count = 0;
                totalMoney = BigDecimal.valueOf(0);
                for (FuelShiftSales total : listOfTotals) {
                    count = count + total.getId();
                    totalMoney = totalMoney.add(total.getTotalMoney());
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void createPdf() {
        fuelShiftSaleService.exportPdf(" ", toogleList, fuelShift, listOfTotals);
    }

    public void createExcel() {
        fuelShiftSaleService.exportExcel(" ", toogleList, fuelShift, listOfTotals);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(fuelShiftSaleService.exportPrinter(" ", toogleList, fuelShift, listOfTotals)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public String calculateAccordingUnit() {
        groupStockTotal.clear();

        for (FuelShiftSales s : listOfTotals) {
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

}
