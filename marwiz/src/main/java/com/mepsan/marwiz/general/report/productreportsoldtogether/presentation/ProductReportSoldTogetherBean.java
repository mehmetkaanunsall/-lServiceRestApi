/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 11:59:42 AM
 */
package com.mepsan.marwiz.general.report.productreportsoldtogether.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.wot.HourInterval;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.productreportsoldtogether.business.IProdutReportSoldTogetherService;
import com.mepsan.marwiz.general.report.productreportsoldtogether.dao.ProductReportSoldTogether;
import com.mepsan.marwiz.system.branch.business.BranchSettingService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class ProductReportSoldTogetherBean extends GeneralReportBean<ProductReportSoldTogether> {

    private int currentYear;
    private String createWhere;
    private List<HourInterval> listOfHourInterval;
    private List<BranchSetting> listOfBranch;
    private BranchSetting selectedBranch;

    @ManagedProperty(value = "#{stockBookFilterBean}")
    private StockBookFilterBean stockBookFilterBean;

    @ManagedProperty(value = "#{produtReportSoldTogetherService}")
    private IProdutReportSoldTogetherService produtReportSoldTogetherService;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private BranchSettingService branchSettingService;

    public void setStockBookFilterBean(StockBookFilterBean stockBookFilterBean) {
        this.stockBookFilterBean = stockBookFilterBean;
    }

    public void setProdutReportSoldTogetherService(IProdutReportSoldTogetherService produtReportSoldTogetherService) {
        this.produtReportSoldTogetherService = produtReportSoldTogetherService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public List<HourInterval> getListOfHourInterval() {
        return listOfHourInterval;
    }

    public void setListOfHourInterval(List<HourInterval> listOfHourInterval) {
        this.listOfHourInterval = listOfHourInterval;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public BranchSetting getSelectedBranch() {
        return selectedBranch;
    }

    public void setSelectedBranch(BranchSetting selectedBranch) {
        this.selectedBranch = selectedBranch;
    }

    public void setBranchSettingService(BranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    @PostConstruct
    @Override
    public void init() {
        //  System.out.println("--------ProductReportSoldTogetherBean------");

        selectedObject = new ProductReportSoldTogether();
        listOfHourInterval = new ArrayList<>();
        Calendar cal = GregorianCalendar.getInstance();
        currentYear = cal.get(Calendar.YEAR);
        selectedBranch = new BranchSetting();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(cal.getTime());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cal.getTime());

        selectedBranch.getBranch().setId(sessionBean.getUser().getLastBranch().getId());
        selectedBranch.getBranch().setName(sessionBean.getUser().getLastBranch().getName());
        selectedBranch.setIsCentralIntegration(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration());

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, false, true, true);
        }

        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker
        updateBranch();
    }

    /**
     * Kitapta seçili olan bilgiyi objeye set eder , güncelleme işlemi yapar.
     */
    public void updateAllInformation() {
        if (stockBookFilterBean.getSelectedData() != null) {
            selectedObject.setStock1(stockBookFilterBean.getSelectedData());
            RequestContext.getCurrentInstance().update("frmProductReportSoldTogether:txtStock");
            stockBookFilterBean.setSelectedData(null);
        }

    }

    /**
     * Zaman aralığı seçimi değiştikçe başlangıç bitiş tarihlerini
     * değiştirir.Eğer vardiya numarası seçildi ise reportType set edilir,
     * exportlarda ve listelemede bu şekilde kullanılır.
     */
    public void changeTimeInterval() {
        switch (selectedObject.getTimeInterval()) {
            case 0:
                Calendar cal = GregorianCalendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                cal.set(Calendar.HOUR_OF_DAY, 00);
                cal.set(Calendar.MINUTE, 00);
                cal.set(Calendar.SECOND, 00);
                selectedObject.setBeginDate(cal.getTime());
                cal.add(Calendar.MONTH, 1);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                selectedObject.setEndDate(cal.getTime());
                break;
            case 6:
                selectedObject.setReportType(0);
                isFind = false;
                break;
            default:
                break;
        }

    }

    /**
     * Saat dilimi dğiştiği zaman saat listesine o dilime göre zamanları ekler.
     */
    public void changeTimezone() {
        listOfHourInterval.clear();
        switch (selectedObject.getTimezone()) {
            case 3:
                listOfHourInterval.add(new HourInterval("0003", "00:00-03:00"));
                listOfHourInterval.add(new HourInterval("0306", "03:00-06:00"));
                listOfHourInterval.add(new HourInterval("0609", "06:00-09:00"));
                listOfHourInterval.add(new HourInterval("0912", "09:00-12:00"));
                listOfHourInterval.add(new HourInterval("1215", "12:00-15:00"));
                listOfHourInterval.add(new HourInterval("1518", "15:00-18:00"));
                listOfHourInterval.add(new HourInterval("1821", "18:00-21:00"));
                listOfHourInterval.add(new HourInterval("2123", "21:00-00:00"));
                break;
            case 6:
                listOfHourInterval.add(new HourInterval("0006", "00:00-06:00"));
                listOfHourInterval.add(new HourInterval("0612", "06:00-12:00"));
                listOfHourInterval.add(new HourInterval("1218", "12:00-18:00"));
                listOfHourInterval.add(new HourInterval("1823", "18:00-00:00"));
                break;
            case 8:
                listOfHourInterval.add(new HourInterval("0008", "00:00-08:00"));
                listOfHourInterval.add(new HourInterval("0816", "08:00-16:00"));
                listOfHourInterval.add(new HourInterval("1623", "16:00-00:00"));
                break;
            default:
                break;
        }
    }

    /**
     * Bu metot gelen ay numarasına göre name alanını set eder.
     *
     * @param month
     * @return
     */
    public String bringSaleMonthName(int month) {

        String name = "";
        switch (month) {
            case 1:
                name = sessionBean.getLoc().getString("january");
                break;
            case 2:
                name = sessionBean.getLoc().getString("february");
                break;
            case 3:
                name = sessionBean.getLoc().getString("march");
                break;
            case 4:
                name = sessionBean.getLoc().getString("april");
                break;
            case 5:
                name = sessionBean.getLoc().getString("may");
                break;
            case 6:
                name = sessionBean.getLoc().getString("june");
                break;
            case 7:
                name = sessionBean.getLoc().getString("july");
                break;
            case 8:
                name = sessionBean.getLoc().getString("august");
                break;
            case 9:
                name = sessionBean.getLoc().getString("september");
                break;
            case 10:
                name = sessionBean.getLoc().getString("october");
                break;
            case 11:
                name = sessionBean.getLoc().getString("november");
                break;
            case 12:
                name = sessionBean.getLoc().getString("december");
                break;
            default:
                break;
        }
        return name;

    }

    @Override
    public void find() {

        isFind = true;

        if (selectedBranch.isIsCentralIntegration() && selectedBranch.getBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, false, true, true, true, true, true, false, true, true);
        }

        if (selectedObject.getTimeInterval() == 0) {
            toogleList.set(4, false);
        } else {
            toogleList.set(4, true);
        }
        if (selectedObject.getReportType() == 0) {
            toogleList.set(3, false);

        } else {
            toogleList.set(3, true);
        }
        if (selectedObject.getReportType() != 5) {
            toogleList.set(1, false);
            toogleList.set(2, false);
        } else {
            toogleList.set(1, true);
            toogleList.set(2, true);
        }
        createWhere = produtReportSoldTogetherService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmProductReportSoldTogetherDatatable:dtbProductReportSoldTogether");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        bringHourInterval();
        listOfObjects = findall(createWhere);
    }

    /**
     * Zaman dilimi için name alanını set eder.
     */
    public void bringHourInterval() {
        for (HourInterval h : listOfHourInterval) {
            if (h.getId().equals(selectedObject.getHourInterval().getId())) {
                selectedObject.getHourInterval().setName(h.getName());
                break;
            }
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
    public LazyDataModel<ProductReportSoldTogether> findall(String where) {
        return new CentrowizLazyDataModel<ProductReportSoldTogether>() {

            @Override
            public List<ProductReportSoldTogether> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<ProductReportSoldTogether> result = produtReportSoldTogetherService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, selectedBranch);
                int count = produtReportSoldTogetherService.count(where, selectedObject, selectedBranch);
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");

                return result;
            }
        };
    }

    public void createPdf() {
        produtReportSoldTogetherService.exportPdf(createWhere, selectedObject, toogleList, selectedBranch);
    }

    public void createExcel() throws IOException {
        produtReportSoldTogetherService.exportExcel(createWhere, selectedObject, toogleList, selectedBranch);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(produtReportSoldTogetherService.exportPrinter(createWhere, selectedObject, toogleList, selectedBranch)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");
    }

    public void updateBranch() {
        selectedObject.setStock1(new Stock());
        for (BranchSetting brs : listOfBranch) {
            if (selectedBranch.getBranch().getId() == brs.getBranch().getId()) {
                selectedBranch.getBranch().setName(brs.getBranch().getName());
                selectedBranch.setIsCentralIntegration(brs.isIsCentralIntegration());
                selectedBranch.getBranch().setConceptType(brs.getBranch().getConceptType());
                break;
            }
        }

    }

}
