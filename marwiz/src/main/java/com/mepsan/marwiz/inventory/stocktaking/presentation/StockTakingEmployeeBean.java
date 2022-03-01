/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.inventory.stocktaking.presentation;

import com.mepsan.marwiz.finance.financingdocument.business.FinancingDocumentService;
import com.mepsan.marwiz.general.common.IncomeExpenseBookFilterBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.exchange.business.ExchangeService;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import com.mepsan.marwiz.general.model.finance.IncomeExpense;
import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.AccountMovement;
import com.mepsan.marwiz.general.model.inventory.StockTaking;
import com.mepsan.marwiz.general.model.inventory.StockTakingItem;
import com.mepsan.marwiz.general.model.inventory.WarehouseMovement;
import com.mepsan.marwiz.general.pattern.GeneralDefinitionBean;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingItemService;
import com.mepsan.marwiz.inventory.stocktaking.business.IStockTakingService;
import com.mepsan.marwiz.inventory.stocktaking.dao.IStockTakingDao;
import com.mepsan.marwiz.inventory.warehousereceipt.business.IWarehouseMovementService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author esra.cabuk
 */
@ManagedBean
@ViewScoped
public class StockTakingEmployeeBean extends GeneralDefinitionBean<AccountMovement> {

    private List<AccountMovement> listOfSelectedObjects;
    private List<StockTaking> listOfStocktakingDiffPrice;
    private boolean isAll;
    private boolean isDirection;
    private boolean isDifference;
    private BigDecimal differencePrice;
    private IncomeExpense incomeExpense;
    private String selectedUser;
    private FinancingDocument financingDocument;
    private StockTaking stockTaking;
    private List<StockTakingItem> stockTakingItemList;
    private BigDecimal employeePrice, incomeExpensePrice, oldEmployeePrice, oldIncomeExpensePrice;

    private boolean isIncomeExpense;
    private String beanName;

    @ManagedProperty(value = "#{stockTakingDao}")
    private IStockTakingDao stockTakingDao;

    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty(value = "#{exchangeService}")
    private ExchangeService exchangeService;

    @ManagedProperty(value = "#{incomeExpenseBookFilterBean}")
    public IncomeExpenseBookFilterBean incomeExpenseBookFilterBean;

    @ManagedProperty(value = "#{stockTakingService}")
    private IStockTakingService stockTakingService;

    @ManagedProperty(value = "#{marwiz}")
    private Marwiz marwiz;

    public List<AccountMovement> getListOfSelectedObjects() {
        return listOfSelectedObjects;
    }

    public void setListOfSelectedObjects(List<AccountMovement> listOfSelectedObjects) {
        this.listOfSelectedObjects = listOfSelectedObjects;
    }

    public List<StockTaking> getListOfStocktakingDiffPrice() {
        return listOfStocktakingDiffPrice;
    }

    public void setListOfStocktakingDiffPrice(List<StockTaking> listOfStocktakingDiffPrice) {
        this.listOfStocktakingDiffPrice = listOfStocktakingDiffPrice;
    }

    public boolean isIsAll() {
        return isAll;
    }

    public void setIsAll(boolean isAll) {
        this.isAll = isAll;
    }

    public void setStockTakingDao(IStockTakingDao stockTakingDao) {
        this.stockTakingDao = stockTakingDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    public boolean isIsDirection() {
        return isDirection;
    }

    public void setIsDirection(boolean isDirection) {
        this.isDirection = isDirection;
    }

    public boolean isIsDifference() {
        return isDifference;
    }

    public void setIsDifference(boolean isDifference) {
        this.isDifference = isDifference;
    }

    public FinancingDocument getFinancingDocument() {
        return financingDocument;
    }

    public void setFinancingDocument(FinancingDocument financingDocument) {
        this.financingDocument = financingDocument;
    }

    public boolean isIsIncomeExpense() {
        return isIncomeExpense;
    }

    public void setIsIncomeExpense(boolean isIncomeExpense) {
        this.isIncomeExpense = isIncomeExpense;
    }

    public String getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setIncomeExpenseBookFilterBean(IncomeExpenseBookFilterBean incomeExpenseBookFilterBean) {
        this.incomeExpenseBookFilterBean = incomeExpenseBookFilterBean;
    }

    public BigDecimal getDifferencePrice() {
        return differencePrice;
    }

    public void setDifferencePrice(BigDecimal differencePrice) {
        this.differencePrice = differencePrice;
    }

    public IncomeExpense getIncomeExpense() {
        return incomeExpense;
    }

    public void setIncomeExpense(IncomeExpense incomeExpense) {
        this.incomeExpense = incomeExpense;
    }

    public StockTaking getStockTaking() {
        return stockTaking;
    }

    public void setStockTaking(StockTaking stockTaking) {
        this.stockTaking = stockTaking;
    }

    public BigDecimal getEmployeePrice() {
        return employeePrice;
    }

    public void setEmployeePrice(BigDecimal employeePrice) {
        this.employeePrice = employeePrice;
    }

    public BigDecimal getIncomeExpensePrice() {
        return incomeExpensePrice;
    }

    public void setIncomeExpensePrice(BigDecimal incomeExpensePrice) {
        this.incomeExpensePrice = incomeExpensePrice;
    }

    public BigDecimal getOldEmployeePrice() {
        return oldEmployeePrice;
    }

    public void setOldEmployeePrice(BigDecimal oldEmployeePrice) {
        this.oldEmployeePrice = oldEmployeePrice;
    }

    public BigDecimal getOldIncomeExpensePrice() {
        return oldIncomeExpensePrice;
    }

    public void setOldIncomeExpensePrice(BigDecimal oldIncomeExpensePrice) {
        this.oldIncomeExpensePrice = oldIncomeExpensePrice;
    }

    public void setStockTakingService(IStockTakingService stockTakingService) {
        this.stockTakingService = stockTakingService;
    }

    public List<StockTakingItem> getStockTakingItemList() {
        return stockTakingItemList;
    }

    public void setStockTakingItemList(List<StockTakingItem> stockTakingItemList) {
        this.stockTakingItemList = stockTakingItemList;
    }

    public void setMarwiz(Marwiz marwiz) {
        this.marwiz = marwiz;
    }

    @PostConstruct
    @Override
    public void init() {
        System.out.println("---------------StockTakingEmployeeBean--------------");
        listOfObjects = new ArrayList<>();
        listOfSelectedObjects = new ArrayList<>();
        listOfStocktakingDiffPrice = new ArrayList<>();
        incomeExpense = new IncomeExpense();
        stockTaking = new StockTaking();
        if (sessionBean.parameter instanceof ArrayList) {
            for (int i = 0; i < ((ArrayList) sessionBean.parameter).size(); i++) {
                if (((ArrayList) sessionBean.parameter).get(i) instanceof StockTaking) {
                    stockTaking = (StockTaking) ((ArrayList) sessionBean.parameter).get(i);
                }
            }
        }
        stockTakingItemList = new ArrayList<>();
    }

    public void setIsAll() {
        if (isAll) {
            listOfSelectedObjects.clear();
            listOfSelectedObjects.addAll(listOfObjects);
        } else {
            listOfSelectedObjects.clear();
        }
        selectEmployee();

    }

    public void openDialog() {
        beanName = "stockTakingEmployeeBean";
        isIncomeExpense = true;
        isDifference = false;
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();
        StockTakingProcessBean stockTakingProcessBean = (StockTakingProcessBean) viewMap.get("stockTakingProcessBean");
        // personelleri listeler
        for (Account account : stockTakingProcessBean.getAccountList()) {
            AccountMovement accountMovement = new AccountMovement();
            accountMovement.setAccount(account);
            listOfObjects.add(accountMovement);
        }

        //fark tutarını hesaplar.
        listOfStocktakingDiffPrice = stockTakingDao.findStockTakingDifference(stockTaking);
        differencePrice = BigDecimal.ZERO;
        if (!listOfStocktakingDiffPrice.isEmpty()) {

            for (StockTaking stockTaking : listOfStocktakingDiffPrice) {
                if (stockTaking.getCurrency().getId() == sessionBean.getUser().getLastBranch().getCurrency().getId()) {
                    differencePrice = differencePrice.add(stockTaking.getDifferencePrice());
                } else {
                    BigDecimal exchange = BigDecimal.ONE;
                    exchange = exchangeService.bringExchangeRate(stockTaking.getCurrency(), sessionBean.getUser().getLastBranch().getCurrency(), sessionBean.getUser());
                    differencePrice = differencePrice.add(stockTaking.getDifferencePrice().multiply(exchange));
                }
            }
        }
        if (differencePrice.compareTo(BigDecimal.ZERO) == 1) {
            isDirection = true;
            isDifference = true;
        } else if (differencePrice.compareTo(BigDecimal.ZERO) == -1) {
            isDirection = false;
            isDifference = true;
            differencePrice = differencePrice.multiply(BigDecimal.valueOf(-1));
        } else {
            isDifference = false;
        }
        stockTaking.setDifferencePrice(differencePrice);
        RequestContext.getCurrentInstance().update("dlgConfirmStockTaking");
        RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmStockTaking').show();");
    }

    public void selectEmployee() {
        for (AccountMovement listOfObject : listOfObjects) {
            listOfObject.setPrice(null);
        }
        BigDecimal calPrice = null;
        if (listOfSelectedObjects.size() > 0) {
            if (employeePrice != null) {
                calPrice = employeePrice.divide(BigDecimal.valueOf(listOfSelectedObjects.size()), 10, RoundingMode.HALF_EVEN);
            }
        }

        for (AccountMovement listOfObject : listOfSelectedObjects) {

            listOfObject.setPrice(calPrice);
        }

        selectedUser = listOfSelectedObjects.size() + " " + sessionBean.getLoc().getString("employee") + " " + sessionBean.getLoc().getString("selected");
        RequestContext.getCurrentInstance().update("frmAproval");
        RequestContext.getCurrentInstance().update("frmStockTakingEmployee:dtbStockTakingEmployee");
    }

    public void updateAllInformation() {

        if (incomeExpenseBookFilterBean.getSelectedData() != null) {
            setIncomeExpense(incomeExpenseBookFilterBean.getSelectedData());
            incomeExpenseBookFilterBean.setSelectedData(null);
        }
        RequestContext.getCurrentInstance().update("frmAproval");
    }

    @Override
    public void create() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void changeEmployeePrice() {
        if (employeePrice == null) {
            employeePrice = BigDecimal.ZERO;
        }
        if (employeePrice.compareTo(differencePrice) == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("theamountenteredcannotexceedthedifferenceamount")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            employeePrice = oldEmployeePrice;
            RequestContext.getCurrentInstance().update("frmAproval");
        } else {
            oldEmployeePrice = employeePrice;
            incomeExpensePrice = differencePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN).subtract(employeePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN));
            selectEmployee();
        }

    }

    public void changeIncomeExpensePrice() {
        if (incomeExpensePrice == null) {
            incomeExpensePrice = BigDecimal.ZERO;
        }
        if (incomeExpensePrice.compareTo(differencePrice) == 1) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("theamountenteredcannotexceedthedifferenceamount")));
            RequestContext.getCurrentInstance().update("grwProcessMessage");
            incomeExpensePrice = oldIncomeExpensePrice;
            RequestContext.getCurrentInstance().update("frmAproval");
        } else {
            oldIncomeExpensePrice = incomeExpensePrice;
            employeePrice = differencePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN).subtract(incomeExpensePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN));
            selectEmployee();
        }

    }

    @Override
    public void save() {
        RequestContext.getCurrentInstance().execute("PF('dlg_StockTakingEmployee').hide()");

    }

    /**
     * Sayımı bitirdikten sonra depodaki stok sayılarıyla sayım arasındaki
     * farklara göre depo giriş çıkış fişi oluşturulur. Sayım miktarları
     * güncellenir ve sayım kapatılır.
     */
    public void finish() {

        boolean warn = false;
        if (isDifference) {

            if (employeePrice == null) {
                employeePrice = BigDecimal.ZERO;
            }
            if (incomeExpensePrice == null) {
                incomeExpensePrice = BigDecimal.ZERO;
            }

            if (employeePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN).compareTo(differencePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN)) == 1 || incomeExpensePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN).compareTo(differencePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN)) == 1) {
                warn = true;
            }

            if (employeePrice.add(incomeExpensePrice).setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN).compareTo(differencePrice.setScale(sessionBean.getUser().getLastBranch().getCurrencyrounding(), RoundingMode.HALF_EVEN)) != 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("thesumoftheenteredamountsshouldbeequaltothedifferenceamount")));
                RequestContext.getCurrentInstance().update("grwProcessMessage");
                warn = true;
            } else {

                if (isDifference && employeePrice != null && employeePrice.compareTo(BigDecimal.ZERO) == 1 && listOfSelectedObjects.isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectemployee")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    warn = true;
                }
                if (isDifference && incomeExpensePrice != null && incomeExpensePrice.compareTo(BigDecimal.ZERO) == 1 && incomeExpense.getId() == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, sessionBean.loc.getString("warning"), sessionBean.loc.getString("pleaseselectincomeexpense")));
                    RequestContext.getCurrentInstance().update("grwProcessMessage");
                    warn = true;
                }
            }
        }

        if (!warn) {
            FinancingDocument financingDocument = new FinancingDocument();
            String accounts = null;
            // fazla veya açık varsa finansman belgesi oluşturur
            if (isDifference) {
                SimpleDateFormat format = new SimpleDateFormat(sessionBean.getUser().getLastBranch().getDateFormat() + " HH:mm:ss");
                String dateformat = format.format(stockTaking.getBeginDate());

                if (incomeExpensePrice.compareTo(BigDecimal.ZERO) == 0) {
                    financingDocument.setIncomeExpense(new IncomeExpense());
                } else {
                    financingDocument.setIncomeExpense(incomeExpense);
                }
                financingDocument.setPrice(incomeExpensePrice);
                financingDocument.setCurrency(sessionBean.getUser().getLastBranch().getCurrency());
                financingDocument.setExchangeRate(BigDecimal.ONE);
                financingDocument.setDocumentDate(new Date());

                if (isDirection) { //gelir - alacak dekontu
                    financingDocument.getFinancingType().setId(50);
                    financingDocument.setDescription(sessionBean.getLoc().getString("stocktakingexcess") + " (" + dateformat + " - " + stockTaking.getName() + ")");

                } else {
                    financingDocument.getFinancingType().setId(49);
                    financingDocument.setDescription(sessionBean.getLoc().getString("stocktakingdeficit") + " (" + dateformat + " - " + stockTaking.getName() + ")");
                }

                if (employeePrice.compareTo(BigDecimal.ZERO) == 0) {
                    listOfSelectedObjects.clear();
                }

                //----personel listesi boş değilse liste json arraye dönüştürülür.------
                if (listOfSelectedObjects.size() > 0) {
                    accounts = stockTakingService.jsonArrayAccounts(listOfSelectedObjects);
                }

            }

            RequestContext.getCurrentInstance().execute("PF('dlg_ConfirmStockTaking').hide();");

            //----- giriş ve çıkış fişleri ve finansman belgeleri oluşturularak depo sayımı güncellenir.
            int result = stockTakingService.finisStockTaking(stockTaking, financingDocument, accounts);
            if (result > 0) {
                RequestContext.getCurrentInstance().execute("PF('dlg_UncountedStocks').hide();");
                RequestContext.getCurrentInstance().execute("finish();");
            }
            sessionBean.createUpdateMessage(result);
        }

    }

    public void cancel() {
        employeePrice = null;
        incomeExpensePrice = null;
        setEmployeePrice(null);
        listOfSelectedObjects.clear();
        for (AccountMovement accountMovement : listOfObjects) {
            accountMovement.setPrice(null);
        }
        setSelectedUser("");
        setIncomeExpense(new IncomeExpense());
        stockTaking.getApprovalEmployee().setId(0);

    }

    public void goToGrid() {
        marwiz.goToPage("/pages/inventory/stocktaking/stocktaking.xhtml", null, 1, 53);
    }

    @Override
    public List<AccountMovement> findall() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
