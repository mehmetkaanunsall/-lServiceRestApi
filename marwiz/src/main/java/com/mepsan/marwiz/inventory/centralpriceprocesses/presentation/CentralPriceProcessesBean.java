/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   04.06.2020 05:19:34
 */
package com.mepsan.marwiz.inventory.centralpriceprocesses.presentation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.pattern.CentrowizLazyDataModel;
import com.mepsan.marwiz.general.pattern.GeneralBean;
import com.mepsan.marwiz.general.pattern.GeneralReportBean;
import com.mepsan.marwiz.inventory.centralpriceprocesses.business.GFCentralPriceProcessService;
import com.mepsan.marwiz.inventory.centralpriceprocesses.business.ICentralPriceProcessService;
import com.mepsan.marwiz.inventory.centralpriceprocesses.dao.CentralPriceProcess;
import com.mepsan.marwiz.system.branch.business.IBranchService;
import com.mepsan.marwiz.system.branch.business.IBranchSettingService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ManagedBean
@ViewScoped
public class CentralPriceProcessesBean extends GeneralBean<CentralPriceProcess> {

    @ManagedProperty(value = "#{branchSettingService}")
    private IBranchSettingService branchSettingService;

    @ManagedProperty(value = "#{sessionBean}")
    public SessionBean sessionBean;

    @ManagedProperty(value = "#{gfCentralPriceProcessService}")
    private GFCentralPriceProcessService gfCentralPriceProcessService;

    @ManagedProperty(value = "#{centralPriceProcessService}")
    private ICentralPriceProcessService centralPriceProcessService;

    private List<BranchSetting> listOfBranch;
    private List<BranchSetting> selectedBranchList;
    private List<CentralPriceProcess> selectedCentralPriceList;
    private boolean isPurchase;
    private int branchStock;
    private boolean isFind;
    private boolean isCheck;
    private boolean isBranchAuth;
    private String branchID;

    public List<CentralPriceProcess> getSelectedCentralPriceList() {
        return selectedCentralPriceList;
    }

    public void setBranchSettingService(IBranchSettingService branchSettingService) {
        this.branchSettingService = branchSettingService;
    }

    public void setSelectedCentralPriceList(List<CentralPriceProcess> selectedCentralPriceList) {
        this.selectedCentralPriceList = selectedCentralPriceList;
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

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public boolean isIsPurchase() {
        return isPurchase;
    }

    public void setIsPurchase(boolean isPurchase) {
        this.isPurchase = isPurchase;
    }

    public void setCentralPriceProcessService(ICentralPriceProcessService centralPriceProcessService) {
        this.centralPriceProcessService = centralPriceProcessService;
    }

    public void setGfCentralPriceProcessService(GFCentralPriceProcessService gfCentralPriceProcessService) {
        this.gfCentralPriceProcessService = gfCentralPriceProcessService;
    }

    public boolean isIsFind() {
        return isFind;
    }

    public void setIsFind(boolean isFind) {
        this.isFind = isFind;
    }

    public boolean isIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isIsBranchAuth() {
        return isBranchAuth;
    }

    public void setIsBranchAuth(boolean isBranchAuth) {
        this.isBranchAuth = isBranchAuth;
    }

    @Override
    @PostConstruct
    public void init() {
        listOfBranch = new ArrayList<>();
        selectedBranchList = new ArrayList<>();
        selectedCentralPriceList = new ArrayList<>();
        selectedObject = new CentralPriceProcess();

        listOfBranch = branchSettingService.findUserAuthorizeBranchForInvoiceAuth();// kullanıcının yetkili olduğu branch listesini çeker

        for (BranchSetting branchSetting : listOfBranch) {
            if (branchSetting.getBranch().getId() == sessionBean.getUser().getLastBranch().getId()) {
                selectedBranchList.add(branchSetting);
                break;
            }
        }
        changeBranch();
    }

    public void find() {
        isFind = true;
        branchStock = 0;
        selectedCentralPriceList.clear();

        int countCentral = 0;
        int countNotCentral = 0;
        int countTotal = 0;
        branchID = "";
        if (!selectedBranchList.isEmpty()) {
            for (BranchSetting branchSetting : selectedBranchList) {
                if (branchSetting.isIsCentralIntegration()) {
                    countCentral = countCentral + 1;
                } else {
                    countNotCentral = countNotCentral + 1;
                }
                countTotal++;
                branchID = branchID + "," + String.valueOf(branchSetting.getBranch().getId());
            }
        } else {
            for (BranchSetting branchSetting : listOfBranch) {
                if (branchSetting.isIsCentralIntegration()) {
                    countCentral = countCentral + 1;
                } else {
                    countNotCentral = countNotCentral + 1;
                }
                countTotal++;
                branchID = branchID + "," + String.valueOf(branchSetting.getBranch().getId());
            }
        }
        if (!branchID.equals("")) {
            branchID = branchID.substring(1, branchID.length());
        }
        if (countCentral == countTotal) {//Hepsi merkezi entegrasyonlu
            branchStock = 1;
        } else if (countNotCentral == countTotal) {//Hepsi merkezi olamayan bağlantısız
            branchStock = 2;
        } else {//Karışık
            branchStock = 3;
        }

        listOfObjects = findall(" ");

        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmCentralPriceProcessDatatable:dtbCentralPriceProcess");
        if (dataTable != null) {
            dataTable.setFirst(0);
        }
        RequestContext.getCurrentInstance().update("frmCentralPriceProcessDatatable:dtbCentralPriceProcess");
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        RequestContext.getCurrentInstance().execute("priceChangeSave();");
    }

    public void confirmSave() {
        int result = 0;
        result = centralPriceProcessService.save(selectedBranchList.isEmpty() ? listOfBranch : selectedBranchList, selectedCentralPriceList, isPurchase);
        if (result > 0) {
            isFind = false;
            RequestContext.getCurrentInstance().update("pgrCentralPriceProcessDatatable");
        }
        sessionBean.createUpdateMessage(result);
    }

    public void remoteSave() {
        if (selectedCentralPriceList.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("atleastonestockpricemustbeentered")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
        } else {
            RequestContext.getCurrentInstance().execute("PF('dlgConfirmSavePrice').show();");
        }
    }

    @Override
    public LazyDataModel<CentralPriceProcess> findall(String where) {
        return new CentrowizLazyDataModel<CentralPriceProcess>() {
            @Override
            public List<CentralPriceProcess> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
                List<CentralPriceProcess> result = centralPriceProcessService.findAll(first, pageSize, sortField, convertSortOrder(sortOrder), filters, where, branchStock, branchID);
                int count = centralPriceProcessService.count(where, branchStock,branchID);
                for (CentralPriceProcess cp : result) {
                    for (CentralPriceProcess cpsel : selectedCentralPriceList) {
                        if (cp.getPriceListItem().getStock().getId() == cpsel.getPriceListItem().getStock().getId()) {
                            cp.getPriceListItem().setPrice(cpsel.getPriceListItem().getPrice());
                            cp.getPriceListItem().getCurrency().setId(cpsel.getPriceListItem().getCurrency().getId());
                            cp.getPriceListItem().getCurrency().setTag(cpsel.getPriceListItem().getCurrency().getTag());
                            cp.getPriceListItem().setIs_taxIncluded(cpsel.getPriceListItem().isIs_taxIncluded());
                        }
                    }
                }
                listOfObjects.setRowCount(count);
                RequestContext.getCurrentInstance().execute("count=" + count + ";");
                return result;
            }
        };
    }

    public void onCellEdit(CellEditEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        selectedObject = context.getApplication().evaluateExpressionGet(context, "#{centralPriceProcess}", CentralPriceProcess.class);

        bringTagOfCurrency();
        if ((selectedObject.getPriceListItem().getPrice() != null && selectedObject.getPriceListItem().getPrice().compareTo(BigDecimal.valueOf(0)) == 1) && selectedObject.getPriceListItem().getCurrency().getId() != 0) {
            if (!selectedCentralPriceList.contains(selectedObject)) {
                selectedCentralPriceList.add(selectedObject);
            } else {
                for (Iterator<CentralPriceProcess> iterator = selectedCentralPriceList.iterator(); iterator.hasNext();) {
                    CentralPriceProcess next = iterator.next();
                    if (next.getPriceListItem().getStock().getId() == selectedObject.getPriceListItem().getStock().getId()) {
                        iterator.remove();
                        break;
                    }
                }
                selectedCentralPriceList.add(selectedObject);
            }

        } else {
            if (selectedCentralPriceList.contains(selectedObject)) {
                for (Iterator<CentralPriceProcess> iterator = selectedCentralPriceList.iterator(); iterator.hasNext();) {
                    CentralPriceProcess next = iterator.next();
                    if (next.getPriceListItem().getStock().getId() == selectedObject.getPriceListItem().getStock().getId()) {
                        iterator.remove();
                        break;
                    }
                }
            }

        }
    }

    public void bringTagOfCurrency() {
        for (Currency s : sessionBean.getCurrencies()) {
            if (s.getId() == selectedObject.getPriceListItem().getCurrency().getId()) {
                selectedObject.getPriceListItem().getCurrency().setTag(s.getNameMap().get(sessionBean.getLangId()).getName());
            }
        }
        if (selectedObject.getPriceListItem().getCurrency().getId() == 0) {
            selectedObject.getPriceListItem().getCurrency().setTag("");
        }
    }

    @Override
    public void generalFilter() {

        if (autoCompleteValue == null) {
            listOfObjects = findall(" ");
        } else {
            gfCentralPriceProcessService.makeSearch(autoCompleteValue, " ", branchStock, branchID);
            listOfObjects = gfCentralPriceProcessService.searchResult;
        }
    }

    public void showList() {
        DataTable dataTable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent("frmCentralPriceProcessDatatable:dtbCentralPriceProcess");
        dataTable.setFirst(0);

        RequestContext.getCurrentInstance().update("frmCentralPriceProcessDatatable:dtbCentralPriceProcess");
    }

    @Override
    public void detailFilter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeBranch() {
        int countAuth = 0;
        if (!selectedBranchList.isEmpty()) {
            for (BranchSetting br : selectedBranchList) {
                if (br.getPurchaseUnitPriceUpdateOptionId() == 0) {//sadece alış faturası
                    countAuth++;
                }
            }
            if (selectedBranchList.size() == countAuth) {
                isBranchAuth = false;
            } else {
                isBranchAuth = true;
            }
        } else {
            for (BranchSetting br : listOfBranch) {
                if (br.getPurchaseUnitPriceUpdateOptionId() == 0) {//sadece alış faturası
                    countAuth++;
                }
            }
            if (listOfBranch.size() == countAuth) {
                isBranchAuth = false;
            } else {
                isBranchAuth = true;
            }
        }
    }

}
