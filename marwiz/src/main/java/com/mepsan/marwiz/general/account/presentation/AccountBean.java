/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.01.2018 01:29:12
 */
package com.mepsan.marwiz.general.account.presentation;

import com.mepsan.marwiz.general.account.business.IAccountService;
import com.mepsan.marwiz.general.categorization.presentation.CategoryBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountUpload;
import com.mepsan.marwiz.general.model.general.Categorization;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.pricelist.dao.ErrorItem;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class AccountBean extends GeneralDefinitionBean<Account> {

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{accountService}")
    public IAccountService accountService;

    @ManagedProperty(value = "#{categoryBean}")
    public CategoryBean categoryBean;

    @ManagedProperty(value = "#{marwiz}")
    public Marwiz marwiz;

    private Object object;  // yeniye bastığında gotopage fonksiyonuna parametre olarak bunu gönderiyoruz. ekleme modunda açılsın diye
    private List<AccountUpload> sampleList;
    private UploadedFile uploadedFile;
    private String fileNames;
    private String fileName;
    private boolean isOpenSaveBtn, isOpenCancelBtn, isOpenTransferBtn, isOpenErrorData;
    private List<ErrorItem> errorList;

    private List<AccountUpload> excelStockList;
    private List<AccountUpload> tempAccountList;
    private List<AccountUpload> tempAccountList2;

    private BigDecimal subTotalBalance;
    private AccountParam accountParam;
    private List<Categorization> selectedCategoryList;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    public List<AccountUpload> getSampleList() {
        return sampleList;
    }

    public void setSampleList(List<AccountUpload> sampleList) {
        this.sampleList = sampleList;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
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

    public List<ErrorItem> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<ErrorItem> errorList) {
        this.errorList = errorList;
    }

    public List<AccountUpload> getExcelStockList() {
        return excelStockList;
    }

    public void setExcelStockList(List<AccountUpload> excelStockList) {
        this.excelStockList = excelStockList;
    }

    public List<AccountUpload> getTempAccountList() {
        return tempAccountList;
    }

    public void setTempAccountList(List<AccountUpload> tempAccountList) {
        this.tempAccountList = tempAccountList;
    }

    public List<AccountUpload> getTempAccountList2() {
        return tempAccountList2;
    }

    public void setTempAccountList2(List<AccountUpload> tempAccountList2) {
        this.tempAccountList2 = tempAccountList2;
    }

    public BigDecimal getSubTotalBalance() {
        return subTotalBalance;
    }

    public void setSubTotalBalance(BigDecimal subTotalBalance) {
        this.subTotalBalance = subTotalBalance;
    }

    public void setCategoryBean(CategoryBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    //-----cari gridinde tutulan filtreleme parametreleri için class oluşturuldu.------------
    public class AccountParam {

        private TreeNode category;
        private String filterValue;
        private boolean isWithoutMovement;
        private boolean isZeroBalance;

        public AccountParam() {
            category = new DefaultTreeNode();
        }

        public String getFilterValue() {
            return filterValue;
        }

        public void setFilterValue(String filterValue) {
            this.filterValue = filterValue;
        }

        public boolean isIsWithoutMovement() {
            return isWithoutMovement;
        }

        public void setIsWithoutMovement(boolean isWithoutMovement) {
            this.isWithoutMovement = isWithoutMovement;
        }

        public boolean isIsZeroBalance() {
            return isZeroBalance;
        }

        public void setIsZeroBalance(boolean isZeroBalance) {
            this.isZeroBalance = isZeroBalance;
        }

        public TreeNode getCategory() {
            return category;
        }

        public void setCategory(TreeNode category) {
            this.category = category;
        }

    }

    public AccountParam getAccountParam() {
        return accountParam;
    }

    public void setAccountParam(AccountParam accountParam) {
        this.accountParam = accountParam;
    }

    public List<Categorization> getSelectedCategoryList() {
        return selectedCategoryList;
    }

    public void setSelectedCategoryList(List<Categorization> selectedCategoryList) {
        this.selectedCategoryList = selectedCategoryList;
    }

    @Override
    @PostConstruct
    public void init() {
        System.out.println("-------------AccountBean");

        accountParam = new AccountParam();
        selectedCategoryList = new ArrayList<>();
        listOfFilteredObjects = new ArrayList<>();
        boolean isThere = false;
        toogleList = new ArrayList<>();
        object = new Object();
        toogleList = Arrays.asList(true, true, true, true, true, true, true, true, true);
        DefaultTreeNode defaultTreeNode = new DefaultTreeNode(new Categorization(0, sessionBean.getLoc().getString("all")));
        categoryBean.getRoot().getChildren().add(0, defaultTreeNode);

        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof AccountParam) {
                    accountParam = (AccountParam) ((ArrayList) sessionBean.parameter).get(i);
                    findNode(categoryBean.getRoot(), (Categorization) accountParam.getCategory().getData());
                    autoCompleteValue = accountParam.getFilterValue();
                    isThere = true;
                }

            }

            if (!isThere) {
                categoryBean.getRoot().getChildren().get(0).setSelected(true);
                findNode(categoryBean.getRoot(), (Categorization) categoryBean.getRoot().getChildren().get(0).getData());
                selectedCategoryList.clear();
                listOfObjects = findall(" ");
                listOfFilteredObjects.addAll(listOfObjects);
                if (!listOfObjects.isEmpty()) {
                    calcSubTotals();
                }
            }
            bringList();

        } else {
            categoryBean.getRoot().getChildren().get(0).setSelected(true);
            findNode(categoryBean.getRoot(), (Categorization) categoryBean.getRoot().getChildren().get(0).getData());
            selectedCategoryList.clear();
            listOfObjects = findall(" ");
            listOfFilteredObjects.addAll(listOfObjects);
            if (!listOfObjects.isEmpty()) {
                calcSubTotals();
            }
        }

        if (marwiz.getPageIdOfGoToPage() == 1) {
            setListBtn(sessionBean.checkAuthority(new int[]{56}, 0));
        } else {
            setListBtn(sessionBean.checkAuthority(new int[]{59}, 0));
        }
    }

    /**
     * Yeni account eklemek için işlem sayfasını açar.
     */
    @Override
    public void create() {
        List<Object> list = new ArrayList<>();
        accountParam.setFilterValue(autoCompleteValue);
        accountParam.setCategory(categoryBean.getSelectedCategory());

        list.add(accountParam);
        list.add(object);

        if (marwiz.getPageIdOfGoToPage() == 86) {
            marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 0, 87);
        } else {

            marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 0, 11);
        }

    }

    public void goToProcess() {
        if (selectedObject != null) {
            List<Object> list = new ArrayList<>();
            accountParam.setFilterValue(autoCompleteValue);
            accountParam.setCategory(categoryBean.getSelectedCategory());
            list.add(accountParam);
            list.add(selectedObject);
            if (marwiz.getPageIdOfGoToPage() == 86) {
                marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 0, 87);
            } else {
                marwiz.goToPage("/pages/general/account/accountprocess.xhtml", list, 0, 11);
            }
        }

    }

    @Override
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Account> findall(String where) {
        if (marwiz.getPageIdOfGoToPage() == 86) {
            return accountService.findAll(" AND acc.is_employee=true " + where);
        } else {
            return accountService.findAll(" AND acc.is_employee=false " + where);
        }
    }

    public boolean renderedColumnValue(BigDecimal balance, int type) {
        if (type == 1) {

            if (balance.compareTo(BigDecimal.valueOf(0)) == -1) {
                return true;
            } else {
                return false;
            }

        } else if (type == 2) {
            if (balance.compareTo(BigDecimal.valueOf(0)) == 1) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public void calcSubTotals() {
        subTotalBalance = new BigDecimal(BigInteger.ZERO);

        for (Account u : listOfFilteredObjects) {

            subTotalBalance = subTotalBalance.add(u.getBalance());

        }
    }

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
        context.update("form:pgrFileUpload");
    }

    public void clearProducts() {
        excelStockList = new ArrayList<>();
        tempAccountList = new ArrayList<>();
        tempAccountList2 = new ArrayList<>();

    }

    public void openFileUpload() {
        resetUpload();
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("frmtoolbar");
        context.update("form:pgrFileUpload");

        sampleList = new ArrayList<>();

        sampleList = accountService.createSampleList();
        context.execute("PF('dlg_stockfileupload').show();");

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

        excelStockList = accountService.processUploadFile(uploadedFile.getInputstream());

        tempAccountList.addAll(excelStockList);
        tempAccountList2.addAll(excelStockList);

        int count = 0;
        for (AccountUpload obj : excelStockList) { // eğer listenin tamamı hatalı ise kaydet butonu kapatılır.
            if (obj.getExcelDataType() == 1) {
                count++;
                break;
            }
        }
        if (count == 0) { // eğer tüm kayıtlar hatalı ise bilgi mesajı verilir.
            isOpenSaveBtn = true;
        }
        isOpenErrorData = false;
        context.execute("PF('dlg_productView').show();");
        context.update("frmProductView:dtbProductView");
        context.update("dlgProductView");
        context.update("btnSave");

        isOpenCancelBtn = false;

    }

    /**
     * Bu metot hatalı kayıtları göstermek/ gizlemek durumunda çalışır.Listeyi
     * günceller
     */
    public void showErrorProductList() {
        RequestContext context = RequestContext.getCurrentInstance();
        if (isOpenErrorData) {
            for (Iterator<AccountUpload> iterator = tempAccountList.iterator(); iterator.hasNext();) {
                AccountUpload value = iterator.next();
                if (value.getExcelDataType() == 1) {
                    iterator.remove();
                }
            }
            excelStockList.clear();
            excelStockList.addAll(tempAccountList);
        } else {
            excelStockList.clear();
            excelStockList.addAll(tempAccountList2);
        }
        context.update("frmProductView:dtbProductView");
    }

    public void saveAccount() {
        errorList = new ArrayList<>();
        RequestContext context = RequestContext.getCurrentInstance();
        excelStockList.clear();
        for (AccountUpload account : tempAccountList2) {
            if (account.getExcelDataType() == 1) {
                excelStockList.add(account);
            }
        }
        String resultJson = accountService.jsonToList(excelStockList);
        excelStockList.clear();
        excelStockList.addAll(tempAccountList2);
        if (resultJson == null || resultJson.equals("[]") || resultJson.equals("")) {
            sessionBean.createUpdateMessage(1);
            context.execute("PF('dlg_productView').hide();");
            listOfObjects.clear();
            listOfObjects = findall(" ");
            if (!listOfObjects.isEmpty()) {
                listOfFilteredObjects.clear();
                listOfFilteredObjects.addAll(listOfObjects);
                calcSubTotals();
            }
            context.update("frmAccount:dtbAccount");
            context.execute("PF('dlg_stockfileupload').hide()");

        } else {// veritabanından geriye dönen hata kodları ve hata mesajları Jsonarray olarak alınır.
            JSONArray jsonArr = new JSONArray(resultJson);
            for (int m = 0; m < jsonArr.length(); m++) {
                ErrorItem item = new ErrorItem();
                String jsonBarcode = jsonArr.getJSONObject(m).getString("code");
                int jsonErrorCode = jsonArr.getJSONObject(m).getInt("errorCode");
                item.setBarcode(jsonBarcode);
                item.setErrorCode(jsonErrorCode);
                switch (item.getErrorCode()) {
                    case -1:
                        item.setErrorString(sessionBean.getLoc().getString("taxnumberisincorrect"));
                        break;
                    case -2:
                        item.setErrorString(sessionBean.getLoc().getString("processcannotbedoneinretailsalecustomer"));
                        break;
                    case -3:
                        item.setErrorString(sessionBean.getLoc().getString("thereisadifferentcurrentinthesystemwiththesamecodeinformation"));
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
        }
    }

    public void createPdf() {

        String clmName = " ";
        if (marwiz.getPageIdOfGoToPage() == 86) {
            clmName = sessionBean.getLoc().getString("surname");
        } else {
            clmName = sessionBean.getLoc().getString("commercialtitle");
        }

        String createWhere = "";
        if (marwiz.getPageIdOfGoToPage() == 86) {
            createWhere = (" AND acc.is_employee=true ");
        } else {
            createWhere = (" AND acc.is_employee=false ");
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmIsPerson")) {
            toogleList.set(0, true);
        } else {
            toogleList.set(0, false);
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmCode")) {
            toogleList.set(1, true);
        } else {
            toogleList.set(1, false);
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmName")) {
            toogleList.set(2, true);
        } else {
            toogleList.set(2, false);
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmTitle")) {
            toogleList.set(3, true);
        } else {
            toogleList.set(3, false);
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmTaxNo")) {
            toogleList.set(4, true);
        } else {
            toogleList.set(4, false);
        }

        if (getColumnVisibility("frmAccount:dtbAccount:clmTaxOffice")) {
            toogleList.set(5, true);
        } else {
            toogleList.set(5, false);
        }
        if (getColumnVisibility("frmAccount:dtbAccount:clmCreditLimit")) {
            toogleList.set(6, true);
        } else {
            toogleList.set(6, false);
        }
        if (getColumnVisibility("frmAccount:dtbAccount:clmType")) {
            toogleList.set(7, true);
        } else {
            toogleList.set(7, false);
        }
        if (getColumnVisibility("frmAccount:dtbAccount:clmStatus")) {
            toogleList.set(8, true);
        } else {
            toogleList.set(8, false);
        }

        if (autoCompleteValue == null) {
            accountService.exportPdf(listOfObjects, clmName, toogleList);

        } else {
            accountService.exportPdf(listOfFilteredObjects, clmName, toogleList);
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

    public void bringList() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmAccount:dtbAccount");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }

        Categorization categorization = new Categorization();
        selectedCategoryList.clear();
        if (categoryBean.getSelectedCategory() != null) {
            categorization.setId(((Categorization) categoryBean.getSelectedCategory().getData()).getId());
            selectedCategoryList.add(((Categorization) categoryBean.getSelectedCategory().getData()));
            findChildren(categoryBean.getSelectedCategory());

        }

        String createWhere = "";
        if (marwiz.getPageIdOfGoToPage() == 86) {
            createWhere = accountService.createWhere(accountParam.isIsWithoutMovement(), accountParam.isIsZeroBalance(), selectedCategoryList, 1);
        } else {
            createWhere = accountService.createWhere(accountParam.isIsWithoutMovement(), accountParam.isIsZeroBalance(), selectedCategoryList, 0);
        }

        listOfObjects = findall(createWhere);

        RequestContext.getCurrentInstance().execute("PF('accountPF').filter();");

    }

    @Override
    public List<Account> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void downloadSampleList() {
        accountService.downloadSampleList(sampleList);
    }
}
