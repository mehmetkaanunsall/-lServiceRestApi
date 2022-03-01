/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.generalstationreport.presentation;

import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.GeneralStation;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.generalstationreport.business.IGeneralStationReportService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

/**
 *
 * @author m.duzoylum
 */
@ManagedBean
@ViewScoped
public class GeneralStationReportBean extends GeneralReportBean<GeneralStation> {

    @ManagedProperty(value = "#{generalStationReportService}")
    public IGeneralStationReportService generalStationReportService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    private Date beginDate;
    private Date endDate;

    private List<Boolean> toogleListMarket;
    private List<Boolean> toogleListAutomat;

    private int lastUnitPrice;
    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private String branchList;
    private BigDecimal totalProfitAmount;
    private BigDecimal totalPurchaseAmount;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalProfitRate;
    private BigDecimal totalProfitMargin;

    private List<GeneralStation> listFuel;
    private List<GeneralStation> listMarket;
    private List<GeneralStation> listAutomat;

    private List<GeneralStation> totalListFuel;
    private List<GeneralStation> totalListMarket;
    private List<GeneralStation> totalListAutomat;
    private Map<Integer, GeneralStation> currencyTotalsCollection;
    private Map<Integer, GeneralStation> currencyTotalsCollection2;
    private Map<Integer, GeneralStation> currencyTotalsCollectionWaste;

    private int centralIntegrationIf;

    private TreeNode rootCategory;
    private int costType;
    private BigDecimal totalPurchaseCost;
    private HashMap<String, List<GeneralStation>> groupAutomatType;
    private List<GeneralStation> listOfWashingMachine;
    private HashMap<Integer, List<GeneralStation>> groupVendingMachine;
    private List<GeneralStation> tempListWendingMachine;
    private HashMap<Integer, GeneralStation> groupVendingMachineCalculated;
    private int reportType;
    private List<GeneralStation> listOfTotalsCategory;

    public BigDecimal getTotalProfitRate() {
        return totalProfitRate;
    }

    public void setTotalProfitRate(BigDecimal totalProfitRate) {
        this.totalProfitRate = totalProfitRate;
    }

    public BigDecimal getTotalProfitMargin() {
        return totalProfitMargin;
    }

    public void setTotalProfitMargin(BigDecimal totalProfitMargin) {
        this.totalProfitMargin = totalProfitMargin;
    }

    public BigDecimal getTotalPurchaseAmount() {
        return totalPurchaseAmount;
    }

    public void setTotalPurchaseAmount(BigDecimal totalPurchaseAmount) {
        this.totalPurchaseAmount = totalPurchaseAmount;
    }

    public BigDecimal getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(BigDecimal totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public BigDecimal getTotalProfitAmount() {
        return totalProfitAmount;
    }

    public void setTotalProfitAmount(BigDecimal totalProfitAmount) {
        this.totalProfitAmount = totalProfitAmount;
    }

    public Map<Integer, GeneralStation> getCurrencyTotalsCollectionWaste() {
        return currencyTotalsCollectionWaste;
    }

    public void setCurrencyTotalsCollectionWaste(Map<Integer, GeneralStation> currencyTotalsCollectionWaste) {
        this.currencyTotalsCollectionWaste = currencyTotalsCollectionWaste;
    }

    public List<Boolean> getToogleListMarket() {
        return toogleListMarket;
    }

    public void setToogleListMarket(List<Boolean> toogleListMarket) {
        this.toogleListMarket = toogleListMarket;
    }

    public List<Boolean> getToogleListAutomat() {
        return toogleListAutomat;
    }

    public void setToogleListAutomat(List<Boolean> toogleListAutomat) {
        this.toogleListAutomat = toogleListAutomat;
    }

   

    public Map<Integer, GeneralStation> getCurrencyTotalsCollection2() {
        return currencyTotalsCollection2;
    }

    public void setCurrencyTotalsCollection2(Map<Integer, GeneralStation> currencyTotalsCollection2) {
        this.currencyTotalsCollection2 = currencyTotalsCollection2;
    }

    public Map<Integer, GeneralStation> getCurrencyTotalsCollection() {
        return currencyTotalsCollection;
    }

    public void setCurrencyTotalsCollection(Map<Integer, GeneralStation> currencyTotalsCollection) {
        this.currencyTotalsCollection = currencyTotalsCollection;
    }

    public List<GeneralStation> getListMarket() {
        return listMarket;
    }

    public void setListMarket(List<GeneralStation> listMarket) {
        this.listMarket = listMarket;
    }

    public List<GeneralStation> getListAutomat() {
        return listAutomat;
    }

    public void setListAutomat(List<GeneralStation> listAutomat) {
        this.listAutomat = listAutomat;
    }

    public List<GeneralStation> getTotalListFuel() {
        return totalListFuel;
    }

    public void setTotalListFuel(List<GeneralStation> totalListFuel) {
        this.totalListFuel = totalListFuel;
    }

    public List<GeneralStation> getTotalListMarket() {
        return totalListMarket;
    }

    public void setTotalListMarket(List<GeneralStation> totalListMarket) {
        this.totalListMarket = totalListMarket;
    }

    public List<GeneralStation> getTotalListAutomat() {
        return totalListAutomat;
    }

    public void setTotalListAutomat(List<GeneralStation> totalListAutomat) {
        this.totalListAutomat = totalListAutomat;
    }

    public int getLastUnitPrice() {
        return lastUnitPrice;
    }

    public void setLastUnitPrice(int lastUnitPrice) {
        this.lastUnitPrice = lastUnitPrice;
    }

    public IGeneralStationReportService getGeneralStationReportService() {
        return generalStationReportService;
    }

    public void setGeneralStationReportService(IGeneralStationReportService generalStationReportService) {
        this.generalStationReportService = generalStationReportService;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public IBranchSettingService getBranchSettingService() {
        return branchSettingService;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
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

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public List<BranchSetting> getSelectedBranchList() {
        return selectedBranchList;
    }

    public void setSelectedBranchList(List<BranchSetting> selectedBranchList) {
        this.selectedBranchList = selectedBranchList;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public int getCentralIntegrationIf() {
        return centralIntegrationIf;
    }

    public void setCentralIntegrationIf(int centralIntegrationIf) {
        this.centralIntegrationIf = centralIntegrationIf;
    }

  
    public TreeNode getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(TreeNode rootCategory) {
        this.rootCategory = rootCategory;
    }

    public int getCostType() {
        return costType;
    }

    public void setCostType(int costType) {
        this.costType = costType;
    }

    public List<GeneralStation> getListFuel() {
        return listFuel;
    }

    public void setListFuel(List<GeneralStation> listFuel) {
        this.listFuel = listFuel;
    }

    public BigDecimal getTotalPurchaseCost() {
        return totalPurchaseCost;
    }

    public void setTotalPurchaseCost(BigDecimal totalPurchaseCost) {
        this.totalPurchaseCost = totalPurchaseCost;
    }

    public HashMap<String, List<GeneralStation>> getGroupAutomatType() {
        return groupAutomatType;
    }

    public void setGroupAutomatType(HashMap<String, List<GeneralStation>> groupAutomatType) {
        this.groupAutomatType = groupAutomatType;
    }

    public List<GeneralStation> getListOfWashingMachine() {
        return listOfWashingMachine;
    }

    public void setListOfWashingMachine(List<GeneralStation> listOfWashingMachine) {
        this.listOfWashingMachine = listOfWashingMachine;
    }

    public HashMap<Integer, List<GeneralStation>> getGroupVendingMachine() {
        return groupVendingMachine;
    }

    public void setGroupVendingMachine(HashMap<Integer, List<GeneralStation>> groupVendingMachine) {
        this.groupVendingMachine = groupVendingMachine;
    }

    public List<GeneralStation> getTempListWendingMachine() {
        return tempListWendingMachine;
    }

    public void setTempListWendingMachine(List<GeneralStation> tempListWendingMachine) {
        this.tempListWendingMachine = tempListWendingMachine;
    }

    public HashMap<Integer, GeneralStation> getGroupVendingMachineCalculated() {
        return groupVendingMachineCalculated;
    }

    public void setGroupVendingMachineCalculated(HashMap<Integer, GeneralStation> groupVendingMachineCalculated) {
        this.groupVendingMachineCalculated = groupVendingMachineCalculated;
    }

    public int getReportType() {
        return reportType;
    }

    public void setReportType(int reportType) {
        this.reportType = reportType;
    }

    public List<GeneralStation> getListOfTotalsCategory() {
        return listOfTotalsCategory;
    }

    public void setListOfTotalsCategory(List<GeneralStation> listOfTotalsCategory) {
        this.listOfTotalsCategory = listOfTotalsCategory;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-- General Station Report Bean -- ");
        Calendar cal = GregorianCalendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        setBeginDate(cal.getTime());

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        setEndDate(cal.getTime());

        listAutomat = new ArrayList<>();
        listFuel = new ArrayList<>();
        listMarket = new ArrayList<>();

        totalListAutomat = new ArrayList<>();
        totalListFuel = new ArrayList<>();
        totalListMarket = new ArrayList<>();

        selectedBranchList = new ArrayList<>();
        currencyTotalsCollection = new HashMap<>();
        currencyTotalsCollection2 = new HashMap<>();
        currencyTotalsCollectionWaste = new HashMap<>();
        groupAutomatType = new HashMap<>();
        listOfWashingMachine = new ArrayList<>();
        groupVendingMachine = new HashMap<>();
        rootCategory = new DefaultTreeNode();
        tempListWendingMachine = new ArrayList<>();
        groupVendingMachineCalculated = new HashMap<>();
        listOfTotalsCategory = new ArrayList<>();
        costType = 1;
        reportType = 1;

        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        toogleListMarket = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        toogleListAutomat = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);

        listOfBranch = branchSettingService.findUserAuthorizeBranch();// kullanıcının yetkili olduğu branch listesini çeker

        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedBranchList.add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedBranchList.add(branchSetting);
                    break;
                }
            }
        }

    }

    public void changeBranch() {
        branchList = "";

        for (BranchSetting branchSetting : selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        boolean isThere = false;
        int count = 0;
        if (selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                isThere = false;
                if (branchSetting.isIsCentralIntegration()) {
                    isThere = true;
                }
                if (isThere) {
                    count++;
                }
            }
            centralIntegrationIf = -1;
            if (listOfBranch.size() > 1) {
                if (count >= 1 && count < listOfBranch.size()) {
                    centralIntegrationIf = -1;
                } else if (count == listOfBranch.size() && isThere) {
                    centralIntegrationIf = 1;
                } else if (count == 0 && !isThere) {
                    centralIntegrationIf = 0;
                }
            } else if (listOfBranch.size() == 1) {
                if (listOfBranch.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 1;
                } else if (!listOfBranch.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 0;
                }
            }
        } else {
            for (BranchSetting branchSetting : selectedBranchList) {
                isThere = false;
                if (branchSetting.isIsCentralIntegration()) {
                    isThere = true;
                }
                if (isThere) {
                    count++;
                }
            }

            centralIntegrationIf = -1;
            if (selectedBranchList.size() > 1) {
                if (count >= 1 && count < selectedBranchList.size()) {
                    centralIntegrationIf = -1;
                } else if (count == selectedBranchList.size() && isThere) {
                    centralIntegrationIf = 1;
                } else if (count == 0 && !isThere) {
                    centralIntegrationIf = 0;
                }
            } else if (selectedBranchList.size() == 1) {
                if (selectedBranchList.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 1;
                } else if (!selectedBranchList.get(0).isIsCentralIntegration()) {
                    centralIntegrationIf = 0;
                }
            }
        }
    }

    @Override
    public void find() {
        totalListAutomat.clear();
        totalListFuel.clear();
        totalListMarket.clear();
        isFind = true;
        String where = "";
        totalProfitAmount = BigDecimal.ZERO;
        totalPurchaseAmount = BigDecimal.ZERO;
        totalSalesAmount = BigDecimal.ZERO;
        totalProfitRate = BigDecimal.ZERO;
        totalProfitMargin = BigDecimal.ZERO;
        totalPurchaseCost = BigDecimal.ZERO;
        changeBranch();

            currencyTotalsCollection.clear();

        switch (reportType) {
            case 1: //Hepsi
                listFuel = findallFuel(where);
                listMarket = findallMarket(where);
                listAutomat = findallAutomat(where);
                if (!listAutomat.isEmpty()) {
                    findWashingMachineSales();
                    calculateGroupAutomatType();
                }

                if (!listMarket.isEmpty()) {
                    RequestContext.getCurrentInstance().execute("count=" + listMarket.size() + ";");
                    findCategory();
                    calcCategorySubTotal();
                }
                break;
            case 2://Akaryakıt ürünleri
                listFuel = findallFuel(where);
                break;
            case 3://Market kategorileri
                listMarket = findallMarket(where);
                if (!listMarket.isEmpty()) {
                    RequestContext.getCurrentInstance().execute("count=" + listMarket.size() + ";");
                    findCategory();
                    calcCategorySubTotal();
                }
                break;
            case 4://Otomat
                listAutomat = findallAutomat(where);
                if (!listAutomat.isEmpty()) {
                    findWashingMachineSales();
                    calculateGroupAutomatType();
                }
                break;
            default:
                break;
        }

    }

    public TreeNode findCategory() {
        rootCategory = new DefaultTreeNode(new GeneralStation(), null);
        rootCategory.setExpanded(true);

        for (GeneralStation p : listMarket) {

            if (p.getCategorization().getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(p, rootCategory);
                findChildCategory(parentTreeNode, listMarket);
            }
        }

        return rootCategory;
    }

    public void findChildCategory(TreeNode categoryTree, List<GeneralStation> list) {
        for (GeneralStation p : list) {
            if (p.getCategorization().getParentId().getId() != 0) {

                if (p.getCategorization().getParentId().getId() == ((GeneralStation) categoryTree.getData()).getCategorization().getId() && p.getBranchSetting().getBranch().getId() == ((GeneralStation) categoryTree.getData()).getBranchSetting().getBranch().getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(p, categoryTree);
                    childTreeNode.setExpanded(true);
                    findChildCategory(childTreeNode, list);
                }
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

    public List<GeneralStation> findallFuel(String where) {
        List<GeneralStation> result = generalStationReportService.findAll(beginDate, endDate, branchList, lastUnitPrice, 1, centralIntegrationIf, costType);
        totalListFuel = generalStationReportService.totals(where, beginDate, endDate, branchList, lastUnitPrice, 1, centralIntegrationIf, costType);

        currencyTotalsCollection.clear();
        currencyTotalsCollection = calculateOverallTotal(1);
        return result;
    }

    public List<GeneralStation> findallMarket(String where) {
        List<GeneralStation> result = generalStationReportService.findAllMarket(beginDate, endDate, branchList, lastUnitPrice, centralIntegrationIf, costType);

        return result;
    }

    public List<GeneralStation> findallAutomat(String where) {
        List<GeneralStation> result = generalStationReportService.findAllAutomat(beginDate, endDate, branchList, lastUnitPrice, costType);
        totalListAutomat = generalStationReportService.totals(where, beginDate, endDate, branchList, lastUnitPrice, 2, centralIntegrationIf, costType);

        if (totalListAutomat.size() > 0) {
            currencyTotalsCollection2.clear();
            currencyTotalsCollection2 = calculateOverallTotal(3);
        }

        return result;

    }

    public Map<String, List<GeneralStation>> calculateGroupAutomatType() {//Otomat satışlarını otomat tipine göre gruplar

        groupAutomatType = new HashMap<>();
        groupAutomatType.clear();

        for (GeneralStation total : listAutomat) {

            if (groupAutomatType.containsKey(total.getVendingMachine().getDeviceType().getTag())) {
                groupAutomatType.get(total.getVendingMachine().getDeviceType().getTag()).add(total);
            } else {

                List<GeneralStation> list = new ArrayList<>();

                list.add(total);

                groupAutomatType.put(total.getVendingMachine().getDeviceType().getTag(), list);
            }

        }

        return groupAutomatType;
    }

    public void calcCategorySubTotal() { //Kategoriye göre market satışlarının alt toplamını hesaplar
        listOfTotalsCategory.clear();
        GeneralStation totalsObj = new GeneralStation();
        totalsObj.setTransferQuantity(BigDecimal.ZERO);
        totalsObj.setTransferAmount(BigDecimal.ZERO);
        totalsObj.setPurchaseQuantity(BigDecimal.ZERO);
        totalsObj.setPurchaseAmount(BigDecimal.ZERO);
        totalsObj.setSalesQuantity(BigDecimal.ZERO);
        totalsObj.setSalesAmount(BigDecimal.ZERO);
        totalsObj.setRemainingAmount(BigDecimal.ZERO);
        totalsObj.setRemainingQuantity(BigDecimal.ZERO);
        totalsObj.setCost(BigDecimal.ZERO);
        for (GeneralStation categoryList : listMarket) {
            if (categoryList.getCategorization().getParentId().getId() == 0) {
                totalsObj.setTransferQuantity(totalsObj.getTransferQuantity().add(categoryList.getTransferQuantity()));
                totalsObj.setTransferAmount(totalsObj.getTransferAmount().add(categoryList.getTransferAmount()));
                totalsObj.setPurchaseQuantity(totalsObj.getPurchaseQuantity().add(categoryList.getPurchaseQuantity()));
                totalsObj.setPurchaseAmount(totalsObj.getPurchaseAmount().add(categoryList.getPurchaseAmount()));
                totalsObj.setSalesQuantity(totalsObj.getSalesQuantity().add(categoryList.getSalesQuantity()));
                totalsObj.setSalesAmount(totalsObj.getSalesAmount().add(categoryList.getSalesAmount()));
                totalsObj.setRemainingAmount(totalsObj.getRemainingAmount().add(categoryList.getRemainingAmount()));
                totalsObj.setRemainingQuantity(totalsObj.getRemainingQuantity().add(categoryList.getRemainingQuantity()));
                totalsObj.setCost(totalsObj.getCost().add(categoryList.getCost()));
            }
        }

        totalsObj.setRemainingQuantity(calculate(totalsObj, 4));
        totalsObj.setRemainingAmount(calculate(totalsObj, 5));
        totalsObj.setProfitAmount(calculate(totalsObj, 2));
        totalsObj.setProfitMargin(calculate(totalsObj, 3));
        totalsObj.setRateofProfit(calculate(totalsObj, 1));

        totalPurchaseAmount = totalPurchaseAmount.add(totalsObj.getPurchaseAmount());
        totalSalesAmount = totalSalesAmount.add(totalsObj.getSalesAmount());
        totalProfitAmount = totalProfitAmount.add(totalsObj.getProfitAmount());
        totalPurchaseCost = totalPurchaseCost.add(totalsObj.getCost());

        if (totalPurchaseCost != null && totalPurchaseCost.compareTo(BigDecimal.ZERO) != 0) {
            totalProfitRate = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalPurchaseCost, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
        } else {
            totalProfitRate = BigDecimal.ZERO;
        }

        if (totalSalesAmount != null && totalSalesAmount.compareTo(BigDecimal.ZERO) != 0) {
            totalProfitMargin = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalSalesAmount, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
        } else {
            totalProfitMargin = BigDecimal.ZERO;
        }
        listOfTotalsCategory.add(totalsObj);
        new DefaultTreeNode(totalsObj, rootCategory);

    }

    public Map<Integer, GeneralStation> calculateOverallTotal(int datatableType) {

        currencyTotalsCollectionWaste = new HashMap<>();
        currencyTotalsCollectionWaste.clear();

        for (GeneralStation total : (datatableType == 1 ? totalListFuel : totalListAutomat)) {

            if (currencyTotalsCollectionWaste.containsKey(total.getSaleCurrencyId().getId())) {

                GeneralStation old = new GeneralStation();
                old.setSaleCurrencyId(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getSaleCurrencyId());

                old.setTransferQuantity(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getTransferQuantity());
                old.setTransferAmount(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getTransferAmount());
                old.setPurchaseQuantity(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getPurchaseQuantity());
                old.setPurchaseAmount(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getPurchaseAmount());
                old.setSalesQuantity(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getSalesQuantity());
                old.setSalesAmount(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getSalesAmount());
                old.setRemainingQuantity(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRemainingQuantity() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRemainingQuantity());
                old.setRemainingAmount(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRemainingAmount() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRemainingAmount());
                old.setRateofProfit(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRateofProfit() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getRateofProfit());
                old.setProfitMargin(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getProfitMargin() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getProfitMargin());
                old.setProfitAmount(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getProfitAmount() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getProfitAmount());
                old.setCost(currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getCost() == null ? BigDecimal.ZERO : currencyTotalsCollectionWaste.get(total.getSaleCurrencyId().getId()).getCost());

                old.setTransferQuantity(old.getTransferQuantity().add(total.getTransferQuantity()));
                old.setTransferAmount(old.getTransferAmount().add(total.getTransferAmount()));
                old.setPurchaseQuantity(old.getPurchaseQuantity().add(total.getPurchaseQuantity()));
                old.setPurchaseAmount(old.getPurchaseAmount().add(total.getPurchaseAmount()));
                old.setSalesQuantity(old.getSalesQuantity().add(total.getSalesQuantity()));
                old.setSalesAmount(old.getSalesAmount().add(total.getSalesAmount()));
                old.setRemainingQuantity(old.getRemainingQuantity().add(total.getRemainingQuantity() == null ? BigDecimal.ZERO : total.getRemainingQuantity()));
                old.setRemainingAmount(old.getRemainingAmount().add(total.getRemainingAmount() == null ? BigDecimal.ZERO : total.getRemainingAmount()));
                old.setRateofProfit(old.getRateofProfit().add(total.getRateofProfit() == null ? BigDecimal.ZERO : total.getRateofProfit()));
                old.setProfitMargin(old.getProfitMargin().add(total.getProfitMargin() == null ? BigDecimal.ZERO : total.getProfitMargin()));
                old.setProfitAmount(old.getProfitAmount().add(total.getProfitAmount() == null ? BigDecimal.ZERO : total.getProfitAmount()));
                old.setCost(old.getCost().add(total.getCost() == null ? BigDecimal.ZERO : total.getCost()));
                old.setProfitAmount(old.getSalesAmount().subtract(old.getPurchaseAmount()));

                currencyTotalsCollectionWaste.put(total.getSaleCurrencyId().getId(), old);

            } else {
                GeneralStation oldNew = new GeneralStation();
                oldNew.setSaleCurrencyId(total.getSaleCurrencyId());
                oldNew.setTransferQuantity(total.getTransferQuantity());
                oldNew.setTransferAmount(total.getTransferAmount());
                oldNew.setPurchaseQuantity(total.getPurchaseQuantity());
                oldNew.setPurchaseAmount(total.getPurchaseAmount());
                oldNew.setSalesQuantity(total.getSalesQuantity());
                oldNew.setSalesAmount(total.getSalesAmount());
                oldNew.setRemainingQuantity(total.getRemainingQuantity() == null ? BigDecimal.ZERO : total.getRemainingQuantity());
                oldNew.setRemainingAmount(total.getRemainingAmount() == null ? BigDecimal.ZERO : total.getRemainingAmount());
                oldNew.setRateofProfit(total.getRateofProfit() == null ? BigDecimal.ZERO : total.getRateofProfit());
                oldNew.setProfitMargin(total.getProfitMargin() == null ? BigDecimal.ZERO : total.getProfitMargin());
                oldNew.setProfitAmount(total.getProfitAmount() == null ? BigDecimal.ZERO : total.getProfitAmount());
                oldNew.setCost(total.getCost() == null ? BigDecimal.ZERO : total.getCost());

                oldNew.setProfitAmount(oldNew.getSalesAmount().subtract(oldNew.getPurchaseAmount()));

                currencyTotalsCollectionWaste.put(total.getSaleCurrencyId().getId(), oldNew);
            }

        }

        for (Map.Entry<Integer, GeneralStation> entry : currencyTotalsCollectionWaste.entrySet()) {
            BigDecimal bd = BigDecimal.ZERO;
            if (entry.getValue().getPurchaseAmount() != null && entry.getValue().getPurchaseAmount().compareTo(BigDecimal.ZERO) != 0) {
                bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getPurchaseAmount())).divide(entry.getValue().getPurchaseAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                bd = BigDecimal.ZERO;
            }
            entry.getValue().setRateofProfit(bd);
            if (entry.getValue().getSalesAmount() != null && entry.getValue().getSalesAmount().compareTo(BigDecimal.ZERO) != 0) {
                bd = ((entry.getValue().getSalesAmount().subtract(entry.getValue().getPurchaseAmount())).divide(entry.getValue().getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                bd = BigDecimal.ZERO;
            }
            entry.getValue().setProfitMargin(bd);

            totalPurchaseAmount = totalPurchaseAmount.add(entry.getValue().getPurchaseAmount());
            totalSalesAmount = totalSalesAmount.add(entry.getValue().getSalesAmount());
            totalProfitAmount = totalProfitAmount.add(entry.getValue().getProfitAmount());
            totalPurchaseCost = totalPurchaseCost.add(entry.getValue().getCost());

            if (totalPurchaseCost != null && totalPurchaseCost.compareTo(BigDecimal.ZERO) != 0) {
                totalProfitRate = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalPurchaseCost, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                totalProfitRate = BigDecimal.ZERO;
            }

            if (totalSalesAmount != null && totalSalesAmount.compareTo(BigDecimal.ZERO) != 0) {
                totalProfitMargin = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalSalesAmount, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                totalProfitMargin = BigDecimal.ZERO;
            }

            RequestContext.getCurrentInstance().execute("updateFieldSet()");
        }

        return currencyTotalsCollectionWaste;
    }

    @Override
    public LazyDataModel<GeneralStation> findall(String where) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal calculate(GeneralStation obj, int type) {

        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        switch (type) {
            case 1://Kar Oranı (Akaryakıt, market, sigara otomatı ve ürün otomatı ve kahve otomatı için kar oranı hesaplar)
                if (obj.getCost() != null && obj.getCost().compareTo(BigDecimal.ZERO) != 0) {
                    total = ((obj.getSalesAmount().subtract(obj.getCost())).divide(obj.getCost(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                } else {
                    total = BigDecimal.ZERO;
                }
                break;
            case 2://Kar Tutarı 
                total = (obj.getSalesAmount().subtract(obj.getPurchaseAmount()));
                break;
            case 3://Kar Marjı (Akaryakıt, market, sigara otomatı ve ürün otomatı ve kahve otomatı için kar marjı hesaplar)
                if (obj.getSalesAmount().compareTo(BigDecimal.ZERO) == 0) {
                    total = BigDecimal.ZERO;
                } else {
                    total = ((obj.getSalesAmount().subtract(obj.getCost())).divide(obj.getSalesAmount(), 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                }
                break;
            case 4://Kalan Miktar
                total = (obj.getTransferQuantity().add(obj.getPurchaseQuantity()).subtract(obj.getSalesQuantity()));
                break;
            case 5://Kalan Tutar
                total = (obj.getTransferAmount().add(obj.getPurchaseAmount()).subtract(obj.getSalesAmount()));
                break;
        }

        return total;
    }

    public void findWashingMachineSales() {

        listOfWashingMachine.clear();
        for (GeneralStation automat : listAutomat) {
            if (automat.getVendingMachine().getDeviceType().getId() == -1) {
                listOfWashingMachine.add(automat); // yıkama makinesi satışlarının listesi
            }

        }

        groupVendingMachine.clear();

        for (GeneralStation total : listOfWashingMachine) { //yıkama satışları yıkama makinesine göre gruplandırıldı

            if (groupVendingMachine.containsKey(total.getVendingMachine().getId())) {
                groupVendingMachine.get(total.getVendingMachine().getId()).add(total);
            } else {

                List<GeneralStation> list = new ArrayList<>();

                list.add(total);

                groupVendingMachine.put(total.getVendingMachine().getId(), list);
            }

        }

        calculateWashingMachineSales(groupVendingMachine); //yıkama makinesi bazında gruplanan satışların elektrik ve su giderleri hesaplanarak makine bazında yeni toplamlar oluşturuldu

    }

    public void calculateWashingMachineSales(Map<Integer, List<GeneralStation>> listMap) {

        BigDecimal salesQuantity = BigDecimal.ZERO;
        tempListWendingMachine.clear();

        for (Map.Entry<Integer, List<GeneralStation>> entry : listMap.entrySet()) {

            if (!entry.getValue().isEmpty()) {
                GeneralStation washing = new GeneralStation();
                washing.getStock().setName(sessionBean.getLoc().getString("electricalexpense")); // elektrik gideri eklenir.
                washing.setSaleType(-1);

                BigDecimal electricQuantity = BigDecimal.ZERO;
                int electricTime = 0, waterTime = 0;
                BigDecimal electricExpense = BigDecimal.ZERO;
                BigDecimal electricTotalWase = BigDecimal.ZERO;
                BigDecimal waterQuantity = BigDecimal.ZERO, waterExpense = BigDecimal.ZERO, waterTotalWase = BigDecimal.ZERO;

                for (GeneralStation automatSaleReport1 : entry.getValue()) {
                    electricQuantity = electricQuantity.add(automatSaleReport1.getElectricQuantity());
                    electricTime += automatSaleReport1.getElectricOperationTime().doubleValue();
                    electricExpense = electricExpense.add(automatSaleReport1.getElectricExpense());
                    electricTotalWase = electricTotalWase.add(automatSaleReport1.getTotalElectricAmount());
                    waterQuantity = waterQuantity.add(automatSaleReport1.getWaterWorkingAmount());
                    waterTime += automatSaleReport1.getWaterWorkingTime();
                    waterExpense = waterExpense.add(automatSaleReport1.getWaterExpense());
                    waterTotalWase = waterTotalWase.add(automatSaleReport1.getWaterWaste());
                }

                washing.setSalesQuantity(electricQuantity);
                washing.setOperationTime(electricTime);
                washing.setTotalExpense(electricExpense);
                washing.setWaste(electricTotalWase);
                washing.setTotalIncome(BigDecimal.valueOf(0));
                int comp = electricExpense.compareTo(BigDecimal.ZERO);
                BigDecimal bd = comp == 0 ? BigDecimal.ZERO : electricExpense.multiply(BigDecimal.valueOf(-1));
                washing.setTotalWinnings(bd);
                entry.getValue().add(washing);

                GeneralStation automatSaleReportWater = new GeneralStation();
                automatSaleReportWater.getStock().setName(sessionBean.getLoc().getString("waterexpense")); // su gideri eklenir.
                automatSaleReportWater.setSaleType(-1);
                automatSaleReportWater.setSalesQuantity(waterQuantity);
                automatSaleReportWater.setOperationTime(waterTime);
                automatSaleReportWater.setTotalExpense(waterExpense);
                automatSaleReportWater.setWaste(waterTotalWase);
                automatSaleReportWater.setTotalIncome(BigDecimal.valueOf(0));
                int comp2 = waterExpense.compareTo(BigDecimal.ZERO);
                BigDecimal bd2 = comp2 == 0 ? BigDecimal.ZERO : waterExpense.multiply(BigDecimal.valueOf(-1));
                automatSaleReportWater.setTotalWinnings(bd2);
                entry.getValue().add(automatSaleReportWater);

                GeneralStation newObj = new GeneralStation();
                newObj.setSalesQuantity(BigDecimal.ZERO);
                newObj.setWaste(BigDecimal.ZERO);
                newObj.setTotalIncome(BigDecimal.ZERO);
                newObj.setTotalExpense(BigDecimal.ZERO);
                newObj.setTotalWinnings(BigDecimal.ZERO);
                for (GeneralStation obj : entry.getValue()) {

                    if (obj.getSaleType() != -1) {
                        newObj.setSalesQuantity(newObj.getSalesQuantity().add(obj.getSalesQuantity()));
                    }
                    newObj.setWaste(newObj.getWaste().add(obj.getWaste()));
                    newObj.setTotalIncome(newObj.getTotalIncome().add(obj.getTotalIncome()));
                    newObj.setTotalExpense(newObj.getTotalExpense().add(obj.getTotalExpense()));
                    newObj.setTotalWinnings(newObj.getTotalWinnings().add(obj.getTotalWinnings()));

                }

                newObj.getBranchSetting().getBranch().setId(entry.getValue().get(0).getBranchSetting().getBranch().getId());
                newObj.getBranchSetting().getBranch().setName(entry.getValue().get(0).getBranchSetting().getBranch().getName());
                newObj.getVendingMachine().getDeviceType().setId(entry.getValue().get(0).getVendingMachine().getDeviceType().getId());
                newObj.getVendingMachine().setId(entry.getValue().get(0).getVendingMachine().getId());
                newObj.getVendingMachine().setName(entry.getValue().get(0).getVendingMachine().getName());
                newObj.setSaleCurrencyId(entry.getValue().get(0).getSaleCurrencyId());

                tempListWendingMachine.add(newObj);

            }

        }

        if (!tempListWendingMachine.isEmpty()) { //elektirik ve su giderleri de dahil edilerek hesaplanan yeni toplamlardan şubeye göre alt toplamlar hesaplandı
            groupVendingMachineCalculated.clear();
            for (GeneralStation total : tempListWendingMachine) {

                if (groupVendingMachineCalculated.containsKey(total.getBranchSetting().getBranch().getId())) {
                    GeneralStation old = new GeneralStation();
                    old.setSaleCurrencyId(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getSaleCurrencyId());

                    old.setSalesQuantity(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getSalesQuantity() == null ? BigDecimal.ZERO : groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getSalesQuantity());
                    old.setWaste(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getWaste() == null ? BigDecimal.ZERO : groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getWaste());
                    old.setTotalExpense(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalExpense() == null ? BigDecimal.ZERO : groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalExpense());
                    old.setTotalIncome(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalIncome() == null ? BigDecimal.ZERO : groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalIncome());
                    old.setTotalWinnings(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalWinnings() == null ? BigDecimal.ZERO : groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getTotalWinnings());
                    old.getBranchSetting().getBranch().setId(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getBranchSetting().getBranch().getId());
                    old.getBranchSetting().getBranch().setName(groupVendingMachineCalculated.get(total.getBranchSetting().getBranch().getId()).getBranchSetting().getBranch().getName());

                    old.setSalesQuantity(old.getSalesQuantity().add(total.getSalesQuantity()));
                    old.setWaste(old.getWaste().add(total.getWaste()));
                    old.setTotalExpense(old.getTotalExpense().add(total.getTotalExpense()));
                    old.setTotalIncome(old.getTotalIncome().add(total.getTotalIncome()));
                    old.setTotalWinnings(old.getTotalWinnings().add(total.getTotalWinnings()));

                    groupVendingMachineCalculated.put(total.getBranchSetting().getBranch().getId(), old);
                } else {

                    GeneralStation oldNew = new GeneralStation();
                    oldNew.setSaleCurrencyId(total.getSaleCurrencyId());
                    oldNew.setSalesQuantity(total.getSalesQuantity());
                    oldNew.setWaste(total.getWaste());
                    oldNew.setTotalExpense(total.getTotalExpense() == null ? BigDecimal.ZERO : total.getTotalExpense());
                    oldNew.setTotalIncome(total.getTotalIncome() == null ? BigDecimal.ZERO : total.getTotalIncome());
                    oldNew.setTotalWinnings(total.getTotalWinnings() == null ? BigDecimal.ZERO : total.getTotalWinnings());
                    oldNew.getBranchSetting().getBranch().setId(total.getBranchSetting().getBranch().getId());
                    oldNew.getBranchSetting().getBranch().setName(total.getBranchSetting().getBranch().getName());

                    groupVendingMachineCalculated.put(total.getBranchSetting().getBranch().getId(), oldNew);
                }

            }

        }

        for (Map.Entry<Integer, GeneralStation> entry : groupVendingMachineCalculated.entrySet()) {

            totalPurchaseAmount = totalPurchaseAmount.add(entry.getValue().getTotalExpense());
            totalSalesAmount = totalSalesAmount.add(entry.getValue().getTotalIncome());
            totalProfitAmount = totalProfitAmount.add(entry.getValue().getTotalWinnings());
            totalPurchaseCost = totalPurchaseCost.add(entry.getValue().getTotalExpense());

            if (totalPurchaseCost != null && totalPurchaseCost.compareTo(BigDecimal.ZERO) != 0) {
                totalProfitRate = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalPurchaseCost, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                totalProfitRate = BigDecimal.ZERO;
            }

            if (totalSalesAmount != null && totalSalesAmount.compareTo(BigDecimal.ZERO) != 0) {
                totalProfitMargin = ((totalSalesAmount.subtract(totalPurchaseCost)).divide(totalSalesAmount, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
            } else {
                totalProfitMargin = BigDecimal.ZERO;
            }

        }

        RequestContext.getCurrentInstance().execute("updateFieldSet()");
    }

    public void createPdf() {

        generalStationReportService.exportPdf(
                beginDate,
                endDate,
                selectedBranchList,
                lastUnitPrice,
                toogleList,
                listFuel,
                totalListFuel,
                currencyTotalsCollection,
                toogleListMarket,
                listMarket,
                toogleListAutomat,
                listAutomat,
                totalListAutomat,
                totalPurchaseAmount,
                totalSalesAmount,
                totalProfitAmount,
                totalProfitRate,
                totalProfitMargin,
                totalPurchaseCost,
                reportType,
                costType,
                listOfTotalsCategory,
                groupAutomatType,
                groupVendingMachineCalculated);
    }

    public void createExcel() {

        generalStationReportService.exportExcel(
                beginDate,
                endDate,
                selectedBranchList,
                branchList,
                lastUnitPrice,
                toogleList,
                listFuel,
                totalListFuel,
                currencyTotalsCollection,
                toogleListMarket,
                listMarket,
                totalListMarket,
                toogleListAutomat,
                listAutomat,
                totalListAutomat,
                currencyTotalsCollection2,
                centralIntegrationIf,
                totalPurchaseAmount,
                totalSalesAmount,
                totalProfitAmount,
                totalProfitRate,
                totalProfitMargin,
                totalPurchaseCost,
                reportType,
                costType,
                listOfTotalsCategory,
                groupAutomatType,
                groupVendingMachineCalculated);
    }

    public void createPrinter() {

        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(
                generalStationReportService.exportPrinter(
                        beginDate,
                        endDate,
                        selectedBranchList,
                        lastUnitPrice,
                        toogleList,
                        listFuel,
                        totalListFuel,
                        currencyTotalsCollection,
                        toogleListMarket,
                        listMarket,
                        toogleListAutomat,
                        totalListAutomat,
                        centralIntegrationIf,
                        totalPurchaseAmount,
                        totalSalesAmount,
                        totalProfitAmount,
                        totalProfitRate,
                        totalProfitMargin,
                        totalPurchaseCost,
                        reportType,
                        costType,
                        listOfTotalsCategory,
                        groupAutomatType,
                        groupVendingMachineCalculated)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }
}
