/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 02:51:43
 */
package com.mepsan.marwiz.general.report.marketshiftreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.SalePayment;
import com.mepsan.marwiz.general.model.general.Sales;
import com.mepsan.marwiz.general.model.general.Shift;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.primefaces.model.chart.PieChartModel;
import com.mepsan.marwiz.general.report.marketshiftreport.business.IMarketShiftReportService;

@ManagedBean
@ViewScoped
public class MarketShiftReportBean extends GeneralReportBean<Shift> {

    @ManagedProperty(value = "#{marketShiftReportService}")
    public IMarketShiftReportService marketShiftReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private List<Sales> listOfSaleByPOS; //graphic ikonuna basılınca POS'a göre vardiya satışları
    private List<Sales> listOfSaleByUser; //graphic ikonuna basılınca kullanıcıya göre vardiya satışları
    private List<SalePayment> listOfSaleBySaleType; //graphic ikonuna basılınca satış tipine göre vardiya satışları
    private BigDecimal posPercentage = BigDecimal.valueOf(0), userPercentage = BigDecimal.valueOf(0), saleTypePercentage = BigDecimal.valueOf(0);
    private PieChartModel pieChartModelSaleType;
    private Currency salesCurrency;
    private BigDecimal totalPriceForPOS, totalMoneyForPOS;
    private BigDecimal totalPriceForUser, totalMoneyForUser;
    private HashMap<Integer, BigDecimal> groupSaleTypeTotal;
    private String saleTypeTotal;

    public void setMarketShiftReportService(IMarketShiftReportService marketShiftReportService) {
        this.marketShiftReportService = marketShiftReportService;
    }

    public List<Sales> getListOfSaleByPOS() {
        return listOfSaleByPOS;
    }

    public void setListOfSaleByPOS(List<Sales> listOfSaleByPOS) {
        this.listOfSaleByPOS = listOfSaleByPOS;
    }

    public List<Sales> getListOfSaleByUser() {
        return listOfSaleByUser;
    }

    public void setListOfSaleByUser(List<Sales> listOfSaleByUser) {
        this.listOfSaleByUser = listOfSaleByUser;
    }

    public List<SalePayment> getListOfSaleBySaleType() {
        return listOfSaleBySaleType;
    }

    public void setListOfSaleBySaleType(List<SalePayment> listOfSaleBySaleType) {
        this.listOfSaleBySaleType = listOfSaleBySaleType;
    }

    public PieChartModel getPieChartModelSaleType() {
        return pieChartModelSaleType;
    }

    public void setPieChartModelSaleType(PieChartModel pieChartModelSaleType) {
        this.pieChartModelSaleType = pieChartModelSaleType;
    }

    public Currency getSalesCurrency() {
        return salesCurrency;
    }

    public void setSalesCurrency(Currency salesCurrency) {
        this.salesCurrency = salesCurrency;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getSaleTypeTotal() {
        return saleTypeTotal;
    }

    public void setSaleTypeTotal(String saleTypeTotal) {
        this.saleTypeTotal = saleTypeTotal;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public BigDecimal getPosPercentage() {
        return posPercentage;
    }

    public void setPosPercentage(BigDecimal posPercentage) {
        this.posPercentage = posPercentage;
    }

    public BigDecimal getUserPercentage() {
        return userPercentage;
    }

    public void setUserPercentage(BigDecimal userPercentage) {
        this.userPercentage = userPercentage;
    }

    public BigDecimal getSaleTypePercentage() {
        return saleTypePercentage;
    }

    public void setSaleTypePercentage(BigDecimal saleTypePercentage) {
        this.saleTypePercentage = saleTypePercentage;
    }

    public BigDecimal getTotalPriceForPOS() {
        return totalPriceForPOS;
    }

    public void setTotalPriceForPOS(BigDecimal totalPriceForPOS) {
        this.totalPriceForPOS = totalPriceForPOS;
    }

    public BigDecimal getTotalMoneyForPOS() {
        return totalMoneyForPOS;
    }

    public void setTotalMoneyForPOS(BigDecimal totalMoneyForPOS) {
        this.totalMoneyForPOS = totalMoneyForPOS;
    }

    public BigDecimal getTotalPriceForUser() {
        return totalPriceForUser;
    }

    public void setTotalPriceForUser(BigDecimal totalPriceForUser) {
        this.totalPriceForUser = totalPriceForUser;
    }

    public BigDecimal getTotalMoneyForUser() {
        return totalMoneyForUser;
    }

    public void setTotalMoneyForUser(BigDecimal totalMoneyForUser) {
        this.totalMoneyForUser = totalMoneyForUser;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------ShiftReportBean");
        selectedObject = new Shift();
        listOfSaleByPOS = new ArrayList<>();
        listOfSaleByUser = new ArrayList<>();
        listOfSaleBySaleType = new ArrayList<>();
        salesCurrency = new Currency();
        groupSaleTypeTotal = new HashMap<>();

        listOfObjects = findall(" ");

        toogleList = Arrays.asList(true, true, true, true, true);

    }

    @Override
    public LazyDataModel<Shift> findall(String where) {
        return new CentrowizLazyDataModel<Shift>() {
            @Override
            public List<Shift> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Shift> result = marketShiftReportService.findAll(first, pageSize, sortField, "", filters, where);
                int count = marketShiftReportService.count(where);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void findGraphic(int id, String shiftNo) {
        selectedObject = new Shift();
        listOfSaleByPOS.clear();
        listOfSaleByUser.clear();
        listOfSaleBySaleType.clear();
        posPercentage = BigDecimal.valueOf(0);
        userPercentage = BigDecimal.valueOf(0);
        saleTypePercentage = BigDecimal.valueOf(0);
        totalMoneyForPOS = BigDecimal.valueOf(0);
        totalPriceForPOS = BigDecimal.valueOf(0);
        totalMoneyForUser = BigDecimal.valueOf(0);
        totalPriceForUser = BigDecimal.valueOf(0);

        selectedObject.setId(id);
        selectedObject.setShiftNo(shiftNo);

        //POS
        listOfSaleByPOS = marketShiftReportService.listOfSalePOS(selectedObject);
        for (Sales s : listOfSaleByPOS) {
            posPercentage = posPercentage.add(s.getTotalMoney());
            totalMoneyForPOS = totalMoneyForPOS.add(s.getTotalMoney());
            totalPriceForPOS = totalPriceForPOS.add(s.getTotalPrice());

        }
        if (!listOfSaleByPOS.isEmpty()) {
            salesCurrency.setId(listOfSaleByPOS.get(0).getCurrency().getId());
        }
        //USER
        listOfSaleByUser = marketShiftReportService.listOfSaleUser(selectedObject);
        for (Sales s : listOfSaleByUser) {
            if (s.getUser().getId() > 0) {
                userPercentage = userPercentage.add(s.getTotalMoney());
                totalMoneyForUser = totalMoneyForUser.add(s.getTotalMoney());
                totalPriceForUser = totalPriceForUser.add(s.getTotalPrice());

            }
        }
        if (!listOfSaleByUser.isEmpty()) {
            salesCurrency.setId(listOfSaleByUser.get(0).getCurrency().getId());
        }

        //SALE TYPE
        listOfSaleBySaleType = marketShiftReportService.listOfSaleType(selectedObject);

        for (SalePayment s : listOfSaleBySaleType) {
            saleTypePercentage = saleTypePercentage.add(s.getPrice());

        }
        calculateTotalSalesType();
        createAreaSaleTypeModel();

        RequestContext.getCurrentInstance().execute("PF('dlg_ShiftGraphic').show()");
    }

    //satış tipine göre grafik
    void createAreaSaleTypeModel() {
        pieChartModelSaleType = new PieChartModel();
        pieChartModelSaleType.setSeriesColors("83BFFF, FFC266, FF7B65, CAEEFC, 9ADBAD, FFF1B2, FFE0B2, FFBEB2, B1AFDB, 278ECF, 4BD762, FFCA1F, FF9416, D42AE8, 535AD7, FF402C");

        for (SalePayment s : listOfSaleBySaleType) {
            String Name = "";
            if (s.getType().getId() > 0) {
                Name = s.getType().getTag() + " " + sessionBean.currencySignOrCode(s.getCurrency().getId(), 0);
            } else if (s.getType().getId() == 0) {
                Name = sessionBean.getLoc().getString("open") + " " + sessionBean.currencySignOrCode(s.getCurrency().getId(), 0);
            }
            pieChartModelSaleType.set(Name, s.getPrice());
        }

        pieChartModelSaleType.setLegendPosition("w");

    }

    /**
     * Satış Tipi Tablosunda toplamı para birimine göre hesaplayan fonksiyondur
     *
     * @return
     */
    public String calculateTotalSalesType() {
        groupSaleTypeTotal.clear();

        for (SalePayment s : listOfSaleBySaleType) {

            if (groupSaleTypeTotal.containsKey(s.getCurrency().getId())) {

                BigDecimal old = groupSaleTypeTotal.get(s.getCurrency().getId());
                groupSaleTypeTotal.put(s.getCurrency().getId(), old.add(s.getPrice()));

            } else {
                groupSaleTypeTotal.put(s.getCurrency().getId(), s.getPrice());
            }
        }

        saleTypeTotal = "";
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : groupSaleTypeTotal.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp == 1) {
                if (temp == 0) {
                    temp = 1;
                    saleTypeTotal += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTypeTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                } else {
                    saleTypeTotal += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue()));
                    if (entry.getKey() != 0) {
                        saleTypeTotal += " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }

        return saleTypeTotal;
    }

    public void goToPrintDetail(int id, String shiftNo, Date beginDate, Date endDate, int saleCount, int statusId) {
        selectedObject = new Shift();
        selectedObject.setId(id);
        selectedObject.setShiftNo(shiftNo);
        selectedObject.setBeginDate(beginDate);
        selectedObject.setEndDate(endDate);
        selectedObject.setSaleCount(saleCount);
        selectedObject.getStatus().setId(statusId);
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/general/report/marketshiftreport/marketshiftreportprintdetail.xhtml", list, 0, 48);
    }

    /**
     * Double clickle çalışan fonksiyondur.
     */
    public void goToDetail() {
        List<Object> list = new ArrayList<>();
        list.add(selectedObject);
        marwiz.goToPage("/pages/general/report/marketshiftreport/marketshiftreportdetail.xhtml", list, 0, 49);
    }

    public void createPdf() {
        marketShiftReportService.exportPdf(" ", toogleList);
    }

    public void createExcel() throws IOException {
        marketShiftReportService.exportExcel(" ", toogleList);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(marketShiftReportService.exportPrinter(" ", toogleList)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public BigDecimal calculatePercentage(BigDecimal money, int type) {
        if (money != null) {
            if (type == 1) {
                if (posPercentage.compareTo(BigDecimal.valueOf(0)) == 0) {
                    return BigDecimal.valueOf(0);
                } else {
                    money = money.multiply(BigDecimal.valueOf(100));
                    money = money.divide(posPercentage, sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN);
                    return money;
                }
            } else if (type == 2) {
                if (userPercentage.compareTo(BigDecimal.valueOf(0)) == 0) {
                    return BigDecimal.valueOf(0);
                } else {
                    money = money.multiply(BigDecimal.valueOf(100));
                    money = money.divide(userPercentage, sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN);
                    return money;
                }
            } else if (type == 3) {
                if (saleTypePercentage.compareTo(BigDecimal.valueOf(0)) == 0) {
                    return BigDecimal.valueOf(0);
                } else {
                    money = money.multiply(BigDecimal.valueOf(100));
                    money = money.divide(saleTypePercentage, sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN);
                    return money;
                }
            }
        }
        return money;
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

}
