
/*
 *
 * Bu sınıf stokları listeler,yeni stok eklemek için yeni sayfa açar.
 *
 * @author Esra Çabuk
 *
 * Created on 12.01.2018 10:55:15
 */
package com.mepsan.marwiz.inventory.stock.presentation;

import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.brand.business.IBrandService;
import com.mepsan.marwiz.general.categorization.presentation.CategoryBean;
import com.mepsan.marwiz.general.common.AccountBookFilterBean;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.gridproperties.service.IGridOrderColumnService;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Brand;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.general.TaxDepartment;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.inventory.StockUpload;
import com.mepsan.marwiz.general.model.inventory.TaxGroup;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.unit.business.IUnitService;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import com.mepsan.marwiz.inventory.stock.business.GFStockService;
import com.mepsan.marwiz.inventory.stock.business.IStockCategorizationService;
import com.mepsan.marwiz.inventory.stock.business.IStockService;
import com.mepsan.marwiz.inventory.stockrequest.presentation.NewStockRequestBean;
import com.mepsan.marwiz.inventory.taxdepartment.business.ITaxDepartmentService;
import com.mepsan.marwiz.inventory.taxgroup.business.ITaxGroupService;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class StockBean extends GeneralBean<Stock> {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    @ManagedProperty(value = "#{stockService}")
    private IStockService stockService;

    @ManagedProperty(value = "#{gfStockService}")
    private GFStockService gfStockService;

    @ManagedProperty(value = "#{newStockRequestBean}")
    private NewStockRequestBean newStockRequestBean;

    @ManagedProperty(value = "#{unitService}")
    private IUnitService unitService;

    @ManagedProperty(value = "#{taxGroupService}")
    private ITaxGroupService taxGroupService;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    public StockBookCheckboxFilterBean stockBookCheckboxFilterBean;

    @ManagedProperty(value = "#{accountBookFilterBean}")
    public AccountBookFilterBean accountBookFilterBean;

    @ManagedProperty(value = "#{brandService}")
    public IBrandService brandService;

    @ManagedProperty(value = "#{categoryBean}")
    public CategoryBean categoryBean;

    @ManagedProperty(value = "#{accountService}")
    public IAccountService accountService;

    @ManagedProperty(value = "#{stockCategorizationService}")
    public IStockCategorizationService stockCategorizationService;

    @ManagedProperty(value = "#{taxDepartmentService}")
    public ITaxDepartmentService taxDepartmentService;


    private Object object;
    private List<Unit> unitList;
    private UploadedFile uploadedFile;
    private List<StockUpload> excelStockList;
    private List<StockUpload> tempStockList;
    private List<StockUpload> tempProductList;
    private List<StockUpload> sampleList;
    private List<TaxGroup> taxGroupList;
    private List<ErrorItem> errorList;
    private List<Account> accountList;
    private List<Categorization> categoryList;
    private Stock stockItem;
    private String fileNames;
    private String fileName;
    private boolean isOpenSaveBtn, isOpenCancelBtn, isOpenTransferBtn, isOpenErrorData;
    private boolean isScrollView;
    private List<Categorization> selectedCategoryList;
    private List<Stock> listOfTotals;
    private HashMap<Integer, BigDecimal> stockTotals;
    private List<TaxDepartment> taxDepartmentList;
    private List<Stock> stockTypeList;

    private int specialItem;

    //-----toplu güncelleme için---------
    private int changeField;
    private List<Stock> listOfStock;
    private Stock batchUpdateStock;
    private List<Brand> brandList;
    private String order;
    //--------------------------

    //-----stokta gridinde tutulan filtreleme parametreleri için class oluşturuldu.------------
    public class StockParam {

        private TreeNode category;
        private String filterValue;
        private boolean isWithoutSalePrice;
        private boolean isNoneZero;
        private boolean isPassiveStock;
        private boolean isService;

        public StockParam() {
            category = new DefaultTreeNode();
        }

        public String getFilterValue() {
            return filterValue;
        }

        public void setFilterValue(String filterValue) {
            this.filterValue = filterValue;
        }

        public boolean isIsWithoutSalePrice() {
            return isWithoutSalePrice;
        }

        public void setIsWithoutSalePrice(boolean isWithoutSalePrice) {
            this.isWithoutSalePrice = isWithoutSalePrice;
        }

        public boolean isIsNoneZero() {
            return isNoneZero;
        }

        public void setIsNoneZero(boolean isNoneZero) {
            this.isNoneZero = isNoneZero;
        }

        public TreeNode getCategory() {
            return category;
        }

        public void setCategory(TreeNode category) {
            this.category = category;
        }

        public boolean isIsPassiveStock() {
            return isPassiveStock;
        }

        public void setIsPassiveStock(boolean isPassiveStock) {
            this.isPassiveStock = isPassiveStock;
        }

        public boolean isIsService() {
            return isService;
        }

        public void setIsService(boolean isService) {
            this.isService = isService;
        }

    }

    private StockParam stockParam;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setGridOrderColumnService(IGridOrderColumnService gridOrderColumnService) {
        this.gridOrderColumnService = gridOrderColumnService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public void setStockService(IStockService stockService) {
        this.stockService = stockService;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setStockCategorizationService(IStockCategorizationService stockCategorizationService) {
        this.stockCategorizationService = stockCategorizationService;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setGfStockService(GFStockService gfStockService) {
        this.gfStockService = gfStockService;
    }

    public void setNewStockRequestBean(NewStockRequestBean newStockRequestBean) {
        this.newStockRequestBean = newStockRequestBean;
    }

    public void setUnitService(IUnitService unitService) {
        this.unitService = unitService;
    }

    public void setTaxGroupService(ITaxGroupService taxGroupService) {
        this.taxGroupService = taxGroupService;
    }

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public boolean isIsOpenSaveBtn() {
        return isOpenSaveBtn;
    }

    public void setIsOpenSaveBtn(boolean isOpenSaveBtn) {
        this.isOpenSaveBtn = isOpenSaveBtn;
    }

    public boolean isIsOpenCancelBtn() {
        return isOpenCancelBtn;
    }

    public void setIsOpenCancelBtn(boolean isOpenCancelBtn) {
        this.isOpenCancelBtn = isOpenCancelBtn;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public List<Categorization> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Categorization> categoryList) {
        this.categoryList = categoryList;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public boolean isIsOpenTransferBtn() {
        return isOpenTransferBtn;
    }

    public void setIsOpenTransferBtn(boolean isOpenTransferBtn) {
        this.isOpenTransferBtn = isOpenTransferBtn;
    }

    public boolean isIsOpenErrorData() {
        return isOpenErrorData;
    }

    public void setIsOpenErrorData(boolean isOpenErrorData) {
        this.isOpenErrorData = isOpenErrorData;
    }

    public List<TaxGroup> getTaxGroupList() {
        return taxGroupList;
    }

    public void setTaxGroupList(List<TaxGroup> taxGroupList) {
        this.taxGroupList = taxGroupList;
    }

    public List<StockUpload> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<StockUpload> sampleList) {
        this.sampleList = sampleList;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getChangeField() {
        return changeField;
    }

    public void setChangeField(int changeField) {
        this.changeField = changeField;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public Stock getBatchUpdateStock() {
        return batchUpdateStock;
    }

    public void setBatchUpdateStock(Stock batchUpdateStock) {
        this.batchUpdateStock = batchUpdateStock;
    }

    public List<Brand> getBrandList() {
        return brandList;
    }

    public void setBrandList(List<Brand> brandList) {
        this.brandList = brandList;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public void setBrandService(IBrandService brandService) {
        this.brandService = brandService;
    }

    public List<TaxDepartment> getTaxDepartmentList() {
        return taxDepartmentList;
    }

    public void setTaxDepartmentList(List<TaxDepartment> taxDepartmentList) {
        this.taxDepartmentList = taxDepartmentList;
    }

    public List<Stock> getStockTypeList() {
        return stockTypeList;
    }

    public void setStockTypeList(List<Stock> stockTypeList) {
        this.stockTypeList = stockTypeList;
    }

    public void setAccountBookFilterBean(AccountBookFilterBean accountBookFilterBean) {
        this.accountBookFilterBean = accountBookFilterBean;
    }

    public boolean isIsScrollView() {
        return isScrollView;
    }

    public void setIsScrollView(boolean isScrollView) {
        this.isScrollView = isScrollView;
    }

    public List<Categorization> getSelectedCategoryList() {
        return selectedCategoryList;
    }

    public void setSelectedCategoryList(List<Categorization> selectedCategoryList) {
        this.selectedCategoryList = selectedCategoryList;
    }

    public void setCategoryBean(CategoryBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    public List<Stock> getListOfTotals() {
        return listOfTotals;
    }

    public void setListOfTotals(List<Stock> listOfTotals) {
        this.listOfTotals = listOfTotals;
    }

    public HashMap<Integer, BigDecimal> getStockTotals() {
        return stockTotals;
    }

    public void setStockTotals(HashMap<Integer, BigDecimal> stockTotals) {
        this.stockTotals = stockTotals;
    }

    public List<StockUpload> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<StockUpload> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public StockParam getStockParam() {
        return stockParam;
    }

    public void setStockParam(StockParam stockParam) {
        this.stockParam = stockParam;
    }

    public void setTaxDepartmentService(ITaxDepartmentService taxDepartmentService) {
        this.taxDepartmentService = taxDepartmentService;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("----------StockBean--------");
        object = new Object();
        stockParam = new StockParam();
        toogleList = createToggleList(sessionBean.getUser());
        if (toogleList.isEmpty()) {
            toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true);
        }

        listOfStock = new ArrayList<>();
        listOfTotals = new ArrayList<>();
        selectedCategoryList = new ArrayList<>();
        stockTotals = new HashMap<>();
        findUnit();
        DefaultTreeNode defaultTreeNode = new DefaultTreeNode(new Categorization(0, sessionBean.getLoc().getString("all")));
        categoryBean.getRoot().getChildren().add(0, defaultTreeNode);
        boolean isThere = false;
        isScrollView = true;
        if (sessionBean.parameter instanceof ArrayList) {

            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {

                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockParam) {
                    stockParam = (StockParam) ((ArrayList) sessionBean.parameter).get(i);
                    findNode(categoryBean.getRoot(), (Categorization) stockParam.getCategory().getData());
                    autoCompleteValue = stockParam.getFilterValue();
                    isThere = true;
                }
            }

            if (!isThere) {
                categoryBean.getRoot().getChildren().get(0).setSelected(true);
                findNode(categoryBean.getRoot(), (Categorization) categoryBean.getRoot().getChildren().get(0).getData());
                selectedCategoryList = new ArrayList<>();
                listOfTotals = stockService.totals(" AND stck.status_id <> 4 AND si.is_passive = FALSE ");
                listOfObjects = findall(" AND stck.status_id <> 4 AND si.is_passive = FALSE ");
            }
            loadList();

        } else {
            categoryBean.getRoot().getChildren().get(0).setSelected(true);
            findNode(categoryBean.getRoot(), (Categorization) categoryBean.getRoot().getChildren().get(0).getData());
            selectedCategoryList = new ArrayList<>();
            listOfTotals = stockService.totals(" AND stck.status_id <> 4 AND si.is_passive = FALSE ");
            listOfObjects = findall(" AND stck.status_id <> 4 AND si.is_passive = FALSE ");
        }

        setListBtn(sessionBean.checkAuthority(new int[]{122, 123}, 0));

        specialItem = sessionBean.getUser().getLastBranchSetting().getSpecialItem();

    }

    /**
     * Artı butonuna basıldığında yeni stok eklemek için işlem sayfasını açar.
     */
    @Override
    public void create() {
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            newStockRequestBean.create();
        } else {
            stockParam.setFilterValue(autoCompleteValue);
            stockParam.setCategory(categoryBean.getSelectedCategory());
            List<Object> list = new ArrayList<>();
            list.add(stockParam);
            list.add(object);
            marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", list, 0, 12);

        }
    }

    /**
     * Sasrtlara göre listeyi tekrar çeker.
     */
    public void bringList() {
        DataTable dataTable = null;
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmStock:dtbStock");
        } else if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1) {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmStock:dtbStockFull");

        } else {
            dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmStock:dtbStockNoIntegration");
        }
        dataTable.setFirst(0);
        generalFilter();

    }

    /**
     * Genel filtre için yazılmıştır.
     */
    @Override
    public void generalFilter() {

        loadList();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1) {
            updateGridProperties("frmStock:dtbStock");
        } else if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1) {
            updateGridProperties("frmStock:dtbStockFull");
        } else {
            updateGridProperties("frmStock:dtbStockNoIntegration");

        }
        //RequestContext.getCurrentInstance().update("frmStock:dtbStock");
    }

    public void loadList() {
        Categorization categorization = new Categorization();
        selectedCategoryList.clear();
        if (categoryBean.getSelectedCategory() != null) {
            categorization.setId(((Categorization) categoryBean.getSelectedCategory().getData()).getId());
            selectedCategoryList.add(((Categorization) categoryBean.getSelectedCategory().getData()));
            findChildren(categoryBean.getSelectedCategory());

        }

        String createWhere = "";
        createWhere = stockService.createWhere(stockParam.isIsWithoutSalePrice(), stockParam.isIsNoneZero(), stockParam.isIsPassiveStock(), selectedCategoryList, stockParam.isIsService());
        if (autoCompleteValue == null) {

            listOfTotals = stockService.totals(createWhere);
            listOfObjects = findall(createWhere);

        } else {

            String where1 = createWhere + " " + gfStockService.createWhere(autoCompleteValue);
            listOfTotals = stockService.totals(where1);
            gfStockService.makeSearch(createWhere, autoCompleteValue, listOfTotals);
            listOfObjects = gfStockService.searchResult;

        }

    }

    public void findChildren(TreeNode node) {
        List<TreeNode> children = node.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {
                selectedCategoryList.add(((Categorization) treeNode.getData()));
                findChildren(treeNode);
            }
        }
    }

    public void findNode(TreeNode node, Categorization categorization) {
        for (TreeNode treeNode : node.getChildren()) {
            if (((Categorization) treeNode.getData()).getId() == categorization.getId()) {
                treeNode.setSelected(true);
                expand(treeNode);
                categoryBean.setSelectedCategory(treeNode);
                break;
            } else {
                findNode(treeNode, categorization);
            }
        }
    }

    public void expand(TreeNode treeNode) {
        if (treeNode.getParent() != null) {
            treeNode.getParent().setExpanded(true);
            expand(treeNode.getParent());
        }
    }

    /**
     * Stok listesini lazy data modele göre çeker.
     *
     * @param where
     * @return
     */
    @Override
    public LazyDataModel<Stock> findall(String where) {
        return new CentrowizLazyDataModel<Stock>() {
            @Override
            public List<Stock> load(int first, int pageSize, String sortField, SortOrder sortOrder, java.util.Map<String, Object> filters) {
                List<Stock> result = stockService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where);
                int count = 0;
                for (Stock stock : listOfTotals) {
                    count += stock.getId();
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    /**
     * Detaylı filtre butonuna basıldığında arama yapar.
     */
    @Override
    public void detailFilter() {

    }

    public void goToProcess() {
        if (selectedObject != null) {
            stockParam.setFilterValue(autoCompleteValue);
            stockParam.setCategory(categoryBean.getSelectedCategory());
            List<Object> list = new ArrayList<>();
            list.add(stockParam);
            list.add(selectedObject);
            marwiz.goToPage("/pages/inventory/stock/stockprocess.xhtml", list, 0, 12);
        }

    }

    public void changeView() {
        isScrollView = !isScrollView;
        RequestContext.getCurrentInstance().update("pngView");
        //  setOrderAfterChange();
    }

    public BigDecimal calculateProfitPercentage(Stock stock) {
        if (stock.getStockInfo().getCurrentSalePrice() == null || stock.getStockInfo().getCurrentSalePrice().compareTo(BigDecimal.ZERO) == 0 || stock.getStockInfo().getCurrentPurchasePrice() == null || stock.getStockInfo().getCurrentPurchasePrice().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return ((stock.getStockInfo().getCurrentSalePrice().subtract(stock.getStockInfo().getCurrentPurchasePrice())).divide(stock.getStockInfo().getCurrentPurchasePrice(), RoundingMode.HALF_EVEN)).multiply(new BigDecimal(100));
        }
    }

    public String calculateTotals(int type) {
        String total = "";

        stockTotals.clear();

        for (Stock u : listOfTotals) {
            switch (type) {
                case 1:
                    //toplam alış miktarı
                    if (stockTotals.containsKey(u.getUnit().getId())) {
                        BigDecimal old = stockTotals.get(u.getUnit().getId());
                        stockTotals.put(u.getUnit().getId(), old.add(u.getStockInfo().getPurchaseCount()));
                    } else {
                        stockTotals.put(u.getUnit().getId(), u.getStockInfo().getPurchaseCount());
                    }
                    break;
                case 2:
                    //toplam satış miktarı
                    if (stockTotals.containsKey(u.getUnit().getId())) {
                        BigDecimal old = stockTotals.get(u.getUnit().getId());
                        stockTotals.put(u.getUnit().getId(), old.add(u.getStockInfo().getSaleCount()));
                    } else {
                        stockTotals.put(u.getUnit().getId(), u.getStockInfo().getSaleCount());
                    }
                    break;
                case 3:
                    // toplam mevcut miktar
                    if (stockTotals.containsKey(u.getUnit().getId())) {
                        BigDecimal old = stockTotals.get(u.getUnit().getId());
                        stockTotals.put(u.getUnit().getId(), old.add(u.getAvailableQuantity()));
                    } else {
                        stockTotals.put(u.getUnit().getId(), u.getAvailableQuantity());
                    }
                    break;
                case 4:
                    // son alış fiyatı kdvli

                    if (u.getStockInfo().getCurrentPurchaseCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentPurchaseCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentPurchaseCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentPurchaseCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), old.add(u.getStockInfo().getCurrentPurchasePriceWithKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), u.getStockInfo().getCurrentPurchasePriceWithKdv());
                    }
                    break;
                case 5:
                    // son alış fiyatı kdvsiz

                    if (u.getStockInfo().getCurrentPurchaseCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentPurchaseCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentPurchaseCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentPurchaseCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), old.add(u.getStockInfo().getCurrentPurchasePrice()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), u.getStockInfo().getCurrentPurchasePrice());
                    }
                    break;
                case 6:
                    // son satış fiyatı kdvli
                    if (u.getStockInfo().getCurrentSaleCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentSaleCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentSaleCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentSaleCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), old.add(u.getStockInfo().getCurrentSalePrice()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), u.getStockInfo().getCurrentSalePrice());
                    }
                    break;
                case 7:
                    // son satış fiyatı kdvsiz
                    if (u.getStockInfo().getCurrentSaleCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentSaleCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentSaleCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentSaleCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), old.add(u.getStockInfo().getCurrentSalePriceWithoutKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), u.getStockInfo().getCurrentSalePriceWithoutKdv());
                    }
                    break;
                case 8:
                    // mevcut alış fiyatı kdvli

                    if (u.getStockInfo().getCurrentPurchaseCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentPurchaseCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentPurchaseCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentPurchaseCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), old.add(u.getStockInfo().getAvailablePurchasePriceWithKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), u.getStockInfo().getAvailablePurchasePriceWithKdv());
                    }
                    break;
                case 9:
                    // mevcut alış fiyatı kdvsiz

                    if (u.getStockInfo().getCurrentPurchaseCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentPurchaseCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentPurchaseCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentPurchaseCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), old.add(u.getStockInfo().getAvailablePurchasePriceWithoutKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentPurchaseCurrency().getId(), u.getStockInfo().getAvailablePurchasePriceWithoutKdv());
                    }
                    break;
                case 10:
                    // mevcut satış fiyatı kdvli
                    if (u.getStockInfo().getCurrentSaleCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentSaleCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentSaleCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentSaleCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), old.add(u.getStockInfo().getAvailableSalePriceWithKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), u.getStockInfo().getAvailableSalePriceWithKdv());
                    }
                    break;
                case 11:
                    // mevcut satış fiyatı kdvsiz
                    if (u.getStockInfo().getCurrentSaleCurrency().getId() == 0) {
                        u.getStockInfo().setCurrentSaleCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                    }
                    if (stockTotals.containsKey(u.getStockInfo().getCurrentSaleCurrency().getId())) {
                        BigDecimal old = stockTotals.get(u.getStockInfo().getCurrentSaleCurrency().getId());
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), old.add(u.getStockInfo().getAvailableSalePriceWithoutKdv()));
                    } else {
                        stockTotals.put(u.getStockInfo().getCurrentSaleCurrency().getId(), u.getStockInfo().getAvailableSalePriceWithoutKdv());
                    }
                    break;
                case 12:
                    //toplam diğer miktar.
                    if (stockTotals.containsKey(u.getUnit().getId())) {
                        BigDecimal old = stockTotals.get(u.getUnit().getId());
                        stockTotals.put(u.getUnit().getId(), old.add(u.getOtherQuantity()));
                    } else {
                        stockTotals.put(u.getUnit().getId(), u.getOtherQuantity());
                    }
                    break;
                default:
                    break;
            }

        }
        int temp = 0;
        for (Map.Entry<Integer, BigDecimal> entry : stockTotals.entrySet()) {
            int comp = entry.getValue().compareTo(BigDecimal.valueOf(0));
            if (comp != 0) {
                if (temp == 0) {
                    temp = 1;

                    if (entry.getKey() != 0) {

                        if (type == 1 || type == 2 || type == 3 || type == 12) {
                            Unit unit = new Unit();
                            for (Unit unit1 : unitList) {
                                if (unit1.getId() == entry.getKey()) {
                                    unit.setSortName(unit1.getSortName());
                                    unit.setUnitRounding(unit1.getUnitRounding());
                                    break;
                                }
                            }
                            total += String.valueOf(unitNumberFormat(unit.getUnitRounding()).format(entry.getValue())) + " " + unit.getSortName();
                        } else {
                            total += String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                        }
                    }
                } else if (entry.getKey() != 0) {

                    if (type == 1 || type == 2 || type == 3 || type == 12) {
                        Unit unit = new Unit();
                        for (Unit unit1 : unitList) {
                            if (unit1.getId() == entry.getKey()) {
                                unit.setSortName(unit1.getSortName());
                                unit.setUnitRounding(unit1.getUnitRounding());
                                break;
                            }
                        }
                        total += " + " + String.valueOf(unitNumberFormat(unit.getUnitRounding()).format(entry.getValue())) + " " + unit.getSortName();
                    } else {
                        total += " + " + String.valueOf(sessionBean.getNumberFormat().format(entry.getValue())) + " " + sessionBean.currencySignOrCode(entry.getKey(), 0);
                    }
                }
            }
        }
        if (total.isEmpty() || total.equals("")) {
            total = "0";
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

    //-----toplu güncelleme için----------
    public void batchUpdate() {

        batchUpdateStock = new Stock();
        findUnit();
        findBrand();
        findTaxDepartment();

        RequestContext.getCurrentInstance().execute("PF('dlg_BatchUpdate').show()");
    }

    public void findBrand() {
        brandList = new ArrayList<>();
        brandList = brandService.findAll(new Item(2));
    }

    public void openDialog() {
        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
    }

    public void updateAllInformation(ActionEvent event) {

        listOfStock.clear();
        if (stockBookCheckboxFilterBean.isAll) {

            if (stockBookCheckboxFilterBean.isIsFilter()) { // filtreyi uygula seçili ise
                Stock stock = new Stock(-1);
                stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
            } else {
                Stock s = new Stock(0);
                if (!stockBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Stock stock = new Stock(0);
                    stock.setName(sessionBean.loc.getString("all"));
                    stockBookCheckboxFilterBean.getTempSelectedDataList().add(0, stock);
                }
            }
        } else if (!stockBookCheckboxFilterBean.isAll) {
            if (!stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0 || stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == -1) {
                    stockBookCheckboxFilterBean.getTempSelectedDataList().remove(stockBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                }
            }
        }
        listOfStock.addAll(stockBookCheckboxFilterBean.getTempSelectedDataList());

        if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0 || stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == -1) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
        }
        RequestContext.getCurrentInstance().update("frmBatchUpdate:txtStock");

    }

    public void updateAllInformation() {
        if (accountBookFilterBean.getSelectedData() != null || accountBookFilterBean.isAll) {
            if (accountBookFilterBean.isAll) {
                Account account = new Account();
                account.setId(0);
                account.setIsPerson(true);
                account.setName(sessionBean.getLoc().getString("nott"));
                batchUpdateStock.setSupplier(account);
            } else {
                batchUpdateStock.setSupplier(accountBookFilterBean.getSelectedData());
            }
            RequestContext.getCurrentInstance().update("frmBatchUpdate:txtSupplier");
            accountBookFilterBean.isAll = false;
            accountBookFilterBean.setSelectedData(null);
        }
    }

    @Override
    public void save() {

        int result = 0;
        result = stockService.batchUpdate(listOfStock, changeField, batchUpdateStock, selectedCategoryList);
        if (result > 0) {
            generalFilter();

            RequestContext.getCurrentInstance().execute("PF('dlg_BatchUpdate').hide()");
        }
        sessionBean.createUpdateMessage(result);
    }

    /**
     * Bu metot merkezi entegrasyona göre tüm birimleri çekmek için kullanılır.
     */
    public void findUnit() {
        unitList = new ArrayList<>();
        unitList = unitService.findAll();
    }

    /**
     * Bu metot merkezi entegrasyona göre tüm vergi gruplarını çeker.
     *
     */
    public void findTaxGroup() {
        taxGroupList = new ArrayList<>();
        taxGroupList = taxGroupService.findAll();
    }

    public void findTaxDepartment() {
        taxDepartmentList = new ArrayList<>();
        taxDepartmentList = taxDepartmentService.listOfTaxDepartment();
    }

    /**
     * Bu metot dosya yükleme dialogu açıldığı zaman verileri sıfırlamak için
     * kullanılır.
     */
    public void resetUpload() {
        clearProducts();
        isOpenTransferBtn = true;
        isOpenCancelBtn = true;
        isOpenSaveBtn = false;
        fileNames = "";
        uploadedFile = null;

    }

    public void clear() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();

        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            context.update("frmStockFileUploadForCentral:pgrFileUpload");
        } else {
            context.update("form:pgrFileUpload");
        }

    }

    public void clearProducts() {
        stockItem = new Stock();
        excelStockList = new ArrayList<>();
        tempProductList = new ArrayList<>();
        tempStockList = new ArrayList<>();

    }

    /**
     * Bu metot excel dosyası yüklemek için dialogu açar
     */
    public void openUploadProcessPage() {
        resetUpload();
        sampleList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        if (sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) { //Merkezi entegrasyon varsa sadece stok kodu ve üretim yeri güncelleme
            context.update("frmToolbarForCentral");
            context.update("frmStockFileUploadForCentral:pgrFileUpload");

            sampleList = stockService.openUploadProcessPageForCentral();

            context.execute("PF('dlg_StockFileUploadForCentral').show();");
        } else {

            context.update("frmtoolbar");
            context.update("form:pgrFileUpload");

            findUnit();
            findTaxGroup();

            sampleList = stockService.openUploadProcessPage();

            context.execute("PF('dlg_stockfileupload').show();");
        }

    }

    public void handleFileUploadFile(FileUploadEvent event) throws IOException {
        resetUpload(); // uploaddan önce tüm kayıtları sıfırlar

        uploadedFile = event.getFile();
        fileName = uploadedFile.getFileName();
        String s = new String(fileName.getBytes(Charset.defaultCharset()), "UTF-8"); // gelen türkçe karakterli excel dosyasının adını utf8 formatında düzenler.
        String substringData = "";
        if (s.length() > 20) { // eğer gelen fileName değeri 20 den büyük ise substring yapılır.
            substringData = s.substring(0, 20);
        } else {
            substringData = s;
        }
        fileNames = substringData.toLowerCase();

        isOpenTransferBtn = false;
        isOpenCancelBtn = false;

        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

    }

    /**
     * Bu metot aktar butonuna basınca çalışır.Excelden okuduğu verileri
     * istenilen formda geriye döndürür.
     */
    public void convertUploadData() throws IOException {
        clearProducts();
        RequestContext context = RequestContext.getCurrentInstance();

        excelStockList = stockService.processUploadFile(uploadedFile.getInputstream());

        tempProductList.addAll(excelStockList);
        tempStockList.addAll(excelStockList);

        int count = 0;
        for (StockUpload obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getExcelDataType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isOpenSaveBtn = true;
        }
        context.execute("PF('dlg_productView').show();");
        context.update("frmProductView:dtbProductView");
        context.update("btnSave");

        isOpenCancelBtn = false;

    }

    public void saveProduct() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();
        for (StockUpload stock : tempProductList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }
        String resultJson = stockService.importProductList(excelStockList);
        excelStockList.clear();
        excelStockList.addAll(tempProductList);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");
            generalFilter();

        } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
            JSONArray jsonArr = new JSONArray(resultJson);
            for (int m = 0; m < jsonArr.length(); m++) {
                ErrorItem item = new ErrorItem();
                String jsonBarcode = jsonArr.getJSONObject(m).getString("barcode");
                int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorCode");
                item.setBarcode(jsonBarcode);
                item.setErrorCode(jsonErrorCode);
                switch (item.getErrorCode()) {
                    case -1:
                        item.setErrorString(sessionBean.getLoc().getString("unitnotavailableinsystem")); // sessionBean.getLoc().getString("")
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("salestaxgroupisnotavailableinthesystem"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("purchasetaxgroupnotavailableonsystem"));
                        break;
                    case -4:
                        item.setErrorString(sessionBean.getLoc().getString("brandnotavailableinsystem")); // sessionBean.getLoc().getString("")
                        break;
                    case -5:
                        item.setErrorString(sessionBean.getLoc().getString("suppliernotavailableinsystem"));
                        break;
                    case -6:
                        item.setErrorString(sessionBean.getLoc().getString("countrynotavailableinsystem"));
                        break;
                    case -7:
                        item.setErrorString(sessionBean.getLoc().getString("alternativebarcodesystemavailable"));
                        break;
                    case -8:
                        item.setErrorString(sessionBean.getLoc().getString("parentcategorynotavailableinsystem"));
                        break;
                    case -9:
                        item.setErrorString(sessionBean.getLoc().getString("subcategorynotavailableinsystem"));
                        break;
                    case -10:
                        item.setErrorString(sessionBean.getLoc().getString("taxdepartmentnotavailableinsystem"));
                        break;
                    case -11:
                        item.setErrorString(sessionBean.getLoc().getString("stocktypenotavailableinsystem"));
                        break;
                    case -12:
                        item.setErrorString(sessionBean.getLoc().getString("stocknamecannotbeempty"));
                        break;
                    case -13:
                        item.setErrorString(sessionBean.getLoc().getString("stockcodecannotbeempty"));
                        break;
                    case -14:
                        item.setErrorString(sessionBean.getLoc().getString("stockunitcannotbeempty"));
                        break;
                    case -15:
                        item.setErrorString(sessionBean.getLoc().getString("isservicecannotbeempty"));
                        break;
                    case -16:
                        item.setErrorString(sessionBean.getLoc().getString("quicksalecannotbeempty"));
                        break;
                    case -17:
                        item.setErrorString(sessionBean.getLoc().getString("departmantcannotbeempty"));
                        break;
                    default:
                        break;
                }

                errorList.add(item);
            }
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            if (jsonArr.length() == excelStockList.size()) {
                message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            context.update("grwProcessMessage");
            context.execute("PF('dlg_productView').hide();");
            context.execute("PF('dlg_productErrorView').show();");
            context.update("frmProductErrorView:dtbProductErrorView");
            generalFilter();

        }
    }

    /**
     * Bu metot hatalı kayıtları göstermek/ gizlemek durumunda çalışır.Listeyi
     * günceller
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<StockUpload> iterator = tempStockList.iterator(); iterator.hasNext();) {
                StockUpload value = iterator.next();
                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }
            excelStockList.clear();
            excelStockList.addAll(tempStockList);
        } else {
            excelStockList.clear();
            excelStockList.addAll(tempProductList);
        }
        context.update("frmProductView:dtbProductView");
    }

    /**
     * Bu metot sistemde tanımlı olan birim listesini çeker.Dailog üzerinde
     * listeler
     */
    public void showUnitList() {
        RequestContext context = RequestContext.getCurrentInstance();
        findUnit();
        context.execute("PF('dlgunit_detail').show()");
        context.update("dlgunit_detail");

    }

    /**
     * Bu metot sistemde tanımlı olan vergi grubu listesini çeler.Dailog
     * üzerinde listeler.
     */
    public void showTaxGroupList() {
        RequestContext context = RequestContext.getCurrentInstance();

        findTaxGroup();
        context.execute("PF('dlgtaxgroup_detail').show()");
        context.update("dlgtaxgroup_detail");
    }

    /**
     * Bu metot sistemde tanımlı olan markaları çeker.Dialog üzerinde listeler.
     */
    public void showBrandList() {
        RequestContext context = RequestContext.getCurrentInstance();

        findBrand(); // marka listesini çeker.
        context.execute("PF('dlgbrand_detail').show()");
        context.update("dlgbrand_detail");
    }

    public void showSupplierList() {
        RequestContext context = RequestContext.getCurrentInstance();

        Account account = new Account();
        account.setId(0);
        account.setName(sessionBean.getLoc().getString("nott"));
        accountList = new ArrayList<>();
        accountList.add(account);
        accountList = accountService.findSupplier();

        context.execute("PF('dlgaccount_detail').show()");
        context.update("dlgaccount_detail");
    }

    public void showCountryList() {
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgcountry_detail').show()");
        context.update("dlgcountry_detail");

    }

    public void showCategoryList() {
        RequestContext context = RequestContext.getCurrentInstance();
        categoryList = stockCategorizationService.listOfCategorization();
        List<Categorization> tempList = new ArrayList<>();
        tempList.addAll(categoryList);

        for (Iterator<Categorization> iterator = categoryList.iterator(); iterator.hasNext();) { // child
            Categorization next = iterator.next();
            for (Categorization categorization : tempList) { // parent      // tek kırılım getmek için kalanları listeden çıkarttık
                if (next.getId() == categorization.getParentId().getId()) {
                    iterator.remove();
                }
            }
        }
        context.execute("PF('dlgcategory_detail').show()");
        context.update("dlgcategory_detail");
    }

    public void showDepartmentList() {
        findTaxDepartment();
        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgdepartment_detail').show()");
        context.update("dlgDepartmentDetail");
    }

    public void showStockTypeList() {

        stockTypeList = new ArrayList<>();
        Stock stock = new Stock();

        stock.setStockType_id(1);
        stock.setName(sessionBean.getLoc().getString("normal"));
        stockTypeList.add(stock);
        stock = new Stock();

        stock.setStockType_id(2);
        stock.setName(sessionBean.getLoc().getString("washing"));
        stockTypeList.add(stock);
        stock = new Stock();

        stock.setStockType_id(3);
        stock.setName(specialItem == 1 ? sessionBean.getLoc().getString("starbucks") : sessionBean.getLoc().getString("coffee"));
        stockTypeList.add(stock);
        stock = new Stock();

        RequestContext context = RequestContext.getCurrentInstance();
        context.execute("PF('dlgstocktype_detail').show()");
    }

    public void showWarn() {
        if (listOfObjects.getRowCount() > 200000) {

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, sessionBean.loc.getString("warning"), sessionBean.getLoc().getString("youdontexportrecordwhichisbiggerthan200000")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }

    }

    public void createPdf() {

        String createWhere = "";
        createWhere = stockService.createWhere(stockParam.isIsWithoutSalePrice(), stockParam.isIsNoneZero(), stockParam.isIsPassiveStock(), selectedCategoryList, stockParam.isIsService());
        if (autoCompleteValue != null) {
            createWhere = createWhere + " " + gfStockService.createWhere(autoCompleteValue);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmBarcode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmBarcode" : "frmStock:dtbStockNoIntegration:clmBarcode")) {
            toogleList.set(0, true);
        } else {
            toogleList.set(0, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmName" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmName" : "frmStock:dtbStockNoIntegration:clmName")) {
            toogleList.set(1, true);
        } else {
            toogleList.set(1, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCode" : "frmStock:dtbStockNoIntegration:clmCode")) {
            toogleList.set(2, true);
        } else {
            toogleList.set(2, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCenterStockCode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCenterStockCode" : "frmStock:dtbStockNoIntegration:clmCenterStockCode")) {
            toogleList.set(3, true);
        } else {
            toogleList.set(3, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmUnit" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmUnit" : "frmStock:dtbStockNoIntegration:clmUnit")) {
            toogleList.set(4, true);
        } else {
            toogleList.set(4, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmDepartmentName" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmDepartmentName" : "frmStock:dtbStockNoIntegration:clmDepartmentName")) {
            toogleList.set(5, true);
        } else {
            toogleList.set(5, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmBrand" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmBrand" : "frmStock:dtbStockNoIntegration:clmBrand")) {
            toogleList.set(6, true);
        } else {
            toogleList.set(6, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmSupplier" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmSupplier" : "frmStock:dtbStockNoIntegration:clmSupplier")) {
            toogleList.set(7, true);
        } else {
            toogleList.set(7, false);
        }
        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            toogleList.set(8, false);
            toogleList.set(9, false);
            toogleList.set(10, false);
            toogleList.set(11, false);
        } else {
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCentralSupplier" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCentralSupplier" : "frmStock:dtbStockNoIntegration:clmCentralSupplier")) {

                if (sessionBean.getUser().getLastBranch().getConceptType() != 1 && sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    toogleList.set(8, false);
                } else {
                    toogleList.set(8, true);
                }
            } else {
                toogleList.set(8, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmRecommendedPurchasePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmRecommendedPurchasePrice" : "frmStock:dtbStockNoIntegration:clmRecommendedPurchasePrice")) {
                toogleList.set(9, true);
            } else {
                toogleList.set(9, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmRecommendedSalePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmRecommendedSalePrice" : "frmStock:dtbStockNoIntegration:clmRecommendedSalePrice")) {
                toogleList.set(10, true);
            } else {
                toogleList.set(10, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmMandatorySalePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmMandatorySalePrce" : "frmStock:dtbStockNoIntegration:clmMandatorySalePrice")) {
                toogleList.set(11, true);
            } else {
                toogleList.set(11, false);
            }
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmPurchaseQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmPurchaseQuantity" : "frmStock:dtbStockNoIntegration:clmPurchaseQuantity")) {
            toogleList.set(12, true);
        } else {
            toogleList.set(12, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmSaleQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmSaleQuantity" : "frmStock:dtbStockNoIntegration:clmSaleQuantity")) {
            toogleList.set(13, true);
        } else {
            toogleList.set(13, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmOtherQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmOtherQuantity" : "frmStock:dtbStockNoIntegration:clmOtherQuantity")) {
            toogleList.set(14, true);
        } else {
            toogleList.set(14, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableQuantity" : "frmStock:dtbStockNoIntegration:clmAvailableQuantity")) {
            toogleList.set(15, true);
        } else {
            toogleList.set(15, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentPurchasePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentPurchasePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmCurrentPurchasePriceWithKdv")) {
            toogleList.set(16, true);
        } else {
            toogleList.set(16, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentPurchasePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentPurchasePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmCurrentPurchasePriceWithoutKdv")) {
            toogleList.set(17, true);
        } else {
            toogleList.set(17, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentSalePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentSalePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmCurrentSalePriceWithKdv")) {
            toogleList.set(18, true);
        } else {
            toogleList.set(18, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentSalePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentSalePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmCurrentSalePriceWithoutKdv")) {
            toogleList.set(19, true);
        } else {
            toogleList.set(19, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailablePurchasePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailablePurchasePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmAvailablePurchasePriceWithKdv")) {
            toogleList.set(20, true);
        } else {
            toogleList.set(20, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailablePurchasePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailablePurchasePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmAvailablePurchasePriceWithoutKdv")) {
            toogleList.set(21, true);
        } else {
            toogleList.set(21, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableSalePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableSalePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmAvailableSalePriceWithKdv")) {
            toogleList.set(22, true);
        } else {
            toogleList.set(22, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableSalePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableSalePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmAvailableSalePriceWithoutKdv")) {
            toogleList.set(23, true);
        } else {
            toogleList.set(23, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmProfitPercentage" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmProfitPercentage" : "frmStock:dtbStockNoIntegration:clmProfitPercentage")) {
            toogleList.set(24, true);
        } else {
            toogleList.set(24, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmTaxGroup" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmTaxGroup" : "frmStock:dtbStockNoIntegration:clmTaxGroup")) {
            toogleList.set(25, true);
        } else {
            toogleList.set(25, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmPurchaseTaxGroup" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmPurchaseTaxGroup" : "frmStock:dtbStockNoIntegration:clmPurchaseTaxGroup")) {
            toogleList.set(26, true);
        } else {
            toogleList.set(26, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCategory" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCategory" : "frmStock:dtbStockNoIntegration:clmCategory")) {
            toogleList.set(27, true);
        } else {
            toogleList.set(27, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmStatus" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmStatus" : "frmStock:dtbStockNoIntegration:clmStatus")) {
            toogleList.set(28, true);
        } else {
            toogleList.set(28, false);
        }

        stockService.exportPdf(createWhere, toogleList);

    }

    public void createExcel() {

        String createWhere = "";
        createWhere = stockService.createWhere(stockParam.isIsWithoutSalePrice(), stockParam.isIsNoneZero(), stockParam.isIsPassiveStock(), selectedCategoryList, stockParam.isIsService());
        if (autoCompleteValue != null) {
            createWhere = createWhere + " " + gfStockService.createWhere(autoCompleteValue);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmBarcode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmBarcode" : "frmStock:dtbStockNoIntegration:clmBarcode")) {
            toogleList.set(0, true);
        } else {
            toogleList.set(0, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmName" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmName" : "frmStock:dtbStockNoIntegration:clmName")) {
            toogleList.set(1, true);
        } else {
            toogleList.set(1, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCode" : "frmStock:dtbStockNoIntegration:clmCode")) {
            toogleList.set(2, true);
        } else {
            toogleList.set(2, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCenterStockCode" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCenterStockCode" : "frmStock:dtbStockNoIntegration:clmCenterStockCode")) {
            toogleList.set(3, true);
        } else {
            toogleList.set(3, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmUnit" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmUnit" : "frmStock:dtbStockNoIntegration:clmUnit")) {
            toogleList.set(4, true);
        } else {
            toogleList.set(4, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmDepartmentName" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmDepartmentName" : "frmStock:dtbStockNoIntegration:clmDepartmentName")) {
            toogleList.set(5, true);
        } else {
            toogleList.set(5, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmBrand" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmBrand" : "frmStock:dtbStockNoIntegration:clmBrand")) {
            toogleList.set(6, true);
        } else {
            toogleList.set(6, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmSupplier" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmSupplier" : "frmStock:dtbStockNoIntegration:clmSupplier")) {
            toogleList.set(7, true);
        } else {
            toogleList.set(7, false);
        }
        if (!sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
            toogleList.set(8, false);
            toogleList.set(9, false);
            toogleList.set(10, false);
            toogleList.set(11, false);
        } else {

            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCentralSupplier" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCentralSupplier" : "frmStock:dtbStockNoIntegration:clmCentralSupplier")) {
                if (sessionBean.getUser().getLastBranch().getConceptType() != 1 && sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration()) {
                    toogleList.set(8, false);
                } else {
                    toogleList.set(8, true);
                }

            } else {
                toogleList.set(8, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmRecommendedPurchasePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmRecommendedPurchasePrice" : "frmStock:dtbStockNoIntegration:clmRecommendedPurchasePrice")) {
                toogleList.set(9, true);
            } else {
                toogleList.set(9, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmRecommendedSalePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmRecommendedSalePrice" : "frmStock:dtbStockNoIntegration:clmRecommendedSalePrice")) {
                toogleList.set(10, true);
            } else {
                toogleList.set(10, false);
            }
            if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmMandatorySalePrice" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmMandatorySalePrce" : "frmStock:dtbStockNoIntegration:clmMandatorySalePrice")) {
                toogleList.set(11, true);
            } else {
                toogleList.set(11, false);
            }
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmPurchaseQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmPurchaseQuantity" : "frmStock:dtbStockNoIntegration:clmPurchaseQuantity")) {
            toogleList.set(12, true);
        } else {
            toogleList.set(12, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmSaleQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmSaleQuantity" : "frmStock:dtbStockNoIntegration:clmSaleQuantity")) {
            toogleList.set(13, true);
        } else {
            toogleList.set(13, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmOtherQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmOtherQuantity" : "frmStock:dtbStockNoIntegration:clmOtherQuantity")) {
            toogleList.set(14, true);
        } else {
            toogleList.set(14, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableQuantity" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableQuantity" : "frmStock:dtbStockNoIntegration:clmAvailableQuantity")) {
            toogleList.set(15, true);
        } else {
            toogleList.set(15, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentPurchasePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentPurchasePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmCurrentPurchasePriceWithKdv")) {
            toogleList.set(16, true);
        } else {
            toogleList.set(16, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentPurchasePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentPurchasePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmCurrentPurchasePriceWithoutKdv")) {
            toogleList.set(17, true);
        } else {
            toogleList.set(17, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentSalePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentSalePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmCurrentSalePriceWithKdv")) {
            toogleList.set(18, true);
        } else {
            toogleList.set(18, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCurrentSalePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCurrentSalePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmCurrentSalePriceWithoutKdv")) {
            toogleList.set(19, true);
        } else {
            toogleList.set(19, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailablePurchasePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailablePurchasePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmAvailablePurchasePriceWithKdv")) {
            toogleList.set(20, true);
        } else {
            toogleList.set(20, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailablePurchasePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailablePurchasePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmAvailablePurchasePriceWithoutKdv")) {
            toogleList.set(21, true);
        } else {
            toogleList.set(21, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableSalePriceWithKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableSalePriceWithKdv" : "frmStock:dtbStockNoIntegration:clmAvailableSalePriceWithKdv")) {
            toogleList.set(22, true);
        } else {
            toogleList.set(22, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmAvailableSalePriceWithoutKdv" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmAvailableSalePriceWithoutKdv" : "frmStock:dtbStockNoIntegration:clmAvailableSalePriceWithoutKdv")) {
            toogleList.set(23, true);
        } else {
            toogleList.set(23, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmProfitPercentage" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmProfitPercentage" : "frmStock:dtbStockNoIntegration:clmProfitPercentage")) {
            toogleList.set(24, true);
        } else {
            toogleList.set(24, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmTaxGroup" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmTaxGroup" : "frmStock:dtbStockNoIntegration:clmTaxGroup")) {
            toogleList.set(25, true);
        } else {
            toogleList.set(25, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmPurchaseTaxGroup" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmPurchaseTaxGroup" : "frmStock:dtbStockNoIntegration:clmPurchaseTaxGroup")) {
            toogleList.set(26, true);
        } else {
            toogleList.set(26, false);
        }

        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmCategory" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmCategory" : "frmStock:dtbStockNoIntegration:clmCategory")) {
            toogleList.set(27, true);
        } else {
            toogleList.set(27, false);
        }
        if (getColumnVisibility(sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() == 1 ? "frmStock:dtbStock:clmStatus" : sessionBean.getUser().getLastBranchSetting().isIsCentralIntegration() && sessionBean.getUser().getLastBranch().getConceptType() != 1 ? "frmStock:dtbStockFull:clmStatus" : "frmStock:dtbStockNoIntegration:clmStatus")) {
            toogleList.set(28, true);
        } else {
            toogleList.set(28, false);
        }

        stockService.exportExcel(createWhere, toogleList);

    }

    public void showLimitMessege() {
        FacesMessage message = new FacesMessage();

        message.setSummary(sessionBean1.loc.getString("notification"));

        message.setDetail(sessionBean1.loc.getString("youdontexportrecordwhichisbiggerthan60000"));

        message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("grwProcessMessage");
    }

    public void waitToExportPdf() throws InterruptedException {
        try {
            Double d = (double) listOfObjects.getRowCount() / 6;
            TimeUnit.MILLISECONDS.sleep(d.intValue());
        } catch (Exception e) {
        }

    }

    public void showLimitMessegeForExcel() {
        FacesMessage message = new FacesMessage();

        message.setSummary(sessionBean1.loc.getString("notification"));

        message.setDetail(sessionBean1.loc.getString("youdontexportrecordwhichisbiggerthan300000"));

        message.setSeverity(FacesMessage.SEVERITY_WARN);
        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("grwProcessMessage");
    }

    public void waitToExportExcel() throws InterruptedException {
        try {
            Double d = (double) listOfObjects.getRowCount() / 2;
            TimeUnit.MILLISECONDS.sleep(d.intValue());
        } catch (Exception e) {
        }

    }

    public void convertUploadDataForCentral() throws IOException {
        clearProducts();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList = stockService.processUploadFileForCentral(uploadedFile.getInputstream());

        tempProductList.addAll(excelStockList);
        tempStockList.addAll(excelStockList);

        int count = 0;
        for (StockUpload obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getExcelDataType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isOpenSaveBtn = true;
        }
        context.execute("PF('dlg_ProductViewForCentral').show();");
        context.update("frmProductViewForCentral:dtbProductView");
        context.update("frmToolbarForCentral:btnSave");

        isOpenCancelBtn = false;
    }

    /**
     * Bu metot hatalı kayıtları göstermek/ gizlemek durumunda çalışır.Listeyi
     * günceller
     */
    public void showErrorProductListForCentral() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<StockUpload> iterator = tempStockList.iterator(); iterator.hasNext();) {
                StockUpload value = iterator.next();
                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }
            excelStockList.clear();
            excelStockList.addAll(tempStockList);
        } else {
            excelStockList.clear();
            excelStockList.addAll(tempProductList);
        }
        context.update("frmProductViewForCentral:dtbProductView");
    }

    public void saveProductForCentral() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();
        for (StockUpload stock : tempProductList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }
        String resultJson = stockService.importProductListForCentral(excelStockList);

        excelStockList.clear();
        excelStockList.addAll(tempProductList);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_ProductViewForCentral').hide();");
            generalFilter();

        } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
            JSONArray jsonArr = new JSONArray(resultJson);
            for (int m = 0; m < jsonArr.length(); m++) {
                ErrorItem item = new ErrorItem();
                String jsonBarcode = jsonArr.getJSONObject(m).getString("barcode");
                int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorCode");
                item.setBarcode(jsonBarcode);
                item.setErrorCode(jsonErrorCode);
                switch (item.getErrorCode()) {
                    case -1:
                        item.setErrorString(sessionBean.getLoc().getString("barcodenotavailableinsystem"));
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("countrynotavailableinsystem"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("taxdepartmentnotavailableinsystem"));
                        break;
                    case -4:
                        item.setErrorString(sessionBean.getLoc().getString("parentcategorynotavailableinsystem"));
                        break;
                    case -5:
                        item.setErrorString(sessionBean.getLoc().getString("subcategorynotavailableinsystem"));
                    case -6:
                        item.setErrorString(sessionBean.getLoc().getString("suppliernotavailableinsystem"));
                        break;
                    default:
                        break;
                }

                errorList.add(item);
            }
            FacesMessage message = new FacesMessage();
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(sessionBean.getLoc().getString("warning"));
            if (jsonArr.length() == excelStockList.size()) {
                message.setDetail(sessionBean.getLoc().getString("failedtotransferbecauseallrecordsinthefileareincorrect"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                message.setDetail(sessionBean.getLoc().getString("somerecordscoludnotbetransferredduetolackofdata"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            context.update("grwProcessMessage");
            context.execute("PF('dlg_ProductViewForCentral').hide();");
            context.execute("PF('dlg_productErrorView').show();");
            context.update("frmProductErrorView:dtbProductErrorView");
            generalFilter();

        }
    }

    public void downloadSampleList() {
        stockService.downloadSampleList(sampleList);
    }

}
