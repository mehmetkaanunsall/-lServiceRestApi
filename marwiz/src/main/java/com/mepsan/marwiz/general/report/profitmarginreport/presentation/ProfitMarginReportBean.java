/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   08.03.2018 12:02:59
 */
package com.mepsan.marwiz.general.report.profitmarginreport.presentation;

import com.mepsan.marwiz.finance.incomeexpense.business.IIncomeExpenseService;
import com.mepsan.marwiz.general.common.CategoryBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.general.report.profitmarginreport.business.IProfitMarginReportService;
import com.mepsan.marwiz.general.report.profitmarginreport.dao.ProfitMarginReport;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.io.IOException;
import java.math.BigDecimal;
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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;

@ManagedBean
@ViewScoped
public class ProfitMarginReportBean extends GeneralReportBean<ProfitMarginReport> {

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{profitMarginReportService}")
    public IProfitMarginReportService profitMarginReportService;

    @ManagedProperty(value = "#{incomeExpenseService}")
    public IIncomeExpenseService incomeExpenseService;

    @ManagedProperty(value = "#{categoryBookCheckboxFilterBean}")
    private CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{branchSettingService}")
    public IBranchSettingService branchSettingService;

    private List<Stock> listOfStock;
    private String createWhere;
    private List<Categorization> listOfCategorization;
    private List<IncomeExpense> listOfIncomeExpense;
    private TreeNode root;
    private BigDecimal totalIncome, totalExpense;
    private ProfitMarginReport totalProfitMargin;
    private TreeNode rootCategory;
    private List<ProfitMarginReport> listCategory;
    public String autoCompleteValue;
    private List<BranchSetting> listOfBranch;
    private List<ProfitMarginReport> listOfTotals;
    String branchList;
    int branchId;
    int tempCount = 0;
    private boolean isThereListBranch;
    int centralIngetrationInf = 0;
    private String warehouseStartQuantity;
    private String warehouseStartPrice;
    private String beginToEndPurchaseQuantity;
    private String beginToEndPurchasePrice;
    private String beginToEndPurchaseReturnQuantity;
    private String beginToEndPurchaseReturnPrice;
    private String beginToEndSalesQuantity;
    private String beginToEndSalesPrice;
    private String totalPurchasePrice;
    private String profitMargin;
    private String profitPercentage;
    private String totalProfit;
    private String warehouseEndQuantity;
    private String warehouseEndPrice;
    private String totalStockProfit;
    private String totalStockTakingPrice;
    private String totalStockTakingQuantity;
    private HashMap<Integer, ProfitMarginReport> groupCurrencyTotal;
    private String totalDifferencePrice;
    private String totalZSalesQuantity;
    private String totalZSalesPrice;
    private String totalExcludingZSalesPrice;
    private String totalExcludingZSalesQuantity;

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public boolean isIsThereListBranch() {
        return isThereListBranch;
    }

    public void setIsThereListBranch(boolean isThereListBranch) {
        this.isThereListBranch = isThereListBranch;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setProfitMarginReportService(IProfitMarginReportService profitMarginReportService) {
        this.profitMarginReportService = profitMarginReportService;
    }

    public List<Categorization> getListOfCategorization() {
        return listOfCategorization;
    }

    public void setListOfCategorization(List<Categorization> listOfCategorization) {
        this.listOfCategorization = listOfCategorization;
    }

    public void setCategoryBookCheckboxFilterBean(CategoryBookCheckboxFilterBean categoryBookCheckboxFilterBean) {
        this.categoryBookCheckboxFilterBean = categoryBookCheckboxFilterBean;
    }

    public TreeNode getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(TreeNode rootCategory) {
        this.rootCategory = rootCategory;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public String getCreateWhere() {
        return createWhere;
    }

    public void setCreateWhere(String createWhere) {
        this.createWhere = createWhere;
    }

    public void setIncomeExpenseService(IIncomeExpenseService incomeExpenseService) {
        this.incomeExpenseService = incomeExpenseService;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getAutoCompleteValue() {
        return autoCompleteValue;
    }

    public void setAutoCompleteValue(String autoCompleteValue) {
        this.autoCompleteValue = autoCompleteValue;
    }

    public ProfitMarginReport getTotalProfitMargin() {
        return totalProfitMargin;
    }

    public void setTotalProfitMargin(ProfitMarginReport totalProfitMargin) {
        this.totalProfitMargin = totalProfitMargin;
    }

    public List<BranchSetting> getListOfBranch() {
        return listOfBranch;
    }

    public void setListOfBranch(List<BranchSetting> listOfBranch) {
        this.listOfBranch = listOfBranch;
    }

    public String getBranchList() {
        return branchList;
    }

    public void setBranchList(String branchList) {
        this.branchList = branchList;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public List<ProfitMarginReport> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<ProfitMarginReport> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public List<IncomeExpense> getListOfIncomeExpense() {
        return listOfIncomeExpense;
    }

    public void setListOfIncomeExpense(List<IncomeExpense> listOfIncomeExpense) {
        this.listOfIncomeExpense = listOfIncomeExpense;
    }

    public List<ProfitMarginReport> getListCategory() {
        return listCategory;
    }

    public void setListCategory(List<ProfitMarginReport> listCategory) {
        this.listCategory = listCategory;
    }

    public int getTempCount() {
        return tempCount;
    }

    public void setTempCount(int tempCount) {
        this.tempCount = tempCount;
    }

    public int getCentralIngetrationInf() {
        return centralIngetrationInf;
    }

    public void setCentralIngetrationInf(int centralIngetrationInf) {
        this.centralIngetrationInf = centralIngetrationInf;
    }

    public String getWarehouseStartQuantity() {
        return warehouseStartQuantity;
    }

    public void setWarehouseStartQuantity(String warehouseStartQuantity) {
        this.warehouseStartQuantity = warehouseStartQuantity;
    }

    public String getWarehouseStartPrice() {
        return warehouseStartPrice;
    }

    public void setWarehouseStartPrice(String warehouseStartPrice) {
        this.warehouseStartPrice = warehouseStartPrice;
    }

    public String getBeginToEndPurchaseQuantity() {
        return beginToEndPurchaseQuantity;
    }

    public void setBeginToEndPurchaseQuantity(String beginToEndPurchaseQuantity) {
        this.beginToEndPurchaseQuantity = beginToEndPurchaseQuantity;
    }

    public String getBeginToEndPurchasePrice() {
        return beginToEndPurchasePrice;
    }

    public void setBeginToEndPurchasePrice(String beginToEndPurchasePrice) {
        this.beginToEndPurchasePrice = beginToEndPurchasePrice;
    }

    public String getBeginToEndPurchaseReturnQuantity() {
        return beginToEndPurchaseReturnQuantity;
    }

    public void setBeginToEndPurchaseReturnQuantity(String beginToEndPurchaseReturnQuantity) {
        this.beginToEndPurchaseReturnQuantity = beginToEndPurchaseReturnQuantity;
    }

    public String getBeginToEndPurchaseReturnPrice() {
        return beginToEndPurchaseReturnPrice;
    }

    public void setBeginToEndPurchaseReturnPrice(String beginToEndPurchaseReturnPrice) {
        this.beginToEndPurchaseReturnPrice = beginToEndPurchaseReturnPrice;
    }

    public String getBeginToEndSalesQuantity() {
        return beginToEndSalesQuantity;
    }

    public void setBeginToEndSalesQuantity(String beginToEndSalesQuantity) {
        this.beginToEndSalesQuantity = beginToEndSalesQuantity;
    }

    public String getBeginToEndSalesPrice() {
        return beginToEndSalesPrice;
    }

    public void setBeginToEndSalesPrice(String beginToEndSalesPrice) {
        this.beginToEndSalesPrice = beginToEndSalesPrice;
    }

    public String getTotalPurchasePrice() {
        return totalPurchasePrice;
    }

    public void setTotalPurchasePrice(String totalPurchasePrice) {
        this.totalPurchasePrice = totalPurchasePrice;
    }

    public String getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(String profitMargin) {
        this.profitMargin = profitMargin;
    }

    public String getProfitPercentage() {
        return profitPercentage;
    }

    public void setProfitPercentage(String profitPercentage) {
        this.profitPercentage = profitPercentage;
    }

    public String getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(String totalProfit) {
        this.totalProfit = totalProfit;
    }

    public String getWarehouseEndQuantity() {
        return warehouseEndQuantity;
    }

    public void setWarehouseEndQuantity(String warehouseEndQuantity) {
        this.warehouseEndQuantity = warehouseEndQuantity;
    }

    public String getWarehouseEndPrice() {
        return warehouseEndPrice;
    }

    public void setWarehouseEndPrice(String warehouseEndPrice) {
        this.warehouseEndPrice = warehouseEndPrice;
    }

    public HashMap<Integer, ProfitMarginReport> getGroupCurrencyTotal() {
        return groupCurrencyTotal;
    }

    public void setGroupCurrencyTotal(HashMap<Integer, ProfitMarginReport> groupCurrencyTotal) {
        this.groupCurrencyTotal = groupCurrencyTotal;
    }

    public String getTotalStockProfit() {
        return totalStockProfit;
    }

    public void setTotalStockProfit(String totalStockProfit) {
        this.totalStockProfit = totalStockProfit;
    }

    public String getTotalStockTakingPrice() {
        return totalStockTakingPrice;
    }

    public void setTotalStockTakingPrice(String totalStockTakingPrice) {
        this.totalStockTakingPrice = totalStockTakingPrice;
    }

    public String getTotalDifferencePrice() {
        return totalDifferencePrice;
    }

    public void setTotalDifferencePrice(String totalDifferencePrice) {
        this.totalDifferencePrice = totalDifferencePrice;
    }

    public String getTotalStockTakingQuantity() {
        return totalStockTakingQuantity;
    }

    public void setTotalStockTakingQuantity(String totalStockTakingQuantity) {
        this.totalStockTakingQuantity = totalStockTakingQuantity;
    }

    public String getTotalZSalesQuantity() {
        return totalZSalesQuantity;
    }

    public void setTotalZSalesQuantity(String totalZSalesQuantity) {
        this.totalZSalesQuantity = totalZSalesQuantity;
    }

    public String getTotalZSalesPrice() {
        return totalZSalesPrice;
    }

    public void setTotalZSalesPrice(String totalZSalesPrice) {
        this.totalZSalesPrice = totalZSalesPrice;
    }

    public String getTotalExcludingZSalesPrice() {
        return totalExcludingZSalesPrice;
    }

    public void setTotalExcludingZSalesPrice(String totalExcludingZSalesPrice) {
        this.totalExcludingZSalesPrice = totalExcludingZSalesPrice;
    }

    public String getTotalExcludingZSalesQuantity() {
        return totalExcludingZSalesQuantity;
    }

    public void setTotalExcludingZSalesQuantity(String totalExcludingZSalesQuantity) {
        this.totalExcludingZSalesQuantity = totalExcludingZSalesQuantity;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("------------ProfitMarginReportBean");

        selectedObject = new ProfitMarginReport();
        listOfStock = new ArrayList<>();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            setCountToggle(1);
        }
        if (selectedObject.isReportType()) {//Özet-Kategori
            toogleList.set(0, false);
            toogleList.set(1, false);
            toogleList.set(2, false);
            toogleList.set(3, false);
            toogleList.set(4, false);
        }

        listOfCategorization = new ArrayList<>();
        listOfTotals = new ArrayList<>();

        selectedObject.setEndDate(new Date());

        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.DAY_OF_YEAR, 1);
        cld.set(Calendar.HOUR_OF_DAY, 00);
        cld.set(Calendar.MINUTE, 00);
        cld.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cld.getTime());

        selectedObject.setIsAllStock(false);
        selectedObject.setIsTaxIncluded(true);
        categoryBookCheckboxFilterBean.isAll = true;
        rootCategory = new DefaultTreeNode();

        listOfIncomeExpense = new ArrayList<>();
        listCategory = new ArrayList<>();
        totalProfitMargin = new ProfitMarginReport();

        listOfBranch = branchSettingService.findUserAuthorizeBranch();
        if (sessionBean.getUser().getLastBranch().isIsCentral()) {
            for (BranchSetting branchSetting : listOfBranch) {
                selectedObject.getSelectedBranchList().add(branchSetting);
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                    selectedObject.getSelectedBranchList().add(branchSetting);
                    break;
                }
            }
        }

        changeBranch();
        isThereListBranch = false;

    }

    /**
     * Bu metot branch değiştiği anda string olara branch id leri birleştirerek
     * where şartı oluşturur.
     */
    public void changeBranch() {
        branchList = "";
        if (selectedObject.getSelectedBranchList().size() == 1) { // category kitabı için şube bilgisini göndermek için kullanılır.
            branchId = selectedObject.getSelectedBranchList().get(0).getBranch().getId();
            selectedObject.setBranchSetting(selectedObject.getSelectedBranchList().get(0));

            listOfStock.clear();
            selectedObject.getStockList().clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

            isThereListBranch = false;

            if (selectedObject.getSelectedBranchList().get(0).getBranch().getConceptType() == 1 && selectedObject.getSelectedBranchList().get(0).isIsCentralIntegration()) {
                toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            } else {
                toogleList = Arrays.asList(true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
                setCountToggle(1);
            }

        } else {
            branchId = -1;
            isThereListBranch = true;

            listOfStock.clear();
            selectedObject.getStockList().clear();
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));

        }
        for (BranchSetting branchSetting : selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList()) {
            branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            if (branchSetting.getBranch().getId() == 0) {
                branchList = "";
                break;
            }
        }
        if (!branchList.equals("")) {
            branchList = branchList.substring(1, branchList.length());
        }

        int countCentralIntegration = 0;
        for (BranchSetting branchSetting : selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList()) {
            if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                countCentralIntegration++;
            }
        }
        if (countCentralIntegration > 0) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            setCountToggle(1);
        }

        if (selectedObject.isReportType()) {//Özet-Kategori
            toogleList.set(0, false);
            toogleList.set(1, false);
            toogleList.set(2, false);
            toogleList.set(3, false);
            toogleList.set(4, false);
        }

    }

    @Override
    public void find() {
        isFind = true;

        selectedObject.getStockList().clear();
        selectedObject.getStockList().addAll(listOfStock);

        selectedObject.getListOfCategorization().clear();
        selectedObject.getListOfCategorization().addAll(listOfCategorization);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(selectedObject.getEndDate());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        selectedObject.setEndDate(calendar.getTime());

        Calendar cld = Calendar.getInstance();
        cld.setTime(selectedObject.getBeginDate());
        cld.set(Calendar.HOUR_OF_DAY, 00);
        cld.set(Calendar.MINUTE, 00);
        cld.set(Calendar.SECOND, 00);
        selectedObject.setBeginDate(cld.getTime());
        if (branchList.isEmpty()) {
            for (BranchSetting branchSetting : listOfBranch) {
                branchList = branchList + "," + String.valueOf(branchSetting.getBranch().getId());
            }
            if (!branchList.equals("")) {
                branchList = branchList.substring(1, branchList.length());
            }
        }
        createWhere = profitMarginReportService.createWhere(selectedObject);
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmProfitMarginDatatable:dtbProfitMargin");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        listOfIncomeExpense = incomeExpenseService.totalIncomeExpense(selectedObject.getBeginDate(), selectedObject.getEndDate(), branchList);
        createTree();
        if (!listOfIncomeExpense.isEmpty()) {
            IncomeExpense income = new IncomeExpense();
            income.setTotalExchagePrice(calculateIncomeExpenseSubTotal(true));
            income.setName(sessionBean.getLoc().getString("totalincome"));
            income.setId(-1);
            income.setIsIncome(true);
            new DefaultTreeNode(income, root);
            IncomeExpense expense = new IncomeExpense();
            expense.setTotalExchagePrice(calculateIncomeExpenseSubTotal(false));
            expense.setName(sessionBean.getLoc().getString("totalexpense"));
            expense.setId(-1);
            expense.setIsIncome(false);
            new DefaultTreeNode(expense, root);
        }
        tempCount = 0;
        boolean isThere = false;
        int count = 0;

        for (BranchSetting branchSetting : selectedObject.getSelectedBranchList()) {
            isThere = false;
            if (branchSetting.isIsCentralIntegration()) {
                isThere = true;
            }
            if (isThere) {
                count++;
            }
        }
        centralIngetrationInf = -1;
        if (selectedObject.getSelectedBranchList().size() > 1) {
            if (count >= 1 && selectedObject.getSelectedBranchList().size() > count) {
                centralIngetrationInf = -1;
            } else if (count == selectedObject.getSelectedBranchList().size() && isThere) {
                centralIngetrationInf = 1;
            } else if (count == 0 && !isThere) {
                centralIngetrationInf = 0;
            }
        } else if (selectedObject.getSelectedBranchList().size() == 1) {
            if (selectedObject.getSelectedBranchList().get(0).isIsCentralIntegration()) {
                centralIngetrationInf = 1;
            } else if (!selectedObject.getSelectedBranchList().get(0).isIsCentralIntegration()) {
                centralIngetrationInf = 0;
            }
        }

        if (selectedObject.isReportType()) {//Özet
            groupCurrencyTotal = new HashMap<>();

            listCategory = profitMarginReportService.findAllCategory(selectedObject, createWhere, branchList, centralIngetrationInf);
//            if (selectedObject.isCalculationType()) {
//                listOfTotals = profitMarginReportService.totalsCategory(createWhere, selectedObject, branchList, centralIngetrationInf);
//            }

            if (!listCategory.isEmpty()) {
                RequestContext.getCurrentInstance().execute("count=" + listCategory.size() + ";");
            }
            findCategory();
            calcGroupTotal();
            subTotalCategory();

        } else {//Detay
            listOfObjects = findall(createWhere);

        }

        RequestContext.getCurrentInstance().execute("Centrowiz.panelClose();");
    }

    @Override
    public LazyDataModel<ProfitMarginReport> findall(String where) {
        return new CentrowizLazyDataModel<ProfitMarginReport>() {
            @Override
            public List<ProfitMarginReport> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<ProfitMarginReport> result = profitMarginReportService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, selectedObject, branchList, centralIngetrationInf);
                listOfTotals = profitMarginReportService.totals(createWhere, selectedObject, branchList, centralIngetrationInf);
                int count = 0;
                for (ProfitMarginReport p : listOfTotals) {
                    count = count + p.getId();
                }
                listOfObjects.setRowCount(count);
                subTotalDetail();

                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                if (tempCount == 0) {
                    tempCount = 1;
                }
                return result;
            }
        };
    }

    public TreeNode findCategory() {
        rootCategory = new DefaultTreeNode(new ProfitMarginReport(), null);
        rootCategory.setExpanded(true);
        for (ProfitMarginReport p : listCategory) {
            if (p.getCategorization().getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(p, rootCategory);
                findChildCategory(parentTreeNode, listCategory);
            }
        }
        return rootCategory;
    }

    public void findChildCategory(TreeNode categoryTree, List<ProfitMarginReport> list) {
        for (ProfitMarginReport p : list) {
            if (p.getCategorization().getParentId().getId() != 0) {
                if (p.getCategorization().getParentId().getId() == ((ProfitMarginReport) categoryTree.getData()).getCategorization().getId() && p.getBranchSetting().getBranch().getName().equals(((ProfitMarginReport) categoryTree.getData()).getBranchSetting().getBranch().getName())) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(p, categoryTree);
                    childTreeNode.setExpanded(true);
                    findChildCategory(childTreeNode, list);
                }
            }
        }
    }

    public TreeNode createTree() {
        boolean isThere = false;
        root = new DefaultTreeNode(new ProfitMarginReport(), null);
        root.setExpanded(true);
        for (IncomeExpense p : listOfIncomeExpense) {
            isThere = false;
            if (p.getParentId().getId() != 0) {
                for (TreeNode t : root.getChildren()) {
                    if (((IncomeExpense) t.getData()).getId() == p.getParentId().getId()) {
                        isThere = true;
                        break;
                    }
                }
                if (!isThere) {
                    p.getParentId().setIsIncome(p.isIsIncome());
                    DefaultTreeNode parentTreeNode = new DefaultTreeNode(p.getParentId(), root);
                    findChildren(parentTreeNode, listOfIncomeExpense);
                }
            }
        }
        return root;
    }

    public void findChildren(DefaultTreeNode treeNode, List<IncomeExpense> list) {
        for (IncomeExpense p : list) {
            if (p.getParentId().getId() != 0) {
                if (p.getParentId().getId() == ((IncomeExpense) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(p, treeNode);
                    childTreeNode.setExpanded(true);
                    findChildren(childTreeNode, list);
                }
            }
        }
    }

    public BigDecimal calcTotalProfit(BigDecimal overallTotalSales, BigDecimal overallTotalPurchase) {
        return overallTotalSales.subtract(overallTotalPurchase);
    }

    public void calcGroupTotal() {
        for (ProfitMarginReport categoryList : listCategory) {
            if (categoryList.getCategorization().getParentId().getId() == 0) {
                if (groupCurrencyTotal.containsKey(categoryList.getCurrency().getId())) {

                    ProfitMarginReport salePrice = new ProfitMarginReport();

                    salePrice.setWarehouseStartQuantity(categoryList.getWarehouseStartQuantity());
                    salePrice.setBeginToEndPurchaseQuantity(categoryList.getBeginToEndPurchaseQuantity());

                    salePrice.setBeginToEndPurchasePrice(categoryList.getBeginToEndPurchasePrice());
                    salePrice.setBeginToEndPurchaseReturnQuantity(categoryList.getBeginToEndPurchaseReturnQuantity());
                    salePrice.setBeginToEndPurchaseReturnPrice(categoryList.getBeginToEndPurchaseReturnPrice());
                    salePrice.setBeginToEndSalesQuantity(categoryList.getBeginToEndSalesQuantity());
                    salePrice.setBeginToEndSalesPrice(categoryList.getBeginToEndSalesPrice());

                    salePrice.setTotalPurchasePrice(categoryList.getTotalPurchasePrice());
                    salePrice.setWarehouseEndQuantity(categoryList.getWarehouseEndQuantity());
                    salePrice.setCurrency(categoryList.getCurrency());
                    salePrice.setQuantity(categoryList.getQuantity());
                    salePrice.setTotalSalesPrice(categoryList.getTotalSalesPrice());
                    salePrice.setTempOverallTotalSales(categoryList.getTempOverallTotalSales());
                    salePrice.setStockTakingPrice(categoryList.getStockTakingPrice());
                    salePrice.setStockTakingQuantity(categoryList.getStockTakingQuantity());
                    salePrice.setDifferencePrice(categoryList.getDifferencePrice());

                    salePrice.setzSalesPrice(categoryList.getzSalesPrice());
                    salePrice.setzSalesQuantity(categoryList.getzSalesQuantity());

                    salePrice.setWarehouseStartPrice(categoryList.getWarehouseStartPrice());
                    salePrice.setWarehouseEndPrice(categoryList.getWarehouseEndPrice());

                    ProfitMarginReport profitMarginReport = new ProfitMarginReport();

                    profitMarginReport.setWarehouseStartQuantity(salePrice.getWarehouseStartQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getWarehouseStartQuantity()));
                    profitMarginReport.setBeginToEndPurchaseQuantity(salePrice.getBeginToEndPurchaseQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndPurchaseQuantity()));
                    profitMarginReport.setBeginToEndPurchasePrice(salePrice.getBeginToEndPurchasePrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndPurchasePrice()));
                    profitMarginReport.setBeginToEndPurchaseReturnQuantity(salePrice.getBeginToEndPurchaseReturnQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndPurchaseReturnQuantity()));
                    profitMarginReport.setBeginToEndPurchaseReturnPrice(salePrice.getBeginToEndPurchaseReturnPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndPurchaseReturnPrice()));

                    profitMarginReport.setBeginToEndSalesQuantity(salePrice.getBeginToEndSalesQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndSalesQuantity()));
                    profitMarginReport.setBeginToEndSalesPrice(salePrice.getBeginToEndSalesPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getBeginToEndSalesPrice()));
                    profitMarginReport.setTotalPurchasePrice(salePrice.getTotalPurchasePrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getTotalPurchasePrice()));
                    profitMarginReport.setTotalSalesPrice(salePrice.getTotalSalesPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getTotalSalesPrice()));
                    profitMarginReport.setTempOverallTotalSales(salePrice.getTempOverallTotalSales().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getTempOverallTotalSales()));

                    profitMarginReport.setWarehouseEndQuantity(salePrice.getWarehouseEndQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getWarehouseEndQuantity()));
                    profitMarginReport.setWarehouseStartPrice(salePrice.getWarehouseStartPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getWarehouseStartPrice()));
                    profitMarginReport.setWarehouseEndPrice(salePrice.getWarehouseEndPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getWarehouseEndPrice()));

                    profitMarginReport.setCurrency(salePrice.getCurrency());

                    profitMarginReport.setStockTakingPrice(salePrice.getStockTakingPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getStockTakingPrice()));
                    profitMarginReport.setStockTakingQuantity(salePrice.getStockTakingQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getStockTakingQuantity()));

                    profitMarginReport.setDifferencePrice(salePrice.getDifferencePrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getDifferencePrice()));

                    profitMarginReport.setzSalesPrice(salePrice.getzSalesPrice().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getzSalesPrice()));
                    profitMarginReport.setzSalesQuantity(salePrice.getzSalesQuantity().add(groupCurrencyTotal.get(categoryList.getCurrency().getId()).getzSalesQuantity()));

                    groupCurrencyTotal.put(categoryList.getCurrency().getId(), profitMarginReport);

                } else {

                    ProfitMarginReport oldNew = new ProfitMarginReport();

                    oldNew.setWarehouseStartQuantity(categoryList.getWarehouseStartQuantity());
                    oldNew.setBeginToEndPurchaseQuantity(categoryList.getBeginToEndPurchaseQuantity());

                    oldNew.setBeginToEndPurchasePrice(categoryList.getBeginToEndPurchasePrice());
                    oldNew.setBeginToEndPurchaseReturnQuantity(categoryList.getBeginToEndPurchaseReturnQuantity());
                    oldNew.setBeginToEndPurchaseReturnPrice(categoryList.getBeginToEndPurchaseReturnPrice());
                    oldNew.setBeginToEndSalesQuantity(categoryList.getBeginToEndSalesQuantity());
                    oldNew.setBeginToEndSalesPrice(categoryList.getBeginToEndSalesPrice());
                    oldNew.setCurrency(categoryList.getCurrency());
                    oldNew.setTotalSalesPrice(categoryList.getTotalSalesPrice());
                    oldNew.setTempOverallTotalSales(categoryList.getTempOverallTotalSales());

                    oldNew.setTotalPurchasePrice(categoryList.getTotalPurchasePrice());
                    oldNew.setWarehouseEndQuantity(categoryList.getWarehouseEndQuantity());

                    oldNew.setStockTakingPrice(categoryList.getStockTakingPrice());
                    oldNew.setStockTakingQuantity(categoryList.getStockTakingQuantity());

                    oldNew.setDifferencePrice(categoryList.getDifferencePrice());
                    oldNew.setzSalesPrice(categoryList.getzSalesPrice());
                    oldNew.setzSalesQuantity(categoryList.getzSalesQuantity());

                    oldNew.setWarehouseStartPrice(categoryList.getWarehouseStartPrice());
                    oldNew.setWarehouseEndPrice(categoryList.getWarehouseEndPrice());

                    groupCurrencyTotal.put(categoryList.getCurrency().getId(), oldNew);
                }

            }
        }
        for (Map.Entry<Integer, ProfitMarginReport> entry : groupCurrencyTotal.entrySet()) {
            ProfitMarginReport value = entry.getValue();
            value.setProfitMargin(calculateProfit(0, value.getTempOverallTotalSales(), value.getTotalPurchasePrice(), value.getQuantity()));
            value.setProfitPercentage(calculateProfit(1, value.getTempOverallTotalSales(), value.getTotalPurchasePrice(), value.getQuantity()));
            value.setTotalProfit(calculateProfit(2, value.getTempOverallTotalSales(), value.getTotalPurchasePrice(), value.getQuantity()));
            if (value.getBeginToEndSalesPrice() != null && value.getzSalesPrice() != null) {
                value.setzSalesPriceExcluding(value.getBeginToEndSalesPrice().subtract(value.getzSalesPrice()));
            } else {
                value.setzSalesPriceExcluding(BigDecimal.valueOf(0));
            }
            if (value.getBeginToEndSalesQuantity() != null && value.getzSalesQuantity() != null) {
                value.setzSalesQuantityExcluding(value.getBeginToEndSalesQuantity().subtract(value.getzSalesQuantity()));
            } else {
                value.setzSalesQuantityExcluding(BigDecimal.valueOf(0));
            }

        }
    }

    public void subTotalCategory() {
        BigDecimal bigWarehouseStartQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndPurchaseQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndSalesQuantity = BigDecimal.ZERO;
        BigDecimal bigWarehouseEndQuantity = BigDecimal.ZERO;
        BigDecimal totalProfitCalc = BigDecimal.ZERO;
        BigDecimal bigTotalStockProfit = BigDecimal.ZERO;
        BigDecimal bigStockTakingQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndPurchaseReturnQuantity = BigDecimal.ZERO;
        BigDecimal bigZSalesQuantity = BigDecimal.ZERO;
        BigDecimal bigExcludingZSalesQuantity = BigDecimal.ZERO;

        warehouseStartQuantity = "";
        warehouseStartPrice = "";
        beginToEndPurchaseQuantity = "";
        beginToEndPurchasePrice = "";
        beginToEndPurchaseReturnQuantity = "";
        beginToEndPurchaseReturnPrice = "";
        beginToEndSalesQuantity = "";
        beginToEndSalesPrice = "";
        totalPurchasePrice = "";
        warehouseEndQuantity = "";
        warehouseEndPrice = "";
        totalProfit = "";
        totalStockProfit = "";
        profitMargin = "";
        profitPercentage = "";
        totalStockTakingPrice = "";
        totalStockTakingQuantity = "";
        totalDifferencePrice = "";
        totalZSalesPrice = "";
        totalZSalesQuantity = "";
        totalExcludingZSalesPrice = "";
        totalExcludingZSalesQuantity = "";

        totalExpense = BigDecimal.valueOf(0);
        totalIncome = BigDecimal.valueOf(0);
        for (IncomeExpense inc : listOfIncomeExpense) {
            if (inc.isIsIncome()) {
                totalIncome = totalIncome.add(inc.getTotalExchagePrice());
            } else {
                totalExpense = totalExpense.add(inc.getTotalExchagePrice());
            }
        }
        listOfTotals.clear();
        if (groupCurrencyTotal.size() == 0) {
            warehouseStartQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            warehouseStartPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            beginToEndPurchaseQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndPurchasePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            beginToEndPurchaseReturnQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndPurchaseReturnPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            beginToEndSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndSalesPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalPurchasePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            warehouseEndQuantity = "";
            warehouseEndPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalProfit = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalStockProfit = sessionBean.getNumberFormat().format(Double.valueOf(0));
            profitMargin = sessionBean.getNumberFormat().format(Double.valueOf(0));
            profitPercentage = sessionBean.getNumberFormat().format(Double.valueOf(0));

            totalStockTakingQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalStockTakingPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalDifferencePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalZSalesPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalZSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));

            totalExcludingZSalesPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalExcludingZSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));

        } else {
            for (Map.Entry<Integer, ProfitMarginReport> entry : groupCurrencyTotal.entrySet()) {
                Integer key = entry.getKey();
                ProfitMarginReport value = entry.getValue();

                String crrCode = sessionBean.currencySignOrCode(key, 0);

                bigWarehouseStartQuantity = bigWarehouseStartQuantity.add(value.getWarehouseStartQuantity()); //d.b miktar
                warehouseStartQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigWarehouseStartQuantity));

                warehouseStartPrice += " + " + sessionBean.getNumberFormat().format(value.getWarehouseStartPrice()) + " " + crrCode + " "; // d.b tutar

                bigBeginToEndPurchaseQuantity = bigBeginToEndPurchaseQuantity.add(value.getBeginToEndPurchaseQuantity()); // alış miktarı
                beginToEndPurchaseQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndPurchaseQuantity));

                beginToEndPurchasePrice += " + " + sessionBean.getNumberFormat().format(value.getBeginToEndPurchasePrice()) + " " + crrCode + " ";

                bigBeginToEndPurchaseReturnQuantity = bigBeginToEndPurchaseReturnQuantity.add(value.getBeginToEndPurchaseReturnQuantity()); // alış miktarı
                beginToEndPurchaseReturnQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndPurchaseReturnQuantity));

                beginToEndPurchaseReturnPrice += " + " + sessionBean.getNumberFormat().format(value.getBeginToEndPurchaseReturnPrice()) + " " + crrCode + " ";

                bigBeginToEndSalesQuantity = bigBeginToEndSalesQuantity.add(value.getBeginToEndSalesQuantity());
                beginToEndSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndSalesQuantity));

                beginToEndSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getBeginToEndSalesPrice()) + " " + crrCode + " ";

                totalPurchasePrice += " + " + sessionBean.getNumberFormat().format(value.getTotalPurchasePrice()) + " " + crrCode + " ";

                bigWarehouseEndQuantity = bigWarehouseEndQuantity.add(value.getWarehouseEndQuantity());
                warehouseEndQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigWarehouseEndQuantity));

                warehouseEndPrice += " + " + sessionBean.getNumberFormat().format(value.getWarehouseEndPrice()) + " " + crrCode + " ";

                if (key == sessionBean.getUser().getLastBranch().getCurrency().getId()) {
                    totalProfitCalc = calculateTotalProfit(value.getTotalProfit(), totalIncome, totalExpense);
                    totalProfit += " + " + sessionBean.getNumberFormat().format(totalProfitCalc) + " " + crrCode + " ";
                } else {
                    totalProfit += " + " + sessionBean.getNumberFormat().format(value.getTotalProfit()) + " " + crrCode + " ";
                }

                profitMargin += " + " + sessionBean.getNumberFormat().format(value.getProfitMargin()) + "%" + " " + crrCode + " ";
                profitPercentage += " + " + sessionBean.getNumberFormat().format(value.getProfitPercentage()) + "%" + " " + crrCode + " ";

                totalStockProfit += " + " + sessionBean.getNumberFormat().format(value.getTotalProfit()) + " " + crrCode + " ";

                bigStockTakingQuantity = bigStockTakingQuantity.add(value.getStockTakingQuantity());
                totalStockTakingQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigStockTakingQuantity));

                bigZSalesQuantity = bigZSalesQuantity.add(value.getzSalesQuantity());
                totalZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigZSalesQuantity));

                totalStockTakingPrice += " + " + sessionBean.getNumberFormat().format(value.getStockTakingPrice()) + " " + crrCode + " ";
                totalDifferencePrice += " + " + sessionBean.getNumberFormat().format(value.getDifferencePrice()) + " " + crrCode + " ";
                totalZSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getzSalesPrice()) + " " + crrCode + " ";

                if (value.getBeginToEndSalesPrice() != null && value.getzSalesPrice() != null) {
                    totalExcludingZSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getBeginToEndSalesPrice().subtract(value.getzSalesPrice())) + " " + crrCode + " ";
                } else {
                    totalExcludingZSalesPrice += " + " + sessionBean.getNumberFormat().format(0) + " " + crrCode + " ";
                }
                if (value.getBeginToEndSalesQuantity() != null && value.getzSalesQuantity() != null) {
                    bigExcludingZSalesQuantity = bigExcludingZSalesQuantity.add(value.getBeginToEndSalesQuantity().subtract(value.getzSalesQuantity()));
                    totalExcludingZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigExcludingZSalesQuantity));
                } else {
                    totalExcludingZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(0));
                }

                RequestContext.getCurrentInstance().execute("updateFieldSet()");
                value.getCategorization().setId(-1);
                value.getCurrency().setId(key);
                listOfTotals.add(value);
                new DefaultTreeNode(value, rootCategory);
            }
        }

    }

    public void subTotalDetail() {
        BigDecimal bigWarehouseStartQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndPurchaseQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndPurchaseReturnQuantity = BigDecimal.ZERO;
        BigDecimal bigBeginToEndSalesQuantity = BigDecimal.ZERO;
        BigDecimal bigWarehouseEndQuantity = BigDecimal.ZERO;
        BigDecimal totalProfitCalc = BigDecimal.ZERO;
        BigDecimal bigTotalStockProfit = BigDecimal.ZERO;
        BigDecimal allProfit = BigDecimal.ZERO;
        BigDecimal bigStockTakingQuantity = BigDecimal.ZERO;
        BigDecimal bigZSalesQuantity = BigDecimal.ZERO;
        BigDecimal bigExcludingZSalesQuantity = BigDecimal.ZERO;

        warehouseStartQuantity = "";
        warehouseStartPrice = "";
        beginToEndPurchaseQuantity = "";
        beginToEndPurchasePrice = "";
        beginToEndPurchaseReturnQuantity = "";
        beginToEndPurchaseReturnPrice = "";
        beginToEndSalesQuantity = "";
        beginToEndSalesPrice = "";
        totalPurchasePrice = "";
        warehouseEndQuantity = "";
        warehouseEndPrice = "";
        totalProfit = "";
        totalStockProfit = "";
        profitMargin = "";
        profitPercentage = "";
        totalStockTakingPrice = "";
        totalStockTakingQuantity = "";
        totalDifferencePrice = "";
        totalZSalesPrice = "";
        totalZSalesQuantity = "";
        totalExcludingZSalesPrice = "";
        totalExcludingZSalesQuantity = "";

        totalExpense = BigDecimal.valueOf(0);
        totalIncome = BigDecimal.valueOf(0);
        for (IncomeExpense inc : listOfIncomeExpense) {
            if (inc.isIsIncome()) {
                totalIncome = totalIncome.add(inc.getTotalExchagePrice());
            } else {
                totalExpense = totalExpense.add(inc.getTotalExchagePrice());
            }
        }

        if (totalIncome == null) {
            totalIncome = BigDecimal.valueOf(0);
        }
        if (totalExpense == null) {
            totalExpense = BigDecimal.valueOf(0);
        }

        if (listOfTotals.size() == 0) {
            warehouseStartQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            warehouseStartPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            beginToEndPurchaseQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndPurchasePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            beginToEndPurchaseReturnQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndPurchaseReturnPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);

            beginToEndSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            beginToEndSalesPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalPurchasePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            warehouseEndQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            warehouseEndPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalProfit = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalStockProfit = sessionBean.getNumberFormat().format(Double.valueOf(0));
            profitMargin = sessionBean.getNumberFormat().format(Double.valueOf(0));
            profitPercentage = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalStockTakingQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalStockTakingPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalDifferencePrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalZSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalZSalesPrice = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
            totalExcludingZSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0));
            totalExcludingZSalesQuantity = sessionBean.getNumberFormat().format(Double.valueOf(0)) + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);

        } else {

            for (ProfitMarginReport value : listOfTotals) {
                String crrCode = sessionBean.currencySignOrCode(value.getCurrency().getId(), 0);

                bigWarehouseStartQuantity = bigWarehouseStartQuantity.add(value.getOverallWarehouseStartQuantity()); //d.b miktar
                warehouseStartQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigWarehouseStartQuantity));

                warehouseStartPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallTotalWarehouseStartPrice()) + " " + crrCode + " "; // d.b tutar

                bigBeginToEndPurchaseQuantity = bigBeginToEndPurchaseQuantity.add(value.getOverallBeginToEndPurchaseQuantity()); // alış miktarı
                beginToEndPurchaseQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndPurchaseQuantity));

                beginToEndPurchasePrice += " + " + sessionBean.getNumberFormat().format(value.getOverallBeginToEndPurchasePrice()) + " " + crrCode + " ";

                bigBeginToEndPurchaseReturnQuantity = bigBeginToEndPurchaseReturnQuantity.add(value.getOverallBeginToEndPurchaseReturnQuantity()); // alış miktarı
                beginToEndPurchaseReturnQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndPurchaseReturnQuantity));

                beginToEndPurchaseReturnPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallBeginToEndPurchaseReturnPrice()) + " " + crrCode + " ";

                bigBeginToEndSalesQuantity = bigBeginToEndSalesQuantity.add(value.getOverallBeginToEndSalesQuantity());
                beginToEndSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigBeginToEndSalesQuantity));

                beginToEndSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallBeginToEndSalesPrice()) + " " + crrCode + " ";

                totalPurchasePrice += " + " + sessionBean.getNumberFormat().format(value.getOverallTotalPurchase()) + " " + crrCode + " ";

                bigWarehouseEndQuantity = bigWarehouseEndQuantity.add(value.getOverallWarehouseEndQuantity());
                warehouseEndQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigWarehouseEndQuantity));

                warehouseEndPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallTotalWarehouseEndPrice()) + " " + crrCode + " ";

                BigDecimal profitMarginT = BigDecimal.ZERO;
                profitMarginT = calculateProfit(0, value.getTempOverallTotalSales(), value.getOverallTotalPurchase(), value.getOverallQuantity());
                profitMargin += " + " + sessionBean.getNumberFormat().format(profitMarginT) + "%" + " " + crrCode + " ";

                BigDecimal profitPercentageT = BigDecimal.ZERO;
                profitPercentageT = calculateProfit(1, value.getTempOverallTotalSales(), value.getOverallTotalPurchase(), value.getOverallQuantity());
                profitPercentage += " + " + sessionBean.getNumberFormat().format(profitPercentageT) + "%" + " " + crrCode + " ";

                BigDecimal calcTotalProfit = calcTotalProfit(value.getTempOverallTotalSales(), value.getOverallTotalPurchase());
                totalStockProfit += " + " + sessionBean.getNumberFormat().format(calcTotalProfit) + " " + crrCode + " ";

                if (value.getCurrency().getId() == sessionBean.getUser().getLastBranch().getCurrency().getId()) {
                    BigDecimal bigDecimal = calcTotalProfit(value.getTempOverallTotalSales(), value.getOverallTotalPurchase());
                    totalProfitCalc = calculateTotalProfit(bigDecimal, totalIncome, totalExpense);
                    totalProfit += " + " + sessionBean.getNumberFormat().format(totalProfitCalc) + " " + crrCode + " ";
                } else {
                    BigDecimal bigDecimal = calcTotalProfit(value.getTempOverallTotalSales(), value.getOverallTotalPurchase());
                    totalProfit += " + " + sessionBean.getNumberFormat().format(bigDecimal) + " " + crrCode + " ";
                }

                bigStockTakingQuantity = bigStockTakingQuantity.add(value.getOverallStockTakingQuantity());
                totalStockTakingQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigStockTakingQuantity));

                totalStockTakingPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallStockTakingPrice()) + " " + crrCode + " ";
                totalDifferencePrice += " + " + sessionBean.getNumberFormat().format(value.getOverallDifferencePrice()) + " " + crrCode + " ";

                bigZSalesQuantity = bigZSalesQuantity.add(value.getOverallZSalesQuantity());
                totalZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigZSalesQuantity));
                totalZSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallZSalesPrice()) + " " + crrCode + " ";

                if (value.getOverallBeginToEndSalesPrice() != null && value.getOverallZSalesPrice() != null) {
                    totalExcludingZSalesPrice += " + " + sessionBean.getNumberFormat().format(value.getOverallBeginToEndSalesPrice().subtract(value.getOverallZSalesPrice())) + " " + crrCode + " ";
                } else {
                    totalExcludingZSalesPrice += " + " + sessionBean.getNumberFormat().format(0) + " " + crrCode + " ";
                }
                if (value.getOverallBeginToEndSalesQuantity() != null && value.getOverallZSalesQuantity() != null) {
                    bigExcludingZSalesQuantity = bigExcludingZSalesQuantity.add(value.getOverallBeginToEndSalesQuantity().subtract(value.getOverallZSalesQuantity()));
                    totalExcludingZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(bigExcludingZSalesQuantity));
                } else {
                    totalExcludingZSalesQuantity = String.valueOf(sessionBean.getNumberFormat().format(0));
                }

                RequestContext.getCurrentInstance().execute("updateFieldSet()");
            }
        }

    }

    public void updateAllInformation(ActionEvent event) {
        if (event.getComponent().getParent().getParent().getId().equals("frmStockBookFilterCheckbox")) {
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
            RequestContext.getCurrentInstance().update("frmProfitMargin:txtStock");
        } else {
            listOfCategorization.clear();
            if (categoryBookCheckboxFilterBean.isAll) {
                Categorization s = new Categorization(0);
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().contains(s)) {
                    categoryBookCheckboxFilterBean.getListOfCategorization().add(0, new Categorization(0, sessionBean.loc.getString("all")));
                }
            } else if (!categoryBookCheckboxFilterBean.isAll) {
                if (!categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                    if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                        categoryBookCheckboxFilterBean.getListOfCategorization().remove(categoryBookCheckboxFilterBean.getListOfCategorization().get(0));
                    }
                }
            }
            listOfCategorization.addAll(categoryBookCheckboxFilterBean.getListOfCategorization());
            if (categoryBookCheckboxFilterBean.getListOfCategorization().isEmpty()) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (categoryBookCheckboxFilterBean.getListOfCategorization().get(0).getId() == 0) {
                categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                categoryBookCheckboxFilterBean.setSelectedCount(categoryBookCheckboxFilterBean.getListOfCategorization().size() + " " + sessionBean.loc.getString("category") + " " + sessionBean.loc.getString("selected"));
            }

            RequestContext.getCurrentInstance().update("frmProfitMargin:txtCategory");
        }

    }

    public void openDialog(int type) {
        if (type == 1) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        } else {
            categoryBookCheckboxFilterBean.getListOfCategorization().clear();
            if (!listOfCategorization.isEmpty()) {
                if (listOfCategorization.get(0).getId() == 0) {
                    categoryBookCheckboxFilterBean.isAll = true;
                } else {
                    categoryBookCheckboxFilterBean.isAll = false;
                }
            }

            categoryBookCheckboxFilterBean.getListOfCategorization().addAll(listOfCategorization);
        }

    }

    public BigDecimal calculateProfit(int type, BigDecimal totalSale, BigDecimal totalPurchase, BigDecimal saleCount) {

        BigDecimal profit = BigDecimal.valueOf(0);
        switch (type) {
            case 0:
                //Profit Margin
                if (totalSale.compareTo(BigDecimal.valueOf(0)) != 0 && totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = ((totalSale.subtract(totalPurchase)).divide(totalSale, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));
                }
                break;
            case 1:
                //Profit Percentage
                if (totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = ((totalSale.subtract(totalPurchase)).divide(totalPurchase, 4, RoundingMode.HALF_EVEN)).multiply(BigDecimal.valueOf(100));

                }
                break;
            case 2:
                //Total Profit
                if (totalPurchase.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = totalSale.subtract(totalPurchase);
                }
                break;
            case 3:
                if (saleCount.compareTo(BigDecimal.valueOf(0)) != 0) {
                    profit = totalPurchase.divide(saleCount, 4, RoundingMode.HALF_EVEN);
                }
                break;
            default:
                break;
        }

        return profit;
    }

    public void createPdf() {
        profitMarginReportService.exportPdf(createWhere, selectedObject, toogleList, totalIncome, totalExpense, totalProfitMargin, listOfIncomeExpense, listCategory, branchList, centralIngetrationInf, listOfTotals, warehouseStartQuantity, warehouseStartPrice, beginToEndPurchaseQuantity, beginToEndPurchasePrice, beginToEndPurchaseReturnQuantity, beginToEndPurchaseReturnPrice, beginToEndSalesQuantity, beginToEndSalesPrice, totalPurchasePrice, profitMargin, profitPercentage, totalStockProfit, warehouseEndQuantity, warehouseEndPrice, totalProfit, totalStockTakingPrice, totalStockTakingQuantity, totalDifferencePrice, totalZSalesPrice, totalZSalesQuantity, totalExcludingZSalesPrice, totalExcludingZSalesQuantity);
    }

    public void createExcel() throws IOException {
        profitMarginReportService.exportExcel(createWhere, selectedObject, toogleList, totalIncome, totalExpense, totalProfitMargin, listOfIncomeExpense, listCategory, branchList, centralIngetrationInf, listOfTotals, warehouseStartQuantity, warehouseStartPrice, beginToEndPurchaseQuantity, beginToEndPurchasePrice, beginToEndPurchaseReturnQuantity, beginToEndPurchaseReturnPrice, beginToEndSalesQuantity, beginToEndSalesPrice, totalPurchasePrice, profitMargin, profitPercentage, totalStockProfit, warehouseEndQuantity, warehouseEndPrice, totalProfit, totalStockTakingPrice, totalStockTakingQuantity, totalDifferencePrice, totalZSalesPrice, totalZSalesQuantity, totalExcludingZSalesPrice, totalExcludingZSalesQuantity);
    }

    public void createPrinter() {
        RequestContext.getCurrentInstance().execute("$(\"#printerPanel\").empty();$(\"#printerPanel\").append('" + StaticMethods.escapeStringForHtml(profitMarginReportService.exportPrinter(createWhere, selectedObject, toogleList, totalIncome, totalExpense, totalProfitMargin, listOfIncomeExpense, listCategory, branchList, centralIngetrationInf, listOfTotals, warehouseStartQuantity, warehouseStartPrice, beginToEndPurchaseQuantity, beginToEndPurchasePrice, beginToEndPurchaseReturnQuantity, beginToEndPurchaseReturnPrice, beginToEndSalesQuantity, beginToEndSalesPrice, totalPurchasePrice, profitMargin, profitPercentage, totalStockProfit, warehouseEndQuantity, warehouseEndPrice, totalProfit, totalStockTakingPrice, totalStockTakingQuantity, totalDifferencePrice, totalZSalesPrice, totalZSalesQuantity, totalExcludingZSalesPrice, totalExcludingZSalesQuantity)) + "');$(\"#printerPanel\").css('display','block');print_page();$(\"#printerPanel\").css('display','none');");

    }

    public boolean controlPurchase(BigDecimal purchase) {
        if (purchase.compareTo(BigDecimal.valueOf(0)) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void resetList() {
        listOfCategorization.clear();
        listOfStock.clear();
        categoryBookCheckboxFilterBean.getListOfCategorization().clear();
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        categoryBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        categoryBookCheckboxFilterBean.isAll = true;

        int countCentralIntegration = 0;
        for (BranchSetting branchSetting : selectedObject.getSelectedBranchList().isEmpty() ? listOfBranch : selectedObject.getSelectedBranchList()) {
            if (branchSetting.isIsCentralIntegration() && branchSetting.getBranch().getConceptType() == 1) {
                countCentralIntegration++;
            }
        }

        if (countCentralIntegration > 0) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        } else {
            toogleList = Arrays.asList(true, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
            setCountToggle(1);
        }

        if (selectedObject.isReportType()) {//Özet-Kategori
            toogleList.set(0, false);
            toogleList.set(1, false);
            toogleList.set(2, false);
            toogleList.set(3, false);
            toogleList.set(4, false);
        } else {//Detay --Stok
            toogleList.set(0, true);
            toogleList.set(1, true);
            toogleList.set(2, true);
            toogleList.set(3, true);
            toogleList.set(4, true);
        }

    }

    public BigDecimal calculateTotalProfit(BigDecimal stockProfit, BigDecimal income, BigDecimal expense) {
        BigDecimal total = BigDecimal.valueOf(0);
        if (stockProfit == null) {
            stockProfit = BigDecimal.valueOf(0);
        }
        if (income == null) {
            income = BigDecimal.valueOf(0);
        }
        if (expense == null) {
            expense = BigDecimal.valueOf(0);
        }
        total = stockProfit.add(income).subtract(expense);

        return total;
    }

    public String calculateIncomeExpense(int id) {
        String t = "";
        BigDecimal total = BigDecimal.valueOf(0);
        for (IncomeExpense p : listOfIncomeExpense) {
            if (p.getParentId().getId() == id) {
                total = total.add(p.getTotalExchagePrice());
            }
        }
        t += String.valueOf(sessionBean.getNumberFormat().format(total));
        t += " " + sessionBean.currencySignOrCode(sessionBean.getUser().getLastBranch().getCurrency().getId(), 0);
        return t;

    }

    public BigDecimal calculateIncomeExpenseSubTotal(boolean isIncome) {
        BigDecimal t = BigDecimal.valueOf(0);
        for (IncomeExpense p : listOfIncomeExpense) {
            if (p.isIsIncome() == isIncome) {
                t = t.add(p.getTotalExchagePrice());
            }
        }
        return t;

    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public BigDecimal substractSalePriceQuantity(BigDecimal total, BigDecimal sales) {
        BigDecimal sub = BigDecimal.valueOf(0);
        if (total != null && sales != null) {
            sub = total.subtract(sales);
        }
        return sub;
    }
}
