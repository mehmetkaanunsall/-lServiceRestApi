/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.02.2018 09:15:46
 */
package com.mepsan.marwiz.general.categorization.presentation;

import com.mepsan.marwiz.general.categorization.business.ICategorizationService;
import com.mepsan.marwiz.general.common.AccountBookCheckboxFilterBean;
import com.mepsan.marwiz.general.common.StockBookCheckboxFilterBean;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.model.inventory.Stock;
import com.mepsan.marwiz.general.model.system.Item;
import com.mepsan.marwiz.general.model.wot.AuthenticationLists;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class CategoryBean extends AuthenticationLists {

    @ManagedProperty(value = "#{categorizationService}")
    private ICategorizationService categorizationService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{stockBookCheckboxFilterBean}")
    private StockBookCheckboxFilterBean stockBookCheckboxFilterBean;
    private List<Stock> listOfStock;

    @ManagedProperty(value = "#{accountBookCheckboxFilterBean}")
    private AccountBookCheckboxFilterBean accountBookCheckboxFilterBean;
    private List<Account> listOfAccount;

    private List<Categorization> listOfSelectedCategory;

    private TreeNode root;
    private TreeNode selectedCategory;
    private Categorization category, selectedData;
    private TreeNode findTreeNode;
    private Categorization removedCategory;
    private int processType;
    private boolean isSelectAllCategory;
    private boolean expanded;

    private String itemId;

    private List<Stock> sampleList;
    private UploadedFile uploadedFile;
    private String fileNames, fileName;
    private boolean isOpenTransferBtn, isOpenCancelBtn, isOpenSaveBtn, isOpenErrorData;
    private List<Stock> excelStockList;
    private List<Stock> tempErrorList;
    private List<Stock> tempStockList;
    private Stock stock;
    private List<ErrorItem> errorList;

    public void setCategorizationService(ICategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(TreeNode selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public Categorization getCategory() {
        return category;
    }

    public void setCategory(Categorization category) {
        this.category = category;
    }

    public Categorization getSelectedData() {
        return selectedData;
    }

    public void setSelectedData(Categorization selectedData) {
        this.selectedData = selectedData;
    }

    public TreeNode getFindTreeNode() {
        return findTreeNode;
    }

    public void setFindTreeNode(TreeNode findTreeNode) {
        this.findTreeNode = findTreeNode;
    }

    public Categorization getRemovedCategory() {
        return removedCategory;
    }

    public void setRemovedCategory(Categorization removedCategory) {
        this.removedCategory = removedCategory;
    }

    public int getProcessType() {
        return processType;
    }

    public void setProcessType(int processType) {
        this.processType = processType;
    }

    public boolean isIsSelectAllCategory() {
        return isSelectAllCategory;
    }

    public void setIsSelectAllCategory(boolean isSelectAllCategory) {
        this.isSelectAllCategory = isSelectAllCategory;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public List<Stock> getListOfStock() {
        return listOfStock;
    }

    public void setListOfStock(List<Stock> listOfStock) {
        this.listOfStock = listOfStock;
    }

    public void setStockBookCheckboxFilterBean(StockBookCheckboxFilterBean stockBookCheckboxFilterBean) {
        this.stockBookCheckboxFilterBean = stockBookCheckboxFilterBean;
    }

    public List<Account> getListOfAccount() {
        return listOfAccount;
    }

    public void setListOfAccount(List<Account> listOfAccount) {
        this.listOfAccount = listOfAccount;
    }

    public void setAccountBookCheckboxFilterBean(AccountBookCheckboxFilterBean accountBookCheckboxFilterBean) {
        this.accountBookCheckboxFilterBean = accountBookCheckboxFilterBean;
    }

    public List<Categorization> getListOfSelectedCategory() {
        return listOfSelectedCategory;
    }

    public void setListOfSelectedCategory(List<Categorization> listOfSelectedCategory) {
        this.listOfSelectedCategory = listOfSelectedCategory;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public List<Stock> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<Stock> sampleList) {
        this.sampleList = sampleList;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isIsOpenTransferBtn() {
        return isOpenTransferBtn;
    }

    public void setIsOpenTransferBtn(boolean isOpenTransferBtn) {
        this.isOpenTransferBtn = isOpenTransferBtn;
    }

    public boolean isIsOpenCancelBtn() {
        return isOpenCancelBtn;
    }

    public void setIsOpenCancelBtn(boolean isOpenCancelBtn) {
        this.isOpenCancelBtn = isOpenCancelBtn;
    }

    public boolean isIsOpenSaveBtn() {
        return isOpenSaveBtn;
    }

    public void setIsOpenSaveBtn(boolean isOpenSaveBtn) {
        this.isOpenSaveBtn = isOpenSaveBtn;
    }

    public boolean isIsOpenErrorData() {
        return isOpenErrorData;
    }

    public void setIsOpenErrorData(boolean isOpenErrorData) {
        this.isOpenErrorData = isOpenErrorData;
    }

    public List<Stock> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<Stock> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public List<Stock> getTempErrorList() {
        return tempErrorList;
    }

    public void setTempErrorList(List<Stock> tempErrorList) {
        this.tempErrorList = tempErrorList;
    }

    public List<Stock> getTempStockList() {
        return tempStockList;
    }

    public void setTempStockList(List<Stock> tempStockList) {
        this.tempStockList = tempStockList;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    @PostConstruct
    public void init() {
        System.out.println("CategoryBean");

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        itemId = (String) request.getAttribute("itemId");
        root = createCategoryTree();

        Long btnParent = (Long) request.getAttribute("btnCategoryBtnParent");
        Long btnChild = (Long) request.getAttribute("btnCategoryBtnChild");
        Long btnEdit = (Long) request.getAttribute("btnCategoryBtnEdit");
        Long btnDelete = (Long) request.getAttribute("btnCategoryBtnDelete");
        Long btnAddToItem = (Long) request.getAttribute("btnCategoryBtnAddToItem");
        if (btnParent == null) {
            btnParent = new Long(0);
        }
        if (btnChild == null) {
            btnChild = new Long(0);
        }
        if (btnEdit == null) {
            btnEdit = new Long(0);
        }
        if (btnDelete == null) {
            btnDelete = new Long(0);
        }
        if (btnAddToItem == null) {
            btnAddToItem = new Long(0);
        }

        setListBtn(sessionBean.checkAuthority(new int[]{(int) (long) btnParent, (int) (long) btnChild, (int) (long) btnEdit, (int) (long) btnDelete, (int) (long) btnAddToItem}, 0));

    }

    public void createDialog(int type) {
        processType = type;
        switch (type) {
            case 1:
                /*   ana categori ekleme      */
                category = new Categorization();
                category.setItem(new Item(Integer.valueOf(itemId)));
                break;
            case 2:
                /*  çocuk ekleme  */
                category = new Categorization();
                category.setItem(new Item(Integer.valueOf(itemId)));
                category.setParentId(selectedData);
                break;
            default:/* düzenleme   */
                category = selectedData;
                break;
        }

        RequestContext.getCurrentInstance().execute("PF('dlg_category').show();");
    }

    public void addToItem() {
        processType = 4;
        listOfStock = new ArrayList<>();
        stockBookCheckboxFilterBean.setSelectedCount("");
        listOfAccount = new ArrayList<>();
        accountBookCheckboxFilterBean.setSelectedCount("");
        listOfSelectedCategory = new ArrayList<>();
        listOfSelectedCategory.add(selectedData);
        convertCategoryToTreeeNode(root, selectedData);
        selectChild(findTreeNode);

        RequestContext.getCurrentInstance().execute("PF('dlg_addtoitem').show();");
    }

    /**
     * Select eventi ile seçilen kategorinin childrenlarını bulup dataliste
     * ekleyen fonksiyondur.
     *
     * @param node seçilen kategori
     */
    public void selectChild(TreeNode node) {
        List<TreeNode> children = node.getChildren();

        if (!children.isEmpty()) {

            for (TreeNode treeNode : children) {
                if (!listOfSelectedCategory.contains((Categorization) treeNode.getData())) {
                    listOfSelectedCategory.add((Categorization) treeNode.getData());
                }

                selectChild(treeNode);
            }
        }

    }

    public void updateAllInformation(ActionEvent event) {
        if ("2".equals(itemId)) {
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

        } else if ("3".equals(itemId) || "26".equals(itemId)) {
            listOfAccount.clear();
            if (accountBookCheckboxFilterBean.isAll) {
                Account s = new Account(0);
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().contains(s)) {
                    Account a = new Account(0);
                    a.setName(sessionBean.loc.getString("all"));
                    accountBookCheckboxFilterBean.getTempSelectedDataList().add(0, a);
                }
            } else if (!accountBookCheckboxFilterBean.isAll) {
                if (!accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                    if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                        accountBookCheckboxFilterBean.getTempSelectedDataList().remove(accountBookCheckboxFilterBean.getTempSelectedDataList().get(0));
                    }
                }
            }
            listOfAccount.addAll(accountBookCheckboxFilterBean.getTempSelectedDataList());

            if (accountBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else if (accountBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
                accountBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
            } else {
                accountBookCheckboxFilterBean.setSelectedCount(accountBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("account") + " " + sessionBean.loc.getString("selected"));
            }
        }

        RequestContext.getCurrentInstance().update("frmAddToItem:txtItem");
    }

    public void openDialog() {
        if ("2".equals(itemId)) {
            stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfStock.isEmpty()) {
                if (listOfStock.get(0).getId() == 0) {
                    stockBookCheckboxFilterBean.isAll = true;
                } else {
                    stockBookCheckboxFilterBean.isAll = false;
                }
            }
            stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        } else if ("3".equals(itemId) || "26".equals(itemId)) {
            accountBookCheckboxFilterBean.getTempSelectedDataList().clear();
            if (!listOfAccount.isEmpty()) {
                if (listOfAccount.get(0).getId() == 0) {
                    accountBookCheckboxFilterBean.isAll = true;
                } else {
                    accountBookCheckboxFilterBean.isAll = false;
                }
            }
            accountBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfAccount);
        }

    }

    /**
     * TreeTable üzerinde kullanılan açma kapama ikonu için yazılmıştır.Veriler
     * sayfa ilk açıldığında kapalı gelir.
     */
    public void expanded() {
        if (expanded) {
            expanded = false;
        } else {
            expanded = true;
        }
        findChildren(root);
    }

    public void findChildren(TreeNode node) {
        for (TreeNode treeNode : node.getChildren()) {
            treeNode.setExpanded(expanded);
            findChildren(treeNode);
        }
    }

    public TreeNode createCategoryTree() {
        category = new Categorization();
        category.setItem(new Item(Integer.valueOf(itemId)));
        root = new DefaultTreeNode(new Categorization(), null);
        root.setExpanded(true);
        List<Categorization> listCegorization = categorizationService.listCategorization(category);
        for (Categorization categorization : listCegorization) {
            if (categorization.getParentId().getId() == 0) {
                DefaultTreeNode parentTreeNode = new DefaultTreeNode(categorization, root);
                findChildren(parentTreeNode, listCegorization);
            }
        }

        return root;
    }

    public void findChildren(DefaultTreeNode treeNode, List<Categorization> list) {

        for (Categorization categorization : list) {
            if (categorization.getParentId().getId() != 0) {
                if (categorization.getParentId().getId() == ((Categorization) treeNode.getData()).getId()) {
                    DefaultTreeNode childTreeNode = new DefaultTreeNode(categorization, treeNode);
                    findChildren(childTreeNode, list);
                }
            }
        }
    }

    public void save() {
        int result = 0;

        for (TreeNode categorization : root.getChildren()) {
            if (categorization.getData().toString().equalsIgnoreCase(category.getName()) && ((Categorization) categorization.getData()).getId() != category.getId()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("error"), sessionBean.loc.getString("categoryalreadyavailable")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                return;
            }
        }
        switch (processType) {

            case 1:
                /*   ana categori ekleme      */

                result = categorizationService.create(category);
                if (result > 0) {
                    category.setId(result);
                    DefaultTreeNode nodeParent = new DefaultTreeNode(category, root);
                    nodeParent.setExpanded(true);
                    RequestContext.getCurrentInstance().execute("PF('dlg_category').hide();");
                }
                break;
            case 2:
                /*  çocuk ekleme  */
                result = categorizationService.create(category);
                if (result > 0) {
                    category.setId(result);
                    convertCategoryToTreeeNode(root, selectedData);
                    DefaultTreeNode nodeChild = new DefaultTreeNode(category, findTreeNode);
                    nodeChild.setExpanded(true);
                    RequestContext.getCurrentInstance().execute("PF('dlg_category').hide();");
                }

                break;
            case 3:/* düzenleme   */
                result = categorizationService.update(category);
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_category').hide();");
                }
                break;
            case 4:/* item a ekleme   */
                if ("2".equals(itemId)) {
                    if (listOfStock.isEmpty() || listOfStock.get(0).getId() == 0) {
                        result = categorizationService.addToItem(Integer.valueOf(itemId), selectedData, categorizationService.jsonArrayCategories(listOfSelectedCategory), null);
                    } else {
                        result = categorizationService.addToItem(Integer.valueOf(itemId), selectedData, categorizationService.jsonArrayCategories(listOfSelectedCategory), categorizationService.jsonArrayStocks(listOfStock));
                    }
                } else if ("3".equals(itemId) || "26".equals(itemId)) {
                    if (listOfAccount.isEmpty() || listOfAccount.get(0).getId() == 0) {
                        result = categorizationService.addToItem(Integer.valueOf(itemId), selectedData, categorizationService.jsonArrayCategories(listOfSelectedCategory), null);
                    } else {
                        result = categorizationService.addToItem(Integer.valueOf(itemId), selectedData, categorizationService.jsonArrayCategories(listOfSelectedCategory), categorizationService.jsonArrayAccounts(listOfAccount));
                    }
                }
                if (result > 0) {
                    RequestContext.getCurrentInstance().execute("PF('dlg_addtoitem').hide();");
                }
                break;
        }

        sessionBean.createUpdateMessage(result);
    }

    public void convertCategoryToTreeeNode(TreeNode node, Categorization categorization) {
        List<TreeNode> children = node.getChildren();
        if (!children.isEmpty()) {
            for (TreeNode treeNode : children) {

                if (((Categorization) treeNode.getData()).getId() == categorization.getId()) {
                    findTreeNode = treeNode;
                    break;

                } else {
                    convertCategoryToTreeeNode(treeNode, categorization);
                }

            }
        }
    }

    public void testBeforeDelete() {
        int result = 0;
        selectedData.setItem(new Item(Integer.valueOf(itemId)));
        result = categorizationService.testBeforeDelete(selectedData);
        if (result == 0) {//Sil
            RequestContext.getCurrentInstance().update("frmCategory:dlgDelete");
            RequestContext.getCurrentInstance().execute("PF('dlgDeleteCategory').show();");
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("itcannotbedeletedbecausecategoryhassubcategory")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        }
    }

    public void delete() {
        int result = 0;
        result = categorizationService.delete(selectedData);
        if (result > 0) {
            convertCategoryToTreeeNode(root, selectedData);
            if (((Categorization) findTreeNode.getParent().getData()).getId() == 0) {
                root.getChildren().remove(findTreeNode);
            } else {
                findTreeNode.getParent().getChildren().remove(findTreeNode);
            }
            RequestContext.getCurrentInstance().update("frmCategorization:treCategory");

        }
        sessionBean.createUpdateMessage(result);
    }

    // ---------------item eklerken----dosya yükleme---için------------------------------
    public void openFileUpload() throws ParseException {
        // SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmtoolbar");
        context.update("form:pgrFileUpload");

        sampleList = new ArrayList<>();
        /**
         * Örnek liste çekilir.
         */
        sampleList = categorizationService.createSampleList();

        context.execute("PF('dlg_fileupload').show();");

    }

    /**
     * Bu metot dosya yükleme dialogu açıldığı zaman verileri sıfırlamak için
     * kullanılır.
     */
    public void resetUpload() {
        clearData();
        isOpenTransferBtn = true;
        isOpenCancelBtn = true;
        isOpenSaveBtn = false;
        fileNames = "";
        uploadedFile = null;

    }

    public void clearData() {
        stock = new Stock();
        excelStockList = new ArrayList<>();
        tempErrorList = new ArrayList<>();
        tempStockList = new ArrayList<>();

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
        // isErrorDataShow = false;

        File destFile = new File(uploadedFile.getFileName());
        FileUtils.copyInputStreamToFile(uploadedFile.getInputstream(), destFile);

    }

    /**
     * Bu metot aktar butonuna basınca çalışır.Excelden okuduğu verileri
     * istenilen formda geriye döndürür.
     */
    public void convertUploadData() {
        clearData();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            excelStockList = categorizationService.processUploadFile(uploadedFile.getInputstream());

            tempErrorList.addAll(excelStockList);
            tempStockList.addAll(excelStockList);

            int count = 0;
            for (Stock obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
                if (obj.getExcelDataType() == 1) {
                    count++;
                    break;
                }
            }
            if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
                isOpenSaveBtn = true;
            }
            context.execute("PF('dlg_productView').show();");
            context.update("btnSave");

            isOpenCancelBtn = false;

        } catch (Exception e) {

        }
    }

    /**
     * Bu metot listede bulunan hatalı kayıtları göstermek için kullanılır.
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<Stock> iterator = tempErrorList.iterator(); iterator.hasNext();) {
                Stock value = iterator.next();
                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }
            excelStockList.clear();
            excelStockList.addAll(tempErrorList);
        } else {
            excelStockList.clear();
            excelStockList.addAll(tempStockList);
        }
        context.update("frmProductView:dtbProductView");
    }

    public void clear() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("form:pgrFileUpload");
    }

    public void saveItem() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();
        for (Stock stock : tempStockList) {
            if (stock.getExcelDataType() == 1) {
                excelStockList.add(stock);
            }
        }
        String resultJson = categorizationService.importProductList(excelStockList);
        excelStockList.clear();
        excelStockList.addAll(tempStockList);

        //System.out.println("----resultJson----" + resultJson);
        JSONArray jsonArr = new JSONArray(resultJson);
        for (int m = 0; m < jsonArr.length(); m++) {
            ErrorItem item = new ErrorItem();
            String jsonBarcode = jsonArr.getJSONObject(m).getString("barcode");
            int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorCode");
            item.setBarcode(jsonBarcode);
            item.setErrorCode(jsonErrorCode);
            if (item.getErrorCode() == -1) {
                item.setErrorString(sessionBean.getLoc().getString("noproductsfoundforbarcodeinformation"));
                errorList.add(item);
            } else {
                Stock stock = new Stock();
                stock.setId(item.getErrorCode());
                stock.setBarcode(item.getBarcode());
                boolean isThere=false;
                for (Stock stock1 : listOfStock) {
                    if(stock1.getId()==stock.getId()){
                        isThere=true;
                        break;
                    }
                }
                if (!isThere) {
                    listOfStock.add(stock);
                }
            }

        }

        stockBookCheckboxFilterBean.getTempSelectedDataList().clear();
        stockBookCheckboxFilterBean.getTempSelectedDataList().addAll(listOfStock);
        if (stockBookCheckboxFilterBean.getTempSelectedDataList().isEmpty()) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else if (stockBookCheckboxFilterBean.getTempSelectedDataList().get(0).getId() == 0) {
            stockBookCheckboxFilterBean.setSelectedCount(sessionBean.loc.getString("all"));
        } else {
            stockBookCheckboxFilterBean.setSelectedCount(stockBookCheckboxFilterBean.getTempSelectedDataList().size() + " " + sessionBean.loc.getString("stock") + " " + sessionBean.loc.getString("selected"));
        }
       

        context.update("frmAddToItem:txtItem");
        context.execute("PF('dlg_productView').hide();");
        context.execute("PF('dlg_fileupload').hide();");

        if (!errorList.isEmpty()) {
            context.execute("PF('dlg_productErrorView').show();");
            context.update("frmProductErrorView:dtbProductErrorView");
        }

    }
    public void downloadSampleList()
    {
        categorizationService.downloadSampleList(sampleList);
    }

}
